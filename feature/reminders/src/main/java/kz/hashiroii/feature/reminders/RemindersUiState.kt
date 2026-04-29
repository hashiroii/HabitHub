package kz.hashiroii.feature.reminders

data class ReminderTime(
    val id: Int,
    val hour: Int,
    val minute: Int,
) {
    val displayTime: String
        get() {
            val period = if (hour < 12) "AM" else "PM"
            val displayHour = when (hour) {
                0 -> 12
                in 13..23 -> hour - 12
                else -> hour
            }
            return "%d:%02d %s".format(displayHour, minute, period)
        }
}

data class RemindersUiState(
    val habitName: String = "",
    val reminders: List<ReminderTime> = emptyList(),
    val isTimePickerVisible: Boolean = false,
)