package kz.hashiroii.feature.habit.edit

sealed interface HabitEditIntent {
    data class SelectDay(val epochDay: Long) : HabitEditIntent
    data class AddCompletionForDay(val epochDay: Long) : HabitEditIntent
    data class RemoveCompletionForDay(val epochDay: Long) : HabitEditIntent
    data object DismissDayDialog : HabitEditIntent
    data object OpenGoalDialog : HabitEditIntent
    data class GoalChanged(val count: Int) : HabitEditIntent
    data object SaveGoal : HabitEditIntent
    data object DismissGoalDialog : HabitEditIntent
    data object NavigateToReminders : HabitEditIntent
    data object Close : HabitEditIntent
}