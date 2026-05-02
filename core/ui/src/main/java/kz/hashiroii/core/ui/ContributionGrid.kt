package kz.hashiroii.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
    val cornerRadius = HabitHubTheme.shapes.gridCell
    val weeks = remember(historyData, startOffset) {
        val padded: List<DayProgress?> = List(startOffset) { null } + historyData
        padded.chunked(7)
    }
    val listState = rememberLazyListState()

    LaunchedEffect(weeks.size, scrollToColumn) {
        if (weeks.isEmpty()) return@LaunchedEffect
        val target = scrollToColumn?.coerceIn(0, weeks.size - 1) ?: (weeks.size - 1)
        listState.scrollToItem(target)
        val info = snapshotFlow { listState.layoutInfo }
            .first { it.visibleItemsInfo.isNotEmpty() && it.viewportSize.width > 0 }
        val viewportWidth = info.viewportSize.width
        val itemSize = info.visibleItemsInfo.firstOrNull { it.index == target }?.size
        if (viewportWidth > 0 && itemSize != null) {
            listState.scrollBy(-(viewportWidth / 2f - itemSize / 2f).coerceAtLeast(0f))
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
        userScrollEnabled = true,
    ) {
        items(weeks) { weekDays ->
            Column(verticalArrangement = Arrangement.spacedBy(cellSpacing)) {
                weekDays.forEach { day ->
                    if (day == null) {
                        Spacer(modifier = Modifier.size(cellSize))
                    } else {
                        val saturation = if (day.goalCount <= 0) 0f
                        else (day.completionCount.toFloat() / day.goalCount).coerceIn(0f, 1f)

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .clip(RoundedCornerShape(cornerRadius))
                                .background(lerp(emptyColor, habitColor, saturation)),
                        )
                    }
                }
                repeat(7 - weekDays.size) {
                    Spacer(modifier = Modifier.size(cellSize))
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