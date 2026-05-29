package com.rmap.mobile.features.bookmarks.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.bookmarks.domain.model.BookmarkTab
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTopBar
import com.rmap.mobile.features.bookmarks.presentation.components.controls.BookmarkSearchBar
import com.rmap.mobile.features.bookmarks.presentation.components.controls.BookmarkStatusFilter
import com.rmap.mobile.features.bookmarks.presentation.components.controls.BookmarkTypeTabs
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.BookmarkRoadmapCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.roadmap.SavedRoadmapCard
import com.rmap.mobile.features.bookmarks.presentation.components.section.BookmarkSectionHeader
import com.rmap.mobile.features.bookmarks.presentation.components.skill.BookmarkSkillCardUiModel
import com.rmap.mobile.features.bookmarks.presentation.components.skill.BookmarkSkillStatus
import com.rmap.mobile.features.bookmarks.presentation.components.skill.SavedRoadmapSkill
import com.rmap.mobile.features.bookmarks.presentation.components.state.BookmarkErrorState
import com.rmap.mobile.features.bookmarks.presentation.components.state.BookmarkLoadingState
import com.rmap.mobile.features.bookmarks.presentation.components.state.EmptyBookmarkState
import com.rmap.mobile.features.bookmarks.presentation.viewmodel.BookmarksUiState
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.navigation.NavBarDestination

private val BookmarkGridMinCellWidth = 300.dp
private val BookmarkExpandedGridMinCellWidth = 320.dp

