package com.rmap.mobile.features.roadmap.data.model

import com.google.gson.annotations.SerializedName

data class PaginatedRoadmapsResponseDto(
    @SerializedName("data") val data: List<RoadmapResponseDto>,
    @SerializedName("meta") val meta: PaginationMetaDto
)

data class PaginationMetaDto(
    @SerializedName("page") val page: Int,
    @SerializedName(value = "perPage", alternate = ["per_page"]) val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName(value = "totalPages", alternate = ["total_pages"]) val totalPages: Int
)

data class RoadmapResponseDto(
    @SerializedName(value = "deadlineDate", alternate = ["deadline_date"])
    val deadlineDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName(value = "estimatedWeeks", alternate = ["estimated_weeks"])
    val estimatedWeeks: Int?,
    @SerializedName(value = "generatedAt", alternate = ["generated_at", "created_at"])
    val generatedAt: String,
    @SerializedName(value = "goalName", alternate = ["goal_name", "role_name"])
    val goalName: String?,
    @SerializedName(value = "hoursPerDay", alternate = ["hours_per_day"])
    val hoursPerDay: Double?,
    @SerializedName("id") val id: String,
    @SerializedName(value = "isTemplate", alternate = ["is_template"])
    val isTemplate: Boolean,
    @SerializedName(value = "roleCategory", alternate = ["role_category"])
    val roleCategory: String,
    @SerializedName("title") val title: String,
    @SerializedName(value = "updatedAt", alternate = ["updated_at"])
    val updatedAt: String,
    @SerializedName(value = "userId", alternate = ["user_id"])
    val userId: String?
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
