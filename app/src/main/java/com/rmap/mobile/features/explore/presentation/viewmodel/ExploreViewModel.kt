package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toCategoryUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toRecommendedCardUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toRoadmapCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadExplore()
    }

    fun loadExplore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profileResult = profileRepository.getProfile()
            val categoryResult = roadmapRepository.getExploreCategories()
            val recommendedResult = roadmapRepository.getRecommendedRoadmaps()
            val popularResult = roadmapRepository.searchRoadmaps(_uiState.value.searchQuery)

            val failure = listOf(profileResult, categoryResult, recommendedResult, popularResult)
                .firstOrNull { it.isFailure }
            if (failure != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = failure.exceptionOrNull()?.message ?: "Unable to load explore"
                    )
                }
                return@launch
            }

            _uiState.value = ExploreUiState(
                userName = profileResult.getOrThrow().userName,
                searchQuery = _uiState.value.searchQuery,
                categories = categoryResult.getOrThrow().map { it.toCategoryUiModel() },
                recommendedItems = recommendedResult.getOrThrow().map { it.toRecommendedCardUiModel() },
                popularRoadmaps = popularResult.getOrThrow().map { it.toRoadmapCardUiModel() },
                isLoading = false
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            roadmapRepository.searchRoadmaps(query)
                .onSuccess { roadmaps ->
                    _uiState.update {
                        it.copy(
                            popularRoadmaps = roadmaps.map { roadmap -> roadmap.toRoadmapCardUiModel() },
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to search roadmaps") }
                }
        }
    }
}
