package com.rmap.mobile.features.airoadmap.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapLibraryContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapLibrarySkeleton
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapPreviewData
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState

@Composable
internal fun AiRoadmapLibraryScreen(
    uiState: AiRoadmapUiState,
    onSearchQueryChange: (String) -> Unit,
    onCreateRoadmapClick: () -> Unit,
    onSeeMoreGeneratedRoadmaps: () -> Unit,
    onSeeAllGeneratedRoadmaps: () -> Unit,
    onSeeLessGeneratedRoadmaps: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    onRoadmapSelected: (String) -> Unit
) {
    if (uiState.isLoadingGeneratedRoadmaps) {
        AiRoadmapLibrarySkeleton()
    } else {
        AiRoadmapLibraryContent(
            roadmaps = uiState.visibleGeneratedRoadmaps,
            searchQuery = uiState.searchQuery,
            searchPlaceholder = stringResource(R.string.ai_roadmap_library_search_placeholder),
            createButtonText = stringResource(R.string.ai_roadmap_library_create),
            sectionTitle = stringResource(R.string.ai_roadmap_library_section_title),
            sectionSubtitle = stringResource(R.string.ai_roadmap_library_section_subtitle),
            emptyTitle = stringResource(R.string.ai_roadmap_library_empty_title),
            emptyBody = stringResource(R.string.ai_roadmap_library_empty_body),
            searchEmptyTitle = stringResource(R.string.ai_roadmap_library_search_empty_title),
            searchEmptyBody = stringResource(R.string.ai_roadmap_library_search_empty_body),
            metadataText = { lessons, weeks ->
                stringResource(R.string.ai_roadmap_library_metadata, lessons, weeks)
            },
            createdAtText = { createdDaysAgo ->
                if (createdDaysAgo == 0) {
                    stringResource(R.string.ai_roadmap_created_today)
                } else {
                    pluralStringResource(
                        R.plurals.ai_roadmap_created_days_ago,
                        createdDaysAgo,
                        createdDaysAgo
                    )
                }
            },
            seeAllText = stringResource(R.string.roadmap_see_all),
            seeLessText = stringResource(R.string.explore_roadmap_library_see_less),
            seeMoreText = { remainingCount ->
                stringResource(
                    R.string.explore_roadmap_library_see_more,
                    remainingCount.coerceAtMost(AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE)
                )
            },
            exploreButtonText = stringResource(R.string.action_explore_roadmaps),
            totalRoadmapCount = uiState.totalGeneratedRoadmapCount,
            hasAnyRoadmaps = uiState.hasAnyGeneratedRoadmaps,
            isSearching = uiState.isSearchingGeneratedRoadmaps,
            canToggleAll = uiState.canToggleAllGeneratedRoadmaps,
            isShowingAll = uiState.isShowingAllGeneratedRoadmaps,
            hasMore = uiState.hasMoreGeneratedRoadmaps,
            onSearchQueryChange = onSearchQueryChange,
            onCreateClick = onCreateRoadmapClick,
            onExploreClick = onExploreRoadmapsClick,
            onRoadmapClick = onRoadmapSelected,
            onSeeMoreClick = onSeeMoreGeneratedRoadmaps,
            onSeeAllClick = onSeeAllGeneratedRoadmaps,
            onSeeLessClick = onSeeLessGeneratedRoadmaps
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibraryScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibraryScreen(
            uiState = AiRoadmapPreviewData.libraryState,
            onSearchQueryChange = {},
            onCreateRoadmapClick = {},
            onSeeMoreGeneratedRoadmaps = {},
            onSeeAllGeneratedRoadmaps = {},
            onSeeLessGeneratedRoadmaps = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Library - Loading", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibraryScreenLoadingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibraryScreen(
            uiState = AiRoadmapUiState(isLoadingGeneratedRoadmaps = true),
            onSearchQueryChange = {},
            onCreateRoadmapClick = {},
            onSeeMoreGeneratedRoadmaps = {},
            onSeeAllGeneratedRoadmaps = {},
            onSeeLessGeneratedRoadmaps = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}
