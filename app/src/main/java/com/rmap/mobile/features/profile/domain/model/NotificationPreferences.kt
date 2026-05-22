package com.rmap.mobile.features.profile.domain.model

enum class NotificationReminderFrequency {
    Daily,
    Weekly
}

data class NotificationPreferences(
    val areNotificationsEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 30,
    val reminderFrequency: NotificationReminderFrequency = NotificationReminderFrequency.Daily
)
