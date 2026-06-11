package com.rmap.mobile.features.home.data.local.mapper

import com.rmap.mobile.features.home.data.local.entity.HomeTrendingRoadmapEntity
import com.rmap.mobile.features.home.data.model.HomeTemplateCategoryDto
import com.rmap.mobile.features.home.data.model.HomeTrendingRoadmapDto
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateCategoryEntity

fun HomeTemplateCategoryDto.toTemplateCategoryEntity(): TemplateCategoryEntity {
    return TemplateCategoryEntity(
        id = category,
        name = label,
        shortName = shortLabel,
        roadmapCount = templatesCount
    )
}

fun TemplateCategoryEntity.toHomeDto(): HomeTemplateCategoryDto {
    return HomeTemplateCategoryDto(
        category = id,
        label = name,
        templatesCount = roadmapCount,
        shortLabel = shortName
    )
}

fun HomeTrendingRoadmapDto.toEntity(): HomeTrendingRoadmapEntity {
    return HomeTrendingRoadmapEntity(
        roadmapId = roadmapId,
        rank = rank,
        title = title,
        roleCategory = roleCategory,
        categoryLabel = categoryLabel,
        estimatedWeeks = estimatedWeeks,
        durationLabel = durationLabel,
        nodesTotal = nodesTotal,
        trendText = trendText
    )
}

fun HomeTrendingRoadmapEntity.toDto(): HomeTrendingRoadmapDto {
    return HomeTrendingRoadmapDto(
        rank = rank,
        roadmapId = roadmapId,
        title = title,
        roleCategory = roleCategory,
        categoryLabel = categoryLabel,
        estimatedWeeks = estimatedWeeks,
        durationLabel = durationLabel,
        nodesTotal = nodesTotal,
        trendText = trendText
    )
}
