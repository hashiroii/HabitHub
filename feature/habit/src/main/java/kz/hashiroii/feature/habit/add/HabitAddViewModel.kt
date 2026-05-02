package kz.hashiroii.feature.habit.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.repository.HabitRepository

sealed interface HabitAddEvent {
    data object Saved : HabitAddEvent
}

@HiltViewModel
class HabitAddViewModel @Inject constructor(
    private val repository: HabitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitAddUiState())
    val uiState: StateFlow<HabitAddUiState> = _uiState.asStateFlow()

    private val _events = Channel<HabitAddEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onIntent(intent: HabitAddIntent) {
        when (intent) {
            is HabitAddIntent.NameChanged -> _uiState.update {
                it.copy(name = intent.value, isNameError = false)
            }
            is HabitAddIntent.DescriptionChanged -> _uiState.update {
                it.copy(description = intent.value)
            }
            is HabitAddIntent.IconSelected -> _uiState.update {
                it.copy(selectedIconName = intent.iconName)
            }
            is HabitAddIntent.ColorSelected -> _uiState.update {
                it.copy(selectedColorHex = intent.colorHex)
            }
            HabitAddIntent.OpenRepetitionDialog -> _uiState.update {
                it.copy(isRepetitionDialogVisible = true)
            }
            is HabitAddIntent.RepetitionCountChanged -> _uiState.update {
                it.copy(goalCount = intent.count.coerceAtLeast(1))
            }
            HabitAddIntent.ConfirmRepetition -> _uiState.update {
                it.copy(isRepetitionDialogVisible = false)
            }
            HabitAddIntent.DismissRepetitionDialog -> _uiState.update {
                it.copy(isRepetitionDialogVisible = false)
            }
            HabitAddIntent.SaveClicked -> save()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(isNameError = true) }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.addHabit(
                    Habit(
                        name = state.name.trim(),
                        description = state.description.trim(),
                        iconName = state.selectedIconName,
                        colorHex = state.selectedColorHex,
                        goalCount = state.goalCount,
                    ),
                )
            }.onSuccess {
                _events.send(HabitAddEvent.Saved)
            }
        }
    }
}