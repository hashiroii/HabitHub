package kz.hashiroii.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(),
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit",
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = HabitHubTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(HabitHubTheme.spacing.large))
                SummaryCard(
                    completedCount = uiState.completedCount,
                    totalCount = uiState.totalCount,
                    streakDays = uiState.streakDays,
                )
                Spacer(modifier = Modifier.height(HabitHubTheme.spacing.xLarge))
            }
            items(uiState.habits, key = { it.id }) { habit ->
                HabitRow(habit = habit)
            }
            item {
                Spacer(modifier = Modifier.height(HabitHubTheme.spacing.xxLarge))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    completedCount: Int,
    totalCount: Int,
    streakDays: Int,
) {
    val isAllComplete = totalCount > 0 && completedCount == totalCount
    val fireColor = if (isAllComplete) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HabitHubTheme.spacing.xLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$completedCount/$totalCount",
                    style = HabitHubTheme.typography.counterNumber.copy(fontSize = 38.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "habits completed today",
                    style = HabitHubTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = fireColor,
                    modifier = Modifier.size(40.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$streakDays days",
                    style = HabitHubTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HabitRow(habit: HabitItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HabitHubTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = habit.color, shape = CircleShape),
        )
        Spacer(modifier = Modifier.width(HabitHubTheme.spacing.large))
        Text(
            text = habit.name,
            style = HabitHubTheme.typography.habitTitle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 0.5.dp,
    )
}

// --- Previews ---

@Preview(name = "Partial · Light", showBackground = true)
@Composable
private fun HomeScreenPartialPreview() {
    HabitHubTheme {
        HomeScreen(uiState = HomeUiState(completedCount = 4, totalCount = 9, streakDays = 7))
    }
}

@Preview(name = "All Complete · Light", showBackground = true)
@Composable
private fun HomeScreenAllCompletePreview() {
    HabitHubTheme {
        HomeScreen(uiState = HomeUiState(completedCount = 9, totalCount = 9, streakDays = 14))
    }
}

@Preview(name = "Empty · Light", showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    HabitHubTheme {
        HomeScreen(uiState = HomeUiState(completedCount = 0, totalCount = 0, streakDays = 0, habits = emptyList()))
    }
}

@Preview(name = "Partial · Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPartialDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HomeScreen(uiState = HomeUiState(completedCount = 4, totalCount = 9, streakDays = 7))
    }
}

@Preview(name = "All Complete · Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenAllCompleteDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HomeScreen(uiState = HomeUiState(completedCount = 9, totalCount = 9, streakDays = 14))
    }
}
