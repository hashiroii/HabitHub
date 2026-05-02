package kz.hashiroii.feature.habit.edit

import kz.hashiroii.core.domain.model.HabitWithStreak

sealed interface HabitEditUiState {
    data object Loading : HabitEditUiState
    data class Success(
        val habitWithStreak: HabitWithStreak,
        val completionsByDay: Map<Long, Int>,
        val selectedDayEpochDay: Long? = null,
        val selectedDayCompletionCount: Int = 0,
        val hasChanges: Boolean = false,
        val isDeleteConfirmVisible: Boolean = false,
        val isDiscardWarningVisible: Boolean = false,
    ) : HabitEditUiState
    data class Error(val message: String) : HabitEditUiState
}