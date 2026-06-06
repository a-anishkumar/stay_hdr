package com.example.hydraflowai.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.hydraflowai.data.local.HydraSQLiteHelper
import com.example.hydraflowai.data.local.entity.Challenge
import com.example.hydraflowai.data.local.entity.DailyGoal
import com.example.hydraflowai.data.local.entity.IntakeRecord
import com.example.hydraflowai.data.local.entity.Streak
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class SqliteHydraDao(
    private val helper: HydraSQLiteHelper,
    private val scope: CoroutineScope
) : HydraDao {

    // Reactive streams cache
    private val intakesSharedFlow = MutableSharedFlow<List<IntakeRecord>>(replay = 1)
    private val streakSharedFlow = MutableSharedFlow<Streak?>(replay = 1)
    private val challengesSharedFlow = MutableSharedFlow<List<Challenge>>(replay = 1)
    private val goalsFlowMap = ConcurrentHashMap<String, MutableSharedFlow<DailyGoal?>>()

    init {
        // Trigger initial emissions
        scope.launch {
            triggerIntakesUpdate()
            triggerStreakUpdate()
            triggerChallengesUpdate()
        }
    }

    private fun triggerIntakesUpdate() {
        val db = helper.readableDatabase
        val list = mutableListOf<IntakeRecord>()
        val cursor = db.rawQuery("SELECT * FROM intake_records ORDER BY timestamp DESC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    IntakeRecord(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        amountMl = cursor.getInt(cursor.getColumnIndexOrThrow("amount_ml")),
                        beverageName = cursor.getString(cursor.getColumnIndexOrThrow("beverage_name")),
                        hydrationScore = cursor.getFloat(cursor.getColumnIndexOrThrow("hydration_score")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("is_synced")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        intakesSharedFlow.tryEmit(list)
    }

    private fun triggerStreakUpdate() {
        val db = helper.readableDatabase
        var streak: Streak? = null
        val cursor = db.rawQuery("SELECT * FROM streaks WHERE id = 1", null)
        if (cursor.moveToFirst()) {
            streak = Streak(
                id = 1,
                currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow("current_streak")),
                longestStreak = cursor.getInt(cursor.getColumnIndexOrThrow("longest_streak")),
                lastDrinkingDate = cursor.getString(cursor.getColumnIndexOrThrow("last_drinking_date"))
            )
        }
        cursor.close()
        streakSharedFlow.tryEmit(streak)
    }

    private fun triggerChallengesUpdate() {
        val db = helper.readableDatabase
        val list = mutableListOf<Challenge>()
        val cursor = db.rawQuery("SELECT * FROM challenges", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Challenge(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        durationDays = cursor.getInt(cursor.getColumnIndexOrThrow("duration_days")),
                        targetDailyMl = cursor.getInt(cursor.getColumnIndexOrThrow("target_daily_ml")),
                        progressDays = cursor.getInt(cursor.getColumnIndexOrThrow("progress_days")),
                        isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        challengesSharedFlow.tryEmit(list)
    }

    private fun triggerGoalUpdate(date: String) {
        val db = helper.readableDatabase
        var goal: DailyGoal? = null
        val cursor = db.rawQuery("SELECT * FROM daily_goals WHERE date = ?", arrayOf(date))
        if (cursor.moveToFirst()) {
            goal = DailyGoal(
                date = date,
                goalMl = cursor.getInt(cursor.getColumnIndexOrThrow("goal_ml"))
            )
        }
        cursor.close()
        getGoalFlowInternal(date).tryEmit(goal)
    }

    private fun getGoalFlowInternal(date: String): MutableSharedFlow<DailyGoal?> {
        return goalsFlowMap.computeIfAbsent(date) {
            val flow = MutableSharedFlow<DailyGoal?>(replay = 1)
            scope.launch {
                triggerGoalUpdate(date)
            }
            flow
        }
    }

    // --- Intake records overrides ---
    override suspend fun insertIntake(record: IntakeRecord) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("timestamp", record.timestamp)
            put("amount_ml", record.amountMl)
            put("beverage_name", record.beverageName)
            put("hydration_score", record.hydrationScore)
            put("is_synced", if (record.isSynced) 1 else 0)
        }
        if (record.id > 0) {
            db.update("intake_records", values, "id = ?", arrayOf(record.id.toString()))
        } else {
            db.insert("intake_records", null, values)
        }
        triggerIntakesUpdate()
    }

    override fun getAllIntakes(): Flow<List<IntakeRecord>> = intakesSharedFlow

    override fun getIntakesBetweenFlow(start: Long, end: Long): Flow<List<IntakeRecord>> {
        // Return filtered flow of intakes cache
        val filteredFlow = MutableSharedFlow<List<IntakeRecord>>(replay = 1)
        scope.launch {
            val list = getIntakesBetween(start, end)
            filteredFlow.emit(list)
        }
        // Also subscribe to changes to update
        scope.launch {
            intakesSharedFlow.collect {
                filteredFlow.emit(getIntakesBetween(start, end))
            }
        }
        return filteredFlow
    }

    override suspend fun getIntakesBetween(start: Long, end: Long): List<IntakeRecord> {
        val db = helper.readableDatabase
        val list = mutableListOf<IntakeRecord>()
        val cursor = db.rawQuery(
            "SELECT * FROM intake_records WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC",
            arrayOf(start.toString(), end.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    IntakeRecord(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        amountMl = cursor.getInt(cursor.getColumnIndexOrThrow("amount_ml")),
                        beverageName = cursor.getString(cursor.getColumnIndexOrThrow("beverage_name")),
                        hydrationScore = cursor.getFloat(cursor.getColumnIndexOrThrow("hydration_score")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("is_synced")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    override suspend fun deleteIntake(record: IntakeRecord) {
        val db = helper.writableDatabase
        db.delete("intake_records", "id = ?", arrayOf(record.id.toString()))
        triggerIntakesUpdate()
    }

    override suspend fun clearIntakes() {
        val db = helper.writableDatabase
        db.delete("intake_records", null, null)
        triggerIntakesUpdate()
    }

    // --- Goals overrides ---
    override suspend fun insertDailyGoal(goal: DailyGoal) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("date", goal.date)
            put("goal_ml", goal.goalMl)
        }
        db.insertWithOnConflict("daily_goals", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        triggerGoalUpdate(goal.date)
    }

    override fun getDailyGoalFlow(date: String): Flow<DailyGoal?> = getGoalFlowInternal(date)

    override suspend fun getDailyGoal(date: String): DailyGoal? {
        val db = helper.readableDatabase
        var goal: DailyGoal? = null
        val cursor = db.rawQuery("SELECT * FROM daily_goals WHERE date = ?", arrayOf(date))
        if (cursor.moveToFirst()) {
            goal = DailyGoal(
                date = date,
                goalMl = cursor.getInt(cursor.getColumnIndexOrThrow("goal_ml"))
            )
        }
        cursor.close()
        return goal
    }

    // --- Streak overrides ---
    override suspend fun insertStreak(streak: Streak) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("id", 1)
            put("current_streak", streak.currentStreak)
            put("longest_streak", streak.longestStreak)
            put("last_drinking_date", streak.lastDrinkingDate)
        }
        db.insertWithOnConflict("streaks", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        triggerStreakUpdate()
    }

    override fun getStreakFlow(): Flow<Streak?> = streakSharedFlow

    override suspend fun getStreak(): Streak? {
        val db = helper.readableDatabase
        var streak: Streak? = null
        val cursor = db.rawQuery("SELECT * FROM streaks WHERE id = 1", null)
        if (cursor.moveToFirst()) {
            streak = Streak(
                id = 1,
                currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow("current_streak")),
                longestStreak = cursor.getInt(cursor.getColumnIndexOrThrow("longest_streak")),
                lastDrinkingDate = cursor.getString(cursor.getColumnIndexOrThrow("last_drinking_date"))
            )
        }
        cursor.close()
        return streak
    }

    // --- Challenges overrides ---
    override suspend fun insertChallenge(challenge: Challenge) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("id", challenge.id)
            put("name", challenge.name)
            put("description", challenge.description)
            put("duration_days", challenge.durationDays)
            put("target_daily_ml", challenge.targetDailyMl)
            put("progress_days", challenge.progressDays)
            put("is_completed", if (challenge.isCompleted) 1 else 0)
        }
        db.insertWithOnConflict("challenges", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        triggerChallengesUpdate()
    }

    override suspend fun insertChallenges(challenges: List<Challenge>) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            challenges.forEach { challenge ->
                val values = ContentValues().apply {
                    put("id", challenge.id)
                    put("name", challenge.name)
                    put("description", challenge.description)
                    put("duration_days", challenge.durationDays)
                    put("target_daily_ml", challenge.targetDailyMl)
                    put("progress_days", challenge.progressDays)
                    put("is_completed", if (challenge.isCompleted) 1 else 0)
                }
                db.insertWithOnConflict("challenges", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        triggerChallengesUpdate()
    }

    override fun getAllChallenges(): Flow<List<Challenge>> = challengesSharedFlow

    override suspend fun getChallenge(id: String): Challenge? {
        val db = helper.readableDatabase
        var challenge: Challenge? = null
        val cursor = db.rawQuery("SELECT * FROM challenges WHERE id = ?", arrayOf(id))
        if (cursor.moveToFirst()) {
            challenge = Challenge(
                id = id,
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                durationDays = cursor.getInt(cursor.getColumnIndexOrThrow("duration_days")),
                targetDailyMl = cursor.getInt(cursor.getColumnIndexOrThrow("target_daily_ml")),
                progressDays = cursor.getInt(cursor.getColumnIndexOrThrow("progress_days")),
                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1
            )
        }
        cursor.close()
        return challenge
    }
}
