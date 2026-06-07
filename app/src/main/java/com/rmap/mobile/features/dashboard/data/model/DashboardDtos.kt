package com.rmap.mobile.features.dashboard.data.model

import com.google.gson.annotations.SerializedName

data class DashboardResponseDto(
    @SerializedName("userProfile") val userProfile: DashboardUserProfileDto,
    @SerializedName("roadmaps") val roadmaps: List<DashboardRoadmapDto>,
    @SerializedName(value = "streakDays", alternate = ["streak_days"])
    val streakDays: Int,
    @SerializedName(value = "activityRecent", alternate = ["activity_recent"])
    val activityRecent: List<DashboardActivityEntryDto>,
    @SerializedName("summary") val summary: DashboardSummaryDto,
    @SerializedName("skillCategories") val skillCategories: List<DashboardSkillCategoryDto>,
    @SerializedName("roadmapStatus") val roadmapStatus: DashboardRoadmapStatusDto
)

data class DashboardUserProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName(value = "fullName", alternate = ["full_name"])
    val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String
)

data class DashboardRoadmapDto(
    @SerializedName(value = "roadmapId", alternate = ["roadmap_id"])
    val roadmapId: String,
    @SerializedName("deadlineDate") val deadlineDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int?,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("isTemplate") val isTemplate: Boolean,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("startedAt") val startedAt: String?,
    @SerializedName("title") val title: String,
    @SerializedName(value = "completionPct", alternate = ["completion_pct"])
    val completionPct: Double,
    @SerializedName(value = "streakDays", alternate = ["streak_days"])
    val streakDays: Int,
    @SerializedName(value = "skillReadinessPct", alternate = ["skill_readiness_pct"])
    val skillReadinessPct: Double,
    @SerializedName(value = "nodesTotal", alternate = ["nodes_total"])
    val nodesTotal: Int,
    @SerializedName(value = "nodesCompleted", alternate = ["nodes_completed"])
    val nodesCompleted: Int,
    @SerializedName(value = "timelineWarning", alternate = ["timeline_warning"])
    val timelineWarning: DashboardTimelineWarningDto?
)

data class DashboardTimelineWarningDto(
    @SerializedName(value = "isBehind", alternate = ["is_behind"])
    val isBehind: Boolean,
    @SerializedName(value = "paceDeficitPct", alternate = ["pace_deficit_pct"])
    val paceDeficitPct: Double,
    @SerializedName(value = "estimatedDelayDays", alternate = ["estimated_delay_days"])
    val estimatedDelayDays: Int,
    @SerializedName("message") val message: String
)

data class DashboardActivityEntryDto(
    @SerializedName(value = "activityDate", alternate = ["activity_date"])
    val activityDate: String,
    @SerializedName(value = "nodesCompleted", alternate = ["nodes_completed"])
    val nodesCompleted: Int
)

data class DashboardSummaryDto(
    @SerializedName("totalRoadmaps") val totalRoadmaps: Int,
    @SerializedName("activeRoadmaps") val activeRoadmaps: Int,
    @SerializedName("completedRoadmaps") val completedRoadmaps: Int,
    @SerializedName("totalSkills") val totalSkills: Int,
    @SerializedName("completedSkills") val completedSkills: Int,
    @SerializedName("inProgressSkills") val inProgressSkills: Int,
    @SerializedName("lockedSkills") val lockedSkills: Int,
    @SerializedName("currentStreak") val currentStreak: Int
)

data class DashboardSkillCategoryDto(
    @SerializedName("category") val category: String,
    @SerializedName("label") val label: String,
    @SerializedName("totalSkills") val totalSkills: Int
)

data class DashboardRoadmapStatusDto(
    @SerializedName("behindPace") val behindPace: Int,
    @SerializedName("onTrack") val onTrack: Int,
    @SerializedName("completed") val completed: Int,
    @SerializedName("notStarted") val notStarted: Int
)
