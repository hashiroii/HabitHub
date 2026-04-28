package kz.hashiroii.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>
    fun observeAllCompletions(): Flow<List<HabitCompletion>>
    suspend fun addHabit(habit: Habit): Long
    suspend fun deleteHabit(habitId: Long)
    suspend fun addCompletion(habitId: Long)
    suspend fun removeLatestCompletion(habitId: Long)
}