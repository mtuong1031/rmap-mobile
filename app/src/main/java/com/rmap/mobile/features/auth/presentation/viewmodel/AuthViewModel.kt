package com.rmap.mobile.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sessionRepository: SessionRepository = RMapAppGraph.sessionRepository
) : ViewModel() {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun onContinueWithGoogle() {
        signIn()
    }

    fun onContinueWithFacebook() {
        signIn()
    }

    private fun signIn() {
        viewModelScope.launch {
            sessionRepository.signInWithDemoProvider()
                .onSuccess { _events.emit(AuthEvent.NavigateToHome) }
                .onFailure { _events.emit(AuthEvent.ShowSignInFailed) }
        }
    }
}
