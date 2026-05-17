package com.rmap.mobile.features.profile.presentation.viewmodel

data class ProfileUiState(
    val name: String = "",
    val role: String = "",
    val avatarUrl: String = "",
    val xp: Int = 0,
    val streak: Int = 0,
    val certificates: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
