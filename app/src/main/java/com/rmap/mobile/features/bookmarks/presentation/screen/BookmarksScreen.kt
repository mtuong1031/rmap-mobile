package com.rmap.mobile.features.bookmarks.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.navigation.NavBarDestination
import com.rmap.mobile.core.ui.components.AppNavigationBar
import com.rmap.mobile.core.ui.components.BackgroundDecorator
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTabSwitcher
import com.rmap.mobile.features.bookmarks.presentation.components.CuratedPathsSection
import com.rmap.mobile.features.bookmarks.presentation.components.FooterHint
import com.rmap.mobile.core.ui.components.Header
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.SpecificSkillsSection
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.bookmarks.presentation.viewmodel.BookmarksUiState

@Composable
fun BookmarksScreen(
    uiState: BookmarksUiState,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Bookmarks,
    onHeaderActionClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onTabSelected: (Int) -> Unit = {},
    onSkillClick: ((SkillCardUiModel) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    val greetingText = stringResource(R.string.home_greeting, uiState.userName)
    val headingText = stringResource(R.string.bookmarks_heading)
    val tabs = listOf(
        stringResource(R.string.bookmarks_tab_saved_roadmaps),
        stringResource(R.string.bookmarks_tab_saved_skills)
    )

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
                    start = Dimens.spacingScreenHorizontal,
                    end = Dimens.spacingScreenHorizontal,
                    top = Dimens.spacingScreenTop,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottom
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxxl)
            ) {
                item {
                    Header(
                        greetingText = greetingText,
                        headingText = headingText,
                        greetingIcon = Icons.Outlined.WbSunny,
                        actionIcon = Icons.Outlined.School,
                        onActionClick = onHeaderActionClick
                    )
                }

                item {
                    BookmarkTabSwitcher(
                        tabs = tabs,
                        selectedIndex = uiState.selectedTab.ordinal,
                        onTabSelected = onTabSelected
                    )
                }

                if (uiState.selectedTab == BookmarkTab.Roadmaps) {
                    item {
                        CuratedPathsSection(
                            roadmapItems = uiState.roadmapItems,
                            savedCount = uiState.roadmapItems.size,
                            onActionClick = onRoadmapActionClick,
                            onShareClick = onRoadmapShareClick
                        )
                    }
                }

                if (uiState.selectedTab == BookmarkTab.Skills) {
                    item {
                        SpecificSkillsSection(
                            skillItems = uiState.skillItems,
                            pinsCount = uiState.skillItems.size,
                            onSkillClick = onSkillClick
                        )
                    }
                }

                item {
                    FooterHint()
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun BookmarksScreenRoadmapsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedDestination by remember { mutableStateOf(NavBarDestination.Bookmarks) }
        BookmarksScreen(
            uiState = BookmarksUiState(userName = "Thinh"),
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {}
        )
    }
}

@Preview(
    name = "Skills Tab",
    showBackground = true,
    backgroundColor = 0xFFF4F8FF,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun BookmarksScreenSkillsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedDestination by remember { mutableStateOf(NavBarDestination.Bookmarks) }
        BookmarksScreen(
            uiState = BookmarksUiState(userName = "Thinh", selectedTab = BookmarkTab.Skills),
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {}
        )
    }
}
