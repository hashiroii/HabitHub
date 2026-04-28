package kz.hashiroii.core.domain.usecase

import app.cash.turbine.test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.FakeHabitRepository
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import org.junit.Before
import org.junit.Test

class GetTodayCompletionSummaryUseCaseTest {
    private lateinit var fakeRepository: FakeHabitRepository
    private lateinit var useCase: GetTodayCompletionSummaryUseCase

    @Before
    fun setup() {
        fakeRepository = FakeHabitRepository()
        useCase = GetTodayCompletionSummaryUseCase(fakeRepository)
    }

    @Test
    fun `all counts are zero when no habits exist`() = runTest {
        useCase().test {
            val summary = awaitItem()
            assertEquals(0, summary.completedHabits)
            assertEquals(0, summary.totalHabits)
            assertEquals(0, summary.overallStreakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completedHabits counts only habits with a completion today`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", "#4CAF50", 1),
            Habit(2L, "Read", "#2196F3", 1),
            Habit(3L, "Meditate", "#9C27B0", 1),
        ))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
            HabitCompletion(2L, 2L, System.currentTimeMillis()),
        ))
        useCase().test {
            val summary = awaitItem()
            assertEquals(2, summary.completedHabits)
            assertEquals(3, summary.totalHabits)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `overall streak is 1 when there are completions today`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", "#4CAF50", 1)))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
        ))
        useCase().test {
            assertEquals(1, awaitItem().overallStreakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple completions of the same habit count as one completed habit`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Water", "#00BCD4", 8)))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
            HabitCompletion(2L, 1L, System.currentTimeMillis()),
            HabitCompletion(3L, 1L, System.currentTimeMillis()),
        ))
        useCase().test {
            assertEquals(1, awaitItem().completedHabits)
            cancelAndIgnoreRemainingEvents()
        }
    }
}