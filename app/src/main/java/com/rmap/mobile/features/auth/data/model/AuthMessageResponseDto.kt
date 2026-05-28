package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class AuthMessageResponseDto(
    @SerializedName("message") val message: String
)
