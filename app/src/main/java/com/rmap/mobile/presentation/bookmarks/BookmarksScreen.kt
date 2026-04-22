package com.rmap.mobile.presentation.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.navigation.RMapNavigationBar
import com.rmap.mobile.presentation.ui.components.BackgroundDecorator
import com.rmap.mobile.presentation.ui.components.BookmarkRoadmapCard
import com.rmap.mobile.presentation.ui.components.BookmarkRoadmapCardUiModel
import com.rmap.mobile.presentation.ui.components.BookmarkSkillCard
import com.rmap.mobile.presentation.ui.components.BookmarkSkillCardUiModel
import com.rmap.mobile.presentation.ui.components.BookmarkTabSwitcher
import com.rmap.mobile.presentation.ui.components.Header
import com.rmap.mobile.presentation.ui.components.RoadmapDifficulty
import com.rmap.mobile.presentation.ui.components.SkillStatus
import com.rmap.mobile.presentation.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.presentation.ui.theme.RMapTheme

private const val TAB_INDEX_ROADMAPS = 0
private const val TAB_INDEX_SKILLS = 1

@Composable
fun BookmarksScreen(
    userName: String,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Bookmarks,
    onHeaderActionClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onRoadmapActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onRoadmapShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)? = null,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    var selectedTabIndex by remember { mutableIntStateOf(TAB_INDEX_ROADMAPS) }

    val greetingText = stringResource(R.string.home_greeting, userName)
    val headingText = stringResource(R.string.bookmarks_heading)
    val tabs = listOf(
        stringResource(R.string.bookmarks_tab_saved_roadmaps),
        stringResource(R.string.bookmarks_tab_saved_skills)
    )

    val roadmapItems = listOf(
        BookmarkRoadmapCardUiModel(
            title = "Full Stack Development",
            difficultyLabel = stringResource(R.string.roadmap_level_intermediate),
            difficulty = RoadmapDifficulty.Intermediate,
            durationLabel = stringResource(R.string.home_roadmap_duration_8_months),
            actionLabel = stringResource(R.string.bookmarks_continue_path),
            coverPlaceholderRes = R.drawable.bg_placeholder_fullstack
        ),
        BookmarkRoadmapCardUiModel(
            title = "UI/UX Masterclass",
            difficultyLabel = stringResource(R.string.roadmap_level_beginner),
            difficulty = RoadmapDifficulty.Beginner,
            durationLabel = stringResource(R.string.home_roadmap_duration_4_months),
            actionLabel = stringResource(R.string.bookmarks_join_now),
            coverPlaceholderRes = R.drawable.bg_placeholder_uiux
        )
    )

    val skillItems = listOf(
        BookmarkSkillCardUiModel(
            title = "Advanced CSS Layouts",
            parentPathName = "Frontend Dev",
            status = SkillStatus.IN_PROGRESS,
            statusLabel = stringResource(R.string.bookmarks_status_in_progress),
            icon = Icons.Outlined.Code
        ),
        BookmarkSkillCardUiModel(
            title = "NoSQL Data Modeling",
            parentPathName = "Backend Systems",
            status = SkillStatus.NOT_STARTED,
            statusLabel = stringResource(R.string.bookmarks_status_not_started),
            icon = Icons.Outlined.DataObject
        )
    )

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
            BackgroundDecorator(
                scrollOffsetY = scrollY,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 72.dp,
                    bottom = innerPadding.calculateBottomPadding() + 72.dp
                ),
                verticalArrangement = Arrangement.spacedBy(28.dp)
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
                        onTabSelected = { selectedTabIndex = it }
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

@Composable
private fun SectionHeader(
    title: String,
    badgeText: String,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Text(
            text = badgeText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.3.sp,
                color = badgeColor
            ),
            modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun CuratedPathsSection(
    roadmapItems: List<BookmarkRoadmapCardUiModel>,
    savedCount: Int,
    onActionClick: ((BookmarkRoadmapCardUiModel) -> Unit)?,
    onShareClick: ((BookmarkRoadmapCardUiModel) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = stringResource(R.string.bookmarks_section_curated_paths),
            badgeText = stringResource(R.string.bookmarks_saved_count, savedCount)
        )

        roadmapItems.forEach { item ->
            BookmarkRoadmapCard(
                item = item,
                onActionClick = onActionClick?.let { callback ->
                    { callback(item) }
                },
                onShareClick = onShareClick?.let { callback ->
                    { callback(item) }
                },
                onBookmarkClick = {}
            )
        }
    }
}

@Composable
private fun SpecificSkillsSection(
    skillItems: List<BookmarkSkillCardUiModel>,
    pinsCount: Int,
    onSkillClick: ((BookmarkSkillCardUiModel) -> Unit)?
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = stringResource(R.string.bookmarks_section_specific_skills),
            badgeText = stringResource(R.string.bookmarks_pins_count, pinsCount)
        )

        skillItems.forEach { item ->
            BookmarkSkillCard(
                item = item,
                onClick = onSkillClick?.let { callback ->
                    { callback(item) }
                }
            )
        }
    }
}

@Composable
private fun FooterHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.7f)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = stringResource(R.string.bookmarks_footer_hint),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        )
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
