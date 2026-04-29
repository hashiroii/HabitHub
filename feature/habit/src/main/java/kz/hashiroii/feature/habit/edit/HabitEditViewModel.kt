package kz.hashiroii.feature.habit.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.domain.repository.HabitRepository
import kz.hashiroii.core.domain.usecase.GetHabitByIdUseCase

sealed interface HabitEditEvent {
    data object Close : HabitEditEvent
    data class NavigateToReminders(val habitId: Long, val habitName: String) : HabitEditEvent
}

private data class EphemeralState(
    val selectedDayEpochDay: Long? = null,
    val isGoalDialogVisible: Boolean = false,
    val pendingGoalCount: Int = 1,
)

@HiltViewModel
class HabitEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getHabitById: GetHabitByIdUseCase,
    private val repository: HabitRepository,
) : ViewModel() {

    private val habitId: Long = checkNotNull(savedStateHandle["habitId"])

    private val _events = Channel<HabitEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _ephemeral = MutableStateFlow(EphemeralState())

    private val habitWithStreakFlow = getHabitById(habitId).filterNotNull()
    private val completionsByDayFlow = repository.observeAllCompletions().map { completions ->
        completions
            .filter { it.habitId == habitId }
            .groupBy { it.completedAt / 86_400_000L }
            .mapValues { it.value.size }
    }

    val uiState: StateFlow<HabitEditUiState> = combine<HabitWithStreak, Map<Long, Int>, EphemeralState, HabitEditUiState>(
        habitWithStreakFlow,
        completionsByDayFlow,
        _ephemeral,
    ) { habitWithStreak, completionsByDay, ephemeral ->
        HabitEditUiState.Success(
            habitWithStreak = habitWithStreak,
            completionsByDay = completionsByDay,
            selectedDayEpochDay = ephemeral.selectedDayEpochDay,
            selectedDayCompletionCount = completionsByDay[ephemeral.selectedDayEpochDay ?: -1L] ?: 0,
            isGoalDialogVisible = ephemeral.isGoalDialogVisible,
            pendingGoalCount = ephemeral.pendingGoalCount,
        )
    }
        .catch { e -> emit(HabitEditUiState.Error(e.message ?: "Unexpected error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HabitEditUiState.Loading,
        )

    fun onIntent(intent: HabitEditIntent) {
        when (intent) {
            is HabitEditIntent.SelectDay -> _ephemeral.update {
                it.copy(selectedDayEpochDay = intent.epochDay)
            }
            is HabitEditIntent.AddCompletionForDay -> dispatchIO {
                repository.addCompletionForDay(
                    habitId = habitId,
                    timestampMillis = intent.epochDay * 86_400_000L + 43_200_000L,
                )
            }
            is HabitEditIntent.RemoveCompletionForDay -> dispatchIO {
                repository.removeCompletionForDay(habitId, intent.epochDay)
            }
            HabitEditIntent.DismissDayDialog -> _ephemeral.update {
                it.copy(selectedDayEpochDay = null)
            }
            HabitEditIntent.OpenGoalDialog -> {
                val goalCount = (uiState.value as? HabitEditUiState.Success)
                    ?.habitWithStreak?.habit?.goalCount ?: 1
                _ephemeral.update { it.copy(isGoalDialogVisible = true, pendingGoalCount = goalCount) }
            }
            is HabitEditIntent.GoalChanged -> _ephemeral.update {
                it.copy(pendingGoalCount = intent.count.coerceAtLeast(1))
            }
            HabitEditIntent.SaveGoal -> _ephemeral.update { it.copy(isGoalDialogVisible = false) }
            HabitEditIntent.DismissGoalDialog -> _ephemeral.update { it.copy(isGoalDialogVisible = false) }
            HabitEditIntent.NavigateToReminders -> {
                val state = uiState.value as? HabitEditUiState.Success ?: return
                viewModelScope.launch {
                    _events.send(
                        HabitEditEvent.NavigateToReminders(
                            habitId = habitId,
                            habitName = state.habitWithStreak.habit.name,
                        ),
                    )
                }
            }
            HabitEditIntent.Close -> viewModelScope.launch {
                _events.send(HabitEditEvent.Close)
            }
        }
    }

    private inline fun dispatchIO(crossinline block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { runCatching { block() } }
    }
}