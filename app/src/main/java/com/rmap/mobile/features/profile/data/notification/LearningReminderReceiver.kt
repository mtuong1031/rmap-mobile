package com.rmap.mobile.features.profile.data.notification

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager

class LearningReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val applicationContext = context.applicationContext
        val application = applicationContext as? Application ?: return
        val preferences = SharedPreferencesNotificationSettingsRepository.readStoredPreferences(applicationContext)
        val notifier = LearningNotificationNotifier(application)
        val reminderContextRepository = SharedPreferencesLearningReminderContextRepository(applicationContext)
        val scheduler = LearningReminderScheduler(applicationContext)

        when (intent.action) {
            ACTION_SHOW_TEST_LEARNING_REMINDER -> {
                notifier.showLearningReminder(
                    reminderContext = reminderContextRepository.getContext()
                )
            }

            ACTION_SNOOZE_TONIGHT -> {
                WorkManager.getInstance(applicationContext)
                    .enqueue(LearningReminderWorker.workRequest(intent.action))
            }

            ACTION_SHOW_SNOOZED_LEARNING_REMINDER -> {
                SharedPreferencesNotificationSettingsRepository.clearStoredSnoozeReminder(applicationContext)
                if (preferences.canScheduleLearningReminders) {
                    notifier.showLearningReminder(
                        preferences = preferences,
                        reminderContext = reminderContextRepository.getContext()
                    )
                }
            }

            else -> {
                if (preferences.canScheduleLearningReminders) {
                    notifier.showLearningReminder(
                        preferences = preferences,
                        reminderContext = reminderContextRepository.getContext()
                    )
                }
                scheduler.applyPreferences(preferences)
            }
        }
    }

    companion object {
        const val ACTION_SHOW_LEARNING_REMINDER = "com.rmap.mobile.action.SHOW_LEARNING_REMINDER"
        const val ACTION_SHOW_SNOOZED_LEARNING_REMINDER = "com.rmap.mobile.action.SHOW_SNOOZED_LEARNING_REMINDER"
        const val ACTION_SHOW_TEST_LEARNING_REMINDER = "com.rmap.mobile.action.SHOW_TEST_LEARNING_REMINDER"
        const val ACTION_SNOOZE_TONIGHT = "com.rmap.mobile.action.SNOOZE_TONIGHT"
    }
}
