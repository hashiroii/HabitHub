package kz.hashiroii.feature.home

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.core.domain.model.DayActivity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.ui.DayProgress
import kz.hashiroii.core.ui.HabitActivityCard

internal const val TAG_HOME_LOADING = "home_loading"
internal const val TAG_HOME_ERROR = "home_error"

// ── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onAddHabit: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState, onIntent = viewModel::onIntent, onAddHabit = onAddHabit)
}

// ── Internal stateless content ────────────────────────────────────────────────

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onAddHabit: () -> Unit = {},
) {
    when (uiState) {
        is HomeUiState.Loading -> LoadingView()
        is HomeUiState.Error -> ErrorView(message = uiState.message)
        is HomeUiState.Success -> SuccessContent(state = uiState, onIntent = onIntent, onAddHabit = onAddHabit)
    }
}

// ── State views ───────────────────────────────────────────────────────────────

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(TAG_HOME_LOADING),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(TAG_HOME_ERROR),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun SuccessContent(
    state: HomeUiState.Success,
    onIntent: (HomeIntent) -> Unit,
    onAddHabit: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = HabitHubTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            item {
                Spacer(modifier = Modifier.height(HabitHubTheme.spacing.large))
                SummaryCard(
                    completedToday = state.completedToday,
                    totalHabits = state.totalHabits,
                    overallStreakDays = state.overallStreakDays,
                )
            }
            items(state.habits, key = { it.habit.id }) { habitWithStreak ->
                HabitActivityCard(
                    habitName = habitWithStreak.habit.name,
                    habitColor = habitWithStreak.habit.colorHex.toComposeColor(),
                    currentCount = habitWithStreak.todayCompletionCount,
                    goalCount = habitWithStreak.habit.goalCount,
                    historyData = habitWithStreak.activityGrid.map { day ->
                        DayProgress(completionCount = day.completionCount, goalCount = day.goalCount)
                    },
                    onAddClick = { onIntent(HomeIntent.AddCompletion(habitWithStreak.habit.id)) },
                    onMinusClick = { onIntent(HomeIntent.RemoveCompletion(habitWithStreak.habit.id)) },
                )
            }
            item { Spacer(modifier = Modifier.height(HabitHubTheme.spacing.xxLarge)) }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(
    completedToday: Int,
    totalHabits: Int,
    overallStreakDays: Int,
) {
    val isAllComplete = totalHabits > 0 && completedToday == totalHabits
    val fireColor = if (isAllComplete) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)

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
                    text = "$completedToday/$totalHabits",
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
                    text = "$overallStreakDays days",
                    style = HabitHubTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun String.toComposeColor(): Color =
    Color(android.graphics.Color.parseColor(this))

// ── Previews ──────────────────────────────────────────────────────────────────

private fun sampleHabits() = listOf(
    HabitWithStreak(Habit(1L, "Morning Run", colorHex = "#4CAF50", goalCount = 1), 7, 1, sampleGrid()),
    HabitWithStreak(Habit(2L, "Read 30 min", colorHex = "#2196F3", goalCount = 1), 3, 0, sampleGrid()),
    HabitWithStreak(Habit(3L, "Meditate", colorHex = "#9C27B0", goalCount = 1), 14, 1, sampleGrid()),
    HabitWithStreak(Habit(4L, "Drink Water", colorHex = "#00BCD4", goalCount = 8), 5, 4, sampleGrid()),
)

private fun sampleGrid() = List(84) { i -> DayActivity(i.toLong(), i % 3, 1) }

@Preview(name = "Loading · Light", showBackground = true)
@Composable
private fun LoadingLightPreview() {
    HabitHubTheme { HomeContent(HomeUiState.Loading, onIntent = {}) }
}

@Preview(name = "Error · Light", showBackground = true)
@Composable
private fun ErrorLightPreview() {
    HabitHubTheme { HomeContent(HomeUiState.Error("Failed to load habits"), onIntent = {}) }
}

@Preview(name = "Partial · Light", showBackground = true)
@Composable
private fun SuccessPartialLightPreview() {
    HabitHubTheme {
        HomeContent(
            uiState = HomeUiState.Success(sampleHabits(), 2, 4, 7),
            onIntent = {},
        )
    }
}

@Preview(name = "All Complete · Light", showBackground = true)
@Composable
private fun SuccessAllCompleteLightPreview() {
    HabitHubTheme {
        HomeContent(
            uiState = HomeUiState.Success(sampleHabits(), 4, 4, 14),
            onIntent = {},
        )
    }
}

@Preview(name = "Partial · Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SuccessPartialDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState.Success(sampleHabits(), 2, 4, 7),
            onIntent = {},
        )
    }
}

@Preview(name = "All Complete · Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SuccessAllCompleteDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState.Success(sampleHabits(), 4, 4, 14),
            onIntent = {},
        )
    }
}