package com.rmap.mobile.features.myroadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.model.DashboardRoadmap
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MyRoadmapViewModel(
    private val dashboardRepository: DashboardRepository = RMapAppGraph.dashboardRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyRoadmapUiState())
    val uiState: StateFlow<MyRoadmapUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            dashboardRepository.getDashboard()
                .onSuccess { dashboard ->
                    _uiState.update {
                        dashboard.toUiState(selectedFilter = it.selectedFilter)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: MY_ROADMAP_LOAD_ERROR
                        )
                    }
                }
        }
    }

    fun onFilterSelected(filter: MyRoadmapFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}

private fun Dashboard.toUiState(selectedFilter: MyRoadmapFilter): MyRoadmapUiState {
    val categoryLabels = skillCategories.associate { category ->
        category.category to category.label
    }

    return MyRoadmapUiState(
        userName = userProfile.fullName.toFirstName(),
        filters = listOf(
            MyRoadmapFilterUiModel(MyRoadmapFilter.Active, summary.activeRoadmaps),
            MyRoadmapFilterUiModel(MyRoadmapFilter.All, summary.totalRoadmaps),
            MyRoadmapFilterUiModel(MyRoadmapFilter.Completed, summary.completedRoadmaps),
            MyRoadmapFilterUiModel(MyRoadmapFilter.Behind, roadmapStatus.behindPace)
        ),
        selectedFilter = selectedFilter,
        roadmaps = roadmaps.map { roadmap ->
            roadmap.toUiModel(categoryLabels[roadmap.roleCategory])
        },
        achievements = skillCategories.map { category ->
            MyRoadmapAchievementUiModel(
                categoryKey = category.category,
                label = category.label,
                totalSkills = category.totalSkills
            )
        },
        completedSkills = summary.completedSkills,
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

private const val MY_ROADMAP_LOAD_ERROR = "Unable to load your roadmaps"
