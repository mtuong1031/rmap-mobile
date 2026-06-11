package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId
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
    private val authRepository: AuthRepository = RMapAppGraph.authRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allLibraryRoadmaps: List<RoadmapSummary> = emptyList()
    private var categoryLabels: Map<String, String> = emptyMap()

    private var currentLibraryPage = 1
    private var isFetchingLibrary = false
    private var serverTotalLibraryCount = 0

    init {
        loadExplore()
    }

    fun loadExplore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val categoryResult = roadmapRepository.getExploreCategories()

            val selectedCategoryId = _uiState.value.selectedCategoryId
            val categories = categoryResult.getOrNull() ?: emptyList()
            val validSelectedCategoryId = selectedCategoryId
                ?.takeIf { id -> categories.any { category -> category.id == id } }

            currentLibraryPage = 1
            val libraryResult = roadmapRepository.searchRoadmaps(
                query = "",
                categoryId = validSelectedCategoryId,
                page = currentLibraryPage,
                perPage = LibraryPageSize
            )

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

            val (roadmaps, totalCount) = libraryResult.getOrThrow()
            allLibraryRoadmaps = roadmaps
            serverTotalLibraryCount = totalCount

            categoryLabels = categories.associate { it.id.toStableLearningId() to it.name }

            _uiState.update { it.copy(categories = categories.map { c -> c.toCategoryUiModel() }) }

            refreshLibrary(
                query = _uiState.value.searchQuery,
                selectedCategoryId = validSelectedCategoryId,
                visibleCount = LibraryPageSize
            )

            _uiState.update { it.copy(isLoading = false) }
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
        fetchLibraryForCurrentFilters()
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
        fetchLibraryForCurrentFilters()
    }

    private fun fetchLibraryForCurrentFilters() {
        if (isFetchingLibrary) return
        isFetchingLibrary = true
        currentLibraryPage = 1

        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingMoreRoadmaps = true) }
            val libraryResult = roadmapRepository.searchRoadmaps(
                query = "", // Server doesn't support query search
                categoryId = _uiState.value.selectedCategoryId,
                page = currentLibraryPage,
                perPage = LibraryPageSize
            )

            if (libraryResult.isSuccess) {
                val (roadmaps, totalCount) = libraryResult.getOrThrow()
                allLibraryRoadmaps = roadmaps
                serverTotalLibraryCount = totalCount
            }

            _uiState.update { it.copy(isFetchingMoreRoadmaps = false) }
            isFetchingLibrary = false

            refreshLibrary(
                query = _uiState.value.searchQuery,
                selectedCategoryId = _uiState.value.selectedCategoryId,
                visibleCount = LibraryPageSize
            )
        }
    }

    fun onSeeMoreRoadmaps() {
        if (isFetchingLibrary) return

        val isFilteringByQueryLocally = _uiState.value.searchQuery.isNotBlank()
        if (isFilteringByQueryLocally || allLibraryRoadmaps.size >= serverTotalLibraryCount) {
            val nextVisibleCount = _uiState.value.libraryVisibleCount + LibraryPageSize
            _uiState.update { it.copy(libraryVisibleCount = nextVisibleCount) }
            refreshLibrary(
                query = _uiState.value.searchQuery,
                selectedCategoryId = _uiState.value.selectedCategoryId,
                visibleCount = nextVisibleCount
            )
            return
        }

        isFetchingLibrary = true
        currentLibraryPage++
        _uiState.update { it.copy(isFetchingMoreRoadmaps = true) }
        viewModelScope.launch {
            val libraryResult = roadmapRepository.searchRoadmaps(
                query = "",
                categoryId = _uiState.value.selectedCategoryId,
                page = currentLibraryPage,
                perPage = LibraryPageSize
            )
            if (libraryResult.isSuccess) {
                val (newRoadmaps, totalCount) = libraryResult.getOrThrow()
                serverTotalLibraryCount = totalCount
                val newRoadmapsFiltered = newRoadmaps.filter { newRm -> allLibraryRoadmaps.none { existing -> existing.id == newRm.id } }
                allLibraryRoadmaps = allLibraryRoadmaps + newRoadmapsFiltered

                val nextVisibleCount = _uiState.value.libraryVisibleCount + LibraryPageSize
                _uiState.update { it.copy(libraryVisibleCount = nextVisibleCount) }
                refreshLibrary(
                    query = _uiState.value.searchQuery,
                    selectedCategoryId = _uiState.value.selectedCategoryId,
                    visibleCount = nextVisibleCount
                )
            } else {
                currentLibraryPage--
            }
            _uiState.update { it.copy(isFetchingMoreRoadmaps = false) }
            isFetchingLibrary = false
        }
    }

    fun onSeeAllRoadmaps() {
        if (isFetchingLibrary) return

        if (allLibraryRoadmaps.size >= serverTotalLibraryCount) {
            _uiState.update { it.copy(libraryVisibleCount = Int.MAX_VALUE) }
            refreshLibrary(
                query = _uiState.value.searchQuery,
                selectedCategoryId = _uiState.value.selectedCategoryId,
                visibleCount = Int.MAX_VALUE
            )
            return
        }

        isFetchingLibrary = true
        _uiState.update { it.copy(isFetchingMoreRoadmaps = true) }
        viewModelScope.launch {
            val libraryResult = roadmapRepository.searchRoadmaps(
                query = "",
                categoryId = _uiState.value.selectedCategoryId,
                page = 1,
                perPage = serverTotalLibraryCount.coerceAtLeast(100)
            )
            if (libraryResult.isSuccess) {
                val (roadmaps, totalCount) = libraryResult.getOrThrow()
                serverTotalLibraryCount = totalCount
                allLibraryRoadmaps = roadmaps
            }
            _uiState.update { it.copy(libraryVisibleCount = Int.MAX_VALUE) }
            refreshLibrary(
                query = _uiState.value.searchQuery,
                selectedCategoryId = _uiState.value.selectedCategoryId,
                visibleCount = Int.MAX_VALUE
            )
            _uiState.update { it.copy(isFetchingMoreRoadmaps = false) }
            isFetchingLibrary = false
        }
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

        val isFilteringByQueryLocally = query.isNotBlank()
        val totalCountForUi = if (isFilteringByQueryLocally) {
            filteredRoadmaps.size
        } else {
            serverTotalLibraryCount.coerceAtLeast(filteredRoadmaps.size)
        }

        _uiState.update {
            it.copy(
                selectedCategoryId = selectedCategoryId,
                libraryRoadmaps = filteredRoadmaps.toVisibleLibraryRoadmaps(visibleCount),
                totalLibraryCount = totalCountForUi,
                libraryVisibleCount = visibleCount,
                errorMessage = null
            )
        }
    }

    private fun List<RoadmapSummary>.filterByCategory(categoryId: String?): List<RoadmapSummary> {
        return if (categoryId == null) {
            this
        } else {
            val stableId = categoryId.toStableLearningId()
            filter { it.categoryId == categoryId || it.categoryId == stableId }
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
