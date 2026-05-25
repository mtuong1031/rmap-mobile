package com.rmap.mobile.features.explore.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.explore.presentation.components.ExploreCategorySection
import com.rmap.mobile.features.explore.presentation.components.ExploreSearchBar
import com.rmap.mobile.features.explore.presentation.components.PopularRoadmapsSection
import com.rmap.mobile.features.explore.presentation.components.RoadmapLibrarySection
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreRoadmapCardUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreUiState
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.navigation.NavBarDestination

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Explore,
    onHeaderActionClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllCategoriesClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onCategoryClick: (CategoryUiModel) -> Unit = {},
    onPopularRoadmapClick: (TrendingRoadmapCardUiModel) -> Unit = {},
    onRoadmapClick: (ExploreRoadmapCardUiModel) -> Unit = {},
    onSeeMoreRoadmapsClick: () -> Unit = {},
    onSeeAllRoadmapsClick: () -> Unit = {},
    onSeeLessRoadmapsClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val selectedCategoryName = uiState.categories
        .firstOrNull { it.id == uiState.selectedCategoryId }
        ?.name

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            RMapNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingNone,
                    end = Dimens.spacingNone,
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
            ) {
                item {
                    RMapHeader(
                        greetingText = "Good morning, ${uiState.userName}",
                        headingText = stringResource(R.string.explore_title),
                        onActionClick = onHeaderActionClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    )
                }

                item {
                    Box(
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                    ) {
                        ExploreSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onFilterClick = onFilterClick
                        )
                    }
                }

                item {
                    ExploreCategorySection(
                        categories = uiState.categories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        onCategoryClick = onCategoryClick,
                        onViewAllClick = onViewAllCategoriesClick
                    )
                }

                item {
                    PopularRoadmapsSection(
                        roadmaps = uiState.popularRoadmaps,
                        onRoadmapClick = onPopularRoadmapClick
                    )
                }

                item {
                    RoadmapLibrarySection(
                        roadmaps = uiState.libraryRoadmaps,
                        selectedCategoryName = selectedCategoryName,
                        totalCount = uiState.totalLibraryCount,
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

@Preview(showBackground = true, name = "Search Bar Component")
@Composable
private fun SearchBarPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            ExploreSearchBar(
                query = "",
                onQueryChange = {},
                onFilterClick = {}
            )
        }
    }
}
