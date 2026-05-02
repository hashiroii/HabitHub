package kz.hashiroii.habithub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.core.domain.model.ThemePreference
import kz.hashiroii.feature.habit.navigation.HabitAddRoute
import kz.hashiroii.feature.habit.navigation.HabitEditRoute
import kz.hashiroii.feature.habit.navigation.habitAddScreen
import kz.hashiroii.feature.habit.navigation.habitEditScreen
import kz.hashiroii.feature.home.navigation.HomeRoute
import kz.hashiroii.feature.home.navigation.SettingsRoute
import kz.hashiroii.feature.home.navigation.homeScreen
import kz.hashiroii.feature.home.navigation.settingsScreen
import kz.hashiroii.feature.reminders.navigation.RemindersRoute
import kz.hashiroii.feature.reminders.navigation.remindersScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreference by viewModel.themePreference.collectAsStateWithLifecycle()
            val darkTheme = when (themePreference) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }
            HabitHubTheme(darkTheme = darkTheme) {
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
            onSettingsClick = { navController.navigate(SettingsRoute) },
        )
        settingsScreen(onClose = { navController.popBackStack() })
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