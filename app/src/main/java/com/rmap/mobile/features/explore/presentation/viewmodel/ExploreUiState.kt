package com.rmap.mobile.features.explore.presentation.viewmodel

data class ExploreUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val libraryRoadmaps: List<ExploreRoadmapCardUiModel> = emptyList(),
    val totalLibraryCount: Int = 0,
    val libraryVisibleCount: Int = 10,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
