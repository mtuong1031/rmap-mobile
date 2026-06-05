package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class RoadmapDetailUiState(
    val roadmapId: String = "",
    val title: String = "",
    val categoryLabel: String = "",
    val progressFraction: Float = 0f,
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val completedRequiredNodes: Int = 0,
    val totalRequiredNodes: Int = 0,
    val nextActionTitle: String = "",
    val nextActionTarget: RoadmapNextActionTarget = RoadmapNextActionTarget.None,
    val primaryAction: RoadmapPrimaryAction = RoadmapPrimaryAction.StartLearning,
    val nextUnlockTitle: String = "",
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isSearchInputFocused: Boolean = false,
    val isBookmarked: Boolean = false,
    val groups: List<RoadmapGroupUiModel> = emptyList(),
    val milestones: List<RoadmapMilestoneUiModel> = emptyList(),
    val contentItems: List<RoadmapDetailContentUiItem> = emptyList(),
    val updatingNodeId: String? = null,
    val isEmpty: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes val errorMessageResId: Int? = null,
)

data class RoadmapGroupUiModel(
    val id: String,
    val title: String,
    val completedRequiredNodes: Int,
    val totalRequiredNodes: Int,
    val progressFraction: Float,
    val state: RoadmapGroupState,
    val nodes: List<RoadmapNodeUiModel> = emptyList()
)

enum class RoadmapGroupState {
    Expanded,
    Completed,
    Locked
}

sealed class RoadmapDetailContentUiItem {
    data class Group(val group: RoadmapGroupUiModel) : RoadmapDetailContentUiItem()
    data class Milestone(val milestone: RoadmapMilestoneUiModel) : RoadmapDetailContentUiItem()
}

data class RoadmapNodeUiModel(
    val id: String,
    val skillId: String = id,
    val title: String,
    val icon: ImageVector,
    val status: RoadmapNodeStatus,
    val requirement: RoadmapNodeRequirement,
    @StringRes val descriptionResId: Int,
    val descriptionArgs: List<String> = emptyList(),
    val resourcesCount: Int = 0,
    val action: RoadmapNodeAction? = null
)

enum class RoadmapNodeStatus {
    Completed,
    InProgress,
    NotStarted,
    Locked
}

enum class RoadmapNodeRequirement {
    Required,
    Optional
}

enum class RoadmapNodeAction {
    Start,
    Review,
    Continue
}

enum class RoadmapPrimaryAction {
    StartLearning,
    ContinueLearning
}

sealed class RoadmapNextActionTarget {
    data object None : RoadmapNextActionTarget()
    data class Node(val nodeId: String) : RoadmapNextActionTarget()
    data class Milestone(val milestoneId: String) : RoadmapNextActionTarget()
}

data class RoadmapMilestoneUiModel(
    val id: String,
    val title: String,
    val description: String,
    val state: RoadmapMilestoneState
)

enum class RoadmapMilestoneState {
    Available,
    Locked
}

internal fun RoadmapDetailUiState.currentSearchNode(): RoadmapNodeUiModel? {
    val nodes = allSearchNodes()
    return nodes.firstOrNull { it.status == RoadmapNodeStatus.InProgress }
        ?: nodes.firstOrNull { it.status == RoadmapNodeStatus.NotStarted }
        ?: nodes.firstOrNull { it.action == RoadmapNodeAction.Continue }
        ?: nodes.firstOrNull { it.action == RoadmapNodeAction.Review }
}

internal fun RoadmapDetailUiState.recentSearchNodes(): List<RoadmapNodeUiModel> {
    val currentNodeId = currentSearchNode()?.id
    return allSearchNodes()
        .filter { it.id != currentNodeId }
        .take(SearchPreviewRecentNodeLimit)
}

internal fun RoadmapDetailUiState.recentSearchMilestones(): List<RoadmapMilestoneUiModel> {
    return milestones.take(SearchPreviewRecentMilestoneLimit)
}

internal fun RoadmapDetailUiState.searchResultNodes(): List<RoadmapNodeUiModel> {
    val query = searchQuery.trim()
    if (query.isEmpty()) return emptyList()
    val nodes = allSearchNodes()

    return nodes
        .filter { node ->
            node.title.contains(query, ignoreCase = true) ||
                node.descriptionArgs.any { it.contains(query, ignoreCase = true) }
        }
        .ifEmpty { nodes.take(SearchPreviewResultNodeFallbackLimit) }
}

internal fun RoadmapDetailUiState.searchResultGroups(): List<RoadmapGroupUiModel> {
    val query = searchQuery.trim()
    if (query.isEmpty()) return emptyList()

    return groups
        .filter { group ->
            group.title.contains(query, ignoreCase = true) ||
                group.nodes.any { it.title.contains(query, ignoreCase = true) }
        }
        .ifEmpty { groups.take(SearchPreviewResultGroupFallbackLimit) }
}

internal fun RoadmapDetailUiState.searchResultMilestones(): List<RoadmapMilestoneUiModel> {
    val query = searchQuery.trim()
    if (query.isEmpty()) return emptyList()

    return milestones
        .filter { it.id.contains(query, ignoreCase = true) }
        .ifEmpty { milestones.take(SearchPreviewResultMilestoneFallbackLimit) }
}

private fun RoadmapDetailUiState.allSearchNodes(): List<RoadmapNodeUiModel> {
    return groups.flatMap { it.nodes }
}

private const val SearchPreviewRecentNodeLimit = 2
private const val SearchPreviewRecentMilestoneLimit = 1
private const val SearchPreviewResultNodeFallbackLimit = 2
private const val SearchPreviewResultGroupFallbackLimit = 1
private const val SearchPreviewResultMilestoneFallbackLimit = 1