@Composable
fun BookmarksScreen(
    uiState: BookmarksUiState,
    windowSizeClass: BookmarkWindowSizeClass,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Bookmarks,
    onHeaderActionClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onRoadmapBookmarkClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onTabSelected: (Int) -> Unit = {},
    onStatusFilterSelected: (Int) -> Unit = {},
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)? = null,
    onSkillBookmarkClick: ((BookmarkSkillCardUiModel) -> Unit)? = null
) {
    val layoutMode = windowSizeClass.toBookmarkLayoutMode()
    val greetingText = stringResource(R.string.home_greeting, uiState.userName)
    val headingText = stringResource(R.string.bookmarks_heading)
    val searchPlaceholder = stringResource(R.string.bookmarks_search_placeholder)
    val tabs = listOf(
        stringResource(R.string.bookmarks_tab_saved_roadmaps),
        stringResource(R.string.bookmarks_tab_saved_skills)
    )
    val statusFilters = listOf(
        stringResource(R.string.bookmarks_status_filter_all),
        stringResource(R.string.bookmarks_status_in_progress),
        stringResource(R.string.bookmarks_status_not_started),
        stringResource(R.string.bookmarks_status_completed)
    )
    val roadmapSectionTitle = stringResource(R.string.bookmarks_section_curated_paths)
    val skillSectionTitle = stringResource(R.string.bookmarks_section_specific_skills)
    val continueLabel = stringResource(R.string.home_hero_continue)
    val startLabel = stringResource(R.string.bookmarks_start)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (layoutMode == BookmarkLayoutMode.Compact) {
                RMapNavigationBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = onDestinationSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        when (layoutMode) {
            BookmarkLayoutMode.Compact -> {
                BookmarkCompactContent(
                    uiState = uiState,
                    greetingText = greetingText,
                    headingText = headingText,
                    searchPlaceholder = searchPlaceholder,
                    tabs = tabs,
                    statusFilters = statusFilters,
                    roadmapSectionTitle = roadmapSectionTitle,
                    skillSectionTitle = skillSectionTitle,
                    continueLabel = continueLabel,
                    startLabel = startLabel,
                    bottomPadding = innerPadding.calculateBottomPadding(),
                    onHeaderActionClick = onHeaderActionClick,
                    onSearchQueryChange = onSearchQueryChange,
                    onTabSelected = onTabSelected,
                    onStatusFilterSelected = onStatusFilterSelected,
                    onRoadmapActionClick = onRoadmapActionClick,
                    onRoadmapShareClick = onRoadmapShareClick,
                    onRoadmapBookmarkClick = onRoadmapBookmarkClick,
                    onSkillClick = onSkillClick,
                    onSkillBookmarkClick = onSkillBookmarkClick
                )
            }

            BookmarkLayoutMode.Medium -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    BookmarkNavigationRail(
                        selectedDestination = selectedDestination,
                        onDestinationSelected = onDestinationSelected
                    )

                    BookmarkGridContent(
                        uiState = uiState,
                        layoutMode = layoutMode,
                        greetingText = greetingText,
                        headingText = headingText,
                        searchPlaceholder = searchPlaceholder,
                        tabs = tabs,
                        statusFilters = statusFilters,
                        roadmapSectionTitle = roadmapSectionTitle,
                        skillSectionTitle = skillSectionTitle,
                        continueLabel = continueLabel,
                        startLabel = startLabel,
                        onHeaderActionClick = onHeaderActionClick,
                        onSearchQueryChange = onSearchQueryChange,
                        onTabSelected = onTabSelected,
                        onStatusFilterSelected = onStatusFilterSelected,
                        onRoadmapActionClick = onRoadmapActionClick,
                        onRoadmapShareClick = onRoadmapShareClick,
                        onRoadmapBookmarkClick = onRoadmapBookmarkClick,
                        onSkillClick = onSkillClick,
                        onSkillBookmarkClick = onSkillBookmarkClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            BookmarkLayoutMode.Expanded -> {
                PermanentNavigationDrawer(
                    drawerContent = {
                        BookmarkDrawerContent(
                            selectedDestination = selectedDestination,
                            onDestinationSelected = onDestinationSelected
                        )
                    }
                ) {
                    BookmarkGridContent(
                        uiState = uiState,
                        layoutMode = layoutMode,
                        greetingText = greetingText,
                        headingText = headingText,
                        searchPlaceholder = searchPlaceholder,
                        tabs = tabs,
                        statusFilters = statusFilters,
                        roadmapSectionTitle = roadmapSectionTitle,
                        skillSectionTitle = skillSectionTitle,
                        continueLabel = continueLabel,
                        startLabel = startLabel,
                        onHeaderActionClick = onHeaderActionClick,
                        onSearchQueryChange = onSearchQueryChange,
                        onTabSelected = onTabSelected,
                        onStatusFilterSelected = onStatusFilterSelected,
                        onRoadmapActionClick = onRoadmapActionClick,
                        onRoadmapShareClick = onRoadmapShareClick,
                        onRoadmapBookmarkClick = onRoadmapBookmarkClick,
                        onSkillClick = onSkillClick,
                        onSkillBookmarkClick = onSkillBookmarkClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkCompactContent(
    uiState: BookmarksUiState,
    greetingText: String,
    headingText: String,
    searchPlaceholder: String,
    tabs: List<String>,
    statusFilters: List<String>,
    roadmapSectionTitle: String,
    skillSectionTitle: String,
    continueLabel: String,
    startLabel: String,
    bottomPadding: Dp,
    onHeaderActionClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onStatusFilterSelected: (Int) -> Unit,
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapBookmarkClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)?,
    onSkillBookmarkClick: ((BookmarkSkillCardUiModel) -> Unit)?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = bookmarkContentPadding(
            layoutMode = BookmarkLayoutMode.Compact,
            bottomPadding = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        bookmarkListItems(
            uiState = uiState,
            layoutMode = BookmarkLayoutMode.Compact,
            greetingText = greetingText,
            headingText = headingText,
            searchPlaceholder = searchPlaceholder,
            tabs = tabs,
            statusFilters = statusFilters,
            roadmapSectionTitle = roadmapSectionTitle,
            skillSectionTitle = skillSectionTitle,
            continueLabel = continueLabel,
            startLabel = startLabel,
            onHeaderActionClick = onHeaderActionClick,
            onSearchQueryChange = onSearchQueryChange,
            onTabSelected = onTabSelected,
            onStatusFilterSelected = onStatusFilterSelected,
            onRoadmapActionClick = onRoadmapActionClick,
            onRoadmapShareClick = onRoadmapShareClick,
            onRoadmapBookmarkClick = onRoadmapBookmarkClick,
            onSkillClick = onSkillClick,
            onSkillBookmarkClick = onSkillBookmarkClick
        )
    }
}

@Composable
private fun BookmarkGridContent(
    uiState: BookmarksUiState,
    layoutMode: BookmarkLayoutMode,
    greetingText: String,
    headingText: String,
    searchPlaceholder: String,
    tabs: List<String>,
    statusFilters: List<String>,
    roadmapSectionTitle: String,
    skillSectionTitle: String,
    continueLabel: String,
    startLabel: String,
    onHeaderActionClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onStatusFilterSelected: (Int) -> Unit,
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapBookmarkClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)?,
    onSkillBookmarkClick: ((BookmarkSkillCardUiModel) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(
            minSize = if (layoutMode == BookmarkLayoutMode.Expanded) {
                BookmarkExpandedGridMinCellWidth
            } else {
                BookmarkGridMinCellWidth
            }
        ),
        modifier = modifier.fillMaxSize(),
        contentPadding = bookmarkContentPadding(
            layoutMode = layoutMode,
            bottomPadding = Dimens.spacingNone
        ),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        bookmarkGridItems(
            uiState = uiState,
            layoutMode = layoutMode,
            greetingText = greetingText,
            headingText = headingText,
            searchPlaceholder = searchPlaceholder,
            tabs = tabs,
            statusFilters = statusFilters,
            roadmapSectionTitle = roadmapSectionTitle,
            skillSectionTitle = skillSectionTitle,
            continueLabel = continueLabel,
            startLabel = startLabel,
            onHeaderActionClick = onHeaderActionClick,
            onSearchQueryChange = onSearchQueryChange,
            onTabSelected = onTabSelected,
            onStatusFilterSelected = onStatusFilterSelected,
            onRoadmapActionClick = onRoadmapActionClick,
            onRoadmapShareClick = onRoadmapShareClick,
            onRoadmapBookmarkClick = onRoadmapBookmarkClick,
            onSkillClick = onSkillClick,
            onSkillBookmarkClick = onSkillBookmarkClick
        )
    }
}

private fun LazyListScope.bookmarkListItems(
    uiState: BookmarksUiState,
    layoutMode: BookmarkLayoutMode,
    greetingText: String,
    headingText: String,
    searchPlaceholder: String,
    tabs: List<String>,
    statusFilters: List<String>,
    roadmapSectionTitle: String,
    skillSectionTitle: String,
    continueLabel: String,
    startLabel: String,
    onHeaderActionClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onStatusFilterSelected: (Int) -> Unit,
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapBookmarkClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)?,
    onSkillBookmarkClick: ((BookmarkSkillCardUiModel) -> Unit)?
) {
    item(key = "bookmark-controls") {
        BookmarkHeaderControls(
            uiState = uiState,
            layoutMode = layoutMode,
            greetingText = greetingText,
            headingText = headingText,
            searchPlaceholder = searchPlaceholder,
            tabs = tabs,
            onHeaderActionClick = onHeaderActionClick,
            onSearchQueryChange = onSearchQueryChange,
            onTabSelected = onTabSelected,
        )
    }

    item(key = "bookmark-section-header") {
        BookmarkSectionHeader(
            title = if (uiState.selectedTab == BookmarkTab.Roadmaps) roadmapSectionTitle else skillSectionTitle,
            trailingContent = {
                BookmarkStatusFilter(
                    filters = statusFilters,
                    selectedIndex = uiState.selectedStatusFilter.ordinal,
                    onFilterSelected = onStatusFilterSelected
                )
            }
        )
    }

    if (uiState.isLoading) {
        item(key = "bookmark-loading") {
            BookmarkLoadingState()
        }
    } else if (uiState.errorMessage != null) {
        item(key = "bookmark-error") {
            BookmarkErrorState(message = uiState.errorMessage)
        }
    } else if (uiState.selectedTab == BookmarkTab.Roadmaps) {
        if (uiState.roadmapItems.isEmpty()) {
            item(key = "bookmark-roadmap-empty") {
                EmptyBookmarkState()
            }
        } else {
            items(
                items = uiState.roadmapItems,
                key = { item -> "bookmark-roadmap-${item.id}" }
            ) { item ->
                SavedRoadmapCard(
                    item = item,
                    onActionClick = onRoadmapActionClick?.let { callback ->
                        { callback(item) }
                    },
                    onShareClick = onRoadmapShareClick?.let { callback ->
                        { callback(item) }
                    },
                    onBookmarkClick = onRoadmapBookmarkClick?.let { callback ->
                        { callback(item) }
                    }
                )
            }
        }
    } else {
        if (uiState.skillItems.isEmpty()) {
            item(key = "bookmark-skill-empty") {
                EmptyBookmarkState()
            }
        } else {
            items(
                items = uiState.skillItems,
                key = { item -> "bookmark-skill-${item.title}-${item.parentPathName}" }
            ) { item ->
                SavedRoadmapSkill(
                    item = item,
                    actionLabel = item.bookmarkActionLabel(
                        continueLabel = continueLabel,
                        startLabel = startLabel
                    ),
                    onClick = onSkillClick?.let { callback ->
                        { callback(item) }
                    },
                    onActionClick = onSkillClick?.let { callback ->
                        { callback(item) }
                    },
                    onBookmarkClick = onSkillBookmarkClick?.let { callback ->
                        { callback(item) }
                    }
                )
            }
        }
    }

}

private fun LazyGridScope.bookmarkGridItems(
    uiState: BookmarksUiState,
    layoutMode: BookmarkLayoutMode,
    greetingText: String,
    headingText: String,
    searchPlaceholder: String,
    tabs: List<String>,
    statusFilters: List<String>,
    roadmapSectionTitle: String,
    skillSectionTitle: String,
    continueLabel: String,
    startLabel: String,
    onHeaderActionClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onStatusFilterSelected: (Int) -> Unit,
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onRoadmapBookmarkClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)?,
    onSkillBookmarkClick: ((BookmarkSkillCardUiModel) -> Unit)?
) {
    item(
        key = "bookmark-controls",
        span = { GridItemSpan(maxLineSpan) }
    ) {
        BookmarkHeaderControls(
            uiState = uiState,
            layoutMode = layoutMode,
            greetingText = greetingText,
            headingText = headingText,
            searchPlaceholder = searchPlaceholder,
            tabs = tabs,
            onHeaderActionClick = onHeaderActionClick,
            onSearchQueryChange = onSearchQueryChange,
            onTabSelected = onTabSelected,
        )
    }

    item(
        key = "bookmark-section-header",
        span = { GridItemSpan(maxLineSpan) }
    ) {
        BookmarkSectionHeader(
            title = if (uiState.selectedTab == BookmarkTab.Roadmaps) roadmapSectionTitle else skillSectionTitle,
            trailingContent = {
                BookmarkStatusFilter(
                    filters = statusFilters,
                    selectedIndex = uiState.selectedStatusFilter.ordinal,
                    onFilterSelected = onStatusFilterSelected
                )
            }
        )
    }

    if (uiState.isLoading) {
        item(
            key = "bookmark-loading",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            BookmarkLoadingState()
        }
    } else if (uiState.errorMessage != null) {
        item(
            key = "bookmark-error",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            BookmarkErrorState(message = uiState.errorMessage)
        }
    } else if (uiState.selectedTab == BookmarkTab.Roadmaps) {
        if (uiState.roadmapItems.isEmpty()) {
            item(
                key = "bookmark-roadmap-empty",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                EmptyBookmarkState()
            }
        } else {
            gridItems(
                items = uiState.roadmapItems,
                key = { item -> "bookmark-roadmap-${item.id}" }
            ) { item ->
                SavedRoadmapCard(
                    item = item,
                    onActionClick = onRoadmapActionClick?.let { callback ->
                        { callback(item) }
                    },
                    onShareClick = onRoadmapShareClick?.let { callback ->
                        { callback(item) }
                    },
                    onBookmarkClick = onRoadmapBookmarkClick?.let { callback ->
                        { callback(item) }
                    }
                )
            }
        }
    } else {
        if (uiState.skillItems.isEmpty()) {
            item(
                key = "bookmark-skill-empty",
                span = { GridItemSpan(maxLineSpan) }
            ) {
                EmptyBookmarkState()
            }
        } else {
            gridItems(
                items = uiState.skillItems,
                key = { item -> "bookmark-skill-${item.title}-${item.parentPathName}" }
            ) { item ->
                SavedRoadmapSkill(
                    item = item,
                    actionLabel = item.bookmarkActionLabel(
                        continueLabel = continueLabel,
                        startLabel = startLabel
                    ),
                    onClick = onSkillClick?.let { callback ->
                        { callback(item) }
                    },
                    onActionClick = onSkillClick?.let { callback ->
                        { callback(item) }
                    },
                    onBookmarkClick = onSkillBookmarkClick?.let { callback ->
                        { callback(item) }
                    }
                )
            }
        }
    }

}

@Composable
private fun BookmarkHeaderControls(
    uiState: BookmarksUiState,
    layoutMode: BookmarkLayoutMode,
    greetingText: String,
    headingText: String,
    searchPlaceholder: String,
    tabs: List<String>,
    onHeaderActionClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (layoutMode == BookmarkLayoutMode.Expanded) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
        ) {
            BookmarkTopBar(
                greetingText = greetingText,
                headingText = headingText,
                onActionClick = onHeaderActionClick,
                modifier = Modifier.weight(0.4f)
            )
            BookmarkControls(
                uiState = uiState,
                searchPlaceholder = searchPlaceholder,
                tabs = tabs,
                onSearchQueryChange = onSearchQueryChange,
                onTabSelected = onTabSelected,
                modifier = Modifier.weight(0.6f)
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            BookmarkTopBar(
                greetingText = greetingText,
                headingText = headingText,
                onActionClick = onHeaderActionClick
            )
            BookmarkControls(
                uiState = uiState,
                searchPlaceholder = searchPlaceholder,
                tabs = tabs,
                onSearchQueryChange = onSearchQueryChange,
                onTabSelected = onTabSelected,
            )
        }
    }
}

@Composable
private fun BookmarkControls(
    uiState: BookmarksUiState,
    searchPlaceholder: String,
    tabs: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        BookmarkSearchBar(
            query = uiState.searchQuery,
            placeholder = searchPlaceholder,
            onQueryChange = onSearchQueryChange
        )
        BookmarkTypeTabs(
            tabs = tabs,
            selectedIndex = uiState.selectedTab.ordinal,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
private fun BookmarkNavigationRail(
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacingXxl))
        NavBarDestination.entries.forEach { destination ->
            NavigationRailItem(
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(destination.labelRes))
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Composable
private fun BookmarkDrawerContent(
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit
) {
    PermanentDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacingXxl))
        NavBarDestination.entries.forEach { destination ->
            NavigationDrawerItem(
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(destination.labelRes))
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

private fun bookmarkContentPadding(
    layoutMode: BookmarkLayoutMode,
    bottomPadding: Dp
): PaddingValues {
    val horizontalPadding = when (layoutMode) {
        BookmarkLayoutMode.Compact -> Dimens.spacingScreenHorizontal
        BookmarkLayoutMode.Medium,
        BookmarkLayoutMode.Expanded -> Dimens.spacingScreenHorizontalWide
    }
    val topPadding = when (layoutMode) {
        BookmarkLayoutMode.Compact -> Dimens.spacingScreenTop
        BookmarkLayoutMode.Medium,
        BookmarkLayoutMode.Expanded -> Dimens.spacingScreenTopCompact
    }
    val resolvedBottomPadding = bottomPadding + when (layoutMode) {
        BookmarkLayoutMode.Compact -> Dimens.spacingScreenBottom
        BookmarkLayoutMode.Medium,
        BookmarkLayoutMode.Expanded -> Dimens.spacingHuge
    }

    return PaddingValues(
        start = horizontalPadding,
        top = topPadding,
        end = horizontalPadding,
        bottom = resolvedBottomPadding
    )
}

private fun BookmarkSkillCardUiModel.bookmarkActionLabel(
    continueLabel: String,
    startLabel: String
): String {
    return if (status == BookmarkSkillStatus.IN_PROGRESS) continueLabel else startLabel
}

private enum class BookmarkLayoutMode {
    Compact,
    Medium,
    Expanded
}

enum class BookmarkWindowSizeClass {
    Compact,
    Medium,
    Expanded;

    companion object {
        fun fromWidth(width: Dp): BookmarkWindowSizeClass {
            return when {
                width < 600.dp -> Compact
                width < 840.dp -> Medium
                else -> Expanded
            }
        }
    }
}

private fun BookmarkWindowSizeClass.toBookmarkLayoutMode(): BookmarkLayoutMode {
    return when (this) {
        BookmarkWindowSizeClass.Compact -> BookmarkLayoutMode.Compact
        BookmarkWindowSizeClass.Medium -> BookmarkLayoutMode.Medium
        BookmarkWindowSizeClass.Expanded -> BookmarkLayoutMode.Expanded
    }
}

private fun previewWindowSizeClass(width: Dp): BookmarkWindowSizeClass {
    return BookmarkWindowSizeClass.fromWidth(width)
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun BookmarksScreenRoadmapsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedDestination by remember { mutableStateOf(NavBarDestination.Bookmarks) }
        BookmarksScreen(
            uiState = BookmarksUiState(
                userName = "Thinh",
                isLoading = false,
                roadmapItems = sampleRoadmaps()
            ),
            windowSizeClass = previewWindowSizeClass(width = 390.dp),
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {}
        )
    }
}

@Preview(
    name = "Roadmaps Empty",
    showBackground = true,
    backgroundColor = 0xFFF4F8FF,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun BookmarksScreenEmptyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarksScreen(
            uiState = BookmarksUiState(userName = "Thinh", isLoading = false),
            windowSizeClass = previewWindowSizeClass(width = 390.dp)
        )
    }
}

@Preview(
    name = "Skills Medium",
    showBackground = true,
    backgroundColor = 0xFFF4F8FF,
    widthDp = 700,
    heightDp = 900
)
@Composable
private fun BookmarksScreenSkillsMediumPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarksScreen(
            uiState = BookmarksUiState(
                userName = "Thinh",
                selectedTab = BookmarkTab.Skills,
                isLoading = false,
                skillItems = sampleSkills()
            ),
            windowSizeClass = previewWindowSizeClass(width = 700.dp)
        )
    }
}

