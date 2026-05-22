package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class NotificationSettingsEvent {
    data object ShowNotificationPermissionRequired : NotificationSettingsEvent()
}
