package com.rmap.mobile.features.myroadmap.domain.model

data class CompletedSkill(
    val id: String,
    val name: String,
    val category: String,
    val completedAt: String
)

data class CompletedSkillPage(
    val skills: List<CompletedSkill>,
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int
) {
    val hasMore: Boolean
        get() = page < totalPages
}
