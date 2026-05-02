package kz.hashiroii.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.hashiroii.core.designsystem.components.HabitHubCard
import kz.hashiroii.core.designsystem.components.HabitHubIconButton
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

@Composable
fun HabitActivityCard(
    habitName: String,
    habitColor: Color,
    currentCount: Int,
    goalCount: Int,
    historyData: List<DayProgress>,
    onAddClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
    startOffset: Int = 0,
    scrollToColumn: Int? = null,
    onCardClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
) {
    HabitHubCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onCardClick),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = HabitHubTheme.spacing.large,
                vertical = HabitHubTheme.spacing.medium,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(HabitHubTheme.spacing.medium))

                Text(
                    text = habitName,
                    style = HabitHubTheme.typography.habitTitle,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(HabitHubTheme.spacing.extraSmall))

                HabitInteractionZone(
                    isGoalReached = currentCount >= goalCount,
                    hasAnyProgress = currentCount > 0,
                    habitColor = habitColor,
                    onAddClick = onAddClick,
                    onMinusClick = onMinusClick,
                )
            }

            Spacer(modifier = Modifier.height(HabitHubTheme.spacing.medium))

            ContributionGrid(
                historyData = historyData,
                habitColor = habitColor,
                startOffset = startOffset,
                scrollToColumn = scrollToColumn,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HabitInteractionZone(
    isGoalReached: Boolean,
    hasAnyProgress: Boolean,
    habitColor: Color,
    onAddClick: () -> Unit,
    onMinusClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HabitHubIconButton(
            onClick = onMinusClick,
            enabled = hasAnyProgress,
            modifier = Modifier.alpha(if (hasAnyProgress) 1f else 0f),
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease completion count",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isGoalReached) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Completed",
                tint = habitColor,
                modifier = Modifier
                    .padding(horizontal = HabitHubTheme.spacing.extraSmall)
                    .size(28.dp),
            )
        } else {
            HabitHubIconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase completion count",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun previewHistory(weeks: Int = 52): List<DayProgress> {
    val pattern = listOf(0, 0, 1, 2, 4, 4, 3)
    return List(weeks * 7) { i ->
        DayProgress(completionCount = pattern[i % pattern.size], goalCount = 4)
    }
}

private val PreviewBlue = Color(0xFF1565C0)
private val PreviewGreen = Color(0xFF2E7D32)

@Preview(name = "Card · Light · Blue · 0%", showBackground = true)
@Composable
private fun CardLightBlueEmptyPreview() {
    HabitHubTheme(darkTheme = false) {
        HabitActivityCard(
            habitName = "Morning Run",
            habitColor = PreviewBlue,
            currentCount = 0,
            goalCount = 4,
            historyData = previewHistory(),
            onAddClick = {},
            onMinusClick = {},
            modifier = Modifier.padding(16.dp),
            icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = PreviewBlue) },
        )
    }
}

@Preview(name = "Card · Light · Blue · 50%", showBackground = true)
@Composable
private fun CardLightBlueHalfPreview() {
    HabitHubTheme(darkTheme = false) {
        HabitActivityCard(
            habitName = "Morning Run",
            habitColor = PreviewBlue,
            currentCount = 2,
            goalCount = 4,
            historyData = previewHistory(),
            onAddClick = {},
            onMinusClick = {},
            modifier = Modifier.padding(16.dp),
            icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = PreviewBlue) },
        )
    }
}

@Preview(name = "Card · Light · Blue · 100%", showBackground = true)
@Composable
private fun CardLightBlueDonePreview() {
    HabitHubTheme(darkTheme = false) {
        HabitActivityCard(
            habitName = "Morning Run",
            habitColor = PreviewBlue,
            currentCount = 4,
            goalCount = 4,
            historyData = previewHistory(),
            onAddClick = {},
            onMinusClick = {},
            modifier = Modifier.padding(16.dp),
            icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = PreviewBlue) },
        )
    }
}

@Preview(name = "Card · Dark · Green · 0%", showBackground = true, backgroundColor = 0xFF11140F)
@Composable
private fun CardDarkGreenEmptyPreview() {
    HabitHubTheme(darkTheme = true) {
        HabitActivityCard(
            habitName = "Drink Water",
            habitColor = PreviewGreen,
            currentCount = 0,
            goalCount = 8,
            historyData = previewHistory(),
            onAddClick = {},
            onMinusClick = {},
            modifier = Modifier.padding(16.dp),
            icon = { Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = PreviewGreen) },
        )
    }
}

@Preview(name = "Card · Dark · Green · 100%", showBackground = true, backgroundColor = 0xFF11140F)
@Composable
private fun CardDarkGreenDonePreview() {
    HabitHubTheme(darkTheme = true) {
        HabitActivityCard(
            habitName = "Drink Water",
            habitColor = PreviewGreen,
            currentCount = 8,
            goalCount = 8,
            historyData = previewHistory(),
            onAddClick = {},
            onMinusClick = {},
            modifier = Modifier.padding(16.dp),
            icon = { Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = PreviewGreen) },
        )
    }
}