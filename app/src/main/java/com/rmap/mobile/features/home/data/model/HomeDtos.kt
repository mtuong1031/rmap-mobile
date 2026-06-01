package com.rmap.mobile.features.home.data.model

import com.google.gson.annotations.SerializedName

data class HomeDashboardResponseDto(
    @SerializedName("activeRoadmaps") val activeRoadmaps: List<HomeActiveRoadmapDto>,
    @SerializedName("metrics") val metrics: HomeMetricsDto
)

data class HomeActiveRoadmapDto(
    @SerializedName("roadmapId") val roadmapId: String,
    @SerializedName("title") val title: String,
    @SerializedName("goalName") val goalName: String,
    @SerializedName("isTemplate") val isTemplate: Boolean,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("startedAt") val startedAt: String,
    @SerializedName("currentGroup") val currentGroup: HomeRoadmapGroupDto?,
    @SerializedName("planNode") val planNode: HomePlanNodeDto?,
    @SerializedName("chapter") val chapter: HomeRoadmapChapterDto?,
    @SerializedName("progress") val progress: HomeRoadmapProgressDto,
    @SerializedName("nextUnlock") val nextUnlock: HomeNextUnlockDto?,
    @SerializedName("paceWarning") val paceWarning: HomePaceWarningDto?
)

data class HomeRoadmapGroupDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class HomePlanNodeDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("nodeType") val nodeType: String,
    @SerializedName("estimatedHours") val estimatedHours: Int?
)

data class HomeRoadmapChapterDto(
    @SerializedName("current") val current: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("label") val label: String
)

data class HomeRoadmapProgressDto(
    @SerializedName("requiredNodesCompleted") val requiredNodesCompleted: Int,
    @SerializedName("requiredNodesTotal") val requiredNodesTotal: Int,
    @SerializedName("requiredCompletionPct") val requiredCompletionPct: Double
)

data class HomeNextUnlockDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class HomePaceWarningDto(
    @SerializedName("isBehind") val isBehind: Boolean,
    @SerializedName("paceDeficitPct") val paceDeficitPct: Double,
    @SerializedName("estimatedDelayDays") val estimatedDelayDays: Int,
    @SerializedName("message") val message: String,
    @SerializedName("title") val title: String,
    @SerializedName("actionLabel") val actionLabel: String
)

data class HomeMetricsDto(
    @SerializedName("roadmapCompletionPct") val roadmapCompletionPct: Double,
    @SerializedName("streakDays") val streakDays: Int,
    @SerializedName("readinessPct") val readinessPct: Double
)

data class HomeTemplateRecommendationsResponseDto(
    @SerializedName("roleCategories") val roleCategories: List<HomeRoleCategoryDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("relevantRoadmaps") val relevantRoadmaps: List<HomeTemplateRoadmapDto>
)

data class HomeRoleCategoryDto(
    @SerializedName("category") val category: String,
    @SerializedName("label") val label: String
)

data class HomeTemplateRoadmapDto(
    @SerializedName("roadmapId") val roadmapId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("goalName") val goalName: String,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("categoryLabel") val categoryLabel: String,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int?,
    @SerializedName("durationLabel") val durationLabel: String?,
    @SerializedName("nodesTotal") val nodesTotal: Int,
    @SerializedName("requiredNodesTotal") val requiredNodesTotal: Int
)

data class HomeTemplateCategoriesResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("categories") val categories: List<HomeTemplateCategoryDto>
)

data class HomeTemplateCategoryDto(
    @SerializedName("category") val category: String,
    @SerializedName("label") val label: String,
    @SerializedName("templatesCount") val templatesCount: Int
)

data class HomeTemplateTrendingsResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("trendings") val trendings: List<HomeTrendingRoadmapDto>
)

data class HomeTrendingRoadmapDto(
    @SerializedName("rank") val rank: Int,
    @SerializedName("roadmapId") val roadmapId: String,
    @SerializedName("title") val title: String,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("categoryLabel") val categoryLabel: String,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int?,
    @SerializedName("durationLabel") val durationLabel: String?,
    @SerializedName("nodesTotal") val nodesTotal: Int,
    @SerializedName("trendText") val trendText: String
)
