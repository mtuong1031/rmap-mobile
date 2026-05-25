package com.rmap.mobile.features.explore.presentation.viewmodel

import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel

data class ExploreUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val popularRoadmaps: List<TrendingRoadmapCardUiModel> = emptyList(),
    val libraryRoadmaps: List<ExploreRoadmapCardUiModel> = emptyList(),
    val totalLibraryCount: Int = 0,
    val libraryVisibleCount: Int = 10,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
