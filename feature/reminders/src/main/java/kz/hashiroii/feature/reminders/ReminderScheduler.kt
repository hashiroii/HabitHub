package kz.hashiroii.feature.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(habitId: Long, reminderId: Int, hour: Int, minute: Int, days: Set<Int> = ReminderTime.ALL_DAYS) {
        cancelAll(reminderId)
        val effectiveDays = if (days.isEmpty()) ReminderTime.ALL_DAYS else days
        for (day in effectiveDays) {
            val alarmId = alarmIdForDay(reminderId, day)
            val triggerAt = nextOccurrenceMillis(day, hour, minute)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                buildIntent(habitId, reminderId),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        }
    }

    fun cancelAll(reminderId: Int) {
        cancel(reminderId)
        for (day in 0..6) {
            cancel(alarmIdForDay(reminderId, day))
        }
    }

    fun cancel(alarmId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun nextOccurrenceMillis(day: Int, hour: Int, minute: Int): Long {
        val calDayOfWeek = when (day) {
            0 -> Calendar.MONDAY
            1 -> Calendar.TUESDAY
            2 -> Calendar.WEDNESDAY
            3 -> Calendar.THURSDAY
            4 -> Calendar.FRIDAY
            5 -> Calendar.SATURDAY
            else -> Calendar.SUNDAY
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        while (cal.get(Calendar.DAY_OF_WEEK) != calDayOfWeek || cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun alarmIdForDay(reminderId: Int, day: Int): Int =
        (reminderId % 300_000_000) * 7 + day

    private fun buildIntent(habitId: Long, reminderId: Int) =
        Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_HABIT_ID, habitId)
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
        }
}
