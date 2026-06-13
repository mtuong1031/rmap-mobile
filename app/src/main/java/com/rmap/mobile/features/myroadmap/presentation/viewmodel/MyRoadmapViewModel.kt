package com.rmap.mobile.features.myroadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.datarefresh.DynamicDataChange
import com.rmap.mobile.core.datarefresh.DynamicDataRefreshCoordinator
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmap
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.myroadmap.domain.repository.CompletedSkillsRepository
import com.rmap.mobile.features.profile.domain.repository.LearningReminderContextRepository
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MyRoadmapViewModel(
    private val dashboardRepository: DashboardRepository = RMapAppGraph.dashboardRepository,
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val dynamicDataRefreshCoordinator: DynamicDataRefreshCoordinator? = runCatching {
        RMapAppGraph.dynamicDataRefreshCoordinator
    }.getOrNull(),
    private val completedSkillsRepository: CompletedSkillsRepository = RMapAppGraph.completedSkillsRepository,
    private val learningReminderContextRepository: LearningReminderContextRepository = RMapAppGraph.learningReminderContextRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyRoadmapUiState())
    val uiState: StateFlow<MyRoadmapUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<MyRoadmapEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<MyRoadmapEvent> = _events.asSharedFlow()
    private var dashboardJob: Job? = null

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                if (authState is AuthState.Authenticated) {
                    loadDashboard()
                } else {
                    dashboardJob?.cancel()
                    _uiState.update { MyRoadmapUiState() }
                }
            }
        }
    }

    fun loadDashboard() {
        if (authRepository.authState.value !is AuthState.Authenticated) {
            _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            return
        }
        dashboardJob?.cancel()
        dashboardJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            dashboardRepository.observeDashboard()
                .collect { result ->
                    result
                        .onSuccess { dashboard ->
                            learningReminderContextRepository.setActiveRoadmap(
                                title = dashboard.firstActiveRoadmapTitle()
                            )
                            _uiState.update {
                                dashboard.toUiState(
                                    selectedFilter = it.selectedFilter,
                                    searchQuery = it.searchQuery
                                )
                            }
                        }
                        .onFailure { error ->
                            val hasCachedRoadmaps = _uiState.value.roadmaps.isNotEmpty()
                            if (hasCachedRoadmaps) {
                                _events.tryEmit(MyRoadmapEvent.DashboardRefreshFailed)
                            }
                            _uiState.update {
                                if (hasCachedRoadmaps) {
                                    it.copy(isLoading = false)
                                } else {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = error.message ?: MY_ROADMAP_LOAD_ERROR
                                    )
                                }
                            }
                        }
                    }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onFilterSelected(filter: MyRoadmapFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun resetRoadmapProgress(roadmapId: String) {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank()) return

        viewModelScope.launch {
            roadmapRepository.resetRoadmapProgress(normalizedRoadmapId)
                .onSuccess {
                    notifyDynamicDataChanged(DynamicDataChange.RoadmapProgressReset(normalizedRoadmapId))
                    _events.emit(MyRoadmapEvent.RoadmapProgressResetSucceeded)
                }
                .onFailure { error ->
                    _events.emit(MyRoadmapEvent.RoadmapActionFailed(error.toUserMessage()))
                }
        }
    }

    fun deleteRoadmap(roadmapId: String, isTemplate: Boolean) {
        val normalizedRoadmapId = roadmapId.trim()
        if (normalizedRoadmapId.isBlank() || isTemplate) return

        viewModelScope.launch {
            roadmapRepository.deleteRoadmap(normalizedRoadmapId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(roadmaps = state.roadmaps.filterNot { roadmap -> roadmap.id == normalizedRoadmapId })
                    }
                    notifyDynamicDataChanged(DynamicDataChange.RoadmapDeleted(normalizedRoadmapId))
                    _events.emit(MyRoadmapEvent.RoadmapDeleted)
                }
                .onFailure { error ->
                    _events.emit(MyRoadmapEvent.RoadmapActionFailed(error.toUserMessage()))
                }
        }
    }

    private fun notifyDynamicDataChanged(change: DynamicDataChange) {
        val coordinator = dynamicDataRefreshCoordinator ?: return
        viewModelScope.launch {
            runCatching { coordinator.notifyChange(change) }
        }
    }
}

private fun Dashboard.firstActiveRoadmapTitle(): String? {
    return roadmaps.firstOrNull { roadmap ->
        roadmap.startedAt != null && roadmap.completionPct < 100.0
    }?.title
}

private fun Dashboard.toUiState(
    selectedFilter: MyRoadmapFilter,
    searchQuery: String
): MyRoadmapUiState {
    val categoryLabels = skillCategories.associate { category ->
        category.category to category.label
    }

    return MyRoadmapUiState(
        userName = userProfile.fullName.toFirstName(),
        selectedFilter = selectedFilter,
        searchQuery = searchQuery,
        roadmaps = roadmaps.map { roadmap ->
            roadmap.toUiModel(categoryLabels[roadmap.roleCategory])
        },
        isLoading = false,
        errorMessage = null
    )
}

private fun DashboardRoadmap.toUiModel(categoryLabel: String?): MyRoadmapCardUiModel {
    return MyRoadmapCardUiModel(
        id = roadmapId,
        title = title,
        categoryKey = roleCategory,
        categoryLabel = categoryLabel ?: roleCategory,
        isTemplate = isTemplate,
        completionPercent = completionPct.roundToInt().coerceIn(0, 100),
        nodesCompleted = nodesCompleted,
        nodesTotal = nodesTotal,
        deadlineDate = deadlineDate,
        estimatedWeeks = estimatedWeeks,
        startedAt = startedAt,
        isBehind = timelineWarning?.isBehind == true
    )
}

private fun String.toFirstName(): String {
    return trim()
        .split(Regex("\\s+"))
        .firstOrNull()
        .orEmpty()
}

private fun Throwable.toUserMessage(): String? {
    return message?.takeIf { it.isNotBlank() }
}

private const val MY_ROADMAP_LOAD_ERROR = "Unable to load your roadmaps"
