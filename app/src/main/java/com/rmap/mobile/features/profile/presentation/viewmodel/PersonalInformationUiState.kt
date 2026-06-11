package com.rmap.mobile.features.profile.presentation.viewmodel

data class PersonalInformationUiState(
    val originalFullName: String = "",
    val originalAvatarUrl: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val avatarSeeds: List<String> = emptyList(),
    val avatarUrlSnapshot: String? = null,
    val isLoading: Boolean = true,
    val isEditingDetails: Boolean = false,
    val isAvatarPickerOpen: Boolean = false,
    val isSaving: Boolean = false,
    val fieldError: PersonalInformationFieldError? = null,
    val errorMessage: String? = null
) {
    val normalizedFullName: String
        get() = fullName.trim()

    val isDirty: Boolean
        get() = normalizedFullName != originalFullName || avatarUrl != originalAvatarUrl

    val isSaveEnabled: Boolean
        get() = !isLoading && !isSaving && isDirty && fieldError == null
}

enum class PersonalInformationFieldError {
    NameTooShort,
    NameTooLong
}
