package kz.hashiroii.feature.reminders.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kz.hashiroii.feature.reminders.RemindersScreen

@Serializable
data class RemindersRoute(val habitId: Long, val habitName: String)

fun NavGraphBuilder.remindersScreen(
    onClose: () -> Unit,
) {
    composable<RemindersRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<RemindersRoute>()
        RemindersScreen(
            habitId = route.habitId,
            habitName = route.habitName,
            onClose = onClose,
        )
    }
}