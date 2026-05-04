package kz.hashiroii.feature.reminders

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(habitName = habitName, reminders = loadReminders()) }
        }
    }

    fun onIntent(intent: RemindersIntent) {
        when (intent) {
            RemindersIntent.AddReminderClicked -> _uiState.update { it.copy(isTimePickerVisible = true) }
            is RemindersIntent.TimeSelected -> {
                _uiState.update { it.copy(isTimePickerVisible = false) }
                val id = System.currentTimeMillis().toInt()
                val reminder = ReminderTime(id = id, hour = intent.hour, minute = intent.minute)
                scheduler.schedule(habitIdValue, id, intent.hour, intent.minute, reminder.days)
                viewModelScope.launch(Dispatchers.IO) {
                    saveReminder(reminder)
                    _uiState.update { it.copy(reminders = loadReminders()) }
                }
            }
            is RemindersIntent.DeleteReminder -> {
                scheduler.cancelAll(intent.reminderId)
                viewModelScope.launch(Dispatchers.IO) {
                    deleteReminder(intent.reminderId)
                    _uiState.update { it.copy(reminders = loadReminders()) }
                }
            }
            is RemindersIntent.ToggleDay -> {
                val updated = _uiState.value.reminders.map { reminder ->
                    if (reminder.id != intent.reminderId) return@map reminder
                    val newDays = if (intent.day in reminder.days) {
                        reminder.days - intent.day
                    } else {
                        reminder.days + intent.day
                    }
                    reminder.copy(days = newDays)
                }
                _uiState.update { it.copy(reminders = updated) }
                val changed = updated.find { it.id == intent.reminderId } ?: return
                scheduler.schedule(habitIdValue, changed.id, changed.hour, changed.minute, changed.days)
                viewModelScope.launch(Dispatchers.IO) { saveReminder(changed) }
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
                val daysBitmask = if (parts.size >= 4) parts[3].toInt() else 0x7F
                ReminderTime(
                    id = parts[0].toInt(),
                    hour = parts[1].toInt(),
                    minute = parts[2].toInt(),
                    days = bitmaskToDays(daysBitmask),
                )
            }.getOrNull()
        }.sortedWith(compareBy({ it.hour }, { it.minute }))
    }

    private fun saveReminder(reminder: ReminderTime) {
        val current = prefs.getStringSet(habitIdKey, mutableSetOf())!!.toMutableSet()
        current.removeAll { it.startsWith("${reminder.id}:") }
        current.add("${reminder.id}:${reminder.hour}:${reminder.minute}:${daysToBitmask(reminder.days)}")
        prefs.edit().putStringSet(habitIdKey, current).apply()
    }

    private fun deleteReminder(reminderId: Int) {
        val current = prefs.getStringSet(habitIdKey, mutableSetOf())!!.toMutableSet()
        current.removeAll { it.startsWith("$reminderId:") }
        prefs.edit().putStringSet(habitIdKey, current).apply()
    }

    private fun bitmaskToDays(mask: Int): Set<Int> =
        (0..6).filter { (mask shr it) and 1 == 1 }.toSet()

    private fun daysToBitmask(days: Set<Int>): Int =
        days.fold(0) { acc, day -> acc or (1 shl day) }
}