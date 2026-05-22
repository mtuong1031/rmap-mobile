package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    val preferences: Flow<NotificationPreferences>

    suspend fun setNotificationsEnabled(isEnabled: Boolean)

    suspend fun setReminderTime(hour: Int, minute: Int)

    suspend fun setReminderFrequency(frequency: NotificationReminderFrequency)
}
