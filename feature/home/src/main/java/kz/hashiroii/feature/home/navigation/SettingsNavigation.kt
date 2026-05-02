package kz.hashiroii.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kz.hashiroii.feature.home.SettingsScreen

@Serializable
object SettingsRoute

fun NavGraphBuilder.settingsScreen(onClose: () -> Unit) {
    composable<SettingsRoute> {
        SettingsScreen(onClose = onClose)
    }
}