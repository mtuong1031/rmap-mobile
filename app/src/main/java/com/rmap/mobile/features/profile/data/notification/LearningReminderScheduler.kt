package com.rmap.mobile.features.profile.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import java.util.Calendar

class LearningReminderScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun applyPreferences(preferences: NotificationPreferences) {
        cancel()
        if (!preferences.areNotificationsEnabled) return

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerAt(preferences),
            repeatInterval(preferences.reminderFrequency),
            reminderPendingIntent()
        )
    }

    fun cancel() {
        alarmManager.cancel(reminderPendingIntent())
    }

    private fun nextTriggerAt(preferences: NotificationPreferences): Long {
        val now = System.currentTimeMillis()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, preferences.reminderHour)
            set(Calendar.MINUTE, preferences.reminderMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (trigger.timeInMillis <= now) {
            trigger.add(
                Calendar.DATE,
                if (preferences.reminderFrequency == NotificationReminderFrequency.Weekly) {
                    DAYS_PER_WEEK
                } else {
                    1
                }
            )
        }

        return trigger.timeInMillis
    }

    private fun repeatInterval(frequency: NotificationReminderFrequency): Long {
        return if (frequency == NotificationReminderFrequency.Weekly) {
            AlarmManager.INTERVAL_DAY * DAYS_PER_WEEK
        } else {
            AlarmManager.INTERVAL_DAY
        }
    }

    private fun reminderPendingIntent(): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java).apply {
            action = LearningReminderReceiver.ACTION_SHOW_LEARNING_REMINDER
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_LEARNING_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        const val DAYS_PER_WEEK = 7
        const val REQUEST_CODE_LEARNING_REMINDER = 4101
    }
}
