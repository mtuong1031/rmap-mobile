package com.rmap.mobile.features.roadmap.domain.repository

import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail

interface SkillLearningRepository {
    suspend fun getSkillDetail(skillId: String): Result<SkillDetail>
    suspend fun getSkillLearningContent(skillId: String): Result<SkillLearningContent>
}
