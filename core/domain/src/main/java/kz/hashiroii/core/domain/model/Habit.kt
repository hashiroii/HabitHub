package kz.hashiroii.core.domain.model

data class Habit(
    val id: Long = 0L,
    val name: String,
    val colorHex: String,
    val goalCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
)