package kz.hashiroii.feature.habit.edit

import kz.hashiroii.core.domain.model.HabitWithStreak

sealed interface HabitEditUiState {
    data object Loading : HabitEditUiState
    data class Success(
        val habitWithStreak: HabitWithStreak,
        val completionsByDay: Map<Long, Int>,
        val selectedDayEpochDay: Long? = null,
        val selectedDayCompletionCount: Int = 0,
        val isGoalDialogVisible: Boolean = false,
        val pendingGoalCount: Int = 1,
    ) : HabitEditUiState
    data class Error(val message: String) : HabitEditUiState
}