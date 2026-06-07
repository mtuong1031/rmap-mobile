package com.rmap.mobile.features.roadmap.data.model

import com.google.gson.annotations.SerializedName

data class TemplateDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("roleCategory") val roleCategory: String,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("isTemplate") val isTemplate: Boolean,
    @SerializedName("estimatedWeeks") val estimatedWeeks: Int?,
    @SerializedName("hoursPerDay") val hoursPerDay: Int?,
    @SerializedName("deadlineDate") val deadlineDate: String?,
    @SerializedName("startedAt") val startedAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("generatedAt") val generatedAt: String?,
    @SerializedName("userId") val userId: String?
)

data class TemplatePaginationMetaDto(
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class PaginatedTemplatesResponseDto(
    @SerializedName("data") val data: List<TemplateDto>,
    @SerializedName("meta") val meta: TemplatePaginationMetaDto
)

data class TemplateCategoryDto(
    @SerializedName("category") val category: String,
    @SerializedName("label") val label: String,
    @SerializedName("templatesCount") val templatesCount: Int,
    @SerializedName("shortLabel") val shortLabel: String = label
)

data class TemplateCategoriesResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("categories") val categories: List<TemplateCategoryDto>
)
