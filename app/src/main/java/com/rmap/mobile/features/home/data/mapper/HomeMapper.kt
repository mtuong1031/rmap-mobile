package com.rmap.mobile.features.home.data.mapper

import com.rmap.mobile.features.home.data.model.HomeActiveRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeDashboardSearchResponseDto
import com.rmap.mobile.features.home.data.model.HomeMetricsDto
import com.rmap.mobile.features.home.data.model.HomeNextUnlockDto
import com.rmap.mobile.features.home.data.model.HomePaceWarningDto
import com.rmap.mobile.features.home.data.model.HomePlanNodeDto
import com.rmap.mobile.features.home.data.model.HomePublicTemplateDto
import com.rmap.mobile.features.home.data.model.HomePublicTemplatesResponseDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapChapterDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapGroupDto
import com.rmap.mobile.features.home.data.model.HomeRoadmapProgressDto
import com.rmap.mobile.features.home.data.model.HomeSearchPageMetaDto
import com.rmap.mobile.features.home.data.model.HomeSearchRoadmapDto
import com.rmap.mobile.features.home.data.model.HomeSearchRoadmapsPageDto
import com.rmap.mobile.features.home.data.model.HomeSearchSkillDto
import com.rmap.mobile.features.home.data.model.HomeSearchSkillsPageDto
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
import com.rmap.mobile.features.home.domain.model.HomeSearchPageMeta
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import com.rmap.mobile.features.home.domain.model.HomeSearchRoadmap
import com.rmap.mobile.features.home.domain.model.HomeSearchRoadmapsPage
import com.rmap.mobile.features.home.domain.model.HomeSearchSkill
import com.rmap.mobile.features.home.domain.model.HomeSearchSkillsPage
import com.rmap.mobile.features.home.domain.model.HomeTemplateCategory
import com.rmap.mobile.features.home.domain.model.HomeTemplateRoadmap
import com.rmap.mobile.features.home.domain.model.HomeTrendingRoadmap

fun toHomeContent(
    dashboard: HomeDashboardResponseDto?,
    templates: HomePublicTemplatesResponseDto
): HomeContent {
    val templateRoadmaps = templates.data.map { it.toHomeTemplateRoadmap() }
    val categories = templateRoadmaps
        .groupBy { it.roleCategory }
        .map { (category, roadmaps) ->
            HomeTemplateCategory(
                category = category,
                label = category.toCategoryLabel().orEmpty(),
                templatesCount = roadmaps.size
            )
        }
    val trendings = templateRoadmaps.mapIndexed { index, roadmap ->
        HomeTrendingRoadmap(
            rank = index + 1,
            roadmapId = roadmap.roadmapId,
            title = roadmap.title,
            roleCategory = roadmap.roleCategory,
            categoryLabel = roadmap.categoryLabel,
            estimatedWeeks = roadmap.estimatedWeeks,
            durationLabel = roadmap.durationLabel,
            nodesTotal = roadmap.nodesTotal,
            trendText = roadmap.categoryLabel
        )
    }

    return HomeContent(
        activeRoadmaps = dashboard?.activeRoadmaps.orEmpty().map { it.toDomain() },
        metrics = dashboard?.metrics?.toDomain() ?: HomeMetrics(
            roadmapCompletionPct = 0.0,
            streakDays = 0,
            readinessPct = 0.0
        ),
        recommendations = templateRoadmaps,
        categories = categories,
        trendings = trendings
    )
}

fun HomePublicTemplatesResponseDto.toSearchDomain(
    query: String,
    roadmapPage: Int,
    roadmapPageSize: Int,
    skillPage: Int
): HomeSearchResult {
    val normalizedQuery = query.trim()
    val matchingTemplates = data.filter { template ->
        normalizedQuery.isBlank() ||
            template.title.contains(normalizedQuery, ignoreCase = true) ||
            template.description.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
            template.goalName.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
            template.roleCategory.contains(normalizedQuery, ignoreCase = true) ||
            template.roleCategory.toCategoryLabel()
                .orEmpty()
                .contains(normalizedQuery, ignoreCase = true)
    }
    val safeRoadmapPage = roadmapPage.coerceAtLeast(1)
    val startIndex = (safeRoadmapPage - 1) * roadmapPageSize
    val roadmaps = matchingTemplates
        .drop(startIndex)
        .take(roadmapPageSize)
        .map { it.toHomeSearchRoadmap() }
    val totalPages = if (matchingTemplates.isEmpty()) {
        0
    } else {
        (matchingTemplates.size + roadmapPageSize - 1) / roadmapPageSize
    }

    return HomeSearchResult(
        query = query,
        roadmaps = HomeSearchRoadmapsPage(
            data = roadmaps,
            meta = HomeSearchPageMeta(
                page = safeRoadmapPage,
                perPage = roadmapPageSize,
                total = matchingTemplates.size,
                totalPages = totalPages
            )
        ),
        skills = HomeSearchSkillsPage(
            data = emptyList(),
            meta = HomeSearchPageMeta(
                page = skillPage.coerceAtLeast(1),
                perPage = 0,
                total = 0,
                totalPages = 0
            )
        ),
        totalResults = matchingTemplates.size,
        roadmapPageSize = roadmapPageSize,
        skillPageSize = 0
    )
}

