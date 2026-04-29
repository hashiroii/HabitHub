package kz.hashiroii.feature.reminders

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val CHANNEL_ID = "habithub_reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        if (habitId == -1L) return

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("habithub://feature_habit/edit/$habitId"),
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Time to build your habit!")
            .setContentText("Tap to log your progress.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(reminderId, notification)
    }
}