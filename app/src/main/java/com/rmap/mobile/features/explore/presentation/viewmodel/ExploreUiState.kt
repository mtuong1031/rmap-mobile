package com.rmap.mobile.features.explore.presentation.viewmodel

import com.rmap.mobile.core.ui.components.RoadmapCardUiModel

data class ExploreUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val categories: List<CategoryUiModel> = emptyList(),
    val recommendedItems: List<RecommendedCardUiModel> = emptyList(),
    val popularRoadmaps: List<RoadmapCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
