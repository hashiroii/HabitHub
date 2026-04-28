package kz.hashiroii.core.domain.model

data class TodayCompletionSummary(
    val completedHabits: Int,
    val totalHabits: Int,
    val overallStreakDays: Int,
)