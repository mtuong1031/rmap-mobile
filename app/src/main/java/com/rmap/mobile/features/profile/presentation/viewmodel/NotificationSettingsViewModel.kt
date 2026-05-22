package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.profile.domain.model.NotificationPreferences
import com.rmap.mobile.features.profile.domain.model.NotificationReminderFrequency
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val notificationSettingsRepository: NotificationSettingsRepository =
        RMapAppGraph.notificationSettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotificationSettingsEvent>()
    val events: SharedFlow<NotificationSettingsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            notificationSettingsRepository.preferences.collect { preferences ->
                _uiState.update { currentState ->
                    preferences.toUiState(
                        isNotificationPermissionGranted = currentState.isNotificationPermissionGranted
                    )
                }
            }
        }
    }

    fun onNotificationPermissionStateChanged(isGranted: Boolean) {
        val shouldDisableNotifications = !isGranted && _uiState.value.allowNotifications
        _uiState.update { it.copy(isNotificationPermissionGranted = isGranted) }

        if (shouldDisableNotifications) {
            viewModelScope.launch {
                notificationSettingsRepository.setNotificationsEnabled(false)
            }
        }
    }

    fun onNotificationPermissionDenied() {
        viewModelScope.launch {
            notificationSettingsRepository.setNotificationsEnabled(false)
            _events.emit(NotificationSettingsEvent.ShowNotificationPermissionRequired)
        }
    }

    fun onAllowNotificationsChange(isAllowed: Boolean) {
        viewModelScope.launch {
            notificationSettingsRepository.setNotificationsEnabled(isAllowed)
        }
    }

    fun onReminderTimeSelected(reminderTime: String) {
        val (hour, minute) = reminderTime.toHourMinute() ?: return
        viewModelScope.launch {
            notificationSettingsRepository.setReminderTime(hour, minute)
        }
    }

    fun onReminderFrequencySelected(reminderFrequency: ReminderFrequency) {
        viewModelScope.launch {
            notificationSettingsRepository.setReminderFrequency(reminderFrequency.toDomain())
        }
    }

    private fun NotificationPreferences.toUiState(
        isNotificationPermissionGranted: Boolean
    ): NotificationSettingsUiState {
        return NotificationSettingsUiState(
            allowNotifications = areNotificationsEnabled,
            reminderTime = "%02d:%02d".format(reminderHour, reminderMinute),
            reminderFrequency = reminderFrequency.toPresentation(),
            isNotificationPermissionGranted = isNotificationPermissionGranted
        )
    }

    private fun ReminderFrequency.toDomain(): NotificationReminderFrequency {
        return when (this) {
            ReminderFrequency.Daily -> NotificationReminderFrequency.Daily
            ReminderFrequency.Weekly -> NotificationReminderFrequency.Weekly
        }
    }

    private fun NotificationReminderFrequency.toPresentation(): ReminderFrequency {
        return when (this) {
            NotificationReminderFrequency.Daily -> ReminderFrequency.Daily
            NotificationReminderFrequency.Weekly -> ReminderFrequency.Weekly
        }
    }

    private fun String.toHourMinute(): Pair<Int, Int>? {
        val parts = split(":")
        if (parts.size != 2) return null

        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null

        return hour to minute
    }
}
