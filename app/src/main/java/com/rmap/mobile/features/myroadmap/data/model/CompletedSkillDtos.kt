package com.rmap.mobile.features.myroadmap.data.model

import com.google.gson.annotations.SerializedName

data class CompletedSkillsResponseDto(
    @SerializedName("data") val data: List<CompletedSkillDto>,
    @SerializedName("meta") val meta: CompletedSkillsPaginationDto
)

data class CompletedSkillDto(
    @SerializedName("skillId") val skillId: String,
    @SerializedName("skillName") val skillName: String,
    @SerializedName("category") val category: String,
    @SerializedName("completedAt") val completedAt: String
)

data class CompletedSkillsPaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName(value = "perPage", alternate = ["per_page"]) val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName(value = "totalPages", alternate = ["total_pages"]) val totalPages: Int
)
