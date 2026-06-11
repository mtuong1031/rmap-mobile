package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class PrivacySecurityEvent {
    data object PasswordChanged : PrivacySecurityEvent()
}
