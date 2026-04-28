package kz.hashiroii.feature.home

import androidx.compose.ui.graphics.Color

data class HabitItem(
    val id: String,
    val name: String,
    val color: Color,
)

data class HomeUiState(
    val completedCount: Int = 4,
    val totalCount: Int = 9,
    val streakDays: Int = 7,
    val habits: List<HabitItem> = sampleHabits,
)

private val sampleHabits = listOf(
    HabitItem("1", "Morning Run", Color(0xFF4CAF50)),
    HabitItem("2", "Read 30 min", Color(0xFF2196F3)),
    HabitItem("3", "Meditate", Color(0xFF9C27B0)),
    HabitItem("4", "Drink Water", Color(0xFF00BCD4)),
    HabitItem("5", "No Sugar", Color(0xFFFF9800)),
    HabitItem("6", "Journal", Color(0xFFE91E63)),
    HabitItem("7", "Walk 10K Steps", Color(0xFF795548)),
    HabitItem("8", "Stretch", Color(0xFF607D8B)),
    HabitItem("9", "Early Sleep", Color(0xFF3F51B5)),
)