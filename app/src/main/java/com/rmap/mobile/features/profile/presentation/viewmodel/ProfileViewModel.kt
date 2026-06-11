package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.LogoutUseCase
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository,
    private val logoutUseCase: LogoutUseCase = RMapAppGraph.logoutUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadProfile()
        observeAuthState()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            profileRepository.getActivity()
                .onSuccess { activity ->
                    _uiState.update {
                        it.copy(
                            streak = activity.streakDays,
                            longestStreak = activity.longestStreak,
                            recentActivity = activity.activity,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to load profile"
                        )
                    }
                }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                val user = (authState as? AuthState.Authenticated)?.user
                _uiState.update {
                    it.copy(
                        name = user?.fullName.orEmpty(),
                        role = user?.role?.toProfileRoleLabel().orEmpty(),
                        avatarUrl = user?.avatarUrl.orEmpty()
                    )
                }
            }
        }
    }

    fun onEditProfile() {
        viewModelScope.launch {
            _events.emit(ProfileEvent.NavigateToPersonalInformation)
        }
    }

    fun onSettingClick(action: ProfileSettingAction) {
        viewModelScope.launch {
            when (action) {
                ProfileSettingAction.PersonalInfo -> _events.emit(ProfileEvent.NavigateToPersonalInformation)
                ProfileSettingAction.Notifications -> _events.emit(ProfileEvent.NavigateToNotificationSettings)
                ProfileSettingAction.Privacy -> _events.emit(ProfileEvent.NavigateToPrivacySecurity)
                ProfileSettingAction.ConnectedAccounts -> _events.emit(ProfileEvent.NavigateToConnectedAccounts)
                ProfileSettingAction.SignOut -> {
                    logoutUseCase()
                        .onSuccess { _events.emit(ProfileEvent.SignedOut) }
                        .onFailure { _events.emit(ProfileEvent.SignedOut) }
                }
                else -> _events.emit(ProfileEvent.ShowComingSoon)
            }
        }
    }

    private fun emitComingSoon() {
        viewModelScope.launch {
            _events.emit(ProfileEvent.ShowComingSoon)
        }
    }
}

private fun String.toProfileRoleLabel(): String {
    return trim()
        .replace("_", " ")
        .replace("-", " ")
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }
        .ifBlank { "User" }
}
