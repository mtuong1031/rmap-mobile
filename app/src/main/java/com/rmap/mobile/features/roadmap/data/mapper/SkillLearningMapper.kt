package com.rmap.mobile.features.roadmap.data.mapper

import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourceDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourcesResponseDto
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillLevelTag
import com.rmap.mobile.features.roadmap.domain.model.SkillResource
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform

fun SkillDetailDto.toDomain(): SkillDetail {
    return SkillDetail(
        id = id.requiredSkillApiField("id"),
        name = name.requiredSkillApiField("name"),
        description = description,
        category = category,
        estimatedHours = estimatedHours
    )
}

fun SkillResourcesResponseDto.toDomain(): List<SkillResource> {
    return data.orEmpty().map { resource -> resource.toDomain() }
}

fun SkillResourceDto.toDomain(defaultSkillId: String? = null): SkillResource {
    return SkillResource(
        id = id.requiredSkillApiField("resource.id"),
        skillId = skillId
            ?.takeIf { it.isNotBlank() }
            ?: defaultSkillId.requiredSkillApiField("resource.skill_id"),
        title = title.requiredSkillApiField("resource.title"),
        url = url.requiredSkillApiField("resource.url"),
        platform = platform.toSkillResourcePlatform(),
        rawPlatform = platform,
        isFree = isFree == true,
        levelTag = levelTag.toSkillLevelTag()
    )
}

private fun String?.toSkillResourcePlatform(): SkillResourcePlatform {
    return when (this?.trim()?.lowercase()) {
        "udemy" -> SkillResourcePlatform.Udemy
        "coursera" -> SkillResourcePlatform.Coursera
        "youtube" -> SkillResourcePlatform.Youtube
        "course" -> SkillResourcePlatform.Course
        "article", "blog" -> SkillResourcePlatform.Article
        else -> SkillResourcePlatform.Other
    }
}

private fun String?.toSkillLevelTag(): SkillLevelTag? {
    return when (this?.lowercase()) {
        "fresher" -> SkillLevelTag.Fresher
        "junior" -> SkillLevelTag.Junior
        "middle" -> SkillLevelTag.Middle
        else -> null
    }
}

private fun String?.requiredSkillApiField(fieldName: String): String {
    return this?.takeIf { it.isNotBlank() }
        ?: error("Missing skill API field: $fieldName")
}
