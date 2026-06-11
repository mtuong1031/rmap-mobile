package com.rmap.mobile.features.profile.data.model

import com.google.gson.annotations.SerializedName

data class UserIntegrationDto(
    @SerializedName("canDisconnect")
    val canDisconnect: Boolean,
    
    @SerializedName("connected")
    val connected: Boolean,
    
    @SerializedName("connectedAt")
    val connectedAt: String?,
    
    @SerializedName("provider")
    val provider: String,
    
    @SerializedName("providerEmail")
    val providerEmail: String?
)
