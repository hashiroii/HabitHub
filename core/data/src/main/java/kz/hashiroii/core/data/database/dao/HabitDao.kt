package kz.hashiroii.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kz.hashiroii.core.data.database.entity.HabitEntity

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteById(id: Long)
}