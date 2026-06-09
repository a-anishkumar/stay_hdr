package com.example.hydraflowai.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.hydraflowai.data.local.dao.HydraDao
import com.example.hydraflowai.data.local.entity.Challenge
import com.example.hydraflowai.data.local.entity.DailyGoal
import com.example.hydraflowai.data.local.entity.IntakeRecord
import com.example.hydraflowai.data.local.entity.Streak
import com.example.hydraflowai.data.model.Beverage
import com.example.hydraflowai.data.weather.ActivityLevel
import com.example.hydraflowai.data.weather.WeatherInfo
import com.example.hydraflowai.data.weather.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WaterRepository(
    private val context: Context,
    private val dao: HydraDao,
    private val weatherService: WeatherService
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hydra_prefs", Context.MODE_PRIVATE)

    // User profile state
    fun getUserWeight(): Float = prefs.getFloat("user_weight", 70.0f)
    fun getLocalWeather() = weatherService.getLocalWeather()
    fun setUserWeight(weight: Float) = prefs.edit().putFloat("user_weight", weight).apply()

    fun getUserHeight(): Float = prefs.getFloat("user_height", 170.0f)
    fun setUserHeight(height: Float) = prefs.edit().putFloat("user_height", height).apply()

    fun getUserAge(): Int = prefs.getInt("user_age", 25)
    fun setUserAge(age: Int) = prefs.edit().putInt("user_age", age).apply()

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)
    fun setOnboardingCompleted(completed: Boolean) = prefs.edit().putBoolean("onboarding_completed", completed).apply()

    fun getUserActivityLevel(): ActivityLevel {
        val name = prefs.getString("user_activity", ActivityLevel.SEDENTARY.name) ?: ActivityLevel.SEDENTARY.name
        return try { ActivityLevel.valueOf(name) } catch (e: Exception) { ActivityLevel.SEDENTARY }
    }
    fun setUserActivityLevel(activityLevel: ActivityLevel) = prefs.edit().putString("user_activity", activityLevel.name).apply()

    // Custom presets state
    fun getCustomPresets(): List<String> {
        val set = prefs.getStringSet("custom_presets_set", setOf("Mug:350", "Bottle:750")) ?: setOf("Mug:350", "Bottle:750")
        return set.toList()
    }
    fun addCustomPreset(name: String, ml: Int) {
        val current = getCustomPresets().toMutableSet()
        current.add("$name:$ml")
        prefs.edit().putStringSet("custom_presets_set", current).apply()
    }
    fun deleteCustomPreset(presetStr: String) {
        val current = getCustomPresets().toMutableSet()
        current.remove(presetStr)
        prefs.edit().putStringSet("custom_presets_set", current).apply()
    }

    // Reminders Configuration
    fun isRemindersEnabled(): Boolean = prefs.getBoolean("reminders_enabled", true)
    fun setRemindersEnabled(enabled: Boolean) = prefs.edit().putBoolean("reminders_enabled", enabled).apply()
    
    fun getReminderIntervalHours(): Float = prefs.getFloat("reminder_interval", 2.0f)
    fun setReminderIntervalHours(hours: Float) = prefs.edit().putFloat("reminder_interval", hours).apply()

    // Google Sign-In Configuration
    fun isGoogleLoggedIn(): Boolean = prefs.getBoolean("google_is_logged_in", false)
    fun getGoogleUserName(): String = prefs.getString("google_user_name", "") ?: ""
    fun getGoogleUserEmail(): String = prefs.getString("google_user_email", "") ?: ""

    fun getGoogleAccounts(): List<String> = listOf(
        "anishkumar.a2006@gmail.com",
        "anish.hydraflow@gmail.com",
        "guest.hydraflow@gmail.com"
    )

    fun signInWithGoogle(email: String) {
        val name = when (email) {
            "anishkumar.a2006@gmail.com" -> "Anish Kumar"
            "anish.hydraflow@gmail.com" -> "Anish Hydra"
            else -> "Hydra Guest"
        }
        prefs.edit().putBoolean("google_is_logged_in", true).putString("google_user_name", name).putString("google_user_email", email).apply()
    }

    fun signOutFromGoogle() {
        prefs.edit().putBoolean("google_is_logged_in", false).putString("google_user_name", "").putString("google_user_email", "").apply()
    }

    fun getStreakRecoveriesLeft(): Int = prefs.getInt("recoveries_left", 1)
    fun decrementStreakRecoveries() = prefs.edit().putInt("recoveries_left", (getStreakRecoveriesLeft() - 1).coerceAtLeast(0)).apply()
    fun resetStreakRecoveries() = prefs.edit().putInt("recoveries_left", 1).apply()

    // Date formatting helper
    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Intake Records
    fun getAllIntakes(): Flow<List<IntakeRecord>> = dao.getAllIntakes()

    fun getTodayIntakes(): Flow<List<IntakeRecord>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        
        return dao.getIntakesBetweenFlow(start, end)
    }

    suspend fun getIntakesForDay(dateStr: String): List<IntakeRecord> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr) ?: return emptyList()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis
        
        return dao.getIntakesBetween(start, end)
    }

    suspend fun addIntake(amountMl: Int, beverage: Beverage) = withContext(Dispatchers.IO) {
        val record = IntakeRecord(
            timestamp = System.currentTimeMillis(),
            amountMl = amountMl,
            beverageName = beverage.displayName,
            hydrationScore = beverage.hydrationFactor
        )
        dao.insertIntake(record)
        checkAndUpdateStreak()
    }

    suspend fun removeIntake(record: IntakeRecord) = withContext(Dispatchers.IO) {
        dao.deleteIntake(record)
        checkAndUpdateStreak()
    }

    // Goals logic
    fun getDailyGoalFlow(date: String = getTodayDateString()): Flow<DailyGoal?> = dao.getDailyGoalFlow(date)

    suspend fun getOrCreateDailyGoal(date: String = getTodayDateString()): Int = withContext(Dispatchers.IO) {
        val existing = dao.getDailyGoal(date)
        if (existing != null) {
            return@withContext existing.goalMl
        }
        recalculateAndSaveDailyGoal(date)
    }

    suspend fun recalculateAndSaveDailyGoal(date: String = getTodayDateString()): Int = withContext(Dispatchers.IO) {
        val weather = WeatherInfo(28f, 60, "Sunny", "Summer") // Default fallback
        val recommended = weatherService.calculateRecommendedWater(
            weightKg = getUserWeight(),
            heightCm = getUserHeight(),
            ageYears = getUserAge(),
            activityLevel = getUserActivityLevel(),
            weather = weather
        )
        dao.insertDailyGoal(DailyGoal(date, recommended))
        recommended
    }

    suspend fun updateDailyGoal(amountMl: Int, date: String = getTodayDateString()) = withContext(Dispatchers.IO) {
        dao.insertDailyGoal(DailyGoal(date, amountMl))
    }

    // Streak System
    fun getStreakFlow(): Flow<Streak?> = dao.getStreakFlow()

    suspend fun checkAndUpdateStreak() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        
        val todayIntakes = getIntakesForDay(today)
        val todayGoal = getOrCreateDailyGoal(today)
        val todayHydrated = todayIntakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }

        var streak = dao.getStreak() ?: Streak(id = 1, currentStreak = 0, longestStreak = 0, lastDrinkingDate = "")

        if (todayHydrated >= todayGoal) {
            if (streak.lastDrinkingDate != today) {
                // Goal reached today! Check if yesterday was also reached to continue the streak
                val yesterdayIntakes = getIntakesForDay(yesterday)
                val yesterdayGoal = getOrCreateDailyGoal(yesterday)
                val yesterdayHydrated = yesterdayIntakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }

                val newStreak = if (streak.lastDrinkingDate == yesterday || yesterdayHydrated >= yesterdayGoal || streak.lastDrinkingDate == "") {
                    streak.currentStreak + 1
                } else {
                    1 // streak broke, but we restart today
                }
                
                val longest = if (newStreak > streak.longestStreak) newStreak else streak.longestStreak
                streak = streak.copy(
                    currentStreak = newStreak,
                    longestStreak = longest,
                    lastDrinkingDate = today
                )
                dao.insertStreak(streak)
                updateChallengeProgress()
            }
        } else {
            // Check if streak was broken (i.e. last drinking date was before yesterday)
            if (streak.lastDrinkingDate != today && streak.lastDrinkingDate != yesterday && streak.lastDrinkingDate != "") {
                // Reset streak
                streak = streak.copy(currentStreak = 0)
                dao.insertStreak(streak)
            }
        }
    }

    suspend fun recoverStreak() = withContext(Dispatchers.IO) {
        if (getStreakRecoveriesLeft() > 0) {
            val yesterday = getYesterdayDateString()
            val streak = dao.getStreak() ?: Streak(id = 1)
            
            // Set yesterday's goal as reached to recover the streak
            val yesterdayGoal = getOrCreateDailyGoal(yesterday)
            dao.insertIntake(
                IntakeRecord(
                    timestamp = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.timeInMillis,
                    amountMl = yesterdayGoal,
                    beverageName = "Water",
                    hydrationScore = 1.0f
                )
            )
            decrementStreakRecoveries()
            checkAndUpdateStreak()
            return@withContext true
        }
        return@withContext false
    }

    // Challenges logic
    fun getChallenges(): Flow<List<Challenge>> = dao.getAllChallenges()

    suspend fun updateChallengeProgress() = withContext(Dispatchers.IO) {
        val streak = dao.getStreak()?.currentStreak ?: 0
        val challenges = dao.getAllChallenges().firstOrNull() ?: emptyList()
        challenges.forEach { challenge ->
            if (!challenge.isCompleted) {
                // Check if streak meets duration
                val newProgress = streak.coerceAtMost(challenge.durationDays)
                val completed = newProgress >= challenge.durationDays
                dao.insertChallenge(
                    challenge.copy(
                        progressDays = newProgress,
                        isCompleted = completed
                    )
                )
            }
        }
    }

    // Mocks for Google Sync (Firebase Authentication & Firestore)
    fun syncWithCloud(): Flow<SyncState> = flow {
        emit(SyncState.SYNCING)
        kotlinx.coroutines.delay(1500) // Simulate network delay
        emit(SyncState.SUCCESS)
    }

    // Mock for Health Connect / Google Fit
    fun syncWithHealthConnect(): Flow<SyncState> = flow {
        emit(SyncState.SYNCING)
        kotlinx.coroutines.delay(1200)
        emit(SyncState.SUCCESS)
    }
}

enum class SyncState {
    IDLE, SYNCING, SUCCESS, ERROR
}
