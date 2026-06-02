package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningResource
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LearningNodeViewModel(
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LearningNodeUiState())
    val uiState: StateFlow<LearningNodeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LearningNodeEvent>()
    val events: SharedFlow<LearningNodeEvent> = _events.asSharedFlow()

    fun loadNode(
        roadmapId: String,
        nodeId: String,
        forceRefresh: Boolean = false
    ) {
        if (
            !forceRefresh &&
            _uiState.value.roadmapId == roadmapId &&
            _uiState.value.nodeId == nodeId &&
            !_uiState.value.isLoading
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    roadmapId = roadmapId,
                    nodeId = nodeId,
                    isLoading = true,
                    errorMessage = null
                )
            }

            repository.getLearningNode(roadmapId, nodeId)
                .onSuccess { detail ->
                    _uiState.update { detail.toLearningNodeUiState() }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun onTakeQuizClick() {
        val state = _uiState.value
        if (!state.isQuizAvailable) return

        viewModelScope.launch {
            _events.emit(
                LearningNodeEvent.NavigateToQuiz(
                    roadmapId = state.roadmapId,
                    nodeId = state.nodeId
                )
            )
        }
    }
}

data class LearningNodeUiState(
    val roadmapId: String = "",
    val nodeId: String = "",
    val title: String = "",
    val description: String? = null,
    val skillName: String? = null,
    val skillDescription: String? = null,
    val estimatedHours: Int? = null,
    val status: LearningNodeStatusUiModel = LearningNodeStatusUiModel.Locked,
    val requirement: RoadmapNodeRequirement = RoadmapNodeRequirement.Required,
    val resources: List<LearningResourceUiModel> = emptyList(),
    val prerequisites: List<String> = emptyList(),
    val isQuizAvailable: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

enum class LearningNodeStatusUiModel {
    Completed,
    InProgress,
    NotStarted,
    Locked
}

data class LearningResourceUiModel(
    val id: String,
    val title: String,
    val url: String,
    val type: String,
    val isFree: Boolean,
    val isPrimary: Boolean
)

sealed class LearningNodeEvent {
    data class NavigateToQuiz(
        val roadmapId: String,
        val nodeId: String
    ) : LearningNodeEvent()
}

private fun LearningNodeDetail.toLearningNodeUiState(): LearningNodeUiState {
    return LearningNodeUiState(
        roadmapId = roadmapId,
        nodeId = nodeId,
        title = title,
        description = description,
        skillName = skillName,
        skillDescription = skillDescription,
        estimatedHours = estimatedHours,
        status = status.toLearningNodeStatusUiModel(),
        requirement = requirement.toRoadmapNodeRequirement(),
        resources = resources.map { resource -> resource.toLearningResourceUiModel() },
        prerequisites = prerequisites.map { prerequisite -> prerequisite.skillName },
        isQuizAvailable = status == LearningStatus.InProgress,
        isLoading = false,
        errorMessage = null
    )
}

private fun LearningStatus.toLearningNodeStatusUiModel(): LearningNodeStatusUiModel {
    return when (this) {
        LearningStatus.Completed -> LearningNodeStatusUiModel.Completed
        LearningStatus.InProgress -> LearningNodeStatusUiModel.InProgress
        LearningStatus.NotStarted -> LearningNodeStatusUiModel.NotStarted
        LearningStatus.Locked -> LearningNodeStatusUiModel.Locked
    }
}

private fun LearningRequirement.toRoadmapNodeRequirement(): RoadmapNodeRequirement {
    return when (this) {
        LearningRequirement.Required -> RoadmapNodeRequirement.Required
        LearningRequirement.Optional -> RoadmapNodeRequirement.Optional
    }
}

private fun LearningResource.toLearningResourceUiModel(): LearningResourceUiModel {
    return LearningResourceUiModel(
        id = id,
        title = title,
        url = url,
        type = type,
        isFree = isFree,
        isPrimary = isPrimary
    )
}
