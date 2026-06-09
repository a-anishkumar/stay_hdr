package com.example.hydraflowai

import android.app.Application
import com.example.hydraflowai.data.coach.RecommendationEngine
import com.example.hydraflowai.data.local.HydraDatabase
import com.example.hydraflowai.data.repository.WaterRepository
import com.example.hydraflowai.data.weather.MockWeatherService
import com.example.hydraflowai.data.weather.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HydraFlowApplication : Application() {
    
    lateinit var database: HydraDatabase
    lateinit var weatherService: WeatherService
    lateinit var recommendationEngine: RecommendationEngine
    lateinit var repository: WaterRepository

    override fun onCreate() {
        super.onCreate()
        
        val scope = CoroutineScope(SupervisorJob())
        database = HydraDatabase.getDatabase(this, scope)
        weatherService = MockWeatherService()
        recommendationEngine = RecommendationEngine()
        repository = WaterRepository(this, database.hydraDao(), weatherService)
    }
}
