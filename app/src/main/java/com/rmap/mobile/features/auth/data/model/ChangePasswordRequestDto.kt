package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
