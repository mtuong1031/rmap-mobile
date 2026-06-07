package com.rmap.mobile.features.roadmap.data.mapper

import com.rmap.mobile.core.domain.model.toRMapCategoryIconKey
import com.rmap.mobile.features.roadmap.data.model.TemplateCategoryDto
import com.rmap.mobile.features.roadmap.data.model.TemplateDto
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary

fun TemplateCategoryDto.toDomain(): RoadmapCategory {
    return RoadmapCategory(
        id = category,
        name = label,
        icon = category.toLearningTopicIcon(),
        shortName = shortLabel,
        roadmapCount = templatesCount
    )
}

fun TemplateDto.toRoadmapSummary(): RoadmapSummary {
    return RoadmapSummary(
        id = id,
        title = title,
        totalLessonsCount = 0,
        completedLessonsCount = 0,
        difficulty = LearningDifficulty.Intermediate,
        durationLabel = estimatedWeeks?.let { "$it weeks" } ?: "Self-paced",
        icon = roleCategory.toLearningTopicIcon(),
        categoryId = roleCategory,
        recommendationBadge = if (isTemplate) "Template" else null,
        skillNodesCount = 0,
        coverPlaceholder = null
    )
}

private fun String.toLearningTopicIcon(): LearningTopicIcon {
    return when (toRMapCategoryIconKey()) {
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Code -> LearningTopicIcon.Code
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.DataObject -> LearningTopicIcon.DataObject
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Devices -> LearningTopicIcon.Devices
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Game -> LearningTopicIcon.Game
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Palette -> LearningTopicIcon.Palette
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Science -> LearningTopicIcon.Science
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Security -> LearningTopicIcon.Security
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.SmartToy -> LearningTopicIcon.SmartToy
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Storage -> LearningTopicIcon.Storage
        com.rmap.mobile.core.domain.model.RMapCategoryIconKey.Terminal -> LearningTopicIcon.Terminal
    }
}
