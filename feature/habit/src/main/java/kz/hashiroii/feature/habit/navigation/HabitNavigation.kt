package kz.hashiroii.feature.habit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kz.hashiroii.feature.habit.add.HabitAddScreen
import kz.hashiroii.feature.habit.edit.HabitEditScreen

@Serializable
object HabitAddRoute

@Serializable
data class HabitEditRoute(val habitId: Long)

@Serializable
data class RemindersRoute(val habitId: Long, val habitName: String)

fun NavGraphBuilder.habitAddScreen(
    onClose: () -> Unit,
) {
    composable<HabitAddRoute> {
        HabitAddScreen(onClose = onClose)
    }
}

fun NavGraphBuilder.habitEditScreen(
    onClose: () -> Unit,
    onNavigateToReminders: (habitId: Long, habitName: String) -> Unit,
) {
    composable<HabitEditRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = "habithub://feature_habit/edit/{habitId}" },
        ),
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<HabitEditRoute>()
        HabitEditScreen(
            habitId = route.habitId,
            onClose = onClose,
            onNavigateToReminders = onNavigateToReminders,
        )
    }
}