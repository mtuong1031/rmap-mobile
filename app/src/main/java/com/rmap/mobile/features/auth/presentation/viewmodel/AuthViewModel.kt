package com.rmap.mobile.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.usecase.LoginUseCase
import com.rmap.mobile.features.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase = RMapAppGraph.loginUseCase,
    private val registerUseCase: RegisterUseCase = RMapAppGraph.registerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onToggleMode() {
        _uiState.update { state ->
            state.copy(
                mode = if (state.mode == AuthMode.Login) AuthMode.Register else AuthMode.Login,
                errorMessage = null,
                isPasswordVisible = false
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (state.mode == AuthMode.Login) {
                loginUseCase(
                    email = state.email,
                    password = state.password
                )
            } else {
                registerUseCase(
                    email = state.email,
                    password = state.password,
                    fullName = state.fullName
                )
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to sign in. Please try again."
                        )
                    }
                }
        }
    }
}
