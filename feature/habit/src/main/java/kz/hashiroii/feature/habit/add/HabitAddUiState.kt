package kz.hashiroii.feature.habit.add

data class HabitAddUiState(
    val name: String = "",
    val description: String = "",
    val selectedIconName: String = "Star",
    val selectedColorHex: String = "#4CAF50",
    val goalCount: Int = 1,
    val isNameError: Boolean = false,
    val isRepetitionDialogVisible: Boolean = false,
)