package com.rmap.mobile.features.bookmarks.presentation

const val TAB_INDEX_ROADMAPS = 0
const val TAB_INDEX_SKILLS = 1

data class BookmarksUiState(
    val userName: String = "Thinh",
    val selectedTabIndex: Int = TAB_INDEX_ROADMAPS,
)
