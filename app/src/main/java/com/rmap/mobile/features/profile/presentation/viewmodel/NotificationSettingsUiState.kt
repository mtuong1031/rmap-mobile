package com.rmap.mobile.features.profile.presentation.viewmodel

enum class ReminderFrequency {
    Daily,
    Weekly
}

data class NotificationSettingsUiState(
    val allowNotifications: Boolean = false,
    val reminderTime: String = "20:30",
    val reminderFrequency: ReminderFrequency = ReminderFrequency.Daily,
    val isNotificationPermissionGranted: Boolean = true
)
