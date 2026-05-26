package com.rmap.mobile.features.bookmarks.presentation.viewmodel

import com.rmap.mobile.features.bookmarks.domain.model.BookmarkStatusFilter
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.skill.BookmarkSkillCardUiModel

data class BookmarksUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val selectedTab: BookmarkTab = BookmarkTab.Roadmaps,
    val selectedStatusFilter: BookmarkStatusFilter = BookmarkStatusFilter.All,
    val roadmapItems: List<BookmarkRoadmapCardUiModel> = emptyList(),
    val skillItems: List<BookmarkSkillCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
