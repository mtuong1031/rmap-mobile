package com.rmap.mobile.features.profile.domain.model

enum class NotificationReminderFrequency {
    Daily,
    Weekly
}

enum class NotificationReminderDay {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}

data class NotificationPreferences(
    val areNotificationsEnabled: Boolean = false,
    val learningRemindersEnabled: Boolean = true,
    val streakProtectionEnabled: Boolean = true,
    val aiRoadmapUpdatesEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 30,
    val reminderFrequency: NotificationReminderFrequency = NotificationReminderFrequency.Daily,
    val reminderDays: Set<NotificationReminderDay> = NotificationReminderDay.entries.toSet(),
    val snoozeReminderAtMillis: Long? = null
) {
    val canScheduleLearningReminders: Boolean
        get() = areNotificationsEnabled && learningRemindersEnabled && reminderDays.isNotEmpty()

    val canSendAiRoadmapUpdates: Boolean
        get() = areNotificationsEnabled && aiRoadmapUpdatesEnabled
}
