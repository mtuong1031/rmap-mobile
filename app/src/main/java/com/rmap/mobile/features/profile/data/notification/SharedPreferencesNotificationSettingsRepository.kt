package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesNotificationSettingsRepository(
    context: Context,
    private val scheduler: LearningReminderScheduler
) : NotificationSettingsRepository {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _preferences = MutableStateFlow(readPreferences())

    override val preferences: Flow<NotificationPreferences> = _preferences.asStateFlow()

    init {
        scheduler.applyPreferences(_preferences.value)
    }

    override suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, isEnabled)
            .apply()
        updateScheduler()
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()
        updateScheduler()
    }

    override suspend fun setReminderFrequency(frequency: NotificationReminderFrequency) {
        sharedPreferences.edit()
            .putString(KEY_REMINDER_FREQUENCY, frequency.name)
            .apply()
        updateScheduler()
    }

    private fun updateScheduler() {
        val updatedPreferences = readPreferences()
        _preferences.value = updatedPreferences
        scheduler.applyPreferences(updatedPreferences)
    }

    private fun readPreferences(): NotificationPreferences {
        return NotificationPreferences(
            areNotificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false),
            reminderHour = sharedPreferences.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR),
            reminderMinute = sharedPreferences.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE),
            reminderFrequency = readReminderFrequency()
        )
    }

    private fun readReminderFrequency(): NotificationReminderFrequency {
        val storedValue = sharedPreferences.getString(
            KEY_REMINDER_FREQUENCY,
            NotificationReminderFrequency.Daily.name
        )

        return NotificationReminderFrequency.entries.firstOrNull { it.name == storedValue }
            ?: NotificationReminderFrequency.Daily
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_notification_settings"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_REMINDER_HOUR = "reminder_hour"
        const val KEY_REMINDER_MINUTE = "reminder_minute"
        const val KEY_REMINDER_FREQUENCY = "reminder_frequency"
        const val DEFAULT_REMINDER_HOUR = 20
        const val DEFAULT_REMINDER_MINUTE = 30
    }
}
