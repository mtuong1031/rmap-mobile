package com.rmap.mobile.features.auth.domain.model

sealed interface AuthState {
    data object Checking : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Unauthenticated : AuthState
}
