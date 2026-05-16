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
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.navigation.NavBarDestination
import com.rmap.mobile.core.ui.components.AppNavigationBar
import com.rmap.mobile.core.ui.components.BackgroundDecorator
import com.rmap.mobile.core.ui.components.Header
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.explore.presentation.components.CategorySection
import com.rmap.mobile.features.explore.presentation.components.ExploreSearchBar
import com.rmap.mobile.features.explore.presentation.components.PopularRoadmapsSection
import com.rmap.mobile.features.explore.presentation.components.RecommendedCard
import com.rmap.mobile.features.explore.presentation.components.RecommendedSection
import com.rmap.mobile.features.explore.presentation.viewmodel.CategoryUiModel
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreUiState
import com.rmap.mobile.features.explore.presentation.viewmodel.RecommendedCardUiModel

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Explore,
    onHeaderActionClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllCategoriesClick: () -> Unit = {},
    onSeeAllPopularClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onCategoryClick: (CategoryUiModel) -> Unit = {},
    onRecommendedClick: (RecommendedCardUiModel) -> Unit = {},
    onRoadmapClick: (RoadmapCardUiModel) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundDecorator(
                scrollOffsetY = scrollY,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingNone,
                    end = Dimens.spacingNone,
                    top = Dimens.spacingScreenTop,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottom
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingHuge)
            ) {
                item {
                    Header(
                        greetingText = stringResource(R.string.home_greeting, uiState.userName),
                        headingText = stringResource(R.string.explore_title),
                        greetingIcon = Icons.Outlined.WbSunny,
                        actionIcon = Icons.Outlined.School,
                        onActionClick = onHeaderActionClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontalWide)
                    )
                }

                item {
                    ExploreSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onFilterClick = onFilterClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontalWide)
                    )
                }

                item {
                    CategorySection(
                        categories = uiState.categories,
                        onCategoryClick = onCategoryClick,
                        onViewAllClick = onViewAllCategoriesClick
                    )
                }

                item {
                    RecommendedSection(
                        items = uiState.recommendedItems,
                        onItemClick = onRecommendedClick
                    )
                }

                item {
                    PopularRoadmapsSection(
                        roadmaps = uiState.popularRoadmaps,
                        onRoadmapClick = onRoadmapClick,
                        onSeeAllClick = onSeeAllPopularClick,
                        modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontalWide)
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

@Preview(showBackground = true, name = "Recommended Card")
@Composable
private fun RecommendedCardPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            RecommendedCard(
                item = RecommendedCardUiModel(
                    "1",
                    "Mastering React & Next.js",
                    "MOST POPULAR",
                    48,
                    "Expert",
                    "",
                    PrimaryLight
                ),
                onClick = {}
            )
        }
    }
}
