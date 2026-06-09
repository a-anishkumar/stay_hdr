package com.example.hydraflowai.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydraflowai.data.coach.RecommendationEngine
import com.example.hydraflowai.data.coach.HydrationInsights
import com.example.hydraflowai.data.repository.WaterRepository
import com.example.hydraflowai.data.weather.ActivityLevel
import com.example.hydraflowai.data.weather.WeatherInfo
import com.example.hydraflowai.data.weather.WeatherService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CoachUiState(
    val weather: WeatherInfo = WeatherInfo(28.5f, 65, "Sunny", "Summer"),
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val insights: HydrationInsights = HydrationInsights("Low", "Calculating...", "Calculating...", emptyList()),
    val baseRecommendedWater: Int = 2000
)

class CoachViewModel(
    private val repository: WaterRepository,
    private val recommendationEngine: RecommendationEngine,
    private val weatherService: WeatherService
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherInfo(28.5f, 65, "Sunny", "Summer"))
    private val _activityState = MutableStateFlow(repository.getUserActivityLevel())

    val uiState: StateFlow<CoachUiState> = combine(
        _weatherState,
        _activityState,
        repository.getTodayIntakes(),
        repository.getDailyGoalFlow()
    ) { weather, activity, todayIntakes, dailyGoal ->
        val goal = dailyGoal?.goalMl ?: repository.getOrCreateDailyGoal()
        val insights = recommendationEngine.generateInsights(todayIntakes, goal)
        
        CoachUiState(
            weather = weather,
            activityLevel = activity,
            insights = insights,
            baseRecommendedWater = goal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CoachUiState()
    )

    fun updateWeather(temperatureC: Float, humidity: Int, condition: String, season: String) {
        viewModelScope.launch {
            val newWeather = WeatherInfo(temperatureC, humidity, condition, season)
            _weatherState.value = newWeather
            
            val weight = repository.getUserWeight()
            val height = repository.getUserHeight()
            val age = repository.getUserAge()
            val activity = _activityState.value
            val recommended = weatherService.calculateRecommendedWater(weight, height, age, activity, newWeather)
            repository.updateDailyGoal(recommended)
        }
    }

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        viewModelScope.launch {
            repository.setUserActivityLevel(activityLevel)
            _activityState.value = activityLevel
            
            val weight = repository.getUserWeight()
            val height = repository.getUserHeight()
            val age = repository.getUserAge()
            val weather = _weatherState.value
            val recommended = weatherService.calculateRecommendedWater(weight, height, age, activityLevel, weather)
            repository.updateDailyGoal(recommended)
        }
    }
}
