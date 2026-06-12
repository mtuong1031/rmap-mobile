package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderDay
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

    override suspend fun setLearningRemindersEnabled(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_LEARNING_REMINDERS_ENABLED, isEnabled)
            .apply()
        updateScheduler()
    }

    override suspend fun setStreakProtectionEnabled(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_STREAK_PROTECTION_ENABLED, isEnabled)
            .apply()
        updateScheduler()
    }

    override suspend fun setAiRoadmapUpdatesEnabled(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_AI_ROADMAP_UPDATES_ENABLED, isEnabled)
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

    override suspend fun setReminderDays(days: Set<NotificationReminderDay>) {
        sharedPreferences.edit()
            .putString(KEY_REMINDER_DAYS, days.toStoredReminderDays())
            .apply()
        updateScheduler()
    }

    override suspend fun scheduleSnoozeTonight() {
        val snoozeAtMillis = scheduler.snoozeTonightTriggerAt()
        sharedPreferences.edit()
            .putLong(KEY_SNOOZE_REMINDER_AT_MILLIS, snoozeAtMillis)
            .apply()
        updateScheduler()
    }

    override suspend fun clearSnoozeReminder() {
        sharedPreferences.edit()
            .remove(KEY_SNOOZE_REMINDER_AT_MILLIS)
            .apply()
        updateScheduler()
    }

    private fun updateScheduler() {
        val updatedPreferences = readPreferences()
        _preferences.value = updatedPreferences
        scheduler.applyPreferences(updatedPreferences)
    }

    private fun readPreferences(): NotificationPreferences {
        return readStoredPreferences(sharedPreferences)
    }

    private fun readReminderFrequency(): NotificationReminderFrequency {
        return readStoredReminderFrequency(sharedPreferences)
    }

    private fun readReminderDays(): Set<NotificationReminderDay> {
        return readStoredReminderDays(sharedPreferences)
    }

    private fun readSnoozeReminderAtMillis(): Long? {
        return readStoredSnoozeReminderAtMillis(sharedPreferences)
    }

    private fun Set<NotificationReminderDay>.toStoredReminderDays(): String {
        return sortedBy { it.ordinal }.joinToString(REMINDER_DAYS_SEPARATOR) { it.name }
    }

    companion object {
        fun readStoredPreferences(context: Context): NotificationPreferences {
            return readStoredPreferences(
                context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            )
        }

        fun clearStoredSnoozeReminder(context: Context) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_SNOOZE_REMINDER_AT_MILLIS)
                .apply()
        }

        private fun readStoredPreferences(
            sharedPreferences: android.content.SharedPreferences
        ): NotificationPreferences {
        return NotificationPreferences(
            areNotificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false),
            learningRemindersEnabled = sharedPreferences.getBoolean(KEY_LEARNING_REMINDERS_ENABLED, true),
            streakProtectionEnabled = sharedPreferences.getBoolean(KEY_STREAK_PROTECTION_ENABLED, true),
            aiRoadmapUpdatesEnabled = sharedPreferences.getBoolean(KEY_AI_ROADMAP_UPDATES_ENABLED, true),
            reminderHour = sharedPreferences.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR),
            reminderMinute = sharedPreferences.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE),
                reminderFrequency = readStoredReminderFrequency(sharedPreferences),
                reminderDays = readStoredReminderDays(sharedPreferences),
                snoozeReminderAtMillis = readStoredSnoozeReminderAtMillis(sharedPreferences)
        )
    }

        private fun readStoredReminderFrequency(
            sharedPreferences: android.content.SharedPreferences
        ): NotificationReminderFrequency {
        val storedValue = sharedPreferences.getString(
            KEY_REMINDER_FREQUENCY,
            NotificationReminderFrequency.Daily.name
        )

        return NotificationReminderFrequency.entries.firstOrNull { it.name == storedValue }
            ?: NotificationReminderFrequency.Daily
    }

        private fun readStoredReminderDays(
            sharedPreferences: android.content.SharedPreferences
        ): Set<NotificationReminderDay> {
        val storedValue = sharedPreferences.getString(KEY_REMINDER_DAYS, null)
        if (storedValue.isNullOrBlank()) return NotificationReminderDay.entries.toSet()

        return storedValue
            .split(REMINDER_DAYS_SEPARATOR)
            .mapNotNull { storedDay ->
                NotificationReminderDay.entries.firstOrNull { it.name == storedDay }
            }
            .toSet()
            .ifEmpty { NotificationReminderDay.entries.toSet() }
    }

        private fun readStoredSnoozeReminderAtMillis(
            sharedPreferences: android.content.SharedPreferences
        ): Long? {
        val storedValue = sharedPreferences.getLong(KEY_SNOOZE_REMINDER_AT_MILLIS, NO_SNOOZE_REMINDER)
        return storedValue.takeIf { it > System.currentTimeMillis() }
    }

        const val PREFERENCES_NAME = "rmap_notification_settings"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LEARNING_REMINDERS_ENABLED = "learning_reminders_enabled"
        private const val KEY_STREAK_PROTECTION_ENABLED = "streak_protection_enabled"
        private const val KEY_AI_ROADMAP_UPDATES_ENABLED = "ai_roadmap_updates_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_REMINDER_FREQUENCY = "reminder_frequency"
        private const val KEY_REMINDER_DAYS = "reminder_days"
        private const val KEY_SNOOZE_REMINDER_AT_MILLIS = "snooze_reminder_at_millis"
        private const val REMINDER_DAYS_SEPARATOR = ","
        private const val NO_SNOOZE_REMINDER = -1L
        private const val DEFAULT_REMINDER_HOUR = 20
        private const val DEFAULT_REMINDER_MINUTE = 30
    }
}
