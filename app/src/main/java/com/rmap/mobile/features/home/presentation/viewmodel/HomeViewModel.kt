package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.home.presentation.components.recommend.HomeRoadmapCardUiModel
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val profileRepository: ProfileRepository = RMapAppGraph.profileRepository,
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadHome()
        observeSavedRoadmaps()
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
                hasInProgressRoadmap = progress.totalLessons > 0 &&
                    progress.completedLessons < progress.totalLessons,
                trendingRoadmaps = roadmapResult.getOrThrow()
                    .take(3)
                    .mapIndexed { index, roadmap ->
                        roadmap.toTrendingRoadmapCardUiModel(rank = index + 1)
                    },
                isLoading = false
            )
        }
    }

    fun onRecommendedRoadmapBookmarkClick(item: HomeRoadmapCardUiModel) {
        viewModelScope.launch {
            val result = if (item.isSaved) {
                bookmarkRepository.deleteRoadmap(item.id)
            } else {
                bookmarkRepository.saveRoadmap(item.id)
            }

            result
                .onSuccess {
                    updateSavedRoadmapState(
                        roadmapId = item.id,
                        isSaved = !item.isSaved
                    )
                    _events.emit(
                        if (item.isSaved) {
                            HomeEvent.RoadmapBookmarkRemoved
                        } else {
                            HomeEvent.RoadmapBookmarkSaved
                        }
                    )
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to update bookmark") }
                    _events.emit(HomeEvent.BookmarkActionFailed)
                }
        }
    }

    private fun observeSavedRoadmaps() {
        viewModelScope.launch {
            bookmarkRepository.observeSavedRoadmaps()
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to load bookmarks") }
                }
                .collect { savedRoadmaps ->
                    _uiState.update {
                        it.copy(
                            savedRoadmapIds = savedRoadmaps
                                .map { bookmark -> bookmark.summary.id }
                                .toSet()
                        )
                    }
                }
        }
    }

    private fun updateSavedRoadmapState(
        roadmapId: String,
        isSaved: Boolean
    ) {
        _uiState.update {
            val nextIds = if (isSaved) {
                it.savedRoadmapIds + roadmapId
            } else {
                it.savedRoadmapIds - roadmapId
            }
            it.copy(savedRoadmapIds = nextIds)
        }
    }
}

sealed class HomeEvent {
    data object RoadmapBookmarkSaved : HomeEvent()
    data object RoadmapBookmarkRemoved : HomeEvent()
    data object BookmarkActionFailed : HomeEvent()
}
