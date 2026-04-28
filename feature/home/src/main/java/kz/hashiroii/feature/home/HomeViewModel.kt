package kz.hashiroii.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.domain.model.TodayCompletionSummary
import kz.hashiroii.core.domain.repository.HabitRepository
import kz.hashiroii.core.domain.usecase.GetHabitsWithStreaksUseCase
import kz.hashiroii.core.domain.usecase.GetTodayCompletionSummaryUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHabitsWithStreaks: GetHabitsWithStreaksUseCase,
    getTodayCompletionSummary: GetTodayCompletionSummaryUseCase,
    private val habitRepository: HabitRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine<List<HabitWithStreak>, TodayCompletionSummary, HomeUiState>(
        getHabitsWithStreaks(),
        getTodayCompletionSummary(),
    ) { habits, summary ->
        HomeUiState.Success(
            habits = habits,
            completedToday = summary.completedHabits,
            totalHabits = summary.totalHabits,
            overallStreakDays = summary.overallStreakDays,
        )
    }
        .catch { e -> emit(HomeUiState.Error(e.message ?: "An unexpected error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.AddCompletion -> dispatchIO { habitRepository.addCompletion(intent.habitId) }
            is HomeIntent.RemoveCompletion -> dispatchIO { habitRepository.removeLatestCompletion(intent.habitId) }
        }
    }

    private inline fun dispatchIO(crossinline block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { runCatching { block() } }
    }
}