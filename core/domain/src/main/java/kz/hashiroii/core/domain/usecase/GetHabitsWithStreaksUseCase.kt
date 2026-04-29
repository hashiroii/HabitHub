package kz.hashiroii.core.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kz.hashiroii.core.domain.model.DayActivity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.domain.repository.HabitRepository

class GetHabitsWithStreaksUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    companion object {
        private const val GRID_WEEKS = 52
        private const val GRID_DAYS = GRID_WEEKS * 7L
        private const val MILLIS_PER_DAY = 86_400_000L
    }

    operator fun invoke(): Flow<List<HabitWithStreak>> =
        combine(
            repository.observeHabits(),
            repository.observeAllCompletions(),
        ) { habits, completions ->
            val todayEpochDay = epochDay(System.currentTimeMillis())
            val gridStartDay = todayEpochDay - GRID_DAYS + 1
            habits.map { habit ->
                val habitCompletions = completions.filter { it.habitId == habit.id }
                HabitWithStreak(
                    habit = habit,
                    streakDays = computeStreak(habitCompletions),
                    todayCompletionCount = countToday(habitCompletions, todayEpochDay),
                    activityGrid = buildActivityGrid(habit, habitCompletions, gridStartDay, todayEpochDay),
                )
            }
        }

    private fun computeStreak(completions: List<HabitCompletion>): Int {
        val completedDays = completions.map { epochDay(it.completedAt) }.toSet()
        val today = epochDay(System.currentTimeMillis())
        val startDay = when {
            today in completedDays -> today
            (today - 1) in completedDays -> today - 1
            else -> return 0
        }
        var streak = 0
        var day = startDay
        while (day in completedDays) { streak++; day-- }
        return streak
    }

    private fun countToday(completions: List<HabitCompletion>, todayEpochDay: Long): Int {
        val todayStart = todayEpochDay * MILLIS_PER_DAY
        return completions.count { it.completedAt >= todayStart }
    }

    private fun buildActivityGrid(
        habit: Habit,
        completions: List<HabitCompletion>,
        fromDay: Long,
        toDay: Long,
    ): List<DayActivity> {
        val countByDay = completions
            .groupBy { epochDay(it.completedAt) }
            .mapValues { it.value.size }
        return (fromDay..toDay).map { day ->
            DayActivity(
                dateEpochDay = day,
                completionCount = countByDay[day] ?: 0,
                goalCount = habit.goalCount,
            )
        }
    }

    private fun epochDay(millis: Long): Long = millis / MILLIS_PER_DAY
}