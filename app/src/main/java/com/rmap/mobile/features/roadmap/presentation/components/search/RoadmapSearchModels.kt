package com.rmap.mobile.features.roadmap.presentation.components.search

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

enum class RoadmapLocalSearchMode {
    Inline,
    Active,
    Typing
}

@Immutable
data class RoadmapQuickFilterUiModel(
    val id: String,
    @param:StringRes val labelResId: Int,
    val selected: Boolean = false
)
