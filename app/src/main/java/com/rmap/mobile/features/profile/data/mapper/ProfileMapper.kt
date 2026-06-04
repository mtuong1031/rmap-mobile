package com.rmap.mobile.features.profile.data.mapper

import com.rmap.mobile.features.profile.data.model.DashboardResponseDto
import com.rmap.mobile.features.profile.data.model.DailyActivityEntryDto
import com.rmap.mobile.features.profile.data.model.RoadmapProgressSummaryDto
import com.rmap.mobile.features.profile.domain.model.UserDailyActivity
import com.rmap.mobile.features.profile.domain.model.UserProfile
import com.rmap.mobile.features.profile.domain.model.UserRoadmapProgress
import com.rmap.mobile.features.profile.domain.model.UserTimelineWarning
import kotlin.math.roundToInt

fun DashboardResponseDto.toDomain(): UserProfile {
    val displayName = userProfile.fullName
        .trim()
        .ifEmpty { userProfile.email.substringBefore("@") }

    return UserProfile(
        userName = displayName.substringBefore(" ").ifEmpty { displayName },
        name = displayName,
        role = userProfile.role.toProfileRoleLabel(),
        avatarUrl = "",
        xp = DEFAULT_XP,
        streakDays = streakDays,
        certificates = DEFAULT_CERTIFICATES,
        roadmaps = roadmaps.map { it.toDomain() },
        recentActivity = activityRecent.map { it.toDomain() }
    )
}

private fun RoadmapProgressSummaryDto.toDomain(): UserRoadmapProgress {
    return UserRoadmapProgress(
        id = roadmapId,
        title = title,
        description = description,
        deadlineDate = deadlineDate,
        estimatedWeeks = estimatedWeeks,
        roleCategory = roleCategory,
        startedAt = startedAt,
        completionPercent = completionPct.roundToInt().coerceIn(0, 100),
        nodesTotal = nodesTotal,
        nodesCompleted = nodesCompleted,
        timelineWarning = timelineWarning?.let { warning ->
            UserTimelineWarning(
                isBehind = warning.isBehind,
                message = warning.message
            )
        }
    )
}

private fun DailyActivityEntryDto.toDomain(): UserDailyActivity {
    return UserDailyActivity(
        activityDate = activityDate,
        nodesCompleted = nodesCompleted
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
