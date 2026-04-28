package kz.hashiroii.core.domain.model

data class HabitCompletion(
    val id: Long = 0L,
    val habitId: Long,
    val completedAt: Long,
)