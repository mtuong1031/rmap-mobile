package com.rmap.mobile.features.roadmap.domain.repository

import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent

interface SkillLearningRepository {
    suspend fun getSkillLearningContent(skillId: String): Result<SkillLearningContent>
}
