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
    private var lastRequestedRoadmapId: String = ""

    fun loadRoadmap(roadmapId: String) {
        loadRoadmap(roadmapId, forceRefresh = false)
    }

    fun refreshRoadmap() {
        loadRoadmap(lastRequestedRoadmapId.ifBlank { _uiState.value.roadmapId }, forceRefresh = true)
    }

    private fun loadRoadmap(
        roadmapId: String,
        forceRefresh: Boolean
    ) {
        val normalizedRoadmapId = roadmapId.trim()
        lastRequestedRoadmapId = normalizedRoadmapId

        if (normalizedRoadmapId.isBlank()) {
            currentDetail = null
            _uiState.update {
                RoadmapDetailUiState(
                    isLoading = false,
                    errorMessageResId = R.string.roadmap_detail_error_invalid_id
                )
            }
            return
        }

        val currentState = _uiState.value
        if (
            !forceRefresh &&
            currentState.roadmapId == normalizedRoadmapId &&
            !currentState.isLoading &&
            currentState.errorMessageResId == null &&
            !currentState.isEmpty
        ) {
            return
        }

        viewModelScope.launch {
            currentDetail = null
            _uiState.update {
                RoadmapDetailUiState(
                    roadmapId = normalizedRoadmapId,
                    isLoading = true
                )
            }
            repository.getRoadmapDetail(normalizedRoadmapId)
                .onSuccess { detail ->
                    currentDetail = detail
                    val loadedState = detail.toLoadedUiState()
                    _uiState.update { loadedState }
                }
                .onFailure {
                    currentDetail = null
                    _uiState.update {
                        it.copy(
                            roadmapId = normalizedRoadmapId,
                            isLoading = false,
                            errorMessageResId = R.string.roadmap_detail_error_load_failed
                        )
                    }
                }
        }
    }

    fun onContinueClick() {
        val node = _uiState.value.currentSearchNode()
        if (node == null) {
            val roadmapId = _uiState.value.roadmapId.takeIf { it.isNotBlank() }
            if (roadmapId != null && _uiState.value.primaryAction == RoadmapPrimaryAction.StartLearning) {
                startRoadmapAndOpenFirstAvailableNode(roadmapId)
            } else {
                emitNodeActionUnavailable()
            }
            return
        }

        onNodeActionClick(node)
    }

    fun onNodeActionClick(node: RoadmapNodeUiModel) {
        val roadmapId = _uiState.value.roadmapId.takeIf { it.isNotBlank() } ?: return

        when (node.status) {
            RoadmapNodeStatus.Locked -> {
                viewModelScope.launch {
                    _events.emit(RoadmapDetailEvent.NodeLocked)
                }
            }

            RoadmapNodeStatus.NotStarted -> startLearningNode(
                roadmapId = roadmapId,
                node = node
            )

            RoadmapNodeStatus.InProgress,
            RoadmapNodeStatus.Completed -> navigateToLearning(
                roadmapId = roadmapId,
                node = node
            )
        }
    }

    fun retryLoadRoadmap() {
        refreshRoadmap()
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
        val skillId = node.skillId.ifBlank { node.id }

        viewModelScope.launch {
            if (node.isBookmarked) {
                bookmarkRepository.deleteSkill(skillId)
                    .onSuccess {
                        _uiState.update { it.withUpdatedSkillBookmark(skillId, isBookmarked = false) }
                        _events.emit(RoadmapDetailEvent.SkillBookmarkRemoved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            } else {
                bookmarkRepository.saveSkill(
                    skillId = skillId,
                    roadmapId = roadmapId
                )
                    .onSuccess {
                        _uiState.update { it.withUpdatedSkillBookmark(skillId, isBookmarked = true) }
                        _events.emit(RoadmapDetailEvent.SkillBookmarkSaved)
                    }
                    .onFailure {
                        _events.emit(RoadmapDetailEvent.BookmarkActionFailed)
                    }
            }
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

    private fun startLearningNode(
        roadmapId: String,
        node: RoadmapNodeUiModel
    ) {
        if (node.skillId.isBlank()) {
            emitNodeActionUnavailable()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updatingNodeId = node.id,
                    errorMessageResId = null
                )
            }
            repository.startRoadmap(roadmapId)
                .onSuccess {
                    val refreshedState = refreshRoadmapAfterStart(roadmapId)
                    val targetNode = refreshedState?.findNodeById(node.id)
                        ?.takeIf { refreshedNode -> refreshedNode.status != RoadmapNodeStatus.Locked }
                        ?: refreshedState?.currentSearchNode()
                        ?: node
                    if (targetNode.status == RoadmapNodeStatus.Locked) {
                        _events.emit(RoadmapDetailEvent.NodeLocked)
                    } else {
                        emitNavigateToLearning(
                            roadmapId = roadmapId,
                            node = targetNode,
                            isCompleted = targetNode.status == RoadmapNodeStatus.Completed
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(updatingNodeId = null) }
                    _events.emit(RoadmapDetailEvent.NodeProgressUpdateFailed)
                }
        }
    }

    private fun navigateToLearning(
        roadmapId: String,
        node: RoadmapNodeUiModel
    ) {
        if (node.skillId.isBlank()) {
            emitNodeActionUnavailable()
            return
        }

        viewModelScope.launch {
            emitNavigateToLearning(
                roadmapId = roadmapId,
                node = node,
                isCompleted = node.status == RoadmapNodeStatus.Completed
            )
        }
    }

    private suspend fun emitNavigateToLearning(
        roadmapId: String,
        node: RoadmapNodeUiModel,
        isCompleted: Boolean
    ) {
        _events.emit(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = roadmapId,
                nodeId = node.id,
                skillId = node.skillId,
                isCompleted = isCompleted
            )
        )
    }

    private fun startRoadmapAndOpenFirstAvailableNode(roadmapId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updatingNodeId = null,
                    errorMessageResId = null
                )
            }
            repository.startRoadmap(roadmapId)
                .onSuccess {
                    val refreshedState = refreshRoadmapAfterStart(roadmapId)
                    val targetNode = refreshedState?.currentSearchNode()
                    if (targetNode != null) {
                        emitNavigateToLearning(
                            roadmapId = roadmapId,
                            node = targetNode,
                            isCompleted = targetNode.status == RoadmapNodeStatus.Completed
                        )
                    } else {
                        _events.emit(RoadmapDetailEvent.NodeProgressUpdated(unlockedNodeCount = 0))
                    }
                }
                .onFailure {
                    _events.emit(RoadmapDetailEvent.NodeProgressUpdateFailed)
                }
        }
    }

    private suspend fun refreshRoadmapAfterStart(roadmapId: String): RoadmapDetailUiState? {
        return repository.getRoadmapDetail(roadmapId)
            .map { detail ->
                currentDetail = detail
                detail.toLoadedUiState()
            }
            .onSuccess { loadedState ->
                _uiState.update { loadedState.copy(updatingNodeId = null) }
            }
            .onFailure {
                _uiState.update { state -> state.copy(updatingNodeId = null) }
            }
            .getOrNull()
    }

    private suspend fun RoadmapDetail.toLoadedUiState(): RoadmapDetailUiState {
        val isBookmarked = bookmarkRepository.isRoadmapSaved(id).getOrDefault(false)
        return toRoadmapDetailUiState()
            .withSavedSkillState()
            .copy(isBookmarked = isBookmarked)
    }

    private suspend fun RoadmapDetailUiState.withSavedSkillState(): RoadmapDetailUiState {
        val savedIds = groups
            .flatMap { group -> group.nodes }
            .map { node -> node.skillId.ifBlank { node.id } }
            .distinct()
            .associateWith { skillId -> bookmarkRepository.isSkillSaved(skillId).getOrDefault(false) }

        return copy(
            groups = groups.map { group ->
                group.copy(
                    nodes = group.nodes.map { node ->
                        val skillId = node.skillId.ifBlank { node.id }
                        node.copy(isBookmarked = savedIds[skillId] == true)
                    }
                )
            }
        )
    }

    private fun RoadmapDetailUiState.findNodeById(nodeId: String): RoadmapNodeUiModel? {
        return groups
            .flatMap { group -> group.nodes }
            .firstOrNull { node -> node.id == nodeId }
    }

    private fun RoadmapDetailUiState.withUpdatedSkillBookmark(
        skillId: String,
        isBookmarked: Boolean
    ): RoadmapDetailUiState {
        return copy(
            groups = groups.map { group ->
                group.copy(
                    nodes = group.nodes.map { node ->
                        if (node.skillId == skillId || node.id == skillId) {
                            node.copy(isBookmarked = isBookmarked)
                        } else {
                            node
                        }
                    }
                )
            }
        )
    }

    private fun emitNodeActionUnavailable() {
        viewModelScope.launch {
            _events.emit(RoadmapDetailEvent.NodeActionUnavailable)
        }
    }
}

sealed class RoadmapDetailEvent {
    data object RoadmapBookmarkSaved : RoadmapDetailEvent()
    data object RoadmapBookmarkRemoved : RoadmapDetailEvent()
    data object SkillBookmarkSaved : RoadmapDetailEvent()
    data object SkillBookmarkRemoved : RoadmapDetailEvent()
    data object BookmarkActionFailed : RoadmapDetailEvent()
    data class NodeProgressUpdated(val unlockedNodeCount: Int) : RoadmapDetailEvent()
    data object NodeProgressUpdateFailed : RoadmapDetailEvent()
    data object NodeActionUnavailable : RoadmapDetailEvent()
    data object NodeLocked : RoadmapDetailEvent()
    data class NavigateToLearning(
        val roadmapId: String,
        val nodeId: String,
        val skillId: String,
        val isCompleted: Boolean
    ) : RoadmapDetailEvent()
}
