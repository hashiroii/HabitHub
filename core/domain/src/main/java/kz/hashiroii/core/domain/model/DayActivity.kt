package kz.hashiroii.core.domain.model

data class DayActivity(
    val dateEpochDay: Long,
    val completionCount: Int,
    val goalCount: Int,
)