package kz.hashiroii.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kz.hashiroii.feature.home.HomeScreen

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onAddHabit: () -> Unit = {},
    onHabitClick: (habitId: Long) -> Unit = {},
) {
    composable<HomeRoute> {
        HomeScreen(onAddHabit = onAddHabit, onHabitClick = onHabitClick)
    }
}