package com.example.hydraflowai.data.coach

import com.example.hydraflowai.data.local.entity.IntakeRecord
import java.util.Calendar

data class HydrationInsights(
    val dehydrationRisk: String, // "Low", "Medium", "High"
    val riskDescription: String,
    val recommendedSchedule: String,
    val tips: List<String>
)

class RecommendationEngine {

    fun generateInsights(
        intakes: List<IntakeRecord>,
        dailyGoalMl: Int,
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): HydrationInsights {
        val totalHydrated = intakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }
        val completionPercentage = if (dailyGoalMl > 0) (totalHydrated.toFloat() / dailyGoalMl * 100f) else 0f

        // 1. Calculate dehydration risk based on current time & completion percentage
        val risk = when {
            completionPercentage >= 90f -> "Low"
            currentHour < 12 -> {
                if (completionPercentage < 15f) "Medium" else "Low"
            }
            currentHour in 12..18 -> {
                if (completionPercentage < 35f) "High" else if (completionPercentage < 60f) "Medium" else "Low"
            }
            else -> { // Night time
                if (completionPercentage < 60f) "High" else if (completionPercentage < 80f) "Medium" else "Low"
            }
        }

        val riskDescription = when (risk) {
            "High" -> "Alert: Your hydration is severely lagging for this time of day. Dehydration risk is high. Please consume water immediately."
            "Medium" -> "Notice: You are slightly behind your ideal schedule. Consider drinking a glass of water soon."
            else -> "Optimal: Excellent job! Your hydration levels are in the safe zone."
        }

        // 2. Predict next optimal drinking time
        val nextSchedule = if (completionPercentage >= 100f) {
            "Goal Achieved! Drink small sips of water as needed to stay comfortable."
        } else {
            val lastIntake = intakes.maxByOrNull { it.timestamp }
            if (lastIntake == null) {
                "Start your day with a quick 300ml glass of fresh water now."
            } else {
                val timeSinceLastMs = System.currentTimeMillis() - lastIntake.timestamp
                val minutesSince = (timeSinceLastMs / (1000 * 60)).toInt()
                if (minutesSince >= 120) {
                    "It has been over 2 hours since your last drink. Consume 250ml of water now."
                } else {
                    val remainingMinutes = 120 - minutesSince
                    "Maintain your pace! Drink 250ml of water in $remainingMinutes minutes."
                }
            }
        }

        // 3. Compile context-based smart tips
        val tips = mutableListOf<String>()
        val caffeineIntakes = intakes.filter { it.beverageName.lowercase().contains("coffee") || it.beverageName.lowercase().contains("tea") }
        if (caffeineIntakes.isNotEmpty()) {
            tips.add("Caffeine has mild diuretic effects. Try drinking an extra 250ml glass of water to offset your tea/coffee consumption.")
        }
        
        val sodas = intakes.filter { it.beverageName.lowercase().contains("soda") || it.beverageName.lowercase().contains("carbonated") }
        if (sodas.isNotEmpty()) {
            tips.add("Carbonated drinks contain high sugar/artificial sweeteners which can slow down real cellular hydration. Swap your next soda for water or coconut water.")
        }

        tips.add("Start your morning with 300ml of water right after waking up to activate your metabolism.")
        tips.add("Drink water in small, consistent sips throughout the day rather than chugging large amounts at once for optimal absorption.")
        tips.add("Did you know? Mild dehydration can mimic hunger signals. Try drinking water when you feel a sudden craving.")

        return HydrationInsights(
            dehydrationRisk = risk,
            riskDescription = riskDescription,
            recommendedSchedule = nextSchedule,
            tips = tips.shuffled().take(3)
        )
    }
}
