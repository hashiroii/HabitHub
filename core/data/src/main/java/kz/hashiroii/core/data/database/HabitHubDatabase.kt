package kz.hashiroii.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kz.hashiroii.core.data.database.dao.HabitCompletionDao
import kz.hashiroii.core.data.database.dao.HabitDao
import kz.hashiroii.core.data.database.entity.HabitCompletionEntity
import kz.hashiroii.core.data.database.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class HabitHubDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}