package com.rmap.mobile.features.bookmarks.presentation.components.roadmap

import androidx.compose.ui.graphics.vector.ImageVector
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkCategoryStyle
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus

typealias BookmarkRoadmapCardUiModel = SavedRoadmapCardUiModel

data class SavedRoadmapCardUiModel(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val categoryIcon: ImageVector,
    val categoryStyle: BookmarkCategoryStyle = BookmarkCategoryStyle.WebDevelopment,
    val nodesLabel: String,
    val durationLabel: String,
    val savedAtLabel: String,
    val actionLabel: String,
    val status: LearningStatus = LearningStatus.NotStarted,
    val statusLabel: String = "",
    val progressPercent: Int? = null
)
