package com.rmap.mobile.presentation.profile

data class ProfileUiState(
    val name: String,
    val role: String,
    val avatarUrl: String,
    val xp: Int,
    val streak: Int,
    val certificates: Int
)
