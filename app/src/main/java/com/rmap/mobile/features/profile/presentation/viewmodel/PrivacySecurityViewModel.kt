package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrivacySecurityViewModel(
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacySecurityUiState())
    val uiState: StateFlow<PrivacySecurityUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PrivacySecurityEvent>()
    val events: SharedFlow<PrivacySecurityEvent> = _events.asSharedFlow()

    fun onCurrentPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                currentPassword = value,
                currentPasswordError = value.validateCurrentPassword(),
                errorMessage = null
            )
        }
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                newPasswordError = value.validateNewPassword(),
                confirmPasswordError = if (it.confirmNewPassword.isBlank()) {
                    null
                } else {
                    it.confirmNewPassword.validateConfirmPassword(value)
                },
                errorMessage = null
            )
        }
    }

    fun onConfirmNewPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                confirmNewPassword = value,
                confirmPasswordError = value.validateConfirmPassword(it.newPassword),
                errorMessage = null
            )
        }
    }

    fun onToggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
    }

    fun onToggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onChangePasswordClick() {
        val state = _uiState.value
        val currentPasswordError = state.currentPassword.validateCurrentPassword()
        val newPasswordError = state.newPassword.validateNewPassword()
        val confirmPasswordError = state.confirmNewPassword.validateConfirmPassword(state.newPassword)

        if (currentPasswordError != null || newPasswordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    currentPasswordError = currentPasswordError,
                    newPasswordError = newPasswordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            authRepository.changePassword(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword
            ).onSuccess {
                _uiState.update {
                    PrivacySecurityUiState()
                }
                _events.emit(PrivacySecurityEvent.PasswordChanged)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: DEFAULT_CHANGE_PASSWORD_ERROR
                    )
                }
            }
        }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val DEFAULT_CHANGE_PASSWORD_ERROR = "Password change failed. Please check your current password and try again."

        fun String.validateCurrentPassword(): PrivacySecurityFieldError? {
            return if (isBlank()) PrivacySecurityFieldError.CurrentPasswordRequired else null
        }

        fun String.validateNewPassword(): PrivacySecurityFieldError? {
            return if (length < MIN_PASSWORD_LENGTH) PrivacySecurityFieldError.NewPasswordTooShort else null
        }

        fun String.validateConfirmPassword(newPassword: String): PrivacySecurityFieldError? {
            return when {
                isBlank() -> PrivacySecurityFieldError.ConfirmPasswordRequired
                this != newPassword -> PrivacySecurityFieldError.PasswordsDoNotMatch
                else -> null
            }
        }
    }
}
