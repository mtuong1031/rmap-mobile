package com.rmap.mobile.features.profile.presentation.viewmodel

data class PrivacySecurityUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSaving: Boolean = false,
    val currentPasswordError: PrivacySecurityFieldError? = null,
    val newPasswordError: PrivacySecurityFieldError? = null,
    val confirmPasswordError: PrivacySecurityFieldError? = null,
    val errorMessage: String? = null
) {
    val isSubmitEnabled: Boolean
        get() = !isSaving &&
            currentPassword.isNotBlank() &&
            newPassword.isNotBlank() &&
            confirmNewPassword.isNotBlank() &&
            currentPasswordError == null &&
            newPasswordError == null &&
            confirmPasswordError == null
}

enum class PrivacySecurityFieldError {
    CurrentPasswordRequired,
    NewPasswordTooShort,
    ConfirmPasswordRequired,
    PasswordsDoNotMatch
}
