package com.example.hydraflowai.data.local.entity

data class IntakeRecord(
    val id: Long = 0,
    val timestamp: Long,
    val amountMl: Int,
    val beverageName: String,
    val hydrationScore: Float,
    val isSynced: Boolean = false
)
