package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
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

    fun loadRoadmap(
        roadmapId: String,
        forceRefresh: Boolean = false
    ) {
        if (!forceRefresh && _uiState.value.roadmapId == roadmapId && !_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageResId = null) }
            repository.getRoadmapDetail(roadmapId)
                .onSuccess { detail ->
                    val detailUiState = detail.toRoadmapDetailUiState()
                    _uiState.update { detailUiState }
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
}

sealed class RoadmapDetailEvent {
    data class NavigateToNodeLearning(
        val roadmapId: String,
        val nodeId: String
    ) : RoadmapDetailEvent()
}
