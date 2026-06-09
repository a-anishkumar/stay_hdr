package com.example.hydraflowai.data.weather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class WeatherInfo(
    val temperatureC: Float,
    val humidityPercent: Int,
    val condition: String,
    val season: String
)

interface WeatherService {
    fun getLocalWeather(): Flow<WeatherInfo>
    fun calculateRecommendedWater(
        weightKg: Float,
        heightCm: Float,
        ageYears: Int,
        activityLevel: ActivityLevel,
        weather: WeatherInfo
    ): Int
}

enum class ActivityLevel(val displayName: String, val adjustmentMl: Int) {
    SEDENTARY("Sedentary (Low)", 0),
    ACTIVE("Active (Moderate)", 400),
    INTENSE("Athletic (High)", 800)
}

class MockWeatherService : WeatherService {
    override fun getLocalWeather(): Flow<WeatherInfo> = flow {
        // Return a mock weather state that resembles a pleasant summer day
        emit(
            WeatherInfo(
                temperatureC = 28.5f,
                humidityPercent = 65,
                condition = "Sunny",
                season = "Summer"
            )
        )
    }

    override fun calculateRecommendedWater(
        weightKg: Float,
        heightCm: Float,
        ageYears: Int,
        activityLevel: ActivityLevel,
        weather: WeatherInfo
    ): Int {
        val baseWater = weightKg * 35f // Weight * 35ml
        val heightAdjustment = (heightCm - 170f) * 10f // Taller individuals need slightly more water
        val ageAdjustment = when {
            ageYears < 18 -> 150f // Growth adjustment
            ageYears > 60 -> -100f // Lower muscle mass baseline water volume
            else -> 0f
        }
        
        // Temperature adjustment: add 30ml for each degree above 22C, up to 1000ml
        val tempAdjustment = if (weather.temperatureC > 22f) {
            ((weather.temperatureC - 22f) * 30f).toInt().coerceAtMost(1000)
        } else {
            0
        }

        // Humidity adjustment: high humidity prevents sweat evaporation, increasing core temp, add extra water
        val humidityAdjustment = if (weather.humidityPercent > 70) 250 else 0

        // Season adjustment
        val seasonAdjustment = when (weather.season.lowercase()) {
            "summer" -> 300
            "spring" -> 100
            "autumn" -> 0
            "winter" -> -200
            else -> 0
        }

        val activityAdjustment = activityLevel.adjustmentMl

        return (baseWater + heightAdjustment + ageAdjustment + tempAdjustment + humidityAdjustment + seasonAdjustment + activityAdjustment).toInt().coerceAtLeast(1500)
    }
}
