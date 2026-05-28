package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String
)
