package com.rmap.mobile.features.myroadmap.domain.repository

import com.rmap.mobile.features.myroadmap.domain.model.CompletedSkillPage

interface CompletedSkillsRepository {
    suspend fun getCompletedSkills(
        category: String,
        page: Int,
        perPage: Int
    ): Result<CompletedSkillPage>
}
