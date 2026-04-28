package kz.hashiroii.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalHabitHubSpacing = staticCompositionLocalOf { HabitHubSpacing() }
val LocalHabitHubShapes = staticCompositionLocalOf { HabitHubShapes() }
val LocalHabitHubTypography = staticCompositionLocalOf { HabitHubTypography() }

object HabitHubTheme {
    val spacing: HabitHubSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalHabitHubSpacing.current

    val shapes: HabitHubShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalHabitHubShapes.current

    val typography: HabitHubTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalHabitHubTypography.current
}