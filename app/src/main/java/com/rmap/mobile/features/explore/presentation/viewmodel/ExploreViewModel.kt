package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toCategoryUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LibraryPageSize = 10

class ExploreViewModel(
    private val roadmapRepository: RoadmapRepository = RMapAppGraph.roadmapRepository,
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allLibraryRoadmaps: List<RoadmapSummary> = emptyList()
    private var categoryLabels: Map<String, String> = emptyMap()

    init {
        loadExplore()
    }

    fun loadExplore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val (categoryResult, libraryResult) = coroutineScope {
                val categories = async { roadmapRepository.getExploreCategories() }
                val library = async { roadmapRepository.searchRoadmaps("") }
                categories.await() to library.await()
            }

            val failure = listOf(categoryResult, libraryResult)
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

            val selectedCategoryId = _uiState.value.selectedCategoryId
            val categories = categoryResult.getOrThrow()
            val validSelectedCategoryId = selectedCategoryId
                ?.takeIf { id -> categories.any { category -> category.id == id } }
            allLibraryRoadmaps = libraryResult.getOrThrow()
            categoryLabels = categories.associate { it.id to it.name }
            val visibleRoadmaps = allLibraryRoadmaps
                .filterByTitle(_uiState.value.searchQuery)
                .filterByCategory(validSelectedCategoryId)
            _uiState.value = ExploreUiState(
                userName = (authRepository.authState.value as? AuthState.Authenticated)
                    ?.user
                    ?.fullName
                    .orEmpty(),
                searchQuery = _uiState.value.searchQuery,
                selectedCategoryId = validSelectedCategoryId,
                categories = categories.map { category -> category.toCategoryUiModel() },
                libraryRoadmaps = visibleRoadmaps.toVisibleLibraryRoadmaps(
                    visibleCount = LibraryPageSize
                ),
                totalLibraryCount = visibleRoadmaps.size,
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
        val filteredRoadmaps = allLibraryRoadmaps
            .filterByTitle(query)
            .filterByCategory(selectedCategoryId)

        _uiState.update {
            it.copy(
                libraryRoadmaps = filteredRoadmaps.toVisibleLibraryRoadmaps(visibleCount),
                totalLibraryCount = filteredRoadmaps.size,
                libraryVisibleCount = visibleCount.coerceAtMost(filteredRoadmaps.size),
                errorMessage = null
            )
        }
    }

    private fun List<RoadmapSummary>.filterByCategory(categoryId: String?): List<RoadmapSummary> {
        return if (categoryId == null) {
            this
        } else {
            filter { it.categoryId == categoryId }
        }
    }

    private fun List<RoadmapSummary>.filterByTitle(query: String): List<RoadmapSummary> {
        val normalizedQuery = query.trim()
        return if (normalizedQuery.isBlank()) {
            this
        } else {
            filter { roadmap -> roadmap.title.contains(normalizedQuery, ignoreCase = true) }
        }
    }

    private fun List<RoadmapSummary>.toVisibleLibraryRoadmaps(
        visibleCount: Int
    ): List<ExploreRoadmapCardUiModel> {
        return take(visibleCount)
            .map { it.toExploreRoadmapCardUiModel() }
    }

    private fun RoadmapSummary.toExploreRoadmapCardUiModel(): ExploreRoadmapCardUiModel {
        return ExploreRoadmapCardUiModel(
            id = id,
            title = title,
            categoryLabel = categoryLabels[categoryId] ?: categoryId.replaceFirstChar { it.uppercase() }
        )
    }
}
