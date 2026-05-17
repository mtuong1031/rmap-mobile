package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.repository.SessionRepository
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
    private val sessionRepository: SessionRepository = RMapAppGraph.sessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            profileRepository.getProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState(
                        name = profile.name,
                        role = profile.role,
                        avatarUrl = profile.avatarUrl,
                        xp = profile.xp,
                        streak = profile.streakDays,
                        certificates = profile.certificates,
                        isLoading = false
                    )
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

    fun onEditProfile() {
        emitComingSoon()
    }

    fun onSettingClick(action: ProfileSettingAction) {
        if (action == ProfileSettingAction.SignOut) {
            viewModelScope.launch {
                sessionRepository.signOut()
                _events.emit(ProfileEvent.SignedOut)
            }
        } else {
            emitComingSoon()
        }
    }

    private fun emitComingSoon() {
        viewModelScope.launch {
            _events.emit(ProfileEvent.ShowComingSoon)
        }
    }
}
