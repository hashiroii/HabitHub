package kz.hashiroii.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HabitHubProgressIndicator(
    progress: Float,
    habitColor: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp,
) {
    val clamped = progress.coerceIn(0f, 1f)

    Canvas(modifier = modifier.height(height)) {
        val radius = CornerRadius(size.height / 2f)

        drawRoundRect(color = trackColor, size = size, cornerRadius = radius)

        if (clamped > 0f) {
            val fillWidth = (size.width * clamped).coerceAtLeast(size.height)
            drawRoundRect(
                color = habitColor,
                size = Size(fillWidth, size.height),
                cornerRadius = radius,
            )
        }
    }
}