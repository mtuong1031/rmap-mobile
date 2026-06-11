package com.rmap.mobile.features.profile.data.mapper

import com.rmap.mobile.features.profile.data.model.ProfileActivityEntryDto
import com.rmap.mobile.features.profile.data.model.ProfileActivityResponseDto
import com.rmap.mobile.features.profile.data.model.ProfileIdentityResponseDto
import com.rmap.mobile.features.profile.domain.model.UserActivitySummary
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.domain.model.UserProfileIdentity

fun ProfileActivityResponseDto.toDomain(): UserActivitySummary {
    return UserActivitySummary(
        streakDays = streakDays,
        longestStreak = longestStreak,
        activity = activity.map { it.toDomain() }
    )
}

private fun ProfileActivityEntryDto.toDomain(): UserDailyActivity {
    return UserDailyActivity(
        activityDate = activityDate,
        nodesCompleted = nodesCompleted
    )
}

fun ProfileIdentityResponseDto.toDomain(): UserProfileIdentity {
    return UserProfileIdentity(
        id = id,
        email = email,
        fullName = fullName,
        avatarUrl = avatarUrl,
        role = role,
        createdAt = createdAt
    )
}
