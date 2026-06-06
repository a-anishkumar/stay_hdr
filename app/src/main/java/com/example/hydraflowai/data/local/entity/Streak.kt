package com.example.hydraflowai.data.local.entity

data class Streak(
    val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastDrinkingDate: String = ""
)
