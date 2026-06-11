package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
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
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository
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
        val state = _uiState.value
        val roadmapId = state.roadmapId.takeIf { it.isNotBlank() }
        if (roadmapId != null && state.primaryAction == RoadmapPrimaryAction.StartLearning) {
            startRoadmapAndOpenFirstAvailableNode(roadmapId)
            return
        }

        when (val target = state.nextActionTarget) {
            is RoadmapNextActionTarget.Milestone -> {
                viewModelScope.launch {
                    _events.emit(RoadmapDetailEvent.MilestoneSelected(target.milestoneId))
                }
                return
            }

            RoadmapNextActionTarget.None -> Unit
            is RoadmapNextActionTarget.Node -> {
                val targetNode = state.findNodeById(target.nodeId)
                if (targetNode != null) {
                    onNodeActionClick(targetNode)
                    return
                }
            }
        }

        val node = state.currentSearchNode()
        if (node == null) {
            emitNodeActionUnavailable()
            return
        }

        onNodeActionClick(node)
    }

    fun onNodeActionClick(node: RoadmapNodeUiModel) {
        val roadmapId = _uiState.value.roadmapId.takeIf { it.isNotBlank() } ?: return

        when (node.status) {
            RoadmapNodeStatus.Locked,
            RoadmapNodeStatus.NotStarted,
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
        val groupTitle = _uiState.value.groups.find { group ->
            group.nodes.any { it.id == node.id }
        }?.title

        _events.emit(
            RoadmapDetailEvent.NavigateToLearning(
                roadmapId = roadmapId,
                nodeId = node.id,
                skillId = node.skillId,
                isCompleted = isCompleted,
                groupTitle = groupTitle
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
                    _events.emit(RoadmapDetailEvent.NodeProgressUpdateFailed(it.toUserMessage()))
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

    private fun RoadmapDetail.toLoadedUiState(): RoadmapDetailUiState {
        return toRoadmapDetailUiState()
    }

    private fun RoadmapDetailUiState.findNodeById(nodeId: String): RoadmapNodeUiModel? {
        return groups
            .flatMap { group -> group.nodes }
            .firstOrNull { node -> node.id == nodeId }
    }

    private fun emitNodeActionUnavailable() {
        viewModelScope.launch {
            _events.emit(RoadmapDetailEvent.NodeActionUnavailable)
        }
    }
}

sealed class RoadmapDetailEvent {
    data class NodeProgressUpdated(val unlockedNodeCount: Int) : RoadmapDetailEvent()
    data class NodeProgressUpdateFailed(val message: String? = null) : RoadmapDetailEvent()
    data object NodeActionUnavailable : RoadmapDetailEvent()
    data class MilestoneSelected(val milestoneId: String) : RoadmapDetailEvent()
    data class NavigateToLearning(
        val roadmapId: String,
        val nodeId: String,
        val skillId: String,
        val isCompleted: Boolean,
        val groupTitle: String? = null
    ) : RoadmapDetailEvent()
}

private fun Throwable.toUserMessage(): String? {
    return message?.takeIf { it.isNotBlank() }
}
