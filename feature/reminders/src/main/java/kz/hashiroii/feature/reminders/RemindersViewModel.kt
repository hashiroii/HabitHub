package kz.hashiroii.feature.reminders

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

sealed interface RemindersEvent {
    data object Close : RemindersEvent
}

@HiltViewModel
class RemindersViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    private lateinit var habitIdKey: String
    private var habitIdValue: Long = -1L

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("habithub_reminders", Context.MODE_PRIVATE)
    }

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    private val _events = Channel<RemindersEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun init(habitId: Long, habitName: String) {
        if (habitIdValue == habitId) return
        habitIdValue = habitId
        habitIdKey = "reminders_$habitId"
        _uiState.update { it.copy(habitName = habitName, reminders = loadReminders()) }
    }

    fun onIntent(intent: RemindersIntent) {
        when (intent) {
            RemindersIntent.AddReminderClicked -> _uiState.update { it.copy(isTimePickerVisible = true) }
            is RemindersIntent.TimeSelected -> {
                _uiState.update { it.copy(isTimePickerVisible = false) }
                val id = System.currentTimeMillis().toInt()
                val reminder = ReminderTime(id = id, hour = intent.hour, minute = intent.minute)
                saveReminder(reminder)
                scheduler.schedule(habitIdValue, id, intent.hour, intent.minute)
                _uiState.update { it.copy(reminders = loadReminders()) }
            }
            is RemindersIntent.DeleteReminder -> {
                scheduler.cancel(intent.reminderId)
                deleteReminder(intent.reminderId)
                _uiState.update { it.copy(reminders = loadReminders()) }
            }
            RemindersIntent.DismissTimePicker -> _uiState.update { it.copy(isTimePickerVisible = false) }
            RemindersIntent.Close -> _events.trySend(RemindersEvent.Close)
        }
    }

    private fun loadReminders(): List<ReminderTime> {
        val raw = prefs.getStringSet(habitIdKey, emptySet()) ?: emptySet()
        return raw.mapNotNull { encoded ->
            runCatching {
                val parts = encoded.split(":")
                ReminderTime(
                    id = parts[0].toInt(),
                    hour = parts[1].toInt(),
                    minute = parts[2].toInt(),
                )
            }.getOrNull()
        }.sortedWith(compareBy({ it.hour }, { it.minute }))
    }

    private fun saveReminder(reminder: ReminderTime) {
        val current = prefs.getStringSet(habitIdKey, mutableSetOf())!!.toMutableSet()
        current.add("${reminder.id}:${reminder.hour}:${reminder.minute}")
        prefs.edit().putStringSet(habitIdKey, current).apply()
    }

    private fun deleteReminder(reminderId: Int) {
        val current = prefs.getStringSet(habitIdKey, mutableSetOf())!!.toMutableSet()
        current.removeAll { it.startsWith("$reminderId:") }
        prefs.edit().putStringSet(habitIdKey, current).apply()
    }
}