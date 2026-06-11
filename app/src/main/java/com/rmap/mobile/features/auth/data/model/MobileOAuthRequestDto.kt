package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class MobileOAuthRequestDto(
    @SerializedName("idToken") val idToken: String
)
