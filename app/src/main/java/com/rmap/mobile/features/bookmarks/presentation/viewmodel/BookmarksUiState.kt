package com.rmap.mobile.features.bookmarks.presentation.viewmodel

import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkStatusFilter
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkRoadmapCardUiModel

data class BookmarksUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val selectedTab: BookmarkTab = BookmarkTab.Roadmaps,
    val selectedStatusFilter: BookmarkStatusFilter = BookmarkStatusFilter.All,
    val roadmapItems: List<BookmarkRoadmapCardUiModel> = emptyList(),
    val skillItems: List<SkillCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
