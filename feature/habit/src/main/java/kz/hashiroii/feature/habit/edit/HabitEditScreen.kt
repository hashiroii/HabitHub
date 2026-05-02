package kz.hashiroii.feature.habit.edit

import android.content.Intent
import android.graphics.Color as AndroidColor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.core.domain.model.DayActivity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.ui.ContributionGrid
import kz.hashiroii.core.ui.DayProgress
import kz.hashiroii.core.ui.HabitIcons

@Composable
fun HabitEditScreen(
    onClose: () -> Unit,
    onNavigateToReminders: (habitId: Long, habitName: String) -> Unit,
    viewModel: HabitEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HabitEditEvent.Close -> onClose()
                is HabitEditEvent.NavigateToReminders ->
                    onNavigateToReminders(event.habitId, event.habitName)
            }
        }
    }

    HabitEditContent(uiState = uiState, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitEditContent(
    uiState: HabitEditUiState,
    onIntent: (HabitEditIntent) -> Unit,
) {
    if (uiState is HabitEditUiState.Success) {
        BackHandler { onIntent(HabitEditIntent.RequestClose) }
    }

    val successState = uiState as? HabitEditUiState.Success
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(successState?.habitWithStreak?.habit?.name ?: "Edit Habit")
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(HabitEditIntent.RequestClose) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (successState != null) {
                        val habit = successState.habitWithStreak.habit
                        IconButton(onClick = { onIntent(HabitEditIntent.NavigateToReminders) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Reminders")
                        }
                        IconButton(onClick = {
                            val deepLink = "habithub://habit/${habit.id}"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, deepLink)
                                putExtra(Intent.EXTRA_TITLE, habit.name)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { onIntent(HabitEditIntent.DeleteHabit) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete habit",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            HabitEditUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            is HabitEditUiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }
            is HabitEditUiState.Success -> SuccessBody(
                state = uiState,
                onIntent = onIntent,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }

    if (uiState is HabitEditUiState.Success) {
        uiState.selectedDayEpochDay?.let { epochDay ->
            DayDetailDialog(
                epochDay = epochDay,
                completionCount = uiState.selectedDayCompletionCount,
                goalCount = uiState.habitWithStreak.habit.goalCount,
                onAdd = { onIntent(HabitEditIntent.AddCompletionForDay(epochDay)) },
                onRemove = { onIntent(HabitEditIntent.RemoveCompletionForDay(epochDay)) },
                onDismiss = { onIntent(HabitEditIntent.DismissDayDialog) },
            )
        }

        if (uiState.isDeleteConfirmVisible) {
            DeleteConfirmDialog(
                habitName = uiState.habitWithStreak.habit.name,
                onConfirm = { onIntent(HabitEditIntent.ConfirmDelete) },
                onDismiss = { onIntent(HabitEditIntent.DismissDelete) },
            )
        }

        if (uiState.isDiscardWarningVisible) {
            DiscardWarningDialog(
                onSave = { onIntent(HabitEditIntent.SaveFromDiscardWarning) },
                onDiscard = { onIntent(HabitEditIntent.DiscardChanges) },
                onDismiss = { onIntent(HabitEditIntent.SaveFromDiscardWarning) },
            )
        }
    }
}

@Composable
private fun SuccessBody(
    state: HabitEditUiState.Success,
    onIntent: (HabitEditIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val habit = state.habitWithStreak.habit
    val habitColor = remember(habit.colorHex) {
        runCatching { Color(AndroidColor.parseColor(habit.colorHex)) }.getOrDefault(Color(0xFF4CAF50))
    }
    val startOffset = remember(state.habitWithStreak.activityGrid) {
        state.habitWithStreak.activityGrid.firstOrNull()?.let { first ->
            LocalDate.ofEpochDay(first.dateEpochDay).dayOfWeek.value % 7
        } ?: 0
    }
    val scrollToColumn = remember(startOffset, state.habitWithStreak.activityGrid) {
        val yearStartDay = state.habitWithStreak.activityGrid.firstOrNull()?.dateEpochDay
            ?: return@remember null
        val todayEpochDay = LocalDate.now().toEpochDay()
        if (todayEpochDay < yearStartDay) return@remember null
        ((startOffset + (todayEpochDay - yearStartDay)).toInt()) / 7
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ContributionGrid(
            historyData = state.habitWithStreak.activityGrid.map {
                DayProgress(it.completionCount, it.goalCount)
            },
            habitColor = habitColor,
            startOffset = startOffset,
            scrollToColumn = scrollToColumn,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        StreakRow(
            streakDays = state.habitWithStreak.streakDays,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        CalendarSection(
            completionsByDay = state.completionsByDay,
            goalCount = habit.goalCount,
            habitColor = habitColor,
            onDayClick = { onIntent(HabitEditIntent.SelectDay(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onIntent(HabitEditIntent.Confirm) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text("Confirm")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StreakRow(
    streakDays: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = if (streakDays > 0) Color(0xFFFF6F00) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$streakDays day streak",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun CalendarSection(
    completionsByDay: Map<Long, Int>,
    goalCount: Int,
    habitColor: Color,
    onDayClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }
    val todayEpochDay = remember { today.toEpochDay() }
    val currentYearMonth = remember { YearMonth.now() }
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 60,
        pageCount = { 120 },
    )

    androidx.compose.foundation.pager.HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) { page ->
        val yearMonth = currentYearMonth.plusMonths((page - 60).toLong())
        MonthCalendar(
            yearMonth = yearMonth,
            todayEpochDay = todayEpochDay,
            completionsByDay = completionsByDay,
            goalCount = goalCount,
            habitColor = habitColor,
            onDayClick = onDayClick,
        )
    }
}

@Composable
private fun MonthCalendar(
    yearMonth: YearMonth,
    todayEpochDay: Long,
    completionsByDay: Map<Long, Int>,
    goalCount: Int,
    habitColor: Color,
    onDayClick: (Long) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = (firstDay.dayOfWeek.value % 7)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, locale)} ${yearMonth.year}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }

        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        repeat(rows) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(7) { col ->
                    val dayIndex = row * 7 + col - firstDayOfWeek
                    if (dayIndex < 0 || dayIndex >= daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val dayOfMonth = dayIndex + 1
                        val epochDay = firstDay.plusDays(dayIndex.toLong()).toEpochDay()
                        val count = completionsByDay[epochDay] ?: 0
                        val saturation = if (goalCount <= 0) 0f
                        else (count.toFloat() / goalCount).coerceIn(0f, 1f)
                        val isToday = epochDay == todayEpochDay
                        val bgColor = when {
                            saturation > 0f -> habitColor.copy(alpha = 0.2f + saturation * 0.8f)
                            else -> Color.Transparent
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .then(
                                    if (isToday) Modifier.border(1.dp, habitColor, CircleShape)
                                    else Modifier,
                                )
                                .clickable { onDayClick(epochDay) },
                        ) {
                            Text(
                                text = dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (saturation > 0.5f) Color.White
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetailDialog(
    epochDay: Long,
    completionCount: Int,
    goalCount: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val date = remember(epochDay) { LocalDate.ofEpochDay(epochDay) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, locale)} ${date.year}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$completionCount / $goalCount",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "completions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onRemove,
                        enabled = completionCount > 0,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text("−", style = MaterialTheme.typography.titleLarge)
                    }
                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismiss) { Text("Done") }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    habitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete habit") },
        text = { Text("\"$habitName\" and all its history will be permanently deleted.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) { Text("Delete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun DiscardWarningDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unsaved changes") },
        text = { Text("You have unsaved changes. Would you like to save them before leaving?") },
        confirmButton = { TextButton(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDiscard) { Text("Discard") } },
    )
}

private fun sampleHabitWithStreak() = HabitWithStreak(
    habit = Habit(1L, "Morning Run", colorHex = "#4CAF50", iconName = "DirectionsRun", goalCount = 1),
    streakDays = 5,
    todayCompletionCount = 1,
    activityGrid = List(364) { i -> DayActivity(i.toLong(), if (i % 3 == 0) 1 else 0, 1) },
)

@Preview(name = "Edit · Light", showBackground = true)
@Composable
private fun HabitEditLightPreview() {
    HabitHubTheme(darkTheme = false) {
        HabitEditContent(
            uiState = HabitEditUiState.Success(
                habitWithStreak = sampleHabitWithStreak(),
                completionsByDay = emptyMap(),
            ),
            onIntent = {},
        )
    }
}

@Preview(name = "Edit · Dark", showBackground = true)
@Composable
private fun HabitEditDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HabitEditContent(
            uiState = HabitEditUiState.Success(
                habitWithStreak = sampleHabitWithStreak(),
                completionsByDay = emptyMap(),
            ),
            onIntent = {},
        )
    }
}