package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.features.profile.domain.model.AppLanguage
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity

data class ProfileUiState(
    val name: String = "",
    val role: String = "",
    val avatarUrl: String = "",
    val xp: Int = 0,
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val certificates: Int = 0,
    val recentActivity: List<UserDailyActivity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showLanguageSheet: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.ENGLISH
)
