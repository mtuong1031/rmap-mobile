package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderDay
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    val preferences: Flow<NotificationPreferences>

    suspend fun setNotificationsEnabled(isEnabled: Boolean)

    suspend fun setLearningRemindersEnabled(isEnabled: Boolean)

    suspend fun setStreakProtectionEnabled(isEnabled: Boolean)

    suspend fun setAiRoadmapUpdatesEnabled(isEnabled: Boolean)

    suspend fun setReminderTime(hour: Int, minute: Int)

    suspend fun setReminderFrequency(frequency: NotificationReminderFrequency)

    suspend fun setReminderDays(days: Set<NotificationReminderDay>)

    suspend fun scheduleSnoozeTonight()

    suspend fun clearSnoozeReminder()
}
