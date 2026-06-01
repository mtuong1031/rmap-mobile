package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.home.presentation.viewmodel.toTrendingRoadmapCardUiModel
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toCategoryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LibraryPageSize = 10

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
            val popularResult = roadmapRepository.getTrendingRoadmaps()
            val libraryResult = roadmapRepository.searchRoadmaps(_uiState.value.searchQuery)

            val failure = listOf(profileResult, categoryResult, popularResult, libraryResult)
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

            val categories = categoryResult.getOrThrow()
            val selectedCategoryId = _uiState.value.selectedCategoryId
                ?.takeIf { id -> categories.any { category -> category.id == id } }
            val libraryRoadmaps = libraryResult.getOrThrow()
            _uiState.value = ExploreUiState(
                userName = profileResult.getOrThrow().userName,
                searchQuery = _uiState.value.searchQuery,
                selectedCategoryId = selectedCategoryId,
                categories = categories.let {
                    val categoryCounts = libraryRoadmaps.categoryCounts()
                    categories.map { category ->
                        category.toCategoryUiModel(roadmapCount = categoryCounts[category.id] ?: 0)
                    }
                },
                popularRoadmaps = popularResult.getOrThrow().mapIndexed { index, roadmap ->
                    roadmap.toTrendingRoadmapCardUiModel(rank = index + 1)
                },
                libraryRoadmaps = libraryRoadmaps.toVisibleLibraryRoadmaps(
                    selectedCategoryId = selectedCategoryId,
                    categoryLabels = categories.associate { it.id to it.name },
                    visibleCount = LibraryPageSize
                ),
                totalLibraryCount = libraryRoadmaps
                    .filterByCategory(selectedCategoryId)
                    .size,
                libraryVisibleCount = LibraryPageSize,
                isLoading = false
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, libraryVisibleCount = LibraryPageSize) }
        refreshLibrary(
            query = query,
            selectedCategoryId = _uiState.value.selectedCategoryId,
            visibleCount = LibraryPageSize
        )
    }

    fun onCategorySelected(category: CategoryUiModel) {
        val nextCategoryId = if (_uiState.value.selectedCategoryId == category.id) {
            null
        } else {
            category.id
        }

        _uiState.update {
            it.copy(
                selectedCategoryId = nextCategoryId,
                libraryVisibleCount = LibraryPageSize
            )
        }
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = nextCategoryId,
            visibleCount = LibraryPageSize
        )
    }

    fun selectCategoryById(categoryId: String) {
        if (_uiState.value.selectedCategoryId == categoryId) return
        if (_uiState.value.categories.isNotEmpty() && _uiState.value.categories.none { it.id == categoryId }) return
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                libraryVisibleCount = LibraryPageSize
            )
        }
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = categoryId,
            visibleCount = LibraryPageSize
        )
    }

    fun onViewAllCategories() {
        _uiState.update {
            it.copy(
                selectedCategoryId = null,
                libraryVisibleCount = LibraryPageSize
            )
        }
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = null,
            visibleCount = LibraryPageSize
        )
    }

    fun onSeeMoreRoadmaps() {
        val nextVisibleCount = _uiState.value.libraryVisibleCount + LibraryPageSize
        _uiState.update { it.copy(libraryVisibleCount = nextVisibleCount) }
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = _uiState.value.selectedCategoryId,
            visibleCount = nextVisibleCount
        )
    }

    fun onSeeAllRoadmaps() {
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = _uiState.value.selectedCategoryId,
            visibleCount = Int.MAX_VALUE
        )
    }

    fun onSeeLessRoadmaps() {
        _uiState.update { it.copy(libraryVisibleCount = LibraryPageSize) }
        refreshLibrary(
            query = _uiState.value.searchQuery,
            selectedCategoryId = _uiState.value.selectedCategoryId,
            visibleCount = LibraryPageSize
        )
    }

    private fun refreshLibrary(
        query: String,
        selectedCategoryId: String?,
        visibleCount: Int
    ) {
        viewModelScope.launch {
            roadmapRepository.searchRoadmaps(query)
                .onSuccess { roadmaps ->
                    val categoryCounts = roadmaps.categoryCounts()
                    val categoryLabels = _uiState.value.categories.associate { it.id to it.name }
                    val filteredRoadmaps = roadmaps.filterByCategory(selectedCategoryId)
                    _uiState.update {
                        it.copy(
                            categories = it.categories.map { category ->
                                category.copy(roadmapCount = categoryCounts[category.id] ?: 0)
                            },
                            libraryRoadmaps = filteredRoadmaps
                                .take(visibleCount)
                                .map { roadmap -> roadmap.toExploreRoadmapCardUiModel(categoryLabels) },
                            totalLibraryCount = filteredRoadmaps.size,
                            libraryVisibleCount = visibleCount.coerceAtMost(filteredRoadmaps.size),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Unable to search roadmaps") }
                }
        }
    }

    private fun List<RoadmapSummary>.categoryCounts(): Map<String, Int> {
        return groupingBy { it.categoryId }.eachCount()
    }

    private fun List<RoadmapSummary>.filterByCategory(categoryId: String?): List<RoadmapSummary> {
        return if (categoryId == null) {
            this
        } else {
            filter { it.categoryId == categoryId }
        }
    }

    private fun List<RoadmapSummary>.toVisibleLibraryRoadmaps(
        selectedCategoryId: String?,
        categoryLabels: Map<String, String>,
        visibleCount: Int
    ): List<ExploreRoadmapCardUiModel> {
        return filterByCategory(selectedCategoryId)
            .take(visibleCount)
            .map { it.toExploreRoadmapCardUiModel(categoryLabels) }
    }

    private fun RoadmapSummary.toExploreRoadmapCardUiModel(
        categoryLabels: Map<String, String>
    ): ExploreRoadmapCardUiModel {
        return ExploreRoadmapCardUiModel(
            id = id,
            title = title,
            categoryLabel = categoryLabels[categoryId] ?: categoryId.replaceFirstChar { it.uppercase() }
        )
    }
}
