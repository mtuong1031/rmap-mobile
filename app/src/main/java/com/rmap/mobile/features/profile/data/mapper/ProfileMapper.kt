package com.rmap.mobile.features.profile.data.mapper

import com.rmap.mobile.features.profile.data.model.DashboardResponseDto
import com.rmap.mobile.features.profile.domain.model.UserProfile

fun DashboardResponseDto.toDomain(): UserProfile {
    val displayName = user.fullName
        .trim()
        .ifEmpty { user.email.substringBefore("@") }

    return UserProfile(
        userName = displayName.substringBefore(" ").ifEmpty { displayName },
        name = displayName,
        role = user.role.toProfileRoleLabel(),
        avatarUrl = "",
        xp = DEFAULT_XP,
        streakDays = streakDays,
        certificates = DEFAULT_CERTIFICATES
    )
}

private fun String.toProfileRoleLabel(): String {
    return trim()
        .replace("_", " ")
        .replace("-", " ")
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }
        .ifBlank { DEFAULT_ROLE }
}

private const val DEFAULT_XP = 0
private const val DEFAULT_CERTIFICATES = 0
private const val DEFAULT_ROLE = "Learner"
