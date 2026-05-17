package com.rmap.mobile.features.profile.domain.model

data class UserProfile(
    val userName: String,
    val name: String,
    val role: String,
    val avatarUrl: String,
    val xp: Int,
    val streakDays: Int,
    val certificates: Int
)
