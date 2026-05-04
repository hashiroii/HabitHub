package kz.hashiroii.habithub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        val deepLinkHabitId = intent?.data?.let { uri ->
            if (uri.scheme == "habithub" && uri.host == "habit") {
                uri.lastPathSegment?.toLongOrNull()
            } else null
        }
        setContent {
            val themePreference by viewModel.themePreference.collectAsStateWithLifecycle()
            val darkTheme = when (themePreference) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }
            HabitHubTheme(darkTheme = darkTheme) {
                HabitHubNavHost(deepLinkHabitId = deepLinkHabitId)
            }
        }
    }
}

@Composable
private fun HabitHubNavHost(deepLinkHabitId: Long? = null) {
    val navController = rememberNavController()
    var deepLinkHandled by remember { mutableStateOf(false) }

    LaunchedEffect(deepLinkHabitId) {
        if (deepLinkHabitId != null && !deepLinkHandled) {
            deepLinkHandled = true
            navController.navigate(HabitEditRoute(deepLinkHabitId))
        }
    }

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