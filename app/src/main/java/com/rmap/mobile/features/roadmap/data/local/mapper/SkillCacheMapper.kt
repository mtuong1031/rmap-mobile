package com.rmap.mobile.features.roadmap.data.local.mapper

import com.rmap.mobile.features.roadmap.data.local.entity.SkillEntity
import com.rmap.mobile.features.roadmap.data.local.entity.SkillResourceEntity
import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourceDto
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillLevelTag
import com.rmap.mobile.features.roadmap.domain.model.SkillResource
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform

fun SkillDetailDto.toEntity(): SkillEntity {
    return SkillEntity(
        id = id.requiredCacheField("skill.id"),
        name = name.requiredCacheField("skill.name"),
        description = description,
        category = category,
        estimatedHours = estimatedHours
    )
}

fun SkillResourceDto.toEntity(defaultSkillId: String): SkillResourceEntity {
    return SkillResourceEntity(
        id = id.requiredCacheField("resource.id"),
        skillId = skillId?.takeIf { it.isNotBlank() } ?: defaultSkillId,
        title = title.requiredCacheField("resource.title"),
        url = url.requiredCacheField("resource.url"),
        platform = platform,
        isFree = isFree == true,
        levelTag = levelTag
    )
}

fun SkillEntity.toSkillDetail(): SkillDetail {
    return SkillDetail(
        id = id,
        name = name,
        description = description,
        category = category,
        estimatedHours = estimatedHours
    )
}

fun SkillResourceEntity.toSkillResource(): SkillResource {
    return SkillResource(
        id = id,
        skillId = skillId,
        title = title,
        url = url,
        platform = platform.toSkillResourcePlatform(),
        rawPlatform = platform,
        isFree = isFree,
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

private fun String?.requiredCacheField(name: String): String {
    return takeIf { !it.isNullOrBlank() } ?: error("Missing cache field: $name")
}
