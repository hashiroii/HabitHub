package kz.hashiroii.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val colorHex: String,
    val goalCount: Int,
    val createdAt: Long,
)