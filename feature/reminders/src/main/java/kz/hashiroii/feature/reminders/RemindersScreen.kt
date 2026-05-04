package kz.hashiroii.feature.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

@Composable
fun RemindersScreen(
    habitId: Long,
    habitName: String,
    onClose: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel(),
) {
    LaunchedEffect(habitId, habitName) {
        viewModel.init(habitId, habitName)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RemindersEvent.Close -> onClose()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RemindersContent(uiState = uiState, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RemindersContent(
    uiState: RemindersUiState,
    onIntent: (RemindersIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reminders_title, uiState.habitName)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(RemindersIntent.Close) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.reminders_back))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(RemindersIntent.AddReminderClicked) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.reminders_add))
            }
        },
    ) { paddingValues ->
        if (uiState.reminders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = stringResource(R.string.reminders_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.reminders_empty_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(uiState.reminders, key = { it.id }) { reminder ->
                    ReminderRow(
                        reminder = reminder,
                        onDelete = { onIntent(RemindersIntent.DeleteReminder(reminder.id)) },
                        onToggleDay = { day -> onIntent(RemindersIntent.ToggleDay(reminder.id, day)) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (uiState.isTimePickerVisible) {
        TimePickerDialog(
            onConfirm = { hour, minute -> onIntent(RemindersIntent.TimeSelected(hour, minute)) },
            onDismiss = { onIntent(RemindersIntent.DismissTimePicker) },
        )
    }
}

@Composable
private fun ReminderRow(
    reminder: ReminderTime,
    onDelete: () -> Unit,
    onToggleDay: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = reminder.displayTime,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.reminders_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        DayToggleRow(days = reminder.days, onToggle = onToggleDay)
    }
}

@Composable
private fun DayToggleRow(
    days: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    val locale = remember { Locale.getDefault() }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (index in 0..6) {
            val dayOfWeek = DayOfWeek.of(index + 1)
            val label = dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
                .take(2)
                .replaceFirstChar { it.uppercaseChar() }
            val selected = index in days
            val bgColor = if (selected) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.surfaceVariant
            val textColor = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable { onToggle(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reminders_time_picker_title)) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text(stringResource(R.string.reminders_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.reminders_cancel))
            }
        },
    )
}

@Preview(name = "Reminders · Light · Empty", showBackground = true)
@Composable
private fun RemindersEmptyPreview() {
    HabitHubTheme(darkTheme = false) {
        RemindersContent(
            uiState = RemindersUiState(habitName = "Morning Run"),
            onIntent = {},
        )
    }
}

@Preview(name = "Reminders · Light · With items", showBackground = true)
@Composable
private fun RemindersWithItemsPreview() {
    HabitHubTheme(darkTheme = false) {
        RemindersContent(
            uiState = RemindersUiState(
                habitName = "Morning Run",
                reminders = listOf(
                    ReminderTime(1, 7, 0, days = setOf(0, 1, 2, 3, 4)),
                    ReminderTime(2, 18, 30, days = setOf(5, 6)),
                ),
            ),
            onIntent = {},
        )
    }
}