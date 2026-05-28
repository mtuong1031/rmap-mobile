package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName(value = "fullName", alternate = ["full_name"]) val fullName: String,
    @SerializedName(value = "avatarUrl", alternate = ["avatar_url"]) val avatarUrl: String?,
    @SerializedName("role") val role: String,
    @SerializedName(value = "createdAt", alternate = ["created_at"]) val createdAt: String
)
