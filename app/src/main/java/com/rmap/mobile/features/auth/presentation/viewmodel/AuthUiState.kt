package com.rmap.mobile.features.auth.presentation.viewmodel

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isRegisterMode: Boolean
        get() = mode == AuthMode.Register
}

enum class AuthMode {
    Login,
    Register
}
