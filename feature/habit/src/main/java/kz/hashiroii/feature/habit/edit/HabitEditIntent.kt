package kz.hashiroii.feature.habit.edit

sealed interface HabitEditIntent {
    data class SelectDay(val epochDay: Long) : HabitEditIntent
    data class AddCompletionForDay(val epochDay: Long) : HabitEditIntent
    data class RemoveCompletionForDay(val epochDay: Long) : HabitEditIntent
    data object DismissDayDialog : HabitEditIntent
    data object NavigateToReminders : HabitEditIntent
    // Confirm button at bottom – closes the screen
    data object Confirm : HabitEditIntent
    // Back gesture / system back – warns if there are unsaved changes
    data object RequestClose : HabitEditIntent
    // Delete flow
    data object DeleteHabit : HabitEditIntent
    data object ConfirmDelete : HabitEditIntent
    data object DismissDelete : HabitEditIntent
    // Discard-warning dialog actions
    data object SaveFromDiscardWarning : HabitEditIntent
    data object DiscardChanges : HabitEditIntent
}