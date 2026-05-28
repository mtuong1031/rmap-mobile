package com.rmap.mobile.features.auth.presentation.viewmodel

sealed class AuthEvent {
    data object NavigateToHome : AuthEvent()
}
