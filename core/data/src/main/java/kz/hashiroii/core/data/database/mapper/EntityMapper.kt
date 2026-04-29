package kz.hashiroii.core.data.database.mapper

import kz.hashiroii.core.data.database.entity.HabitCompletionEntity
import kz.hashiroii.core.data.database.entity.HabitEntity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion

internal fun HabitEntity.toDomain() = Habit(
    id = id,
    name = name,
    description = description,
    iconName = iconName,
    colorHex = colorHex,
    goalCount = goalCount,
    createdAt = createdAt,
)

internal fun Habit.toEntity() = HabitEntity(
    id = id,
    name = name,
    description = description,
    iconName = iconName,
    colorHex = colorHex,
    goalCount = goalCount,
    createdAt = createdAt,
)

internal fun HabitCompletionEntity.toDomain() = HabitCompletion(
    id = id,
    habitId = habitId,
    completedAt = completedAt,
)