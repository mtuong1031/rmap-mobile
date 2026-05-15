package com.rmap.mobile.features.profile.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = "Thinh Duy",
            role = "Aspiring Frontend Developer",
            avatarUrl = "",
            xp = 450,
            streak = 5,
            certificates = 2
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
