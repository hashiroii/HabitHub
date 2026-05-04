package kz.hashiroii.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

data class DayProgress(
    val completionCount: Int,
    val goalCount: Int,
)

@Composable
fun ContributionGrid(
    historyData: List<DayProgress>,
    habitColor: Color,
    modifier: Modifier = Modifier,
    cellSize: Dp = 12.dp,
    cellSpacing: Dp = 3.dp,
    startOffset: Int = 0,
    scrollToColumn: Int? = null,
) {
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    val cornerRadiusDp = HabitHubTheme.shapes.gridCell

    val weeks = remember(historyData, startOffset) {
        val padded: List<DayProgress?> = List(startOffset) { null } + historyData
        padded.chunked(7)
    }

    val density = LocalDensity.current
    val cellSizePx = with(density) { cellSize.toPx() }
    val cellSpacingPx = with(density) { cellSpacing.toPx() }
    val cellStepPx = cellSizePx + cellSpacingPx
    val cornerRadiusPx = with(density) { cornerRadiusDp.toPx() }

    val gridWidthDp = with(density) {
        if (weeks.isEmpty()) 0.dp else (weeks.size * cellStepPx - cellSpacingPx).toDp()
    }
    val gridHeightDp = with(density) { (7 * cellStepPx - cellSpacingPx).toDp() }

    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier) {
        val viewportWidthPx = constraints.maxWidth.toFloat()

        LaunchedEffect(weeks.size, scrollToColumn) {
            if (weeks.isEmpty()) return@LaunchedEffect
            val target = scrollToColumn?.coerceIn(0, weeks.size - 1) ?: (weeks.size - 1)
            val centerOffset = (viewportWidthPx / 2f - cellSizePx / 2f).coerceAtLeast(0f)
            scrollState.scrollTo((target * cellStepPx - centerOffset).toInt().coerceAtLeast(0))
        }

        Canvas(
            modifier = Modifier
                .height(gridHeightDp)
                .horizontalScroll(scrollState)
                .width(gridWidthDp),
        ) {
            weeks.forEachIndexed { colIndex, week ->
                week.forEachIndexed { rowIndex, day ->
                    if (day == null) return@forEachIndexed
                    val saturation = if (day.goalCount <= 0) 0f
                    else (day.completionCount.toFloat() / day.goalCount).coerceIn(0f, 1f)
                    drawRoundRect(
                        color = lerp(emptyColor, habitColor, saturation),
                        topLeft = Offset(colIndex * cellStepPx, rowIndex * cellStepPx),
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(cornerRadiusPx),
                    )
                }
            }
        }
    }
}

private fun sampleHistory(weeks: Int = 52): List<DayProgress> {
    val pattern = listOf(0, 0, 1, 2, 4, 4, 3)
    return List(weeks * 7) { i ->
        DayProgress(completionCount = pattern[i % pattern.size], goalCount = 4)
    }
}

@Preview(name = "Grid · Light · Blue", showBackground = true)
@Composable
private fun ContributionGridLightBluePreview() {
    HabitHubTheme(darkTheme = false) {
        ContributionGrid(
            historyData = sampleHistory(),
            habitColor = Color(0xFF1565C0),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Grid · Dark · Green", showBackground = true, backgroundColor = 0xFF11140F)
@Composable
private fun ContributionGridDarkGreenPreview() {
    HabitHubTheme(darkTheme = true) {
        ContributionGrid(
            historyData = sampleHistory(),
            habitColor = Color(0xFF2E7D32),
            modifier = Modifier.padding(16.dp),
        )
    }
}