fun HomeDashboardSearchResponseDto.toDomain(): HomeSearchResult {
    return HomeSearchResult(
        query = query,
        roadmaps = roadmaps.toDomain(),
        skills = skills.toDomain(),
        totalResults = meta.totalResults,
        roadmapPageSize = meta.roadmapPageSize,
        skillPageSize = meta.skillPageSize
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

private fun HomeSearchRoadmapsPageDto.toDomain(): HomeSearchRoadmapsPage {
    return HomeSearchRoadmapsPage(
        data = data.map { it.toDomain() },
        meta = meta.toDomain()
    )
}

private fun HomeSearchSkillsPageDto.toDomain(): HomeSearchSkillsPage {
    return HomeSearchSkillsPage(
        data = data.map { it.toDomain() },
        meta = meta.toDomain()
    )
}

private fun HomeSearchRoadmapDto.toDomain(): HomeSearchRoadmap {
    return HomeSearchRoadmap(
        roadmapId = roadmapId,
        title = title,
        description = description,
        goalName = goalName,
        isTemplate = isTemplate,
        roadmapType = roadmapType,
        roleCategory = roleCategory,
        categoryLabel = categoryLabel,
        estimatedWeeks = estimatedWeeks,
        durationLabel = durationLabel
    )
}

private fun HomeSearchSkillDto.toDomain(): HomeSearchSkill {
    return HomeSearchSkill(
        skillId = skillId,
        name = name,
        description = description,
        roleCategory = roleCategory.normalizedCategoryId(fallback = categoryLabel ?: name),
        categoryLabel = categoryLabel?.takeIf { it.isNotBlank() }
            ?: roleCategory.toCategoryLabel()
            ?: name,
        defaultEstimatedHours = defaultEstimatedHours
    )
}

private fun HomeSearchPageMetaDto.toDomain(): HomeSearchPageMeta {
    return HomeSearchPageMeta(
        page = page,
        perPage = perPage,
        total = total,
        totalPages = totalPages
    )
}

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

private fun HomePublicTemplateDto.toHomeTemplateRoadmap(): HomeTemplateRoadmap {
    val categoryLabel = roleCategory.toCategoryLabel().orEmpty()
    return HomeTemplateRoadmap(
        roadmapId = id,
        title = title,
        description = description,
        goalName = goalName?.takeIf { it.isNotBlank() } ?: title,
        roleCategory = roleCategory,
        categoryLabel = categoryLabel,
        estimatedWeeks = estimatedWeeks,
        durationLabel = estimatedWeeks?.let { "$it weeks" },
        nodesTotal = 0,
        requiredNodesTotal = 0
    )
}

private fun HomePublicTemplateDto.toHomeSearchRoadmap(): HomeSearchRoadmap {
    return HomeSearchRoadmap(
        roadmapId = id,
        title = title,
        description = description,
        goalName = goalName?.takeIf { it.isNotBlank() } ?: title,
        isTemplate = isTemplate,
        roadmapType = "template",
        roleCategory = roleCategory,
        categoryLabel = roleCategory.toCategoryLabel().orEmpty(),
        estimatedWeeks = estimatedWeeks,
        durationLabel = estimatedWeeks?.let { "$it weeks" }
    )
}

private fun HomeTemplateCategoryDto.toDomain(): HomeTemplateCategory = HomeTemplateCategory(
    category = category,
    label = label,
    templatesCount = templatesCount,
    shortLabel = shortLabel
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

private fun String?.normalizedCategoryId(fallback: String): String {
    return takeIf { !it.isNullOrBlank() }
        ?: fallback.toStableCategoryId()
}

private fun String?.toCategoryLabel(): String? {
    return takeIf { !it.isNullOrBlank() }
        ?.split('_')
        ?.filter { it.isNotBlank() }
        ?.joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { firstChar -> firstChar.uppercase() }
        }
}

private fun String.toStableCategoryId(): String {
    return trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .uppercase()
        .ifBlank { "UNCATEGORIZED" }
}
