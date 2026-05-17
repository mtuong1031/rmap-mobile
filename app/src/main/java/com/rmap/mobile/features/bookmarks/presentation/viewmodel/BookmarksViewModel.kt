package com.rmap.mobile.features.bookmarks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.components.SkillStatus
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toDrawableRes
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toLabel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toRoadmapDifficulty
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

            _uiState.value = BookmarksUiState(
                userName = profileResult.getOrThrow().userName,
                selectedTab = _uiState.value.selectedTab,
                roadmapItems = roadmapResult.getOrThrow().map { it.toBookmarkRoadmapCardUiModel() },
                skillItems = skillResult.getOrThrow().map { it.toSkillCardUiModel() },
                isLoading = false
            )
        }
    }

    fun onTabSelected(index: Int) {
        val tab = BookmarkTab.entries.getOrNull(index) ?: return
        _uiState.update { it.copy(selectedTab = tab) }
    }
}

private fun RoadmapSummary.toBookmarkRoadmapCardUiModel(): BookmarkRoadmapCardUiModel {
    return BookmarkRoadmapCardUiModel(
        id = id,
        title = title,
        difficultyLabel = difficulty.toLabel(),
        difficulty = difficulty.toRoadmapDifficulty(),
        durationLabel = durationLabel,
        actionLabel = if (completedLessonsCount > 0) "Continue Path" else "Join Now",
        coverPlaceholderRes = coverPlaceholder.toDrawableRes()
    )
}

private fun SkillBookmark.toSkillCardUiModel(): SkillCardUiModel {
    return SkillCardUiModel(
        title = title,
        parentPathName = parentPathName,
        status = if (status == LearningStatus.InProgress) SkillStatus.IN_PROGRESS else SkillStatus.NOT_STARTED,
        statusLabel = if (status == LearningStatus.InProgress) "In Progress" else "Not Started",
        icon = icon.toImageVector()
    )
}
