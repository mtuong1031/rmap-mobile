package com.rmap.mobile.features.profile.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPreferencesTest {
    @Test
    fun `default preferences keep notifications off but feature toggles ready`() {
        val preferences = NotificationPreferences()

        assertFalse(preferences.areNotificationsEnabled)
        assertTrue(preferences.learningRemindersEnabled)
        assertTrue(preferences.streakProtectionEnabled)
        assertTrue(preferences.aiRoadmapUpdatesEnabled)
        assertFalse(preferences.canScheduleLearningReminders)
        assertFalse(preferences.canSendAiRoadmapUpdates)
        assertTrue(preferences.reminderDays.containsAll(NotificationReminderDay.entries))
    }

    @Test
    fun `learning reminders require master switch learning switch and days`() {
        assertTrue(
            NotificationPreferences(
                areNotificationsEnabled = true,
                learningRemindersEnabled = true
            ).canScheduleLearningReminders
        )
        assertFalse(
            NotificationPreferences(
                areNotificationsEnabled = true,
                learningRemindersEnabled = false
            ).canScheduleLearningReminders
        )
        assertFalse(
            NotificationPreferences(
                areNotificationsEnabled = true,
                reminderDays = emptySet()
            ).canScheduleLearningReminders
        )
    }

    @Test
    fun `ai roadmap updates require master switch and ai switch`() {
        assertTrue(
            NotificationPreferences(
                areNotificationsEnabled = true,
                aiRoadmapUpdatesEnabled = true
            ).canSendAiRoadmapUpdates
        )
        assertFalse(
            NotificationPreferences(
                areNotificationsEnabled = true,
                aiRoadmapUpdatesEnabled = false
            ).canSendAiRoadmapUpdates
        )
    }
}
