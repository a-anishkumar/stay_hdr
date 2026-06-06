package com.example.hydraflowai.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydraflowai.data.local.entity.Challenge
import com.example.hydraflowai.data.local.entity.Streak
import com.example.hydraflowai.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StreakUiState(
    val streak: Streak = Streak(id = 1, currentStreak = 0, longestStreak = 0, lastDrinkingDate = ""),
    val challenges: List<Challenge> = emptyList(),
    val recoveriesLeft: Int = 1,
    val recoverySuccess: Boolean? = null
)

class StreakViewModel(
    private val repository: WaterRepository
) : ViewModel() {

    private val _recoveryStatus = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<StreakUiState> = combine(
        repository.getStreakFlow(),
        repository.getChallenges(),
        _recoveryStatus
    ) { streak, challenges, recovery ->
        StreakUiState(
            streak = streak ?: Streak(id = 1),
            challenges = challenges,
            recoveriesLeft = repository.getStreakRecoveriesLeft(),
            recoverySuccess = recovery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakUiState()
    )

    fun recoverStreak() {
        viewModelScope.launch {
            val success = repository.recoverStreak()
            _recoveryStatus.value = success
        }
    }

    fun clearRecoveryStatus() {
        _recoveryStatus.value = null
    }
}
