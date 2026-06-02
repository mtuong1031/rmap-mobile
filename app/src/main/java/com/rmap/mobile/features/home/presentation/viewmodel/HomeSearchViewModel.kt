package com.rmap.mobile.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.ui.theme.OnPrimaryContainerLight
import com.rmap.mobile.core.ui.theme.OnSecondaryContainerLight
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.SecondaryContainerLight
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import com.rmap.mobile.features.home.domain.model.HomeSearchRoadmap
import com.rmap.mobile.features.home.domain.model.HomeSearchSkill
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.home.domain.repository.RecentSearchRepository
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapBookmarkSnapshotUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemStyle
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchRoadmapItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillItemUiModel
import com.rmap.mobile.features.home.presentation.components.search.HomeSearchSkillStatusStyle
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryDisplayLabel
import com.rmap.mobile.features.roadmap.domain.model.toRoadmapCategoryIcon
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeSearchViewModel(
    private val homeRepository: HomeRepository = RMapAppGraph.homeRepository,
    private val bookmarkRepository: BookmarkRepository = RMapAppGraph.bookmarkRepository,
    private val recentSearchRepository: RecentSearchRepository = RMapAppGraph.recentSearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeSearchUiState())
    val uiState: StateFlow<HomeSearchUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeSearchEvent>()
    val events: SharedFlow<HomeSearchEvent> = _events.asSharedFlow()

    private var searchJob: Job? = null
    private var roadmapMoreJob: Job? = null
    private var skillMoreJob: Job? = null

    init {
        observeRecentSearches()
        searchJob = viewModelScope.launch {
            performSearch(query = "", roadmapPage = 1, skillPage = 1)
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                isLoading = true,
                isLoadingMoreRoadmaps = false,
                isLoadingMoreSkills = false,
                errorMessage = null
            )
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(HOME_SEARCH_DEBOUNCE_MILLIS)
            performSearch(
                query = query.trim(),
                roadmapPage = 1,
                skillPage = 1
            )
        }
    }

    fun onSeeMoreRoadmapsClick() {
        val state = _uiState.value
        if (!state.hasMoreRoadmaps || state.isLoadingMoreRoadmaps || state.isLoading) return

        roadmapMoreJob?.cancel()
        roadmapMoreJob = viewModelScope.launch {
            val nextPage = state.roadmapPage + 1
            _uiState.update { it.copy(isLoadingMoreRoadmaps = true, errorMessage = null) }
            homeRepository.searchDashboard(
                query = state.query.trim(),
                roadmapPage = nextPage,
                skillPage = state.skillPage
            )
                .onSuccess { result ->
                    val mappedRoadmaps = result.roadmaps.data.toRoadmapUiModels()
                    _uiState.update {
                        it.copy(
                            roadmaps = it.roadmaps + mappedRoadmaps,
                            roadmapPage = result.roadmaps.meta.page,
                            roadmapTotal = result.roadmaps.meta.total,
                            hasMoreRoadmaps = result.roadmaps.meta.hasNextPage,
                            isLoadingMoreRoadmaps = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingMoreRoadmaps = false,
                            errorMessage = error.message ?: HOME_SEARCH_LOAD_ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    fun onClearRecentSearchesClick() {
        viewModelScope.launch {
            recentSearchRepository.clearSearches()
        }
    }

    fun onRemoveRecentSearchClick(query: String) {
        viewModelScope.launch {
            recentSearchRepository.removeSearch(query)
        }
    }

    fun onSeeMoreSkillsClick() {
        val state = _uiState.value
        if (!state.hasMoreSkills || state.isLoadingMoreSkills || state.isLoading) return

        skillMoreJob?.cancel()
        skillMoreJob = viewModelScope.launch {
            val nextPage = state.skillPage + 1
            _uiState.update { it.copy(isLoadingMoreSkills = true, errorMessage = null) }
            homeRepository.searchDashboard(
                query = state.query.trim(),
                roadmapPage = state.roadmapPage,
                skillPage = nextPage
            )
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            skills = it.skills + result.skills.data.map { skill -> skill.toSkillUiModel() },
                            skillPage = result.skills.meta.page,
                            skillTotal = result.skills.meta.total,
                            hasMoreSkills = result.skills.meta.hasNextPage,
                            isLoadingMoreSkills = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingMoreSkills = false,
                            errorMessage = error.message ?: HOME_SEARCH_LOAD_ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    fun onRoadmapBookmarkClick(item: HomeSearchRoadmapItemUiModel) {
        viewModelScope.launch {
            val result = if (item.isSaved) {
                bookmarkRepository.deleteRoadmap(item.id)
            } else {
                bookmarkRepository.saveRoadmap(item.snapshot.toDomain())
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

    private suspend fun performSearch(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ) {
        _uiState.update {
            it.copy(
                isLoading = true,
                isLoadingMoreRoadmaps = false,
                isLoadingMoreSkills = false,
                errorMessage = null
            )
        }
        homeRepository.searchDashboard(
            query = query,
            roadmapPage = roadmapPage,
            skillPage = skillPage
        )
            .onSuccess { result -> updateSearchResult(result) }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        roadmaps = emptyList(),
                        skills = emptyList(),
                        roadmapTotal = 0,
                        skillTotal = 0,
                        isLoading = false,
                        errorMessage = error.message ?: HOME_SEARCH_LOAD_ERROR_MESSAGE
                    )
                }
            }
    }

    private suspend fun updateSearchResult(result: HomeSearchResult) {
        val roadmaps = result.roadmaps.data.toRoadmapUiModels()
        recentSearchRepository.saveSearch(result.query)
        _uiState.update {
            it.copy(
                query = result.query,
                roadmaps = roadmaps,
                skills = result.skills.data.map { skill -> skill.toSkillUiModel() },
                roadmapPage = result.roadmaps.meta.page,
                skillPage = result.skills.meta.page,
                roadmapTotal = result.roadmaps.meta.total,
                skillTotal = result.skills.meta.total,
                hasMoreRoadmaps = result.roadmaps.meta.hasNextPage,
                hasMoreSkills = result.skills.meta.hasNextPage,
                isLoading = false,
                isLoadingMoreRoadmaps = false,
                isLoadingMoreSkills = false,
                errorMessage = null
            )
        }
    }

    private suspend fun List<HomeSearchRoadmap>.toRoadmapUiModels(): List<HomeSearchRoadmapItemUiModel> {
        val savedIds = map { roadmap -> roadmap.roadmapId }
            .associateWith { roadmapId -> bookmarkRepository.isRoadmapSaved(roadmapId).getOrDefault(false) }

        return map { roadmap ->
            roadmap.toHomeSearchRoadmapItemUiModel(
                isSaved = savedIds[roadmap.roadmapId] == true
            )
        }
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.recentSearches
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: HOME_SEARCH_RECENT_ERROR_MESSAGE)
                    }
                }
                .collect { recentSearches ->
                    _uiState.update { it.copy(recentSearches = recentSearches) }
                }
        }
    }

    private fun updateSavedState(
        roadmapId: String,
        isSaved: Boolean
    ) {
        _uiState.update {
            it.copy(
                roadmaps = it.roadmaps.map { roadmap ->
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
    val recentSearches: List<String> = emptyList(),
    val roadmaps: List<HomeSearchRoadmapItemUiModel> = emptyList(),
    val skills: List<HomeSearchSkillItemUiModel> = emptyList(),
    val roadmapPage: Int = 1,
    val skillPage: Int = 1,
    val roadmapTotal: Int = 0,
    val skillTotal: Int = 0,
    val hasMoreRoadmaps: Boolean = false,
    val hasMoreSkills: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMoreRoadmaps: Boolean = false,
    val isLoadingMoreSkills: Boolean = false,
    val errorMessage: String? = null
)

sealed class HomeSearchEvent {
    data object RoadmapBookmarkSaved : HomeSearchEvent()
    data object RoadmapBookmarkRemoved : HomeSearchEvent()
    data object BookmarkActionFailed : HomeSearchEvent()
}

private fun HomeSearchRoadmap.toHomeSearchRoadmapItemUiModel(
    isSaved: Boolean
): HomeSearchRoadmapItemUiModel {
    val icon = roleCategory.toRoadmapCategoryIcon()
    val displayLabel = roleCategory.toRoadmapCategoryDisplayLabel(categoryLabel)
    val duration = toDurationText()
    return HomeSearchRoadmapItemUiModel(
        id = roadmapId,
        title = title,
        categoryLabel = displayLabel,
        metadataText = duration,
        style = toHomeSearchRoadmapItemStyle(icon),
        snapshot = HomeSearchRoadmapBookmarkSnapshotUiModel(
            roadmapId = roadmapId,
            title = title,
            categoryId = roleCategory,
            categoryLabel = displayLabel,
            nodesTotal = 0,
            durationLabel = duration,
            iconKey = icon.name
        ),
        isSaved = isSaved,
        leadingIcon = icon.toImageVector()
    )
}

private fun HomeSearchSkill.toSkillUiModel(): HomeSearchSkillItemUiModel {
    return HomeSearchSkillItemUiModel(
        id = skillId,
        title = name,
        parentText = roleCategory.toRoadmapCategoryDisplayLabel(categoryLabel),
        statusText = defaultEstimatedHours?.let { "$it h" } ?: HOME_SEARCH_SELF_PACED_TEXT,
        statusStyle = defaultEstimatedHours?.let {
            HomeSearchSkillStatusStyle(
                containerColor = PrimaryContainerLight,
                contentColor = PrimaryLight
            )
        } ?: HomeSearchSkillStatusStyle(
            containerColor = PrimaryContainerLight,
            contentColor = OnSurfacePlaceholderLight
        )
    )
}

private fun HomeSearchRoadmap.toDurationText(): String {
    return durationLabel?.takeIf { it.isNotBlank() }
        ?: estimatedWeeks?.let { "$it weeks" }
        ?: HOME_SEARCH_SELF_PACED_TEXT
}

private fun HomeSearchRoadmap.toHomeSearchRoadmapItemStyle(
    icon: LearningTopicIcon
): HomeSearchRoadmapItemStyle {
    return when (icon) {
        LearningTopicIcon.Palette -> HomeSearchRoadmapItemStyle(
            iconContainerColor = SecondaryContainerLight,
            iconContentColor = OnSecondaryContainerLight,
            categoryColor = OnSecondaryContainerLight
        )
        LearningTopicIcon.SmartToy,
        LearningTopicIcon.Science,
        LearningTopicIcon.Game,
        LearningTopicIcon.Security -> HomeSearchRoadmapItemStyle(
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

private fun HomeSearchRoadmapBookmarkSnapshotUiModel.toDomain(): RoadmapBookmarkSnapshot {
    return RoadmapBookmarkSnapshot(
        roadmapId = roadmapId,
        title = title,
        categoryId = categoryId,
        categoryLabel = categoryLabel,
        nodesTotal = nodesTotal,
        durationLabel = durationLabel,
        iconKey = iconKey
    )
}

private const val HOME_SEARCH_DEBOUNCE_MILLIS = 500L
private const val HOME_SEARCH_LOAD_ERROR_MESSAGE = "Unable to load search results"
private const val HOME_SEARCH_RECENT_ERROR_MESSAGE = "Unable to load recent searches"
private const val HOME_SEARCH_SELF_PACED_TEXT = "Self-paced"
