package kz.hashiroii.feature.habit.add

sealed interface HabitAddIntent {
    data class NameChanged(val value: String) : HabitAddIntent
    data class DescriptionChanged(val value: String) : HabitAddIntent
    data class IconSelected(val iconName: String) : HabitAddIntent
    data class ColorSelected(val colorHex: String) : HabitAddIntent
    data object SaveClicked : HabitAddIntent
    data object OpenRepetitionDialog : HabitAddIntent
    data class RepetitionCountChanged(val count: Int) : HabitAddIntent
    data object ConfirmRepetition : HabitAddIntent
    data object DismissRepetitionDialog : HabitAddIntent
}