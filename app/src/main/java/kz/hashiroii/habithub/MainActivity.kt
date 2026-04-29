package kz.hashiroii.habithub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.feature.habit.navigation.HabitAddRoute
import kz.hashiroii.feature.habit.navigation.habitAddScreen
import kz.hashiroii.feature.habit.navigation.habitEditScreen
import kz.hashiroii.feature.home.navigation.HomeRoute
import kz.hashiroii.feature.home.navigation.homeScreen
import kz.hashiroii.feature.reminders.navigation.RemindersRoute
import kz.hashiroii.feature.reminders.navigation.remindersScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitHubTheme {
                HabitHubNavHost()
            }
        }
    }
}

@Composable
private fun HabitHubNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        homeScreen(
            onAddHabit = { navController.navigate(HabitAddRoute) },
            onHabitClick = { habitId -> navController.navigate(HabitEditRoute(habitId)) },
        )
        habitAddScreen(
            onClose = { navController.popBackStack() },
        )
        habitEditScreen(
            onClose = { navController.popBackStack() },
            onNavigateToReminders = { habitId, habitName ->
                navController.navigate(RemindersRoute(habitId = habitId, habitName = habitName))
            },
        )
        remindersScreen(
            onClose = { navController.popBackStack() },
        )
    }
}