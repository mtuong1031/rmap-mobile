package com.rmap.mobile.features.profile.presentation.viewmodel

enum class ReminderFrequency {
    Daily,
    Weekly
}

enum class ReminderDay {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

data class NotificationSettingsUiState(
    val allowNotifications: Boolean = false,
    val learningRemindersEnabled: Boolean = true,
    val streakProtectionEnabled: Boolean = true,
    val aiRoadmapUpdatesEnabled: Boolean = true,
    val reminderTime: String = "20:30",
    val reminderFrequency: ReminderFrequency = ReminderFrequency.Daily,
    val reminderDays: Set<ReminderDay> = ReminderDay.entries.toSet(),
    val isNotificationPermissionGranted: Boolean = true
) {
    val areReminderControlsEnabled: Boolean
        get() = allowNotifications && learningRemindersEnabled
}
