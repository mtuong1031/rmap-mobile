package com.rmap.mobile.features.profile.domain.model

data class UserProfile(
    val userName: String,
    val name: String,
    val role: String,
    val avatarUrl: String,
    val xp: Int,
    val streakDays: Int,
    val certificates: Int,
    val roadmaps: List<UserRoadmapProgress> = emptyList(),
    val recentActivity: List<UserDailyActivity> = emptyList()
) {
    val activeRoadmaps: List<UserRoadmapProgress>
        get() = roadmaps.filter { it.startedAt != null }
}

data class UserRoadmapProgress(
    val id: String,
    val title: String,
    val description: String?,
    val deadlineDate: String?,
    val estimatedWeeks: Int?,
    val roleCategory: String,
    val startedAt: String?,
    val completionPercent: Int,
    val nodesTotal: Int,
    val nodesCompleted: Int,
    val timelineWarning: UserTimelineWarning?
)

data class UserTimelineWarning(
    val isBehind: Boolean,
    val message: String
)

data class UserDailyActivity(
    val activityDate: String,
    val nodesCompleted: Int
)
