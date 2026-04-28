package kz.hashiroii.core.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.hashiroii.core.data.database.dao.HabitCompletionDao
import kz.hashiroii.core.data.database.dao.HabitDao
import kz.hashiroii.core.data.database.entity.HabitCompletionEntity
import kz.hashiroii.core.data.database.mapper.toDomain
import kz.hashiroii.core.data.database.mapper.toEntity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.repository.HabitRepository

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
) : HabitRepository {

    override fun observeHabits(): Flow<List<Habit>> =
        habitDao.observeAll().map { it.map { entity -> entity.toDomain() } }

    override fun observeAllCompletions(): Flow<List<HabitCompletion>> =
        completionDao.observeAll().map { it.map { entity -> entity.toDomain() } }

    override suspend fun addHabit(habit: Habit): Long =
        habitDao.insert(habit.toEntity())

    override suspend fun deleteHabit(habitId: Long) =
        habitDao.deleteById(habitId)

    override suspend fun addCompletion(habitId: Long) {
        completionDao.insert(
            HabitCompletionEntity(
                habitId = habitId,
                completedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun removeLatestCompletion(habitId: Long) =
        completionDao.deleteLatestByHabitId(habitId)
}