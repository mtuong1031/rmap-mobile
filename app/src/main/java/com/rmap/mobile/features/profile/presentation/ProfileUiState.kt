package com.rmap.mobile.features.profile.presentation

data class ProfileUiState(
    val name: String,
    val role: String,
    val avatarUrl: String,
    val xp: Int,
    val streak: Int,
    val certificates: Int
)
