package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.profile.domain.model.AppLanguage
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
import java.util.Locale

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
        loadCurrentLanguage()
        loadProfile()
        observeAuthState()
    }

    private fun loadCurrentLanguage() {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = if (locales.isEmpty) Locale.getDefault().language else locales.get(0)?.language
        _uiState.update { it.copy(currentLanguage = AppLanguage.fromTag(tag)) }
    }

    fun loadProfile() {
        viewModelScope.launch {
            if (authRepository.authState.value !is AuthState.Authenticated) {
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                return@launch
            }

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
                val isAuthenticated = authState is AuthState.Authenticated
                val user = (authState as? AuthState.Authenticated)?.user
                _uiState.update {
                    it.copy(
                        isAuthenticated = isAuthenticated,
                        name = user?.fullName.orEmpty(),
                        role = user?.role?.toProfileRoleLabel().orEmpty(),
                        avatarUrl = user?.avatarUrl.orEmpty()
                    )
                }

                // Reload profile data when authentication state changes to authenticated
                if (isAuthenticated && _uiState.value.recentActivity.isEmpty()) {
                    loadProfile()
                }
            }
        }
    }

    fun onEditProfile() {
        viewModelScope.launch {
            if (_uiState.value.isAuthenticated) {
                _events.emit(ProfileEvent.NavigateToPersonalInformation)
            } else {
                _events.emit(ProfileEvent.NavigateToAuth)
            }
        }
    }

    fun onSettingClick(action: ProfileSettingAction) {
        viewModelScope.launch {
            // Language and Theme (future) don't require auth
            if (action == ProfileSettingAction.Language) {
                _uiState.update { it.copy(showLanguageSheet = true) }
                return@launch
            }

            // All other actions require auth
            if (!_uiState.value.isAuthenticated) {
                _events.emit(ProfileEvent.NavigateToAuth)
                return@launch
            }

            when (action) {
                ProfileSettingAction.PersonalInfo -> _events.emit(ProfileEvent.NavigateToPersonalInformation)
                ProfileSettingAction.Notifications -> _events.emit(ProfileEvent.NavigateToNotificationSettings)
                ProfileSettingAction.Privacy -> _events.emit(ProfileEvent.NavigateToPrivacySecurity)
                ProfileSettingAction.ConnectedAccounts -> _events.emit(ProfileEvent.NavigateToConnectedAccounts)
                ProfileSettingAction.Language -> {} // Handled above
                ProfileSettingAction.SignOut -> {
                    logoutUseCase()
                        .onSuccess { _events.emit(ProfileEvent.SignedOut) }
                        .onFailure { _events.emit(ProfileEvent.SignedOut) }
                }
            }
        }
    }

    fun onLanguageSelected(language: AppLanguage) {
        _uiState.update { it.copy(showLanguageSheet = false, currentLanguage = language) }
        val localeList = LocaleListCompat.forLanguageTags(language.tag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun onDismissLanguageSheet() {
        _uiState.update { it.copy(showLanguageSheet = false) }
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
