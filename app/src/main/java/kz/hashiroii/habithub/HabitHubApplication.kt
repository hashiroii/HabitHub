package kz.hashiroii.habithub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import kz.hashiroii.feature.reminders.ReminderReceiver

@HiltAndroidApp
class HabitHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channel = NotificationChannel(
            ReminderReceiver.CHANNEL_ID,
            "Habit Reminders",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Daily reminders to complete your habits"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}