package com.rmap.mobile.features.roadmap.data.remote.model

import com.google.gson.annotations.SerializedName

data class SkillDetailDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "skill_name", alternate = ["name", "skillName"]) val name: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName(value = "category", alternate = ["roleCategory"]) val category: String? = null,
    @SerializedName(value = "estimated_hours", alternate = ["estimatedHours", "defaultEstimatedHours"])
    val estimatedHours: Int? = null
)

data class SkillResourceDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "skill_id", alternate = ["skillId"]) val skillId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName(value = "platform", alternate = ["resourceType"]) val platform: String? = null,
    @SerializedName(value = "is_free", alternate = ["isFree"]) val isFree: Boolean? = null,
    @SerializedName(value = "level_tag", alternate = ["levelTag"]) val levelTag: String? = null
)

data class SkillResourcesResponseDto(
    @SerializedName("data") val data: List<SkillResourceDto>? = null
)
