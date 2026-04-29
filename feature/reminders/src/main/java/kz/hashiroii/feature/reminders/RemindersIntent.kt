package kz.hashiroii.feature.reminders

sealed interface RemindersIntent {
    data object AddReminderClicked : RemindersIntent
    data class TimeSelected(val hour: Int, val minute: Int) : RemindersIntent
    data class DeleteReminder(val reminderId: Int) : RemindersIntent
    data object DismissTimePicker : RemindersIntent
    data object Close : RemindersIntent
}