package com.rmap.mobile.features.home.data.mapper

import com.rmap.mobile.features.home.data.model.HomeActiveRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeMetricsDto
import com.rmap.mobile.features.home.data.model.HomeNextUnlockDto
import com.rmap.mobile.features.home.data.model.HomePaceWarningDto
import com.rmap.mobile.features.home.data.model.HomePlanNodeDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapChapterDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapGroupDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapProgressDto
import com.rmap.mobile.features.home.data.model.HomeTemplateCategoriesResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateCategoryDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRecommendationsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeTemplateTrendingsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTrendingRoadmapDto
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeMetrics
import com.rmap.mobile.features.home.domain.model.HomeNextUnlock
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomePlanNode
import com.rmap.mobile.features.home.domain.model.HomeRoadmapChapter
import com.rmap.mobile.features.home.domain.model.HomeRoadmapGroup
import com.rmap.mobile.features.home.domain.model.HomeRoadmapProgress
import com.rmap.mobile.features.home.domain.model.HomeTemplateCategory
import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap

fun toHomeContent(
    dashboard: HomeDashboardResponseDto,
    recommendations: HomeTemplateRecommendationsResponseDto,
    categories: HomeTemplateCategoriesResponseDto,
    trendings: HomeTemplateTrendingsResponseDto
): HomeContent {
    return HomeContent(
        activeRoadmaps = dashboard.activeRoadmaps.map { it.toDomain() },
        metrics = dashboard.metrics.toDomain(),
        recommendations = recommendations.relevantRoadmaps.map { it.toDomain() },
        categories = categories.categories.map { it.toDomain() },
        trendings = trendings.trendings.map { it.toDomain() }
    )
}

private fun HomeActiveRoadmapDto.toDomain(): HomeActiveRoadmap = HomeActiveRoadmap(
    roadmapId = roadmapId,
    title = title,
    goalName = goalName,
    roleCategory = roleCategory,
    startedAt = startedAt,
    currentGroup = currentGroup?.toDomain(),
    planNode = planNode?.toDomain(),
    chapter = chapter?.toDomain(),
    progress = progress.toDomain(),
    nextUnlock = nextUnlock?.toDomain(),
    paceWarning = paceWarning?.toDomain()
)

private fun HomeRoadmapGroupDto.toDomain(): HomeRoadmapGroup = HomeRoadmapGroup(id = id, name = name)

private fun HomePlanNodeDto.toDomain(): HomePlanNode = HomePlanNode(
    id = id,
    name = name,
    description = description,
    nodeType = nodeType,
    estimatedHours = estimatedHours
)

private fun HomeRoadmapChapterDto.toDomain(): HomeRoadmapChapter = HomeRoadmapChapter(
    current = current,
    total = total,
    label = label
)

private fun HomeRoadmapProgressDto.toDomain(): HomeRoadmapProgress = HomeRoadmapProgress(
    requiredNodesCompleted = requiredNodesCompleted,
    requiredNodesTotal = requiredNodesTotal,
    requiredCompletionPct = requiredCompletionPct
)

private fun HomeNextUnlockDto.toDomain(): HomeNextUnlock = HomeNextUnlock(id = id, name = name)

private fun HomePaceWarningDto.toDomain(): HomePaceWarning = HomePaceWarning(
    isBehind = isBehind,
    paceDeficitPct = paceDeficitPct,
    estimatedDelayDays = estimatedDelayDays,
    message = message,
    title = title,
    actionLabel = actionLabel
)

private fun HomeMetricsDto.toDomain(): HomeMetrics = HomeMetrics(
    roadmapCompletionPct = roadmapCompletionPct,
    streakDays = streakDays,
    readinessPct = readinessPct
)

private fun HomeTemplateRoadmapDto.toDomain(): HomeTemplateRoadmap = HomeTemplateRoadmap(
    roadmapId = roadmapId,
    title = title,
    description = description,
    goalName = goalName,
    roleCategory = roleCategory,
    categoryLabel = categoryLabel,
    estimatedWeeks = estimatedWeeks,
    durationLabel = durationLabel,
    nodesTotal = nodesTotal,
    requiredNodesTotal = requiredNodesTotal
)

private fun HomeTemplateCategoryDto.toDomain(): HomeTemplateCategory = HomeTemplateCategory(
    category = category,
    label = label,
    templatesCount = templatesCount
)

private fun HomeTrendingRoadmapDto.toDomain(): HomeTrendingRoadmap = HomeTrendingRoadmap(
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
