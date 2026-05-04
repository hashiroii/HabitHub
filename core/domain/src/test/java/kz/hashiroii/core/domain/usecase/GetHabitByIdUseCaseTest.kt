package kz.hashiroii.core.domain.usecase

import app.cash.turbine.test
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.FakeHabitRepository
import kz.hashiroii.core.domain.model.Habit
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetHabitByIdUseCaseTest {

    private lateinit var fakeRepository: FakeHabitRepository
    private lateinit var useCase: GetHabitByIdUseCase

    @Before
    fun setup() {
        fakeRepository = FakeHabitRepository()
        useCase = GetHabitByIdUseCase(GetHabitsWithStreaksUseCase(fakeRepository))
    }

    @Test
    fun `returns null when no habits exist`() = runTest {
        useCase(1L).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returns matching habit by id`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1),
            Habit(2L, "Read", colorHex = "#2196F3", goalCount = 1),
        ))
        useCase(2L).test {
            assertEquals(2L, awaitItem()?.habit?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returns null when id does not match any habit`() = runTest {
        fakeRepository.setHabits(listOf(
            Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1),
        ))
        useCase(99L).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updates reactively when habit is deleted`() = runTest {
        fakeRepository.setHabits(listOf(Habit(1L, "Run", colorHex = "#4CAF50", goalCount = 1)))
        useCase(1L).test {
            assertEquals(1L, awaitItem()?.habit?.id)
            fakeRepository.deleteHabit(1L)
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}