package com.example.hydraflowai.data.local.dao

import com.example.hydraflowai.data.local.entity.Challenge
import com.example.hydraflowai.data.local.entity.DailyGoal
import com.example.hydraflowai.data.local.entity.IntakeRecord
import com.example.hydraflowai.data.local.entity.Streak
import kotlinx.coroutines.flow.Flow

interface HydraDao {
    suspend fun insertIntake(record: IntakeRecord)
    fun getAllIntakes(): Flow<List<IntakeRecord>>
    fun getIntakesBetweenFlow(start: Long, end: Long): Flow<List<IntakeRecord>>
    suspend fun getIntakesBetween(start: Long, end: Long): List<IntakeRecord>
    suspend fun deleteIntake(record: IntakeRecord)
    suspend fun clearIntakes()
    suspend fun insertDailyGoal(goal: DailyGoal)
    fun getDailyGoalFlow(date: String): Flow<DailyGoal?>
    suspend fun getDailyGoal(date: String): DailyGoal?
    suspend fun insertStreak(streak: Streak)
    fun getStreakFlow(): Flow<Streak?>
    suspend fun getStreak(): Streak?
    suspend fun insertChallenge(challenge: Challenge)
    suspend fun insertChallenges(challenges: List<Challenge>)
    fun getAllChallenges(): Flow<List<Challenge>>
    suspend fun getChallenge(id: String): Challenge?
}
