package com.example.hydraflowai.data.local.entity

data class Challenge(
    val id: String,
    val name: String,
    val description: String,
    val durationDays: Int,
    val targetDailyMl: Int,
    val progressDays: Int = 0,
    val isCompleted: Boolean = false
)
