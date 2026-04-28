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
import kz.hashiroii.feature.home.navigation.HomeRoute
import kz.hashiroii.feature.home.navigation.homeScreen

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
        homeScreen()
    }
}