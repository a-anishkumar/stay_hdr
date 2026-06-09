package com.example.hydraflowai.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydraflowai.data.local.entity.IntakeRecord
import com.example.hydraflowai.data.model.Beverage
import com.example.hydraflowai.data.repository.WaterRepository
import com.example.hydraflowai.data.weather.ActivityLevel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalHydratedMl: Int = 0,
    val dailyGoalMl: Int = 2000,
    val remainingMl: Int = 2000,
    val completionPercent: Float = 0f,
    val todayLogs: List<IntakeRecord> = emptyList(),
    val weightKg: Float = 70.0f,
    val weatherTempC: Float = 28.5f,
    val weatherSeason: String = "Summer",
    val weatherCondition: String = "Sunny"
)

class DashboardViewModel(
    val repository: WaterRepository
) : ViewModel() {

    private val _weight = MutableStateFlow(repository.getUserWeight())
    val weight = _weight.asStateFlow()

    private val _presets = MutableStateFlow(repository.getCustomPresets())
    val presets = _presets.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.isGoogleLoggedIn())
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow(repository.getGoogleUserName())
    val userName = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(repository.getGoogleUserEmail())
    val userEmail = _userEmail.asStateFlow()

    val googleAccounts = repository.getGoogleAccounts()

    fun signIn(email: String) {
        repository.signInWithGoogle(email)
        _isLoggedIn.value = true
        _userName.value = repository.getGoogleUserName()
        _userEmail.value = repository.getGoogleUserEmail()
    }

    fun signOut() {
        repository.signOutFromGoogle()
        _isLoggedIn.value = false
        _userName.value = ""
        _userEmail.value = ""
    }

    private val _remindersEnabled = MutableStateFlow(repository.isRemindersEnabled())
    val remindersEnabled = _remindersEnabled.asStateFlow()

    private val _reminderInterval = MutableStateFlow(repository.getReminderIntervalHours())
    val reminderInterval = _reminderInterval.asStateFlow()

    fun addPreset(name: String, ml: Int) {
        repository.addCustomPreset(name, ml)
        _presets.value = repository.getCustomPresets()
    }

    fun deletePreset(presetStr: String) {
        repository.deleteCustomPreset(presetStr)
        _presets.value = repository.getCustomPresets()
    }

    fun setRemindersEnabled(enabled: Boolean) {
        repository.setRemindersEnabled(enabled)
        _remindersEnabled.value = enabled
    }

    fun setReminderInterval(hours: Float) {
        repository.setReminderIntervalHours(hours)
        _reminderInterval.value = hours
    }

    init {
        // Ensure today's goal is computed and cached in the DB
        viewModelScope.launch {
            repository.getOrCreateDailyGoal()
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getTodayIntakes(),
        repository.getDailyGoalFlow(),
        repository.getLocalWeather()
    ) { intakes, dailyGoal, weather ->
        val totalHydrated = intakes.sumOf { (it.amountMl * it.hydrationScore).toInt() }
        val goal = dailyGoal?.goalMl ?: repository.getOrCreateDailyGoal()
        val remaining = (goal - totalHydrated).coerceAtLeast(0)
        val completion = if (goal > 0) (totalHydrated.toFloat() / goal.toFloat()).coerceAtMost(1.0f) else 0f
        
        DashboardUiState(
            totalHydratedMl = totalHydrated,
            dailyGoalMl = goal,
            remainingMl = remaining,
            completionPercent = completion,
            todayLogs = intakes,
            weightKg = repository.getUserWeight(),
            weatherTempC = weather.temperatureC,
            weatherSeason = weather.season,
            weatherCondition = weather.condition
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun addWater(amountMl: Int, beverage: Beverage) {
        viewModelScope.launch {
            repository.addIntake(amountMl, beverage)
        }
    }

    fun deleteIntake(record: IntakeRecord) {
        viewModelScope.launch {
            repository.removeIntake(record)
        }
    }

    fun updateGoal(goalMl: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(goalMl)
        }
    }

    fun updateWeight(weight: Float) {
        repository.setUserWeight(weight)
        _weight.value = weight
        // Recalculate today's goal if weight changes
        viewModelScope.launch {
            val rec = repository.getOrCreateDailyGoal()
            repository.updateDailyGoal(rec)
        }
    }

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        repository.setUserActivityLevel(activityLevel)
        viewModelScope.launch {
            val rec = repository.getOrCreateDailyGoal()
            repository.updateDailyGoal(rec)
        }
    }
}




