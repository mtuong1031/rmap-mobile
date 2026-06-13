package com.rmap.mobile.features.explore.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.explore.presentation.components.ExploreCategorySection
import com.rmap.mobile.features.explore.presentation.components.ExploreContentSkeleton
import com.rmap.mobile.core.ui.components.RMapSearchBar
import com.rmap.mobile.features.explore.presentation.components.ExploreSearchBarSkeleton
import com.rmap.mobile.features.explore.presentation.components.RoadmapLibrarySection
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreRoadmapCardUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreUiState
import com.rmap.mobile.navigation.NavBarDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Explore,
    reselectEvent: Flow<NavBarDestination> = emptyFlow(),
    onHeaderActionClick: () -> Unit = {},
    avatarUrl: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onCategoryClick: (CategoryUiModel) -> Unit = {},
    onRoadmapClick: (ExploreRoadmapCardUiModel) -> Unit = {},
    onSeeMoreRoadmapsClick: () -> Unit = {},
    onSeeAllRoadmapsClick: () -> Unit = {},
    onSeeLessRoadmapsClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(reselectEvent) {
        reselectEvent.collectLatest {
            listState.animateScrollToItem(0)
        }
    }

    val selectedCategoryName = uiState.categories
        .firstOrNull { it.id == uiState.selectedCategoryId }
        ?.name

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingNone,
                    end = Dimens.spacingNone,
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact + Dimens.floatingNavBarHeight
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
            ) {
                item {
                    RMapHeader(
                        greetingText = stringResource(R.string.explore_greeting),
                        headingText = stringResource(R.string.explore_title),
                        actionImageUrl = avatarUrl,
                        onActionClick = onHeaderActionClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    )
                }

                item {
                    Box(
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    ) {
                        if (uiState.isLoading) {
                            ExploreSearchBarSkeleton()
                        } else {
                            RMapSearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = onSearchQueryChange,
                                placeholder = stringResource(R.string.explore_search_placeholder),
                                height = Dimens.exploreSearchBarHeight,
                                contentPadding = PaddingValues(
                                    start = Dimens.spacingLg,
                                    end = Dimens.spacingLg
                                )
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    item {
                        ExploreContentSkeleton()
                    }
                } else {
                    item {
                        ExploreCategorySection(
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategoryClick = onCategoryClick
                        )
                    }

                    item {
                        RoadmapLibrarySection(
                            roadmaps = uiState.libraryRoadmaps,
                            selectedCategoryName = selectedCategoryName,
                            totalCount = uiState.totalLibraryCount,
                            isFetchingMore = uiState.isFetchingMoreRoadmaps,
                            onRoadmapClick = onRoadmapClick,
                            onSeeMoreClick = onSeeMoreRoadmapsClick,
                            onSeeAllClick = onSeeAllRoadmapsClick,
                            onSeeLessClick = onSeeLessRoadmapsClick
                        )
                    }
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Explore Screen - Light", widthDp = 390, heightDp = 900, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ExploreScreenLightPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ExploreScreen(uiState = ExploreUiState(userName = "Minh"))
    }
}

@Preview(showBackground = true, name = "Explore Screen - Dark", widthDp = 390, heightDp = 900, backgroundColor = 0xFF000000)
@Composable
private fun ExploreScreenDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        ExploreScreen(uiState = ExploreUiState(userName = "Minh"))
    }
}

@Preview(showBackground = true, name = "Explore Screen - Loading", widthDp = 390, heightDp = 900, backgroundColor = 0xFFF4F8FF)
@Composable
private fun ExploreScreenLoadingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ExploreScreen(
            uiState = ExploreUiState(
                userName = "Minh",
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, name = "Search Bar Component")
@Composable
private fun SearchBarPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            RMapSearchBar(
                query = "",
                onQueryChange = {},
                placeholder = stringResource(R.string.explore_search_placeholder),
                height = Dimens.exploreSearchBarHeight,
                contentPadding = PaddingValues(
                    start = Dimens.spacingLg,
                    end = Dimens.spacingLg
                )
            )
        }
    }
}
