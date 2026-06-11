package com.rmap.mobile.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectedAccountsViewModel(
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectedAccountsUiState())
    val uiState: StateFlow<ConnectedAccountsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ConnectedAccountsEvent>()
    val eventFlow: SharedFlow<ConnectedAccountsEvent> = _eventFlow.asSharedFlow()

    init {
        loadIntegrations()
    }

    fun loadIntegrations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            profileRepository.getIntegrations()
                .onSuccess { integrations ->
                    _uiState.update { 
                        it.copy(
                            integrations = integrations,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load integrations"
                        )
                    }
                }
        }
    }

    fun disconnectIntegration(provider: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            profileRepository.disconnectIntegration(provider)
                .onSuccess {
                    loadIntegrations()
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar("Disconnected $provider successfully"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isActionLoading = false) }
                    val message = if (error is AppException) error.message else "Failed to disconnect"
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar(message ?: "An error occurred"))
                }
        }
    }

    fun linkGoogleAccount(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            authRepository.linkWithGoogle(idToken)
                .onSuccess {
                    loadIntegrations()
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar("Linked Google account successfully"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isActionLoading = false) }
                    val message = if (error is AppException) error.message else "Failed to link Google account"
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar(message ?: "An error occurred"))
                }
        }
    }

    fun linkGithubAccount(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            authRepository.linkWithGithub(code)
                .onSuccess {
                    loadIntegrations()
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar("Linked GitHub account successfully"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isActionLoading = false) }
                    val message = if (error is AppException) error.message else "Failed to link GitHub account"
                    _eventFlow.emit(ConnectedAccountsEvent.ShowSnackbar(message ?: "An error occurred"))
                }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventFlow.emit(ConnectedAccountsEvent.NavigateBack)
        }
    }
}
