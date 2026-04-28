package kz.hashiroii.core.domain.model

data class HabitWithStreak(
    val habit: Habit,
    val streakDays: Int,
    val todayCompletionCount: Int,
    val activityGrid: List<DayActivity>,
)