package com.rmap.mobile.features.bookmarks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.domain.model.toRMapCategoryCompactLabel
import com.rmap.mobile.core.domain.model.toRMapCategoryIconKey
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkStatusFilter
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkCategoryStyle
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.skill.BookmarkSkillCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.skill.BookmarkSkillStatus
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.toLearningTopicIcon
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val BOOKMARKS_LOAD_ERROR_MESSAGE = "Unable to load bookmarks"
private const val BOOKMARK_ACTION_CONTINUE = "Continue"
private const val BOOKMARK_ACTION_START = "Start"
private const val BOOKMARK_STATUS_COMPLETED = "Completed"
private const val BOOKMARK_STATUS_IN_PROGRESS = "In Progress"
private const val BOOKMARK_STATUS_NOT_STARTED = "Not Started"
private const val BOOKMARK_SAVED_RECENTLY = "Last saved yesterday"
private const val BOOKMARK_SAVED_DEFAULT = "Saved 3 days ago"

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository,
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()
    private var allRoadmapItems: List<BookmarkRoadmapCardUiModel> = emptyList()
    private var allSkillItems: List<BookmarkSkillCardUiModel> = emptyList()
    private var loadJob: Job? = null

    init {
        loadBookmarks()
    }

    fun loadBookmarks() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profileResult = profileRepository.getProfile()
            if (profileResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = profileResult.exceptionOrNull()?.message ?: BOOKMARKS_LOAD_ERROR_MESSAGE
                    )
                }
                return@launch
            }

            combine(
                bookmarkRepository.observeSavedRoadmaps(),
                bookmarkRepository.observeSavedSkills()
            ) { roadmaps, skills -> roadmaps to skills }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: BOOKMARKS_LOAD_ERROR_MESSAGE
                        )
                    }
                }
                .collect { (roadmaps, skills) ->
                    val selectedStatusFilter = _uiState.value.selectedStatusFilter
                    val searchQuery = _uiState.value.searchQuery
                    allRoadmapItems = roadmaps.map { it.toBookmarkRoadmapCardUiModel() }
                    allSkillItems = skills.map { it.toSkillCardUiModel() }

                    _uiState.update {
                        it.copy(
                            userName = profileResult.getOrThrow().userName,
                            roadmapItems = allRoadmapItems
                                .filterRoadmapsByStatus(selectedStatusFilter)
                                .filterRoadmapsByQuery(searchQuery),
                            skillItems = allSkillItems
                                .filterSkillsByStatus(selectedStatusFilter)
                                .filterSkillsByQuery(searchQuery),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
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

    fun onRoadmapBookmarkClick(item: BookmarkRoadmapCardUiModel) {
        viewModelScope.launch {
            bookmarkRepository.deleteRoadmap(item.id)
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: BOOKMARKS_LOAD_ERROR_MESSAGE) }
                }
        }
    }

    fun onSkillBookmarkClick(item: BookmarkSkillCardUiModel) {
        viewModelScope.launch {
            bookmarkRepository.deleteSkill(item.skillId)
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: BOOKMARKS_LOAD_ERROR_MESSAGE) }
                }
        }
    }

}

private fun RoadmapBookmark.toBookmarkRoadmapCardUiModel(): BookmarkRoadmapCardUiModel {
    val roadmap = summary
    val categoryIcon = roadmap.categoryId.toRMapCategoryIconKey().toLearningTopicIcon()

    return BookmarkRoadmapCardUiModel(
        id = roadmap.id,
        title = roadmap.title,
        categoryLabel = roadmap.categoryId.toRMapCategoryCompactLabel(),
        categoryIcon = categoryIcon.toImageVector(),
        categoryStyle = categoryIcon.toBookmarkCategoryStyle(),
        nodesLabel = "${roadmap.skillNodesCount} Nodes",
        durationLabel = roadmap.durationLabel,
        actionLabel = if (status == LearningStatus.InProgress) BOOKMARK_ACTION_CONTINUE else BOOKMARK_ACTION_START,
        status = status,
        statusLabel = status.toBookmarkStatusLabel(),
        progressPercent = if (status == LearningStatus.InProgress && roadmap.totalLessonsCount > 0) {
            ((roadmap.completedLessonsCount.toFloat() / roadmap.totalLessonsCount.toFloat()) * 100)
                .toInt()
                .coerceIn(1, 100)
        } else {
            null
        },
        savedAtLabel = if (status == LearningStatus.InProgress) BOOKMARK_SAVED_RECENTLY else BOOKMARK_SAVED_DEFAULT
    )
}

private fun SkillBookmark.toSkillCardUiModel(): BookmarkSkillCardUiModel {
    val categoryIcon = parentPathName.toRMapCategoryIconKey().toLearningTopicIcon()
    val skillStatus = when (status) {
        LearningStatus.Completed -> BookmarkSkillStatus.COMPLETED
        LearningStatus.InProgress -> BookmarkSkillStatus.IN_PROGRESS
        else -> BookmarkSkillStatus.NOT_STARTED
    }
    val statusLabel = when (status) {
        LearningStatus.Completed -> BOOKMARK_STATUS_COMPLETED
        LearningStatus.InProgress -> BOOKMARK_STATUS_IN_PROGRESS
        else -> BOOKMARK_STATUS_NOT_STARTED
    }

    return BookmarkSkillCardUiModel(
        title = title,
        parentPathName = parentPathName.toRMapCategoryCompactLabel(parentPathName),
        status = skillStatus,
        statusLabel = statusLabel,
        icon = categoryIcon.toImageVector(),
        skillId = skillId
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

private fun List<BookmarkSkillCardUiModel>.filterSkillsByStatus(
    statusFilter: BookmarkStatusFilter
): List<BookmarkSkillCardUiModel> {
    return when (statusFilter) {
        BookmarkStatusFilter.All -> this
        BookmarkStatusFilter.InProgress -> filter { it.status == BookmarkSkillStatus.IN_PROGRESS }
        BookmarkStatusFilter.NotStarted -> filter { it.status == BookmarkSkillStatus.NOT_STARTED }
        BookmarkStatusFilter.Completed -> filter { it.status == BookmarkSkillStatus.COMPLETED }
    }
}

private fun List<BookmarkSkillCardUiModel>.filterSkillsByQuery(
    query: String
): List<BookmarkSkillCardUiModel> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return this

    return filter { item ->
        item.title.contains(normalizedQuery, ignoreCase = true) ||
            item.parentPathName.contains(normalizedQuery, ignoreCase = true)
    }
}

private fun LearningTopicIcon.toBookmarkCategoryStyle(): BookmarkCategoryStyle {
    return when (this) {
        LearningTopicIcon.Palette -> BookmarkCategoryStyle.Design
        LearningTopicIcon.Terminal -> BookmarkCategoryStyle.DevOps
        else -> BookmarkCategoryStyle.WebDevelopment
    }
}

private fun LearningStatus.toBookmarkStatusLabel(): String {
    return when (this) {
        LearningStatus.Completed -> BOOKMARK_STATUS_COMPLETED
        LearningStatus.InProgress -> BOOKMARK_STATUS_IN_PROGRESS
        LearningStatus.Locked,
        LearningStatus.NotStarted -> BOOKMARK_STATUS_NOT_STARTED
    }
}
