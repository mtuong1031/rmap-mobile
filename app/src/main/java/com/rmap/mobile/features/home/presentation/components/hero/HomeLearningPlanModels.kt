package com.rmap.mobile.features.home.presentation.components.hero

import kotlin.math.roundToInt

data class HomeLearningPlanUiModel(
    val id: String,
    val roadmapTitle: String,
    val skillTitle: String,
    val chapterText: String? = null,
    val requiredSkillText: String,
    val timeLeftText: String? = null,
    val completedRequiredNodes: Int,
    val totalRequiredNodes: Int,
    val progressPercentage: Int? = null,
    val nextUnlockText: String? = null,
    val currentNodeId: String? = null,
    val lastStudiedAtMillis: Long? = null,
    val lastAccessedAtMillis: Long? = null,
    val updatedAtMillis: Long? = null,
    val createdAtMillis: Long? = null,
    val startedAtMillis: Long? = null,
    val canContinue: Boolean = true
)

internal enum class LearningPlanCardVariant {
    Large,
    Carousel
}

internal fun getInProgressRoadmaps(
    roadmaps: List<HomeLearningPlanUiModel>
): List<HomeLearningPlanUiModel> {
    return roadmaps.sortedWith(::sortInProgressRoadmaps)
}

private fun sortInProgressRoadmaps(
    first: HomeLearningPlanUiModel,
    second: HomeLearningPlanUiModel
): Int {
    val firstLastStudied = first.lastStudiedAtMillis
        ?: first.lastAccessedAtMillis
        ?: first.updatedAtMillis
        ?: first.createdAtMillis
        ?: 0L
    val secondLastStudied = second.lastStudiedAtMillis
        ?: second.lastAccessedAtMillis
        ?: second.updatedAtMillis
        ?: second.createdAtMillis
        ?: 0L

    if (firstLastStudied != secondLastStudied) {
        return secondLastStudied.compareTo(firstLastStudied)
    }

    if (first.canContinue != second.canContinue) {
        return if (second.canContinue) 1 else -1
    }

    val firstProgress = getProgressPercentage(
        completedRequiredNodes = first.completedRequiredNodes,
        totalRequiredNodes = first.totalRequiredNodes,
        progressPercentage = first.progressPercentage
    )
    val secondProgress = getProgressPercentage(
        completedRequiredNodes = second.completedRequiredNodes,
        totalRequiredNodes = second.totalRequiredNodes,
        progressPercentage = second.progressPercentage
    )

    if (firstProgress != secondProgress) {
        return secondProgress.compareTo(firstProgress)
    }

    val firstStartedAt = first.startedAtMillis ?: first.createdAtMillis ?: 0L
    val secondStartedAt = second.startedAtMillis ?: second.createdAtMillis ?: 0L
    return secondStartedAt.compareTo(firstStartedAt)
}

internal fun getProgressPercentage(
    completedRequiredNodes: Int,
    totalRequiredNodes: Int,
    progressPercentage: Int?
): Int {
    progressPercentage?.let { return clampProgress(it) }

    if (totalRequiredNodes <= 0) {
        return 0
    }

    return ((completedRequiredNodes.toFloat() / totalRequiredNodes.toFloat()) * 100)
        .roundToInt()
        .let(::clampProgress)
}

private fun clampProgress(value: Int): Int {
    return value.coerceIn(0, 100)
}
