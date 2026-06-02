package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoadmapDetailViewModel(
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapDetailUiState())
    val uiState: StateFlow<RoadmapDetailUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<RoadmapDetailEvent>()
    val events: SharedFlow<RoadmapDetailEvent> = _events.asSharedFlow()
    private var currentDetail: RoadmapDetail? = null

    fun loadRoadmap(
        roadmapId: String,
        forceRefresh: Boolean = false
    ) {
        if (!forceRefresh && _uiState.value.roadmapId == roadmapId && !_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageResId = null) }
            repository.getRoadmapDetail(roadmapId)
                .onSuccess { detail ->
                    currentDetail = detail
                    val isBookmarked = bookmarkRepository.isRoadmapSaved(detail.id).getOrDefault(false)
                    val detailUiState = detail.toRoadmapDetailUiState()
                    _uiState.update {
                        detailUiState
                            .withSavedSkillState()
                            .copy(isBookmarked = isBookmarked)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            roadmapId = roadmapId,
                            isLoading = false,
                            errorMessageResId = R.string.roadmap_detail_error_load_failed
                        )
                    }
                }
        }
    }

    fun onBookmarkClick() {
        val detail = currentDetail ?: return

        viewModelScope.launch {
            if (_uiState.value.isBookmarked) {
                bookmarkRepository.deleteRoadmap(detail.id)
                    .onSuccess {
                        _uiState.update { it.copy(isBookmarked = false) }
                        _events.emit(RoadmapDetailEvent.RoadmapBookmarkRemoved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            } else {
                bookmarkRepository.saveRoadmap(detail.id)
                    .onSuccess {
                        _uiState.update { it.copy(isBookmarked = true) }
                        _events.emit(RoadmapDetailEvent.RoadmapBookmarkSaved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            }
        }
    }

    fun onNodeBookmarkClick(node: RoadmapNodeUiModel) {
        val roadmapId = _uiState.value.roadmapId.takeIf { it.isNotBlank() } ?: return

        viewModelScope.launch {
            if (node.isBookmarked) {
                bookmarkRepository.deleteSkill(node.id)
                    .onSuccess {
                        _uiState.update { it.withUpdatedSkillBookmark(node.id, isBookmarked = false) }
                        _events.emit(RoadmapDetailEvent.SkillBookmarkRemoved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            } else {
                bookmarkRepository.saveSkill(
                    skillId = node.id,
                    roadmapId = roadmapId
                )
                    .onSuccess {
                        _uiState.update { it.withUpdatedSkillBookmark(node.id, isBookmarked = true) }
                        _events.emit(RoadmapDetailEvent.SkillBookmarkSaved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            }
        }
    }

    fun onContinueLearningClick() {
        val node = _uiState.value.currentSearchNode() ?: return
        onNodeActionClick(node)
    }

    fun onNodeActionClick(node: RoadmapNodeUiModel) {
        if (node.status == RoadmapNodeStatus.Locked) return
        val roadmapId = _uiState.value.roadmapId.takeIf { it.isNotBlank() } ?: return

        viewModelScope.launch {
            _events.emit(
                RoadmapDetailEvent.NavigateToNodeLearning(
                    roadmapId = roadmapId,
                    nodeId = node.id
                )
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isSearchActive = true
            )
        }
    }

    fun onSearchFocus() {
        _uiState.update {
            it.copy(
                isSearchActive = true,
                isSearchInputFocused = true
            )
        }
    }

    fun onSearchFocusChange(isFocused: Boolean) {
        _uiState.update { it.copy(isSearchInputFocused = isFocused) }
    }

    fun onSearchClearClick() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onSearchBackClick() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                isSearchActive = false,
                isSearchInputFocused = false
            )
        }
    }

    private suspend fun RoadmapDetailUiState.withSavedSkillState(): RoadmapDetailUiState {
        val savedIds = groups
            .flatMap { group -> group.nodes }
            .map { node -> node.id }
            .distinct()
            .associateWith { skillId -> bookmarkRepository.isSkillSaved(skillId).getOrDefault(false) }

        return copy(
            groups = groups.map { group ->
                group.copy(
                    nodes = group.nodes.map { node ->
                        node.copy(isBookmarked = savedIds[node.id] == true)
                    }
                )
            }
        )
    }

    private fun RoadmapDetailUiState.withUpdatedSkillBookmark(
        skillId: String,
        isBookmarked: Boolean
    ): RoadmapDetailUiState {
        return copy(
            groups = groups.map { group ->
                group.copy(
                    nodes = group.nodes.map { node ->
                        if (node.id == skillId) {
                            node.copy(isBookmarked = isBookmarked)
                        } else {
                            node
                        }
                    }
                )
            }
        )
    }
}

sealed class RoadmapDetailEvent {
    data class NavigateToNodeLearning(
        val roadmapId: String,
        val nodeId: String
    ) : RoadmapDetailEvent()

    data object RoadmapBookmarkSaved : RoadmapDetailEvent()
    data object RoadmapBookmarkRemoved : RoadmapDetailEvent()
    data object SkillBookmarkSaved : RoadmapDetailEvent()
    data object SkillBookmarkRemoved : RoadmapDetailEvent()
    data object BookmarkActionFailed : RoadmapDetailEvent()
}
