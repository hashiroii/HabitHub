package kz.hashiroii.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kz.hashiroii.feature.settings.SettingsScreen

@Serializable
object SettingsRoute

fun NavGraphBuilder.settingsScreen(onClose: () -> Unit) {
    composable<SettingsRoute> {
        SettingsScreen(onClose = onClose)
    }
}