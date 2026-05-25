package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color

data class CategoryUiModel(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val roadmapCount: Int = 0
)

data class ExploreRoadmapCardUiModel(
    val id: String,
    val title: String,
    val categoryLabel: String
)
