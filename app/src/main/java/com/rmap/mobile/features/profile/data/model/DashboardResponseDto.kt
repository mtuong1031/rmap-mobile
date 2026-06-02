package com.rmap.mobile.features.profile.data.model

import com.google.gson.annotations.SerializedName

data class DashboardResponseDto(
    @SerializedName("user") val user: DashboardUserProfileDto,
    @SerializedName(value = "activeRoadmap", alternate = ["active_roadmap"])
    val activeRoadmap: RoadmapProgressSummaryDto?,
    @SerializedName(value = "streakDays", alternate = ["streak_days"])
    val streakDays: Int,
    @SerializedName(value = "activityRecent", alternate = ["activity_recent"])
    val activityRecent: List<DailyActivityEntryDto>
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

data class DailyActivityEntryDto(
    @SerializedName(value = "activityDate", alternate = ["activity_date"])
    val activityDate: String,
    @SerializedName(value = "nodesCompleted", alternate = ["nodes_completed"])
    val nodesCompleted: Int
)

data class RoadmapProgressSummaryDto(
    @SerializedName(value = "roadmapId", alternate = ["roadmap_id"])
    val roadmapId: String,
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
    val timelineWarning: TimelineWarningDto?
)

data class TimelineWarningDto(
    @SerializedName(value = "isBehind", alternate = ["is_behind"])
    val isBehind: Boolean,
    @SerializedName(value = "paceDeficitPct", alternate = ["pace_deficit_pct"])
    val paceDeficitPct: Double,
    @SerializedName(value = "estimatedDelayDays", alternate = ["estimated_delay_days"])
    val estimatedDelayDays: Int,
    @SerializedName("message") val message: String
)