@Preview(
    name = "Roadmaps Expanded",
    showBackground = true,
    backgroundColor = 0xFFF4F8FF,
    widthDp = 1000,
    heightDp = 720
)
@Composable
private fun BookmarksScreenExpandedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarksScreen(
            uiState = BookmarksUiState(
                userName = "Thinh",
                isLoading = false,
                roadmapItems = sampleRoadmaps()
            ),
            windowSizeClass = previewWindowSizeClass(width = 1000.dp)
        )
    }
}

private fun sampleRoadmaps(): List<BookmarkRoadmapCardUiModel> {
    return listOf(
        BookmarkRoadmapCardUiModel(
            id = "full-stack-development",
            title = "Full Stack Development",
            categoryLabel = "Web Development",
            categoryIcon = Icons.Outlined.Code,
            nodesLabel = "64 Nodes",
            durationLabel = "8 months",
            savedAtLabel = "Last saved yesterday",
            actionLabel = "Continue",
            status = LearningStatus.InProgress,
            statusLabel = "In Progress",
            progressPercent = 45
        ),
        BookmarkRoadmapCardUiModel(
            id = "ui-ux-masterclass",
            title = "UI/UX Masterclass",
            categoryLabel = "Design",
            categoryIcon = Icons.Outlined.Code,
            nodesLabel = "32 Nodes",
            durationLabel = "4 months",
            savedAtLabel = "Saved 3 days ago",
            actionLabel = "Start",
            status = LearningStatus.NotStarted
        ),
        BookmarkRoadmapCardUiModel(
            id = "cloud-infrastructure",
            title = "Cloud Infrastructure",
            categoryLabel = "DevOps",
            categoryIcon = Icons.Outlined.Code,
            nodesLabel = "48 Nodes",
            durationLabel = "6 months",
            savedAtLabel = "Saved 1 week ago",
            actionLabel = "Start",
            status = LearningStatus.NotStarted
        )
    )
}

private fun sampleSkills(): List<BookmarkSkillCardUiModel> {
    return listOf(
        BookmarkSkillCardUiModel(
            title = "Advanced CSS Layouts",
            parentPathName = "Frontend Pro",
            status = BookmarkSkillStatus.IN_PROGRESS,
            statusLabel = "In Progress",
            icon = Icons.Outlined.Code
        ),
        BookmarkSkillCardUiModel(
            title = "NoSQL Data Modeling",
            parentPathName = "Backend Systems",
            status = BookmarkSkillStatus.NOT_STARTED,
            statusLabel = "Not Started",
            icon = Icons.Outlined.DataObject
        )
    )
}
