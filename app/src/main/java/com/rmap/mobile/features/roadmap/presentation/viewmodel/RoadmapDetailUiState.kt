package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.Normalizer
import java.util.Locale

data class RoadmapDetailUiState(
    val roadmapId: String = "",
    val isTemplate: Boolean = false,
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
    val groups: List<RoadmapGroupUiModel> = emptyList(),
    val milestones: List<RoadmapMilestoneUiModel> = emptyList(),
    val contentItems: List<RoadmapDetailContentUiItem> = emptyList(),
    val updatingNodeId: String? = null,
    val isEmpty: Boolean = true,
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageResId: Int? = null,
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
    @param:StringRes val descriptionResId: Int,
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
    val query = searchQuery.toRoadmapSearchQuery() ?: return emptyList()

    return groups.flatMap { group ->
        group.nodes.filter { node ->
            node.matchesSearchQuery(query, groupTitle = group.title)
        }
    }
}

internal fun RoadmapDetailUiState.searchResultGroups(): List<RoadmapGroupUiModel> {
    val query = searchQuery.toRoadmapSearchQuery() ?: return emptyList()

    return groups
        .filter { group -> group.matchesSearchQuery(query) }
}

internal fun RoadmapDetailUiState.searchResultMilestones(): List<RoadmapMilestoneUiModel> {
    val query = searchQuery.toRoadmapSearchQuery() ?: return emptyList()

    return milestones
        .filter { milestone -> milestone.matchesSearchQuery(query) }
}

private fun RoadmapDetailUiState.allSearchNodes(): List<RoadmapNodeUiModel> {
    return groups.flatMap { it.nodes }
}

private data class RoadmapSearchQuery(
    val normalizedText: String,
    val tokens: List<String>,
    val semanticFilter: RoadmapSearchSemanticFilter?
)

private enum class RoadmapSearchSemanticFilter {
    Required,
    Optional,
    Completed,
    InProgress,
    NotStarted,
    Locked,
    Milestone,
    Chapter
}

private fun String.toRoadmapSearchQuery(): RoadmapSearchQuery? {
    val normalizedText = toSearchText()
    if (normalizedText.isBlank()) return null

    return RoadmapSearchQuery(
        normalizedText = normalizedText,
        tokens = normalizedText.split(SearchTokenSeparator).filter { it.isNotBlank() },
        semanticFilter = normalizedText.toSemanticFilter()
    )
}

private fun RoadmapNodeUiModel.matchesSearchQuery(
    query: RoadmapSearchQuery,
    groupTitle: String
): Boolean {
    query.semanticFilter?.let { filter ->
        return when (filter) {
            RoadmapSearchSemanticFilter.Required -> requirement == RoadmapNodeRequirement.Required
            RoadmapSearchSemanticFilter.Optional -> requirement == RoadmapNodeRequirement.Optional
            RoadmapSearchSemanticFilter.Completed -> status == RoadmapNodeStatus.Completed
            RoadmapSearchSemanticFilter.InProgress -> status == RoadmapNodeStatus.InProgress
            RoadmapSearchSemanticFilter.NotStarted -> status == RoadmapNodeStatus.NotStarted
            RoadmapSearchSemanticFilter.Locked -> status == RoadmapNodeStatus.Locked
            RoadmapSearchSemanticFilter.Milestone,
            RoadmapSearchSemanticFilter.Chapter -> false
        }
    }

    return searchText(
        id,
        skillId,
        title,
        groupTitle,
        requirement.searchLabel,
        status.searchLabel,
        action?.searchLabel.orEmpty(),
        descriptionArgs.joinToString(separator = SearchTextSeparator)
    ).matchesTokens(query)
}

private fun RoadmapGroupUiModel.matchesSearchQuery(query: RoadmapSearchQuery): Boolean {
    query.semanticFilter?.let { filter ->
        return when (filter) {
            RoadmapSearchSemanticFilter.Chapter -> true
            RoadmapSearchSemanticFilter.Completed -> state == RoadmapGroupState.Completed
            RoadmapSearchSemanticFilter.Locked -> state == RoadmapGroupState.Locked ||
                nodes.any { it.status == RoadmapNodeStatus.Locked }
            RoadmapSearchSemanticFilter.Required -> nodes.any { it.requirement == RoadmapNodeRequirement.Required }
            RoadmapSearchSemanticFilter.Optional -> nodes.any { it.requirement == RoadmapNodeRequirement.Optional }
            RoadmapSearchSemanticFilter.InProgress -> nodes.any { it.status == RoadmapNodeStatus.InProgress }
            RoadmapSearchSemanticFilter.NotStarted -> nodes.any { it.status == RoadmapNodeStatus.NotStarted }
            RoadmapSearchSemanticFilter.Milestone -> false
        }
    }

    return searchText(
        id,
        title,
        state.searchLabel,
        nodes.joinToString(separator = SearchTextSeparator) { node ->
            searchText(
                node.id,
                node.skillId,
                node.title,
                node.requirement.searchLabel,
                node.status.searchLabel,
                node.descriptionArgs.joinToString(separator = SearchTextSeparator)
            )
        }
    ).matchesTokens(query)
}

