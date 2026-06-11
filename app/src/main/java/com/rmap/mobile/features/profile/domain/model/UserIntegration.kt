package com.rmap.mobile.features.profile.domain.model

data class UserIntegration(
    val provider: String,
    val connected: Boolean,
    val canDisconnect: Boolean,
    val connectedAt: String?,
    val providerEmail: String?
)
