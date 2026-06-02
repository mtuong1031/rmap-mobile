package com.rmap.mobile.features.roadmap.data.model

import com.google.gson.annotations.SerializedName

data class RoadmapNodesListResponseDto(
    @SerializedName(value = "nodes", alternate = ["data"])
    val nodes: List<RoadmapNodeWithUserProgressDto>
)

data class RoadmapNodeWithUserProgressDto(
    @SerializedName("id") val id: String,
    @SerializedName(value = "roadmapId", alternate = ["roadmap_id"])
    val roadmapId: String,
    @SerializedName(value = "parentId", alternate = ["parent_id", "parent_node_id"])
    val parentId: String?,
    @SerializedName(value = "skillId", alternate = ["skill_id"])
    val skillId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName(value = "nodeType", alternate = ["node_type", "relation_type"])
    val nodeType: String,
    @SerializedName(value = "estimatedHours", alternate = ["estimated_hours", "skill_estimated_hours"])
    val estimatedHours: Double?,
    @SerializedName(value = "posX", alternate = ["pos_x"])
    val posX: Double,
    @SerializedName(value = "posY", alternate = ["pos_y"])
    val posY: Double,
    @SerializedName("progress") val progress: UserNodeProgressDto?
)

data class UserNodeProgressDto(
    @SerializedName("id") val id: String,
    @SerializedName(value = "roadmapNodeId", alternate = ["roadmap_node_id"])
    val roadmapNodeId: String,
    @SerializedName("status") val status: String,
    @SerializedName(value = "startedAt", alternate = ["started_at"])
    val startedAt: String?,
    @SerializedName(value = "completedAt", alternate = ["completed_at"])
    val completedAt: String?,
    @SerializedName(value = "quizScorePct", alternate = ["quiz_score_pct"])
    val quizScorePct: Double?,
    @SerializedName(value = "quizPassed", alternate = ["quiz_passed"])
    val quizPassed: Boolean?
)
