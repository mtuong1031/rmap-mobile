package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.datarefresh.DynamicDataChange
import com.rmap.mobile.core.datarefresh.DynamicDataRefreshCoordinator
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

import com.rmap.mobile.core.notification.AppNotification
import com.rmap.mobile.core.notification.AppNotificationAction
import com.rmap.mobile.core.notification.AppNotificationManager
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.widget.domain.usecase.RefreshContinueLearningWidgetUseCase

class RoadmapLearningViewModel(
    private val skillLearningRepository: SkillLearningRepository = RMapAppGraph.skillLearningRepository,
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val dynamicDataRefreshCoordinator: DynamicDataRefreshCoordinator? = null,
    private val authRepository: AuthRepository? = null,
    private val appNotificationManager: AppNotificationManager? = null,
    private val refreshContinueLearningWidget: RefreshContinueLearningWidgetUseCase? = null
) : ViewModel() {
    private val activeAuthRepository: AuthRepository
        get() = authRepository ?: run {
            try {
                RMapAppGraph.authRepository
            } catch (e: Exception) {
                object : AuthRepository {
                    override val authState = kotlinx.coroutines.flow.MutableStateFlow<AuthState>(
                        AuthState.Authenticated(com.rmap.mobile.features.auth.domain.model.User("test-id", "test-email", "test-name", null, "user", ""))
                    )
                    override suspend fun loginWithGoogle(idToken: String): Result<com.rmap.mobile.features.auth.domain.model.User> = Result.failure(UnsupportedOperationException())
                    override suspend fun loginWithGithub(code: String): Result<com.rmap.mobile.features.auth.domain.model.User> = Result.failure(UnsupportedOperationException())
                    override suspend fun linkWithGoogle(idToken: String): Result<Unit> = Result.failure(UnsupportedOperationException())
                    override suspend fun linkWithGithub(code: String): Result<Unit> = Result.failure(UnsupportedOperationException())
                    override suspend fun logout(): Result<Unit> = Result.success(Unit)
                    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = Result.failure(UnsupportedOperationException())
                    override suspend fun getCurrentUser(): Result<com.rmap.mobile.features.auth.domain.model.User> = Result.failure(UnsupportedOperationException())
                }
            }
        }

    private val activeDynamicDataRefreshCoordinator: DynamicDataRefreshCoordinator?
        get() = dynamicDataRefreshCoordinator ?: runCatching {
            RMapAppGraph.dynamicDataRefreshCoordinator
        }.getOrNull()

    private val activeAppNotificationManager: AppNotificationManager
        get() = appNotificationManager ?: run {
            try {
                RMapAppGraph.appNotificationManager
            } catch (e: Exception) {
                AppNotificationManager()
            }
        }
    private val _uiState = MutableStateFlow(RoadmapLearningUiState())
    val uiState: StateFlow<RoadmapLearningUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RoadmapLearningEvent>()
    val events: SharedFlow<RoadmapLearningEvent> = _events.asSharedFlow()

    private var lastRequestedRoadmapId: String = ""
    private var lastRequestedNodeId: String = ""
    private var lastRequestedSkillId: String = ""
    private var lastRequestedCompleted: Boolean = false
    private var lastRequestedGroupTitle: String? = null

    fun loadLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean,
        groupTitle: String? = null
    ) {
        loadLearningContent(
            roadmapId = roadmapId,
            nodeId = nodeId,
            skillId = skillId,
            isCompleted = isCompleted,
            groupTitle = groupTitle,
            forceRefresh = false
        )
    }

    fun retryLoadLearningContent() {
        loadLearningContent(
            roadmapId = lastRequestedRoadmapId.ifBlank { _uiState.value.roadmapId },
            nodeId = lastRequestedNodeId.ifBlank { _uiState.value.nodeId },
            skillId = lastRequestedSkillId.ifBlank { _uiState.value.skillId },
            isCompleted = lastRequestedCompleted || _uiState.value.isCompleted,
            groupTitle = lastRequestedGroupTitle,
            forceRefresh = true
        )
    }

    fun refreshLearningContent() {
        loadLearningContent(
            roadmapId = lastRequestedRoadmapId.ifBlank { _uiState.value.roadmapId },
            nodeId = lastRequestedNodeId.ifBlank { _uiState.value.nodeId },
            skillId = lastRequestedSkillId.ifBlank { _uiState.value.skillId },
            isCompleted = lastRequestedCompleted || _uiState.value.isCompleted,
            groupTitle = lastRequestedGroupTitle,
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
                    notifyDynamicDataChanged(
                        DynamicDataChange.NodeCompleted(
                            roadmapId = roadmapId,
                            nodeId = nodeId
                        )
                    )
                    _uiState.update {
                        it.copy(
                            isCompleted = true,
                            isCompleting = false
                        )
                    }
                    refreshContinueLearningWidgetInBackground()
                    _events.emit(RoadmapLearningEvent.NodeCompleted)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isCompleting = false) }
                    if (error is AppException && error.type == NetworkErrorType.Unauthorized) {
                        return@onFailure
                    }
                    _events.emit(RoadmapLearningEvent.NodeCompletionFailed)
                }
        }
    }

    private fun refreshContinueLearningWidgetInBackground() {
        val refreshWidget = refreshContinueLearningWidget
            ?: runCatching { RMapAppGraph.refreshContinueLearningWidgetUseCase }.getOrNull()
            ?: return

        viewModelScope.launch {
            refreshWidget()
        }
    }

    fun onStartRoadmapForQuizClick() {
        val state = _uiState.value
        if (state.isCompleted || state.canTakeQuiz || state.isNodeLocked || state.isStartingRoadmapForQuiz) return

        val roadmapId = state.roadmapId.takeIf { it.isNotBlank() } ?: return
        val nodeId = state.nodeId.takeIf { it.isNotBlank() } ?: return

        val isGuest = activeAuthRepository.authState.value == AuthState.Unauthenticated
        if (isGuest) {
            viewModelScope.launch {
                activeAppNotificationManager.enqueue(
                    AppNotification(
                        titleResId = R.string.auth_required_title,
                        messageResId = R.string.auth_required_start_roadmap_message,
                        variant = AppNotificationVariant.Warning,
                        actionLabelResId = R.string.action_login,
                        action = AppNotificationAction.Login
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isStartingRoadmapForQuiz = true,
                    errorMessageResId = null
                )
            }
            roadmapRepository.startRoadmap(roadmapId)
                .onSuccess {
                    notifyDynamicDataChanged(DynamicDataChange.RoadmapStarted(roadmapId))
                    _uiState.update {
                        it.copy(
                            canTakeQuiz = true,
                            canMarkCompleted = false,
                            isNodeLocked = false,
                            isStartingRoadmapForQuiz = false,
                            completionBlockedMessageResId = null
                        )
                    }
                    _events.emit(RoadmapLearningEvent.NavigateToQuiz(roadmapId = roadmapId, nodeId = nodeId))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isStartingRoadmapForQuiz = false) }
                    if (error is AppException && error.type == NetworkErrorType.Unauthorized) {
                        return@onFailure
                    }
                    _events.emit(RoadmapLearningEvent.NodeCompletionFailed)
                }
        }
    }

    private fun notifyDynamicDataChanged(change: DynamicDataChange) {
        val coordinator = activeDynamicDataRefreshCoordinator ?: return
        viewModelScope.launch {
            runCatching { coordinator.notifyChange(change) }
        }
    }

    private fun loadLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String,
        isCompleted: Boolean,
        groupTitle: String?,
        forceRefresh: Boolean
    ) {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedNodeId = nodeId.trim()
        val normalizedSkillId = skillId.trim()

        lastRequestedRoadmapId = normalizedRoadmapId
        lastRequestedNodeId = normalizedNodeId
        lastRequestedSkillId = normalizedSkillId
        lastRequestedCompleted = isCompleted
        lastRequestedGroupTitle = groupTitle

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
                        ).let { state ->
                            if (!groupTitle.isNullOrBlank()) state.copy(nodeTitle = groupTitle) else state
                        }
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
                            ).let { state ->
                                if (!groupTitle.isNullOrBlank()) state.copy(nodeTitle = groupTitle) else state
                            }
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
    data class NavigateToQuiz(val roadmapId: String, val nodeId: String) : RoadmapLearningEvent()
}
