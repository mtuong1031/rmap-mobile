package com.rmap.mobile.features.profile.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LearningReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifier = LearningNotificationNotifier(context)

        when (intent.action) {
            ACTION_MARK_STREAK -> {
                val result = LearningStreakStore(context).markToday()
                notifier.showStreakCelebration(result)
            }

            else -> notifier.showLearningReminder()
        }
    }

    companion object {
        const val ACTION_SHOW_LEARNING_REMINDER = "com.rmap.mobile.action.SHOW_LEARNING_REMINDER"
        const val ACTION_MARK_STREAK = "com.rmap.mobile.action.MARK_STREAK"
    }
}
