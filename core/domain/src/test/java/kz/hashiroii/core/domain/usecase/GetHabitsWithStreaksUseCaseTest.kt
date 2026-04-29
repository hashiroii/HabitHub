package kz.hashiroii.core.domain.usecase

import app.cash.turbine.test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.FakeHabitRepository
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.model.HabitWithStreak
import org.junit.Before
import org.junit.Test

class GetHabitsWithStreaksUseCaseTest {
    private lateinit var fakeRepository: FakeHabitRepository
    private lateinit var useCase: GetHabitsWithStreaksUseCase

    @Before
    fun setup() {
        fakeRepository = FakeHabitRepository()
        useCase = GetHabitsWithStreaksUseCase(fakeRepository)
    }

    @Test
    fun `returns empty list when there are no habits`() = runTest {
        useCase().test {
            assertEquals(emptyList<HabitWithStreak>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `streak is 1 when habit completed today`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
        ))
        useCase().test {
            assertEquals(1, awaitItem().first().streakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `streak is 0 when habit has never been completed`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        useCase().test {
            assertEquals(0, awaitItem().first().streakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `todayCompletionCount equals number of completions logged today`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Water", colorHex = "#2196F3", goalCount = 8)))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
            HabitCompletion(2L, 1L, System.currentTimeMillis()),
            HabitCompletion(3L, 1L, System.currentTimeMillis()),
        ))
        useCase().test {
            assertEquals(3, awaitItem().first().todayCompletionCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `activity grid covers exactly 12 weeks`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        useCase().test {
            assertEquals(84, awaitItem().first().activityGrid.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `each habit gets its own streak calculated independently`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1),
            Habit(2L, "Read", colorHex = "#2196F3", goalCount = 1),
        ))
        fakeRepository.setCompletions(listOf(
            HabitCompletion(1L, 1L, System.currentTimeMillis()),
        ))
        useCase().test {
            val result = awaitItem()
            assertEquals(1, result.find { it.habit.id == 1L }?.streakDays)
            assertEquals(0, result.find { it.habit.id == 2L }?.streakDays)
            cancelAndIgnoreRemainingEvents()
        }
    }
}