package kz.hashiroii.core.domain.usecase

import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kz.hashiroii.core.domain.model.DayActivity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitCompletion
import kz.hashiroii.core.domain.model.HabitWithStreak
import kz.hashiroii.core.domain.repository.HabitRepository

class GetHabitsWithStreaksUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L
    }

    operator fun invoke(): Flow<List<HabitWithStreak>> =
        combine(
            repository.observeHabits(),
            repository.observeAllCompletions(),
        ) { habits, completions ->
            val today = LocalDate.now()
            val todayEpochDay = today.toEpochDay()
            val yearStart = LocalDate.of(today.year, 1, 1).toEpochDay()
            val yearEnd = LocalDate.of(today.year, 12, 31).toEpochDay()
            habits.map { habit ->
                val habitCompletions = completions.filter { it.habitId == habit.id }
                HabitWithStreak(
                    habit = habit,
                    streakDays = computeStreak(habitCompletions, todayEpochDay),
                    todayCompletionCount = countToday(habitCompletions, todayEpochDay),
                    activityGrid = buildActivityGrid(habit, habitCompletions, yearStart, yearEnd),
                )
            }
        }.flowOn(Dispatchers.Default)

    private fun computeStreak(completions: List<HabitCompletion>, today: Long): Int {
        val completedDays = completions.map { epochDay(it.completedAt) }.toSet()
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