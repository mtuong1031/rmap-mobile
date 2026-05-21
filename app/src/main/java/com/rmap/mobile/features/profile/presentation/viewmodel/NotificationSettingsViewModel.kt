package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    fun onAllowNotificationsChange(isAllowed: Boolean) {
        _uiState.update { it.copy(allowNotifications = isAllowed) }
    }

    fun onReminderTimeSelected(reminderTime: String) {
        _uiState.update { it.copy(reminderTime = reminderTime) }
    }

    fun onReminderFrequencySelected(reminderFrequency: ReminderFrequency) {
        _uiState.update { it.copy(reminderFrequency = reminderFrequency) }
    }
}
