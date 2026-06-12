package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderDay
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `preferences are mapped to ui state`() = runTest {
        val repository = FakeNotificationSettingsRepository(
            NotificationPreferences(
                areNotificationsEnabled = true,
                learningRemindersEnabled = true,
                streakProtectionEnabled = false,
                aiRoadmapUpdatesEnabled = true,
                reminderHour = 8,
                reminderMinute = 15,
                reminderFrequency = NotificationReminderFrequency.Weekly,
                reminderDays = setOf(NotificationReminderDay.Monday, NotificationReminderDay.Friday)
            )
        )

        val viewModel = NotificationSettingsViewModel(repository)
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.allowNotifications)
        assertTrue(state.learningRemindersEnabled)
        assertFalse(state.streakProtectionEnabled)
        assertTrue(state.aiRoadmapUpdatesEnabled)
        assertEquals("08:15", state.reminderTime)
        assertEquals(ReminderFrequency.Weekly, state.reminderFrequency)
        assertEquals(setOf(ReminderDay.Monday, ReminderDay.Friday), state.reminderDays)
    }

    @Test
    fun `notification permission denial disables notifications and emits event`() = runTest {
        val repository = FakeNotificationSettingsRepository(
            NotificationPreferences(areNotificationsEnabled = true)
        )
        val viewModel = NotificationSettingsViewModel(repository)
        val event = async { viewModel.events.first() }
        runCurrent()

        viewModel.onNotificationPermissionDenied()
        runCurrent()

        assertFalse(repository.currentPreferences.areNotificationsEnabled)
        assertEquals(NotificationSettingsEvent.ShowNotificationPermissionRequired, event.await())
    }

    @Test
    fun `toggle handlers update repository preferences`() = runTest {
        val repository = FakeNotificationSettingsRepository()
        val viewModel = NotificationSettingsViewModel(repository)
        runCurrent()

        viewModel.onAllowNotificationsChange(true)
        viewModel.onLearningRemindersEnabledChange(false)
        viewModel.onStreakProtectionEnabledChange(false)
        viewModel.onAiRoadmapUpdatesEnabledChange(false)
        viewModel.onReminderFrequencySelected(ReminderFrequency.Weekly)
        viewModel.onReminderDayToggled(ReminderDay.Sunday)
        runCurrent()

        val preferences = repository.currentPreferences
        assertTrue(preferences.areNotificationsEnabled)
        assertFalse(preferences.learningRemindersEnabled)
        assertFalse(preferences.streakProtectionEnabled)
        assertFalse(preferences.aiRoadmapUpdatesEnabled)
        assertEquals(NotificationReminderFrequency.Weekly, preferences.reminderFrequency)
        assertFalse(NotificationReminderDay.Sunday in preferences.reminderDays)
    }

    @Test
    fun `custom reminder time is normalized in ui state`() = runTest {
        val repository = FakeNotificationSettingsRepository()
        val viewModel = NotificationSettingsViewModel(repository)
        runCurrent()

        viewModel.onReminderTimeSelected("8:05")
        runCurrent()

        assertEquals(8, repository.currentPreferences.reminderHour)
        assertEquals(5, repository.currentPreferences.reminderMinute)
        assertEquals("08:05", viewModel.uiState.value.reminderTime)
    }

    @Test
    fun `last reminder day cannot be removed`() = runTest {
        val repository = FakeNotificationSettingsRepository(
            NotificationPreferences(reminderDays = setOf(NotificationReminderDay.Monday))
        )
        val viewModel = NotificationSettingsViewModel(repository)
        runCurrent()

        viewModel.onReminderDayToggled(ReminderDay.Monday)
        runCurrent()

        assertEquals(setOf(NotificationReminderDay.Monday), repository.currentPreferences.reminderDays)
    }

    private class FakeNotificationSettingsRepository(
        initialPreferences: NotificationPreferences = NotificationPreferences()
    ) : NotificationSettingsRepository {
        private val preferencesState = MutableStateFlow(initialPreferences)
        override val preferences: Flow<NotificationPreferences> = preferencesState

        val currentPreferences: NotificationPreferences
            get() = preferencesState.value

        override suspend fun setNotificationsEnabled(isEnabled: Boolean) {
            preferencesState.update { it.copy(areNotificationsEnabled = isEnabled) }
        }

        override suspend fun setLearningRemindersEnabled(isEnabled: Boolean) {
            preferencesState.update { it.copy(learningRemindersEnabled = isEnabled) }
        }

        override suspend fun setStreakProtectionEnabled(isEnabled: Boolean) {
            preferencesState.update { it.copy(streakProtectionEnabled = isEnabled) }
        }

        override suspend fun setAiRoadmapUpdatesEnabled(isEnabled: Boolean) {
            preferencesState.update { it.copy(aiRoadmapUpdatesEnabled = isEnabled) }
        }

        override suspend fun setReminderTime(hour: Int, minute: Int) {
            preferencesState.update { it.copy(reminderHour = hour, reminderMinute = minute) }
        }

        override suspend fun setReminderFrequency(frequency: NotificationReminderFrequency) {
            preferencesState.update { it.copy(reminderFrequency = frequency) }
        }

        override suspend fun setReminderDays(days: Set<NotificationReminderDay>) {
            preferencesState.update { it.copy(reminderDays = days) }
        }

        override suspend fun scheduleSnoozeTonight() {
            preferencesState.update { it.copy(snoozeReminderAtMillis = SNOOZE_AT_MILLIS) }
        }

        override suspend fun clearSnoozeReminder() {
            preferencesState.update { it.copy(snoozeReminderAtMillis = null) }
        }

        private companion object {
            const val SNOOZE_AT_MILLIS = 1_000L
        }
    }
}
