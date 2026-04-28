package kz.hashiroii.feature.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.repository.HabitRepository

class FakeHabitRepository : HabitRepository {
    private val habitsFlow = MutableStateFlow<List<Habit>>(emptyList())
    private val completionsFlow = MutableStateFlow<List<HabitCompletion>>(emptyList())

    fun setHabits(habits: List<Habit>) { habitsFlow.value = habits }
    fun setCompletions(completions: List<HabitCompletion>) { completionsFlow.value = completions }

    override fun observeHabits(): Flow<List<Habit>> = habitsFlow
    override fun observeAllCompletions(): Flow<List<HabitCompletion>> = completionsFlow

    override suspend fun addHabit(habit: Habit): Long {
        val newId = (habitsFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        habitsFlow.value = habitsFlow.value + habit.copy(id = newId)
        return newId
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitsFlow.value = habitsFlow.value.filter { it.id != habitId }
    }

    override suspend fun addCompletion(habitId: Long) {
        val newId = (completionsFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        completionsFlow.value = completionsFlow.value + HabitCompletion(
            id = newId,
            habitId = habitId,
            completedAt = System.currentTimeMillis(),
        )
    }

    override suspend fun removeLatestCompletion(habitId: Long) {
        val latest = completionsFlow.value
            .filter { it.habitId == habitId }
            .maxByOrNull { it.completedAt } ?: return
        completionsFlow.value = completionsFlow.value.filter { it.id != latest.id }
    }
}