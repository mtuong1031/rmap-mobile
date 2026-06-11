package com.rmap.mobile.features.profile.data.mapper

import com.rmap.mobile.features.profile.data.model.UserIntegrationDto
import com.rmap.mobile.features.profile.domain.model.UserIntegration

fun UserIntegrationDto.toDomain(): UserIntegration {
    return UserIntegration(
        provider = this.provider,
        connected = this.connected,
        canDisconnect = this.canDisconnect,
        connectedAt = this.connectedAt,
        providerEmail = this.providerEmail
    )
}
