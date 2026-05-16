package com.rmap.mobile.features.bookmarks.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
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
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTabSwitcher
import com.rmap.mobile.features.bookmarks.presentation.components.CuratedPathsSection
import com.rmap.mobile.features.bookmarks.presentation.components.FooterHint
import com.rmap.mobile.core.ui.components.Header
import com.rmap.mobile.core.ui.components.RoadmapDifficulty
import com.rmap.mobile.core.ui.components.SkillCardUiModel
import com.rmap.mobile.core.ui.components.SkillStatus
import com.rmap.mobile.features.bookmarks.presentation.components.SpecificSkillsSection
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarksScreen(
    userName: String,
    modifier: Modifier = Modifier,
    selectedTabIndex: Int = TAB_INDEX_ROADMAPS,
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

    val greetingText = stringResource(R.string.home_greeting, userName)
    val headingText = stringResource(R.string.bookmarks_heading)
    val tabs = listOf(
        stringResource(R.string.bookmarks_tab_saved_roadmaps),
        stringResource(R.string.bookmarks_tab_saved_skills)
    )

    val roadmapItems = listOf(
        BookmarkRoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_frontend_pro),
            difficultyLabel = stringResource(R.string.roadmap_level_intermediate),
            difficulty = RoadmapDifficulty.Intermediate,
            durationLabel = stringResource(R.string.home_roadmap_duration_6_months),
            actionLabel = stringResource(R.string.bookmarks_continue_path),
            coverPlaceholderRes = R.drawable.bg_placeholder_fullstack
        ),
        BookmarkRoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_full_stack_development),
            difficultyLabel = stringResource(R.string.roadmap_level_intermediate),
            difficulty = RoadmapDifficulty.Intermediate,
            durationLabel = stringResource(R.string.home_roadmap_duration_8_months),
            actionLabel = stringResource(R.string.bookmarks_continue_path),
            coverPlaceholderRes = R.drawable.bg_placeholder_fullstack
        ),
        BookmarkRoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_ui_ux_masterclass),
            difficultyLabel = stringResource(R.string.roadmap_level_beginner),
            difficulty = RoadmapDifficulty.Beginner,
            durationLabel = stringResource(R.string.home_roadmap_duration_4_months),
            actionLabel = stringResource(R.string.bookmarks_join_now),
            coverPlaceholderRes = R.drawable.bg_placeholder_uiux
        )
    )

    val skillItems = listOf(
        SkillCardUiModel(
            title = stringResource(R.string.skill_advanced_css_layouts),
            parentPathName = stringResource(R.string.skill_parent_frontend_dev),
            status = SkillStatus.IN_PROGRESS,
            statusLabel = stringResource(R.string.bookmarks_status_in_progress),
            icon = Icons.Outlined.Code
        ),
        SkillCardUiModel(
            title = stringResource(R.string.skill_nosql_data_modeling),
            parentPathName = stringResource(R.string.skill_parent_backend_systems),
            status = SkillStatus.NOT_STARTED,
            statusLabel = stringResource(R.string.bookmarks_status_not_started),
            icon = Icons.Outlined.DataObject
        )
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
                        selectedIndex = selectedTabIndex,
                        onTabSelected = onTabSelected
                    )
                }

                if (selectedTabIndex == TAB_INDEX_ROADMAPS) {
                    item {
                        CuratedPathsSection(
                            roadmapItems = roadmapItems,
                            savedCount = roadmapItems.size,
                            onActionClick = onRoadmapActionClick,
                            onShareClick = onRoadmapShareClick
                        )
                    }
                }

                if (selectedTabIndex == TAB_INDEX_SKILLS) {
                    item {
                        SpecificSkillsSection(
                            skillItems = skillItems,
                            pinsCount = skillItems.size,
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
            userName = "Thinh",
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
            userName = "Thinh",
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {}
        )
    }
}
