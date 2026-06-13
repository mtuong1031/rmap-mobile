package com.rmap.mobile.features.profile.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rmap.mobile.MainActivity
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import java.util.Calendar

class LearningReminderScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun applyPreferences(preferences: NotificationPreferences) {
        cancelRecurringReminder()
        cancelSnoozeReminder()
        if (!preferences.canScheduleLearningReminders) return

        scheduleOneShotReminder(
            triggerAtMillis = nextRecurringTriggerAt(preferences),
            pendingIntent = recurringReminderPendingIntent()
        )

        preferences.snoozeReminderAtMillis
            ?.takeIf { it > System.currentTimeMillis() }
            ?.let { snoozeAtMillis ->
                scheduleOneShotReminder(
                    snoozeAtMillis,
                    snoozeReminderPendingIntent()
                )
            }
    }

    fun cancel() {
        cancelRecurringReminder()
        cancelSnoozeReminder()
    }

    fun snoozeTonightTriggerAt(nowMillis: Long = System.currentTimeMillis()): Long {
        val trigger = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, SNOOZE_TONIGHT_HOUR)
            set(Calendar.MINUTE, SNOOZE_TONIGHT_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (trigger.timeInMillis <= nowMillis) {
            trigger.add(Calendar.DATE, 1)
        }

        return trigger.timeInMillis
    }

    fun scheduleTestReminder(delayMillis: Long = TEST_REMINDER_DELAY_MILLIS) {
        scheduleOneShotReminder(
            triggerAtMillis = System.currentTimeMillis() + delayMillis,
            pendingIntent = testReminderPendingIntent()
        )
    }

    private fun cancelRecurringReminder() {
        alarmManager?.cancel(recurringReminderPendingIntent())
    }

    private fun cancelSnoozeReminder() {
        alarmManager?.cancel(snoozeReminderPendingIntent())
    }

    private fun nextRecurringTriggerAt(preferences: NotificationPreferences): Long {
        val now = System.currentTimeMillis()

        return (0..DAYS_PER_WEEK).asSequence()
            .map { dayOffset ->
                Calendar.getInstance().apply {
                    timeInMillis = now
                    add(Calendar.DATE, dayOffset)
                    set(Calendar.HOUR_OF_DAY, preferences.reminderHour)
                    set(Calendar.MINUTE, preferences.reminderMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }
            .first { trigger -> trigger.timeInMillis > now }
            .timeInMillis
    }

    private fun scheduleOneShotReminder(
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        if (canScheduleExactAlarm()) {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager?.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun canScheduleExactAlarm(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager?.canScheduleExactAlarms() == true
    }

    private fun recurringReminderPendingIntent(): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java).apply {
            action = LearningReminderReceiver.ACTION_SHOW_LEARNING_REMINDER
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_LEARNING_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun snoozeReminderPendingIntent(): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java).apply {
            action = LearningReminderReceiver.ACTION_SHOW_SNOOZED_LEARNING_REMINDER
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_SNOOZE_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun testReminderPendingIntent(): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java).apply {
            action = LearningReminderReceiver.ACTION_SHOW_TEST_LEARNING_REMINDER
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TEST_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        const val DAYS_PER_WEEK = 7
        const val REQUEST_CODE_LEARNING_REMINDER = 4101
        const val REQUEST_CODE_SNOOZE_REMINDER = 4102
        const val REQUEST_CODE_TEST_REMINDER = 4103
        const val SNOOZE_TONIGHT_HOUR = 20
        const val SNOOZE_TONIGHT_MINUTE = 30
        const val TEST_REMINDER_DELAY_MILLIS = 10_000L
    }
}
