package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toRoadmapCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profileResult = profileRepository.getProfile()
            val progressResult = roadmapRepository.getLearningProgress()
            val roadmapResult = roadmapRepository.getTrendingRoadmaps()

            val failure = listOf(profileResult, progressResult, roadmapResult).firstOrNull { it.isFailure }
            if (failure != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = failure.exceptionOrNull()?.message ?: "Unable to load home"
                    )
                }
                return@launch
            }

            val profile = profileResult.getOrThrow()
            val progress = progressResult.getOrThrow()
            _uiState.value = HomeUiState(
                userName = profile.userName,
                progressFraction = progress.progressFraction,
                completedLessons = progress.completedLessons,
                totalLessons = progress.totalLessons,
                streakDays = progress.streakDays,
                todayGoalCompleted = progress.todayGoalCompleted,
                todayGoalTotal = progress.todayGoalTotal,
                completedRoadmaps = progress.completedRoadmaps,
                trendingRoadmaps = roadmapResult.getOrThrow().map { it.toRoadmapCardUiModel() },
                isLoading = false
            )
        }
    }
}
