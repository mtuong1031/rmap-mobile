package com.rmap.mobile.features.dashboard.domain.model

data class Dashboard(
    val userProfile: DashboardUserProfile,
    val roadmaps: List<DashboardRoadmap>,
    val streakDays: Int,
    val activityRecent: List<DashboardActivityEntry>,
    val summary: DashboardSummary,
    val skillCategories: List<DashboardSkillCategory>,
    val roadmapStatus: DashboardRoadmapStatus
)

data class DashboardUserProfile(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val createdAt: String
)

data class DashboardRoadmap(
    val roadmapId: String,
    val deadlineDate: String?,
    val description: String?,
    val estimatedWeeks: Int?,
    val goalName: String?,
    val isTemplate: Boolean,
    val roleCategory: String,
    val startedAt: String?,
    val title: String,
    val completionPct: Double,
    val streakDays: Int,
    val skillReadinessPct: Double,
    val nodesTotal: Int,
    val nodesCompleted: Int,
    val timelineWarning: DashboardTimelineWarning?
)

data class DashboardTimelineWarning(
    val isBehind: Boolean,
    val paceDeficitPct: Double,
    val estimatedDelayDays: Int,
    val message: String
)

data class DashboardActivityEntry(
    val activityDate: String,
    val nodesCompleted: Int
)

data class DashboardSummary(
    val totalRoadmaps: Int,
    val activeRoadmaps: Int,
    val completedRoadmaps: Int,
    val totalSkills: Int,
    val completedSkills: Int,
    val inProgressSkills: Int,
    val lockedSkills: Int,
    val currentStreak: Int
)

data class DashboardSkillCategory(
    val category: String,
    val label: String,
    val totalSkills: Int,
    val completedSkills: Int
)

data class DashboardRoadmapStatus(
    val behindPace: Int,
    val onTrack: Int,
    val completed: Int,
    val notStarted: Int
)
