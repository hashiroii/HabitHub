package kz.hashiroii.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kz.hashiroii.core.data.database.entity.HabitCompletionEntity

@Dao
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<HabitCompletionEntity>>

    @Insert
    suspend fun insert(completion: HabitCompletionEntity): Long

    @Query("""
        DELETE FROM habit_completions
        WHERE id = (
            SELECT id FROM habit_completions
            WHERE habitId = :habitId
            ORDER BY completedAt DESC
            LIMIT 1
        )
    """)
    suspend fun deleteLatestByHabitId(habitId: Long)

    @Query("""
        DELETE FROM habit_completions
        WHERE id = (
            SELECT id FROM habit_completions
            WHERE habitId = :habitId
            AND completedAt / 86400000 = :epochDay
            ORDER BY completedAt DESC
            LIMIT 1
        )
    """)
    suspend fun deleteByHabitIdAndEpochDay(habitId: Long, epochDay: Long)
}