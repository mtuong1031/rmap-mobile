package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.features.profile.domain.model.UserIntegration

data class ConnectedAccountsUiState(
    val integrations: List<UserIntegration> = emptyList(),
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null
)
