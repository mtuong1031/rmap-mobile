package com.rmap.mobile.features.myroadmap.data.mapper

import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillDto
import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillsResponseDto
import com.rmap.mobile.features.myroadmap.domain.model.CompletedSkill
import com.rmap.mobile.features.myroadmap.domain.model.CompletedSkillPage

fun CompletedSkillsResponseDto.toDomain(): CompletedSkillPage {
    return CompletedSkillPage(
        skills = data.distinctBy { it.skillId }.map(CompletedSkillDto::toDomain),
        page = meta.page,
        perPage = meta.perPage,
        total = meta.total,
        totalPages = meta.totalPages
    )
}

private fun CompletedSkillDto.toDomain(): CompletedSkill {
    return CompletedSkill(
        id = skillId,
        name = skillName,
        category = category,
        completedAt = completedAt
    )
}
