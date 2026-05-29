package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.ui.theme.OnPrimaryContainerLight
import com.rmap.mobile.core.ui.theme.OnSecondaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.SecondaryContainerLight
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemStyle
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemUiModel
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeSearchViewModel(
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeSearchUiState())
    val uiState: StateFlow<HomeSearchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeSearchEvent>()
    val events: SharedFlow<HomeSearchEvent> = _events.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadRoadmaps(query = "")
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        loadRoadmaps(query = query)
    }

    fun onRecommendedRoadmapBookmarkClick(item: HomeSearchRoadmapItemUiModel) {
        viewModelScope.launch {
            val result = if (item.isSaved) {
                bookmarkRepository.deleteRoadmap(item.id)
            } else {
                bookmarkRepository.saveRoadmap(item.id)
            }

            result
                .onSuccess {
                    updateSavedState(item.id, isSaved = !item.isSaved)
                    _events.emit(
                        if (item.isSaved) {
                            HomeSearchEvent.RoadmapBookmarkRemoved
                        } else {
                            HomeSearchEvent.RoadmapBookmarkSaved
                        }
                    )
                }
                .onFailure {
                    _events.emit(HomeSearchEvent.BookmarkActionFailed)
                }
        }
    }

    private fun loadRoadmaps(query: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (query.isBlank()) {
                roadmapRepository.getRecommendedRoadmaps()
            } else {
                roadmapRepository.searchRoadmaps(query)
            }

            result
                .onSuccess { roadmaps ->
                    val savedIds = roadmaps
                        .map { roadmap -> roadmap.id }
                        .associateWith { roadmapId ->
                            bookmarkRepository.isRoadmapSaved(roadmapId).getOrDefault(false)
                        }

                    _uiState.update {
                        it.copy(
                            recommendedRoadmaps = roadmaps.map { roadmap ->
                                roadmap.toHomeSearchRoadmapItemUiModel(
                                    isSaved = savedIds[roadmap.id] == true
                                )
                            },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            recommendedRoadmaps = emptyList(),
                            isLoading = false,
                            errorMessage = error.message ?: HOME_SEARCH_LOAD_ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    private fun updateSavedState(
        roadmapId: String,
        isSaved: Boolean
    ) {
        _uiState.update {
            it.copy(
                recommendedRoadmaps = it.recommendedRoadmaps.map { roadmap ->
                    if (roadmap.id == roadmapId) {
                        roadmap.copy(isSaved = isSaved)
                    } else {
                        roadmap
                    }
                }
            )
        }
    }
}

data class HomeSearchUiState(
    val query: String = "",
    val recommendedRoadmaps: List<HomeSearchRoadmapItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class HomeSearchEvent {
    data object RoadmapBookmarkSaved : HomeSearchEvent()
    data object RoadmapBookmarkRemoved : HomeSearchEvent()
    data object BookmarkActionFailed : HomeSearchEvent()
}

private fun RoadmapSummary.toHomeSearchRoadmapItemUiModel(
    isSaved: Boolean
): HomeSearchRoadmapItemUiModel {
    return HomeSearchRoadmapItemUiModel(
        id = id,
        title = title,
        categoryLabel = toHomeSearchCategoryLabel(),
        metadataText = durationLabel,
        style = toHomeSearchRoadmapItemStyle(),
        isSaved = isSaved,
        leadingIcon = icon.toImageVector()
    )
}

private fun RoadmapSummary.toHomeSearchCategoryLabel(): String {
    return when {
        icon == LearningTopicIcon.Palette -> "Design"
        categoryId.equals("devops", ignoreCase = true) -> "DevOps"
        categoryId.equals("ai", ignoreCase = true) -> "AI"
        categoryId.equals("frontend", ignoreCase = true) -> "Frontend"
        categoryId.equals("backend", ignoreCase = true) -> "Backend"
        else -> categoryId.replaceFirstChar { it.uppercase() }
    }
}

private fun RoadmapSummary.toHomeSearchRoadmapItemStyle(): HomeSearchRoadmapItemStyle {
    return when {
        icon == LearningTopicIcon.Palette -> HomeSearchRoadmapItemStyle(
            iconContainerColor = SecondaryContainerLight,
            iconContentColor = OnSecondaryContainerLight,
            categoryColor = OnSecondaryContainerLight
        )
        categoryId.equals("ai", ignoreCase = true) -> HomeSearchRoadmapItemStyle(
            iconContainerColor = SecondaryContainerLight,
            iconContentColor = PrimaryLight,
            categoryColor = PrimaryLight
        )
        else -> HomeSearchRoadmapItemStyle(
            iconContainerColor = PrimaryContainerLight,
            iconContentColor = PrimaryLight,
            categoryColor = OnPrimaryContainerLight
        )
    }
}

private const val HOME_SEARCH_LOAD_ERROR_MESSAGE = "Unable to load roadmaps"
