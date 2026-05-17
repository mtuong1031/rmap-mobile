package com.rmap.mobile.features.explore.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryUiModel(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

data class RecommendedCardUiModel(
    val id: String,
    val title: String,
    val badgeText: String,
    val skillNodesCount: Int,
    val level: String,
    val coverImageUrl: String,
    val accentColor: Color
)
