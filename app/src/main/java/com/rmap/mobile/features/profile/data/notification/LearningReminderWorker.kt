package com.rmap.mobile.features.profile.data.notification

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class LearningReminderWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val application = applicationContext as? Application ?: return Result.failure()
        val scheduler = LearningReminderScheduler(applicationContext)
        val repository = SharedPreferencesNotificationSettingsRepository(
            context = applicationContext,
            scheduler = scheduler
        )
        val reminderContextRepository = SharedPreferencesLearningReminderContextRepository(
            context = applicationContext
        )
        val notifier = LearningNotificationNotifier(application)

        when (inputData.getString(KEY_ACTION)) {
            LearningReminderReceiver.ACTION_SHOW_TEST_LEARNING_REMINDER -> {
                notifier.showLearningReminder(
                    reminderContext = reminderContextRepository.getContext()
                )
            }

            LearningReminderReceiver.ACTION_SNOOZE_TONIGHT -> {
                repository.scheduleSnoozeTonight()
            }

            LearningReminderReceiver.ACTION_SHOW_SNOOZED_LEARNING_REMINDER -> {
                val preferences = repository.preferences.first()
                repository.clearSnoozeReminder()
                if (preferences.canScheduleLearningReminders) {
                    notifier.showLearningReminder(
                        preferences = preferences,
                        reminderContext = reminderContextRepository.getContext()
                    )
                }
            }

            else -> {
                val preferences = repository.preferences.first()
                if (preferences.canScheduleLearningReminders) {
                    notifier.showLearningReminder(
                        preferences = preferences,
                        reminderContext = reminderContextRepository.getContext()
                    )
                }
                scheduler.applyPreferences(repository.preferences.first())
            }
        }

        return Result.success()
    }

    companion object {
        private const val KEY_ACTION = "action"

        fun workRequest(action: String?): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<LearningReminderWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(
                            KEY_ACTION,
                            action ?: LearningReminderReceiver.ACTION_SHOW_LEARNING_REMINDER
                        )
                        .build()
                )
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        }
    }
}
