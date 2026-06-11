package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonalInformationViewModel(
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PersonalInformationUiState(
            avatarSeeds = generateAvatarSeeds()
        )
    )
    val uiState: StateFlow<PersonalInformationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PersonalInformationEvent>()
    val events: SharedFlow<PersonalInformationEvent> = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    fun onFullNameChanged(value: String) {
        _uiState.update {
            it.copy(
                fullName = value,
                fieldError = value.validateFullName(),
                errorMessage = null
            )
        }
    }

    fun onStartEditingDetails() {
        _uiState.update { it.copy(isEditingDetails = true, errorMessage = null) }
    }

    fun onCancelEditDetails() {
        _uiState.update {
            it.copy(
                fullName = it.originalFullName,
                fieldError = null,
                isEditingDetails = false,
                errorMessage = null
            )
        }
    }

    fun onOpenAvatarPicker() {
        _uiState.update {
            it.copy(
                avatarUrlSnapshot = it.avatarUrl,
                isAvatarPickerOpen = true,
                errorMessage = null
            )
        }
    }

    fun onCancelAvatarPicker() {
        _uiState.update {
            it.copy(
                avatarUrl = it.avatarUrlSnapshot ?: it.originalAvatarUrl,
                avatarUrlSnapshot = null,
                isAvatarPickerOpen = false,
                errorMessage = null
            )
        }
    }

    fun onAvatarSelected(seed: String) {
        _uiState.update {
            it.copy(
                avatarUrl = buildPersonalInformationAvatarUrl(seed),
                errorMessage = null
            )
        }
    }

    fun onResetSelectedAvatar() {
        _uiState.update {
            it.copy(
                avatarUrl = it.avatarUrlSnapshot ?: it.originalAvatarUrl,
                errorMessage = null
            )
        }
    }

    fun onRegenerateAvatars() {
        _uiState.update {
            it.copy(
                avatarSeeds = generateAvatarSeeds(),
                avatarUrl = it.avatarUrlSnapshot ?: it.originalAvatarUrl,
                errorMessage = null
            )
        }
    }

    fun onSaveClick() {
        val state = _uiState.value
        val fieldError = state.fullName.validateFullName()

        if (fieldError != null) {
            _uiState.update { it.copy(fieldError = fieldError) }
            return
        }

        if (!state.isSaveEnabled) {
            return
        }

        viewModelScope.launch {
            val fullName = state.normalizedFullName
            val avatarUrl = state.avatarUrl

            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            profileRepository.updateProfile(
                fullName = fullName,
                avatarUrl = avatarUrl
            ).onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        originalFullName = profile.fullName,
                        originalAvatarUrl = profile.avatarUrl.orEmpty(),
                        fullName = profile.fullName,
                        avatarUrl = profile.avatarUrl.orEmpty(),
                        avatarUrlSnapshot = null,
                        isAvatarPickerOpen = false,
                        isEditingDetails = false,
                        isSaving = false,
                        fieldError = null,
                        errorMessage = null
                    )
                }
                authRepository.getCurrentUser()
                _events.emit(PersonalInformationEvent.ProfileUpdated)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: DEFAULT_SAVE_ERROR
                    )
                }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        val user = authState.user
                        _uiState.update { state ->
                            if (state.isDirty && !state.isSaving) {
                                state.copy(isLoading = false)
                            } else {
                                state.copy(
                                    originalFullName = user.fullName,
                                    originalAvatarUrl = user.avatarUrl.orEmpty(),
                                    fullName = user.fullName,
                                    avatarUrl = user.avatarUrl.orEmpty(),
                                    isLoading = false,
                                    fieldError = null,
                                    errorMessage = null
                                )
                            }
                        }
                    }

                    AuthState.Checking -> _uiState.update { it.copy(isLoading = true) }
                    AuthState.Unauthenticated -> _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private companion object {
        const val AVATAR_COUNT = 32
        const val DEFAULT_SAVE_ERROR = "Profile update failed. Please try again."

        fun generateAvatarSeeds(): List<String> {
            return List(AVATAR_COUNT) { UUID.randomUUID().toString() }
        }

        fun String.validateFullName(): PersonalInformationFieldError? {
            val length = trim().length
            return when {
                length < 2 -> PersonalInformationFieldError.NameTooShort
                length > 100 -> PersonalInformationFieldError.NameTooLong
                else -> null
            }
        }
    }
}

private const val AVATAR_BASE_URL = "https://api.dicebear.com/10.x/adventurer/svg?seed="

internal fun buildPersonalInformationAvatarUrl(seed: String): String {
    return AVATAR_BASE_URL + java.net.URLEncoder.encode(seed, Charsets.UTF_8.name())
}
