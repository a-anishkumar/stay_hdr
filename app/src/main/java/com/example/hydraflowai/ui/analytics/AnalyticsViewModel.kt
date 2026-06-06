package com.example.hydraflowai.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydraflowai.data.local.entity.IntakeRecord
import com.example.hydraflowai.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class AnalyticsUiState(
    val dailyAverageMl: Int = 0,
    val hydrationScore: Int = 0, // 0 to 100
    val consistencyPercent: Int = 0,
    val weeklyChartData: List<Pair<String, Int>> = emptyList(), // DayOfWeek to AmountMl
    val monthlyHeatmapData: Map<String, Boolean> = emptyMap(), // DateString to GoalReached
    val beverageDistribution: Map<String, Int> = emptyMap() // BeverageName to Count
)

class AnalyticsViewModel(
    private val repository: WaterRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = repository.getAllIntakes().map { intakes ->
        if (intakes.isEmpty()) {
            return@map AnalyticsUiState()
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val groupedByDate = intakes.groupBy { format.format(Date(it.timestamp)) }
        
        val dailyTotals = groupedByDate.mapValues { (_, records) ->
            records.sumOf { (it.amountMl * it.hydrationScore).toInt() }
        }

        val totalDays = dailyTotals.size
        val avgIntake = if (totalDays > 0) dailyTotals.values.sum() / totalDays else 0

        var reachedDaysCount = 0
        val heatmap = mutableMapOf<String, Boolean>()
        
        dailyTotals.forEach { (dateStr, amount) ->
            val goal = 2000
            val reached = amount >= goal
            if (reached) {
                reachedDaysCount++
            }
            heatmap[dateStr] = reached
        }

        val consistency = if (totalDays > 0) (reachedDaysCount * 100) / totalDays else 0
        val score = ((avgIntake.coerceAtMost(2500) / 2500f * 60f) + (consistency * 0.4f)).toInt().coerceIn(0, 100)

        val weeklyData = mutableListOf<Pair<String, Int>>()
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        
        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -i)
            val dateKey = format.format(cal.time)
            val dayName = dayFormat.format(cal.time)
            weeklyData.add(Pair(dayName, dailyTotals[dateKey] ?: 0))
        }

        val beverages = intakes.groupBy { it.beverageName }.mapValues { it.value.size }

        AnalyticsUiState(
            dailyAverageMl = avgIntake,
            hydrationScore = score,
            consistencyPercent = consistency,
            weeklyChartData = weeklyData,
            monthlyHeatmapData = heatmap,
            beverageDistribution = beverages
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )
}
