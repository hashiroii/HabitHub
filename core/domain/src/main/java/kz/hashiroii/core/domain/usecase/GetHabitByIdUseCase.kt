package kz.hashiroii.core.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.hashiroii.core.domain.model.HabitWithStreak

class GetHabitByIdUseCase @Inject constructor(
    private val getHabitsWithStreaks: GetHabitsWithStreaksUseCase,
) {
    operator fun invoke(habitId: Long): Flow<HabitWithStreak?> =
        getHabitsWithStreaks().map { list -> list.firstOrNull { it.habit.id == habitId } }
}