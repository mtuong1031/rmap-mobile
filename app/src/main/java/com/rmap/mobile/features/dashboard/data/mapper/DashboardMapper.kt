package com.rmap.mobile.features.dashboard.data.mapper

import com.rmap.mobile.features.dashboard.data.model.DashboardActivityEntryDto
import com.rmap.mobile.features.dashboard.data.model.DashboardResponseDto
import com.rmap.mobile.features.dashboard.data.model.DashboardRoadmapDto
import com.rmap.mobile.features.dashboard.data.model.DashboardRoadmapStatusDto
import com.rmap.mobile.features.dashboard.data.model.DashboardSkillCategoryDto
import com.rmap.mobile.features.dashboard.data.model.DashboardSummaryDto
import com.rmap.mobile.features.dashboard.data.model.DashboardTimelineWarningDto
import com.rmap.mobile.features.dashboard.data.model.DashboardUserProfileDto
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.model.DashboardActivityEntry
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmap
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmapStatus
import com.rmap.mobile.features.dashboard.domain.model.DashboardSkillCategory
import com.rmap.mobile.features.dashboard.domain.model.DashboardSummary
import com.rmap.mobile.features.dashboard.domain.model.DashboardTimelineWarning
import com.rmap.mobile.features.dashboard.domain.model.DashboardUserProfile

fun DashboardResponseDto.toDomain(): Dashboard {
    return Dashboard(
        userProfile = userProfile.toDomain(),
        roadmaps = roadmaps.map { it.toDomain() },
        streakDays = streakDays,
        activityRecent = activityRecent.map { it.toDomain() },
        summary = summary.toDomain(),
        skillCategories = skillCategories.map { it.toDomain() },
        roadmapStatus = roadmapStatus.toDomain()
    )
}

private fun DashboardUserProfileDto.toDomain(): DashboardUserProfile {
    return DashboardUserProfile(
        id = id,
        email = email,
        fullName = fullName,
        role = role,
        createdAt = createdAt
    )
}

private fun DashboardRoadmapDto.toDomain(): DashboardRoadmap {
    return DashboardRoadmap(
        roadmapId = roadmapId,
        deadlineDate = deadlineDate,
        description = description,
        estimatedWeeks = estimatedWeeks,
        goalName = goalName,
        isTemplate = isTemplate,
        roleCategory = roleCategory,
        startedAt = startedAt,
        title = title,
        completionPct = completionPct.coerceIn(0.0, 100.0),
        streakDays = streakDays,
        skillReadinessPct = skillReadinessPct.coerceIn(0.0, 100.0),
        nodesTotal = nodesTotal,
        nodesCompleted = nodesCompleted,
        timelineWarning = timelineWarning?.toDomain()
    )
}

private fun DashboardTimelineWarningDto.toDomain(): DashboardTimelineWarning {
    return DashboardTimelineWarning(
        isBehind = isBehind,
        paceDeficitPct = paceDeficitPct,
        estimatedDelayDays = estimatedDelayDays,
        message = message
    )
}

private fun DashboardActivityEntryDto.toDomain(): DashboardActivityEntry {
    return DashboardActivityEntry(
        activityDate = activityDate,
        nodesCompleted = nodesCompleted
    )
}

private fun DashboardSummaryDto.toDomain(): DashboardSummary {
    return DashboardSummary(
        totalRoadmaps = totalRoadmaps,
        activeRoadmaps = activeRoadmaps,
        completedRoadmaps = completedRoadmaps,
        totalSkills = totalSkills,
        completedSkills = completedSkills,
        inProgressSkills = inProgressSkills,
        lockedSkills = lockedSkills,
        currentStreak = currentStreak
    )
}

private fun DashboardSkillCategoryDto.toDomain(): DashboardSkillCategory {
    return DashboardSkillCategory(
        category = category,
        label = label,
        totalSkills = totalSkills,
        completedSkills = completedSkills
    )
}

private fun DashboardRoadmapStatusDto.toDomain(): DashboardRoadmapStatus {
    return DashboardRoadmapStatus(
        behindPace = behindPace,
        onTrack = onTrack,
        completed = completed,
        notStarted = notStarted
    )
}
