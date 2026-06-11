package com.rmap.mobile.features.profile.data.model

import com.google.gson.annotations.SerializedName

data class ProfileActivityResponseDto(
    @SerializedName(value = "streakDays", alternate = ["streak_days"])
    val streakDays: Int,
    @SerializedName(value = "longestStreak", alternate = ["longest_streak"])
    val longestStreak: Int,
    @SerializedName("activity") val activity: List<ProfileActivityEntryDto>
)

data class ProfileActivityEntryDto(
    @SerializedName(value = "activityDate", alternate = ["activity_date"])
    val activityDate: String,
    @SerializedName(value = "nodesCompleted", alternate = ["nodes_completed"])
    val nodesCompleted: Int
)

data class UpdateUserProfileRequestDto(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class ProfileIdentityResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName(value = "fullName", alternate = ["full_name"]) val fullName: String,
    @SerializedName(value = "avatarUrl", alternate = ["avatar_url"]) val avatarUrl: String?,
    @SerializedName("role") val role: String,
    @SerializedName(value = "createdAt", alternate = ["created_at"]) val createdAt: String
)
