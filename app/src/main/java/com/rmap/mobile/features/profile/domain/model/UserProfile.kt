package com.rmap.mobile.features.profile.domain.model

data class UserProfile(
    val userName: String,
    val name: String,
    val role: String,
    val avatarUrl: String,
    val xp: Int,
    val certificates: Int
)

data class UserActivitySummary(
    val streakDays: Int,
    val longestStreak: Int,
    val activity: List<UserDailyActivity> = emptyList()
)

data class UserDailyActivity(
    val activityDate: String,
    val nodesCompleted: Int
)