private fun RoadmapMilestoneUiModel.matchesSearchQuery(query: RoadmapSearchQuery): Boolean {
    query.semanticFilter?.let { filter ->
        return when (filter) {
            RoadmapSearchSemanticFilter.Milestone -> true
            RoadmapSearchSemanticFilter.Locked -> state == RoadmapMilestoneState.Locked
            RoadmapSearchSemanticFilter.InProgress -> state == RoadmapMilestoneState.Available
            RoadmapSearchSemanticFilter.Completed,
            RoadmapSearchSemanticFilter.Required,
            RoadmapSearchSemanticFilter.Optional,
            RoadmapSearchSemanticFilter.NotStarted,
            RoadmapSearchSemanticFilter.Chapter -> false
        }
    }

    return searchText(
        id,
        title,
        description,
        state.searchLabel,
        RoadmapSearchSemanticFilter.Milestone.name
    ).matchesTokens(query)
}

private fun String.matchesTokens(query: RoadmapSearchQuery): Boolean {
    return contains(query.normalizedText) || query.tokens.all(::contains)
}

private fun searchText(vararg values: String): String {
    return values
        .filter { it.isNotBlank() }
        .joinToString(separator = SearchTextSeparator)
        .toSearchText()
}

private fun String.toSearchText(): String {
    val withoutDiacritics = Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(SearchDiacriticRegex, "")

    return SearchWordRegex
        .replace(withoutDiacritics.lowercase(Locale.ROOT), SearchTextSeparator)
        .trim()
        .replace(SearchWhitespaceRegex, SearchTextSeparator)
}

private fun String.toSemanticFilter(): RoadmapSearchSemanticFilter? {
    return when (this) {
        "required" -> RoadmapSearchSemanticFilter.Required
        "optional" -> RoadmapSearchSemanticFilter.Optional
        "completed", "complete", "done" -> RoadmapSearchSemanticFilter.Completed
        "in progress", "progress", "continue" -> RoadmapSearchSemanticFilter.InProgress
        "not started", "start" -> RoadmapSearchSemanticFilter.NotStarted
        "locked", "lock" -> RoadmapSearchSemanticFilter.Locked
        "milestone", "project" -> RoadmapSearchSemanticFilter.Milestone
        "chapter", "group", "section" -> RoadmapSearchSemanticFilter.Chapter
        else -> null
    }
}

private val RoadmapNodeRequirement.searchLabel: String
    get() = when (this) {
        RoadmapNodeRequirement.Required -> "required"
        RoadmapNodeRequirement.Optional -> "optional"
    }

private val RoadmapNodeStatus.searchLabel: String
    get() = when (this) {
        RoadmapNodeStatus.Completed -> "completed"
        RoadmapNodeStatus.InProgress -> "in progress"
        RoadmapNodeStatus.NotStarted -> "not started"
        RoadmapNodeStatus.Locked -> "locked"
    }

private val RoadmapNodeAction.searchLabel: String
    get() = when (this) {
        RoadmapNodeAction.Start -> "start"
        RoadmapNodeAction.Review -> "review"
        RoadmapNodeAction.Continue -> "continue"
    }

private val RoadmapGroupState.searchLabel: String
    get() = when (this) {
        RoadmapGroupState.Expanded -> "in progress"
        RoadmapGroupState.Completed -> "completed"
        RoadmapGroupState.Locked -> "locked"
    }

private val RoadmapMilestoneState.searchLabel: String
    get() = when (this) {
        RoadmapMilestoneState.Available -> "available"
        RoadmapMilestoneState.Locked -> "locked"
    }

private const val SearchPreviewRecentNodeLimit = 2
private const val SearchPreviewRecentMilestoneLimit = 1
private const val SearchTextSeparator = " "
private val SearchTokenSeparator = Regex("\\s+")
private val SearchDiacriticRegex = Regex("\\p{Mn}+")
private val SearchWordRegex = Regex("[^\\p{Alnum}]+")
private val SearchWhitespaceRegex = Regex("\\s+")
