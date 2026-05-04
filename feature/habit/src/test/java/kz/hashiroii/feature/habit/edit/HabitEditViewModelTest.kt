package kz.hashiroii.feature.habit.edit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.usecase.GetHabitByIdUseCase
import kz.hashiroii.core.domain.usecase.GetHabitsWithStreaksUseCase
import kz.hashiroii.feature.habit.FakeHabitRepository
import kz.hashiroii.feature.habit.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HabitEditViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var repo: FakeHabitRepository
    private lateinit var viewModel: HabitEditViewModel

    private val habitId = 1L
    private val testHabit = Habit(id = habitId, name = "Morning Run", colorHex = "#4CAF50", goalCount = 1)

    @Before
    fun setup() {
        repo = FakeHabitRepository()
        repo.setHabits(listOf(testHabit))
        viewModel = HabitEditViewModel(
            savedStateHandle = SavedStateHandle(mapOf("habitId" to habitId)),
            getHabitById = GetHabitByIdUseCase(GetHabitsWithStreaksUseCase(repo)),
            repository = repo,
        )
    }

    @Test
    fun `state eventually becomes Success with correct habit name`() = runTest {
        viewModel.uiState.test {
            val state = awaitSuccess()
            assertEquals("Morning Run", state.habitWithStreak.habit.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectDay updates selectedDayEpochDay`() = runTest {
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.SelectDay(19_000L))
            assertEquals(19_000L, (awaitItem() as HabitEditUiState.Success).selectedDayEpochDay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissDayDialog clears selectedDayEpochDay`() = runTest {
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.SelectDay(19_000L))
            awaitItem()
            viewModel.onIntent(HabitEditIntent.DismissDayDialog)
            assertNull((awaitItem() as HabitEditUiState.Success).selectedDayEpochDay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddCompletionForDay sets hasChanges and increments count`() = runTest {
        val epochDay = 19_000L
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.AddCompletionForDay(epochDay))
            val state = awaitItem() as HabitEditUiState.Success
            assertTrue(state.hasChanges)
            assertEquals(1, state.completionsByDay[epochDay] ?: 0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteHabit shows confirmation dialog`() = runTest {
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.DeleteHabit)
            assertTrue((awaitItem() as HabitEditUiState.Success).isDeleteConfirmVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DismissDelete hides confirmation dialog`() = runTest {
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.DeleteHabit)
            awaitItem()
            viewModel.onIntent(HabitEditIntent.DismissDelete)
            assertFalse((awaitItem() as HabitEditUiState.Success).isDeleteConfirmVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmDelete emits Close event`() = runTest {
        viewModel.uiState.test { awaitSuccess() }
        viewModel.events.test {
            viewModel.onIntent(HabitEditIntent.ConfirmDelete)
            assertIs<HabitEditEvent.Close>(awaitItem())
        }
    }

    @Test
    fun `RequestClose with no changes emits Close`() = runTest {
        viewModel.uiState.test { awaitSuccess() }
        viewModel.events.test {
            viewModel.onIntent(HabitEditIntent.RequestClose)
            assertIs<HabitEditEvent.Close>(awaitItem())
        }
    }

    @Test
    fun `RequestClose with changes shows discard warning`() = runTest {
        viewModel.uiState.test {
            awaitSuccess()
            viewModel.onIntent(HabitEditIntent.AddCompletionForDay(19_000L))
            awaitItem()
            viewModel.onIntent(HabitEditIntent.RequestClose)
            assertTrue((awaitItem() as HabitEditUiState.Success).isDiscardWarningVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateToReminders emits correct event`() = runTest {
        viewModel.uiState.test { awaitSuccess() }
        viewModel.events.test {
            viewModel.onIntent(HabitEditIntent.NavigateToReminders)
            val event = awaitItem()
            assertIs<HabitEditEvent.NavigateToReminders>(event)
            assertEquals(habitId, event.habitId)
            assertEquals("Morning Run", event.habitName)
        }
    }

    // Skip past Loading and return the first Success item
    private suspend fun ReceiveTurbine<HabitEditUiState>.awaitSuccess(): HabitEditUiState.Success {
        var item = awaitItem()
        while (item !is HabitEditUiState.Success) {
            item = awaitItem()
        }
        return item
    }
}