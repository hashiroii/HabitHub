package kz.hashiroii.feature.home

import kz.hashiroii.core.domain.model.HabitWithStreak

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class  Success(
        val habits: List<HabitWithStreak>,
        val completedToday: Int,
        val totalHabits: Int,
        val overallStreakDays: Int,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}