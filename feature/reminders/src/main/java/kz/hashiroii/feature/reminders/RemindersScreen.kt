package kz.hashiroii.feature.reminders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                title = { Text("Reminders · ${uiState.habitName}") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(RemindersIntent.Close) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(RemindersIntent.AddReminderClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Add reminder")
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
                    text = "No reminders yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap + to add a daily reminder",
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
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                contentDescription = "Delete reminder",
                tint = MaterialTheme.colorScheme.error,
            )
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
        title = { Text("Select time") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
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
                    ReminderTime(1, 7, 0),
                    ReminderTime(2, 18, 30),
                ),
            ),
            onIntent = {},
        )
    }
}