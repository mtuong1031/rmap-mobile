package com.rmap.mobile.features.profile.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val scheduler = LearningReminderScheduler(context.applicationContext)
                val repository = SharedPreferencesNotificationSettingsRepository(
                    context = context.applicationContext,
                    scheduler = scheduler
                )
                scheduler.applyPreferences(repository.preferences.first())
            } finally {
                pendingResult.finish()
            }
        }
    }
}
