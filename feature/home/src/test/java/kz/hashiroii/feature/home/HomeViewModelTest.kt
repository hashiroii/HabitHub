package kz.hashiroii.feature.home

import app.cash.turbine.test
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.usecase.GetHabitsWithStreaksUseCase
import kz.hashiroii.core.domain.usecase.GetTodayCompletionSummaryUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var fakeRepository: FakeHabitRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        fakeRepository = FakeHabitRepository()
        viewModel = HomeViewModel(
            getHabitsWithStreaks = GetHabitsWithStreaksUseCase(fakeRepository),
            getTodayCompletionSummary = GetTodayCompletionSummaryUseCase(fakeRepository),
            habitRepository = fakeRepository,
        )
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertIs<HomeUiState.Loading>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state transitions to Success after first emission`() = runTest {
        viewModel.uiState.test {
            assertIs<HomeUiState.Loading>(awaitItem())
            assertIs<HomeUiState.Success>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Success reflects correct habit count`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1),
            Habit(2L, "Read", colorHex = "#2196F3", goalCount = 1),
        ))
        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals(2, state.totalHabits)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Success reflects completedToday from repository`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1),
            Habit(2L, "Read", colorHex = "#2196F3", goalCount = 1),
        ))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
        ))
        viewModel.uiState.test {
            skipItems(1) // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals(1, state.completedToday)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state updates reactively when repository emits new habits`() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertEquals(0, (awaitItem() as HomeUiState.Success).totalHabits)

            fakeRepository.setHabits(listOf(Habit(1L, "Meditate", colorHex = "#9C27B0", goalCount = 1)))

            assertEquals(1, (awaitItem() as HomeUiState.Success).totalHabits)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddCompletion intent increments todayCompletionCount`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertEquals(0, (awaitItem() as HomeUiState.Success).habits.first().todayCompletionCount)

            viewModel.onIntent(HomeIntent.AddCompletion(1L))

            val updated = awaitItem() as HomeUiState.Success
            assertEquals(1, updated.habits.first().todayCompletionCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RemoveCompletion intent decrements todayCompletionCount`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
        ))
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertEquals(1, (awaitItem() as HomeUiState.Success).habits.first().todayCompletionCount)

            viewModel.onIntent(HomeIntent.RemoveCompletion(1L))

            val updated = awaitItem() as HomeUiState.Success
            assertEquals(0, updated.habits.first().todayCompletionCount)
            cancelAndIgnoreRemainingEvents()
        }
    }
}