package kz.hashiroii.core.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.model.TodayCompletionSummary
import kz.hashiroii.core.domain.repository.HabitRepository

class GetTodayCompletionSummaryUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }

    operator fun invoke(): Flow<TodayCompletionSummary> =
        combine(
            repository.observeHabits(),
            repository.observeAllCompletions(),
        ) { habits, completions ->
            val todayStart = epochDay(System.currentTimeMillis()) * MILLIS_PER_DAY
            val completedHabitIds = completions
                .filter { it.completedAt >= todayStart }
                .map { it.habitId }
                .toSet()
            TodayCompletionSummary(
                completedHabits = completedHabitIds.size,
                totalHabits = habits.size,
                overallStreakDays = computeOverallStreak(completions),
            )
        }

    private fun computeOverallStreak(completions: List<HabitCompletion>): Int {
        val activeDays = completions.map { epochDay(it.completedAt) }.toSet()
        val today = epochDay(System.currentTimeMillis())
        val startDay = when {
            today in activeDays -> today
            (today - 1) in activeDays -> today - 1
            else -> return 0
        }
        var streak = 0
        var day = startDay
        while (day in activeDays) { streak++; day-- }
        return streak
    }

    private fun epochDay(millis: Long): Long = millis / MILLIS_PER_DAY
}