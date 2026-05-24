package com.rmap.mobile.features.bookmarks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.components.SkillStatus
import com.rmap.mobile.core.ui.theme.OnPrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkStatusFilter
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository,
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()
    private var allRoadmapItems: List<BookmarkRoadmapCardUiModel> = emptyList()
    private var allSkillItems: List<SkillCardUiModel> = emptyList()

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profileResult = profileRepository.getProfile()
            val roadmapResult = bookmarkRepository.getSavedRoadmaps()
            val skillResult = bookmarkRepository.getSavedSkills()
            val failure = listOf(profileResult, roadmapResult, skillResult).firstOrNull { it.isFailure }
            if (failure != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = failure.exceptionOrNull()?.message ?: "Unable to load bookmarks"
                    )
                }
                return@launch
            }

            val selectedStatusFilter = _uiState.value.selectedStatusFilter
            val searchQuery = _uiState.value.searchQuery
            allRoadmapItems = roadmapResult.getOrThrow().map { it.toBookmarkRoadmapCardUiModel() }
            allSkillItems = skillResult.getOrThrow().map { it.toSkillCardUiModel() }

            _uiState.value = BookmarksUiState(
                userName = profileResult.getOrThrow().userName,
                searchQuery = searchQuery,
                selectedTab = _uiState.value.selectedTab,
                selectedStatusFilter = selectedStatusFilter,
                roadmapItems = allRoadmapItems
                    .filterRoadmapsByStatus(selectedStatusFilter)
                    .filterRoadmapsByQuery(searchQuery),
                skillItems = allSkillItems
                    .filterSkillsByStatus(selectedStatusFilter)
                    .filterSkillsByQuery(searchQuery),
                isLoading = false
            )
        }
    }

    fun onTabSelected(index: Int) {
        val tab = BookmarkTab.entries.getOrNull(index) ?: return
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onStatusFilterSelected(index: Int) {
        val statusFilter = BookmarkStatusFilter.entries.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                selectedStatusFilter = statusFilter,
                roadmapItems = allRoadmapItems
                    .filterRoadmapsByStatus(statusFilter)
                    .filterRoadmapsByQuery(it.searchQuery),
                skillItems = allSkillItems
                    .filterSkillsByStatus(statusFilter)
                    .filterSkillsByQuery(it.searchQuery)
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                roadmapItems = allRoadmapItems
                    .filterRoadmapsByStatus(it.selectedStatusFilter)
                    .filterRoadmapsByQuery(query),
                skillItems = allSkillItems
                    .filterSkillsByStatus(it.selectedStatusFilter)
                    .filterSkillsByQuery(query)
            )
        }
    }
}

private fun RoadmapSummary.toBookmarkRoadmapCardUiModel(): BookmarkRoadmapCardUiModel {
    val status = when {
        totalLessonsCount > 0 && completedLessonsCount >= totalLessonsCount -> LearningStatus.Completed
        completedLessonsCount > 0 -> LearningStatus.InProgress
        else -> LearningStatus.NotStarted
    }

    return BookmarkRoadmapCardUiModel(
        id = id,
        title = title,
        categoryLabel = icon.toBookmarkCategoryLabel(),
        categoryIcon = icon.toImageVector(),
        categoryBackgroundColor = icon.toBookmarkCategoryBackgroundColor(),
        categoryContentColor = icon.toBookmarkCategoryContentColor(),
        nodesLabel = "$skillNodesCount Nodes",
        durationLabel = durationLabel,
        actionLabel = if (completedLessonsCount > 0) "Continue" else "Start",
        status = status,
        statusLabel = status.toBookmarkStatusLabel(),
        progressPercent = if (status == LearningStatus.InProgress && totalLessonsCount > 0) {
            ((completedLessonsCount.toFloat() / totalLessonsCount.toFloat()) * 100).toInt().coerceIn(1, 100)
        } else {
            null
        },
        savedAtLabel = if (completedLessonsCount > 0) "Last saved yesterday" else "Saved 3 days ago"
    )
}

private fun SkillBookmark.toSkillCardUiModel(): SkillCardUiModel {
    val skillStatus = when (status) {
        LearningStatus.Completed -> SkillStatus.COMPLETED
        LearningStatus.InProgress -> SkillStatus.IN_PROGRESS
        else -> SkillStatus.NOT_STARTED
    }
    val statusLabel = when (status) {
        LearningStatus.Completed -> "Completed"
        LearningStatus.InProgress -> "In Progress"
        else -> "Not Started"
    }

    return SkillCardUiModel(
        title = title,
        parentPathName = parentPathName,
        status = skillStatus,
        statusLabel = statusLabel,
        icon = icon.toImageVector()
    )
}

private fun List<BookmarkRoadmapCardUiModel>.filterRoadmapsByStatus(
    statusFilter: BookmarkStatusFilter
): List<BookmarkRoadmapCardUiModel> {
    return when (statusFilter) {
        BookmarkStatusFilter.All -> this
        BookmarkStatusFilter.InProgress -> filter { it.status == LearningStatus.InProgress }
        BookmarkStatusFilter.NotStarted -> filter { it.status == LearningStatus.NotStarted }
        BookmarkStatusFilter.Completed -> filter { it.status == LearningStatus.Completed }
    }
}

private fun List<BookmarkRoadmapCardUiModel>.filterRoadmapsByQuery(
    query: String
): List<BookmarkRoadmapCardUiModel> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return this

    return filter { item ->
        item.title.contains(normalizedQuery, ignoreCase = true) ||
            item.categoryLabel.contains(normalizedQuery, ignoreCase = true)
    }
}

private fun List<SkillCardUiModel>.filterSkillsByStatus(
    statusFilter: BookmarkStatusFilter
): List<SkillCardUiModel> {
    return when (statusFilter) {
        BookmarkStatusFilter.All -> this
        BookmarkStatusFilter.InProgress -> filter { it.status == SkillStatus.IN_PROGRESS }
        BookmarkStatusFilter.NotStarted -> filter { it.status == SkillStatus.NOT_STARTED }
        BookmarkStatusFilter.Completed -> filter { it.status == SkillStatus.COMPLETED }
    }
}

private fun List<SkillCardUiModel>.filterSkillsByQuery(
    query: String
): List<SkillCardUiModel> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return this

    return filter { item ->
        item.title.contains(normalizedQuery, ignoreCase = true) ||
            item.parentPathName.contains(normalizedQuery, ignoreCase = true)
    }
}

private fun LearningTopicIcon.toBookmarkCategoryLabel(): String {
    return when (this) {
        LearningTopicIcon.Palette -> "Design"
        LearningTopicIcon.Terminal -> "DevOps"
        else -> "Web Development"
    }
}

private fun LearningTopicIcon.toBookmarkCategoryBackgroundColor(): Color {
    return when (this) {
        LearningTopicIcon.Palette -> Color(0xFFFAF5FF)
        LearningTopicIcon.Terminal -> Color(0xFFFFF7ED)
        else -> PrimaryContainerLight
    }
}

private fun LearningTopicIcon.toBookmarkCategoryContentColor(): Color {
    return when (this) {
        LearningTopicIcon.Palette -> Color(0xFF9810FA)
        LearningTopicIcon.Terminal -> Color(0xFFF54900)
        else -> OnPrimaryContainerLight
    }
}

private fun LearningStatus.toBookmarkStatusLabel(): String {
    return when (this) {
        LearningStatus.Completed -> "Completed"
        LearningStatus.InProgress -> "In Progress"
        LearningStatus.Locked,
        LearningStatus.NotStarted -> "Not Started"
    }
}
