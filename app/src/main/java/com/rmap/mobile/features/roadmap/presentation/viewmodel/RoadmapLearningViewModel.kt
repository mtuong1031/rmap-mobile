package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoadmapLearningViewModel(
    private val skillLearningRepository: SkillLearningRepository = RMapAppGraph.skillLearningRepository,
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapLearningUiState())
    val uiState: StateFlow<RoadmapLearningUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RoadmapLearningEvent>()
    val events: SharedFlow<RoadmapLearningEvent> = _events.asSharedFlow()

    private var lastRequestedRoadmapId: String = ""
    private var lastRequestedNodeId: String = ""
    private var lastRequestedSkillId: String = ""
    private var lastRequestedCompleted: Boolean = false

    fun loadLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean
    ) {
        loadLearningContent(
            roadmapId = roadmapId,
            nodeId = nodeId,
            skillId = skillId,
            isCompleted = isCompleted,
            forceRefresh = false
        )
    }

    fun retryLoadLearningContent() {
        loadLearningContent(
            roadmapId = lastRequestedRoadmapId.ifBlank { _uiState.value.roadmapId },
            nodeId = lastRequestedNodeId.ifBlank { _uiState.value.nodeId },
            skillId = lastRequestedSkillId.ifBlank { _uiState.value.skillId },
            isCompleted = lastRequestedCompleted || _uiState.value.isCompleted,
            forceRefresh = true
        )
    }

    fun refreshLearningContent() {
        loadLearningContent(
            roadmapId = lastRequestedRoadmapId.ifBlank { _uiState.value.roadmapId },
            nodeId = lastRequestedNodeId.ifBlank { _uiState.value.nodeId },
            skillId = lastRequestedSkillId.ifBlank { _uiState.value.skillId },
            isCompleted = lastRequestedCompleted || _uiState.value.isCompleted,
            forceRefresh = true
        )
    }

    fun onMarkCompletedClick() {
        val state = _uiState.value
        if (state.isCompleted || state.isCompleting) return

        val roadmapId = state.roadmapId.takeIf { it.isNotBlank() } ?: return
        val nodeId = state.nodeId.takeIf { it.isNotBlank() } ?: return
        if (!state.canMarkCompleted) {
            viewModelScope.launch {
                _events.emit(RoadmapLearningEvent.NodeCompletionRequiresQuiz)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCompleting = true,
                    errorMessageResId = null
                )
            }
            // TODO Backend requirement FR-13 + FR-17: completion must go through quiz first.
            roadmapRepository.updateNodeProgress(
                roadmapId = roadmapId,
                nodeId = nodeId,
                status = LearningStatus.Completed
            )
                .onSuccess {
                    lastRequestedCompleted = true
                    _uiState.update {
                        it.copy(
                            isCompleted = true,
                            isCompleting = false
                        )
                    }
                    _events.emit(RoadmapLearningEvent.NodeCompleted)
                }
                .onFailure {
                    _uiState.update { it.copy(isCompleting = false) }
                    _events.emit(RoadmapLearningEvent.NodeCompletionFailed)
                }
        }
    }

    private fun loadLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean,
        forceRefresh: Boolean
    ) {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        val normalizedSkillId = skillId.trim()

        lastRequestedRoadmapId = normalizedRoadmapId
        lastRequestedNodeId = normalizedNodeId
        lastRequestedSkillId = normalizedSkillId
        lastRequestedCompleted = isCompleted

        if (normalizedRoadmapId.isBlank() || normalizedNodeId.isBlank() || normalizedSkillId.isBlank()) {
            _uiState.update {
                RoadmapLearningUiState(
                    roadmapId = normalizedRoadmapId,
                    nodeId = normalizedNodeId,
                    skillId = normalizedSkillId,
                    isCompleted = isCompleted,
                    isLoading = false,
                    errorMessageResId = R.string.roadmap_learning_error_invalid_args
                )
            }
            return
        }

        val currentState = _uiState.value
        if (
            !forceRefresh &&
            currentState.skillId == normalizedSkillId &&
            currentState.nodeId == normalizedNodeId &&
            currentState.skill != null &&
            !currentState.isLoading &&
            currentState.errorMessageResId == null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                RoadmapLearningUiState(
                    roadmapId = normalizedRoadmapId,
                    nodeId = normalizedNodeId,
                    skillId = normalizedSkillId,
                    isCompleted = isCompleted,
                    isLoading = true
                )
            }
            val roadmapNodeContentResult = roadmapRepository.getRoadmapNodeLearningContent(
                roadmapId = normalizedRoadmapId,
                nodeId = normalizedNodeId,
                skillId = normalizedSkillId
            )

            roadmapNodeContentResult
                .onSuccess { content ->
                    _uiState.update {
                        content.toRoadmapLearningUiState(
                            roadmapId = normalizedRoadmapId,
                            nodeId = normalizedNodeId,
                            skillId = normalizedSkillId,
                            isCompleted = lastRequestedCompleted
                        )
                    }
                }.onFailure { error ->
                    val skillContent = if (error.isNotFound()) {
                        skillLearningRepository.getSkillLearningContent(normalizedSkillId).getOrNull()
                    } else {
                        null
                    }
                    val fallbackContent = skillContent ?: if (error.isNotFound()) {
                        loadRoadmapNodeFallbackContent(
                            roadmapId = normalizedRoadmapId,
                            nodeId = normalizedNodeId,
                            skillId = normalizedSkillId
                        )
                    } else {
                        null
                    }

                    if (fallbackContent != null) {
                        _uiState.update {
                            fallbackContent.toRoadmapLearningUiState(
                                roadmapId = normalizedRoadmapId,
                                nodeId = normalizedNodeId,
                                skillId = normalizedSkillId,
                                isCompleted = lastRequestedCompleted
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessageResId = R.string.roadmap_learning_error_load_failed
                            )
                        }
                    }
                }
        }
    }

    private suspend fun loadRoadmapNodeFallbackContent(
        roadmapId: String,
        nodeId: String,
        skillId: String
    ): SkillLearningContent? {
        val detail = roadmapRepository.getRoadmapDetail(roadmapId).getOrNull() ?: return null
        return detail.toSkillLearningFallback(
            nodeId = nodeId,
            skillId = skillId
        )
    }

    private fun Throwable.isNotFound(): Boolean {
        return this is AppException && type == NetworkErrorType.NotFound
    }

    private fun RoadmapDetail.toSkillLearningFallback(
        nodeId: String,
        skillId: String
    ): SkillLearningContent? {
        sections.forEach { section ->
            section.modules.forEach { module ->
                if (module.id == nodeId || module.skillId == skillId) {
                    return SkillLearningContent(
                        skill = SkillDetail(
                            id = module.skillId.ifBlank { skillId },
                            name = module.title,
                            description = module.description ?: description,
                            category = roleName.ifBlank { title },
                            estimatedHours = module.estimatedHours
                        ),
                        resources = emptyList()
                    )
                }

                module.subLessons.firstOrNull { subLesson ->
                    subLesson.id == nodeId || subLesson.skillId == skillId
                }?.let { subLesson ->
                    return SkillLearningContent(
                        skill = SkillDetail(
                            id = subLesson.skillId.ifBlank { skillId },
                            name = subLesson.title,
                            description = subLesson.description ?: description,
                            category = roleName.ifBlank { title },
                            estimatedHours = subLesson.estimatedHours
                        ),
                        resources = emptyList()
                    )
                }
            }
        }

        return null
    }
}

sealed class RoadmapLearningEvent {
    data object NodeCompleted : RoadmapLearningEvent()
    data object NodeCompletionFailed : RoadmapLearningEvent()
    data object NodeCompletionRequiresQuiz : RoadmapLearningEvent()
}
