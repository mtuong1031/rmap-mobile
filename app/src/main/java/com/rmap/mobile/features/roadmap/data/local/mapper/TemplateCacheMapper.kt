package com.rmap.mobile.features.roadmap.data.local.mapper

import com.rmap.mobile.features.roadmap.data.local.entity.TemplateCategoryEntity
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateRoadmapEntity
import com.rmap.mobile.features.roadmap.data.remote.model.TemplateCategoryDto
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryIcon

fun TemplateCategoryDto.toEntity(): TemplateCategoryEntity {
    return TemplateCategoryEntity(
        id = category,
        name = label,
        shortName = shortLabel,
        roadmapCount = templatesCount
    )
}

fun TemplateCategoryEntity.toRoadmapCategory(): RoadmapCategory {
    return RoadmapCategory(
        id = id,
        name = name,
        icon = id.toRoadmapCategoryIcon(),
        shortName = shortName,
        roadmapCount = roadmapCount
    )
}

fun RoadmapSummary.toEntity(): TemplateRoadmapEntity {
    return TemplateRoadmapEntity(
        id = id,
        title = title,
        totalLessonsCount = totalLessonsCount,
        completedLessonsCount = completedLessonsCount,
        difficulty = difficulty.name,
        durationLabel = durationLabel,
        icon = icon.name,
        categoryId = categoryId,
        recommendationBadge = recommendationBadge,
        skillNodesCount = skillNodesCount
    )
}

fun TemplateRoadmapEntity.toRoadmapSummary(): RoadmapSummary {
    return RoadmapSummary(
        id = id,
        title = title,
        totalLessonsCount = totalLessonsCount,
        completedLessonsCount = completedLessonsCount,
        difficulty = enumValueOrDefault(difficulty, LearningDifficulty.Beginner),
        durationLabel = durationLabel,
        icon = enumValueOrDefault(icon, LearningTopicIcon.Code),
        categoryId = categoryId,
        recommendationBadge = recommendationBadge,
        skillNodesCount = skillNodesCount
    )
}

private inline fun <reified T : Enum<T>> enumValueOrDefault(
    value: String,
    default: T
): T {
    return runCatching { enumValueOf<T>(value) }.getOrDefault(default)
}
