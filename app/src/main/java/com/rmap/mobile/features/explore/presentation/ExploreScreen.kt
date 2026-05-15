package com.rmap.mobile.features.explore.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.navigation.NavBarDestination
import com.rmap.mobile.core.ui.components.AppNavigationBar
import com.rmap.mobile.core.ui.components.BackgroundDecorator
import com.rmap.mobile.core.ui.components.Header
import com.rmap.mobile.core.ui.components.RoadmapCard
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.RoadmapDifficulty
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.DifficultyExpertContentColor
import com.rmap.mobile.core.ui.theme.ExploreBlueContainerColor
import com.rmap.mobile.core.ui.theme.ExploreGreenContainerColor
import com.rmap.mobile.core.ui.theme.ExplorePurpleContainerColor
import com.rmap.mobile.core.ui.theme.ExploreRoseContainerColor
import com.rmap.mobile.core.ui.theme.NeutralSoftSurfaceColor
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.explore.presentation.components.CategorySection
import com.rmap.mobile.features.explore.presentation.components.ExploreSearchBar
import com.rmap.mobile.features.explore.presentation.components.PopularRoadmapsSection
import com.rmap.mobile.features.explore.presentation.components.RecommendedCard
import com.rmap.mobile.features.explore.presentation.components.RecommendedSection

data class CategoryUiModel(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val backgroundColor: Color
)

data class RecommendedCardUiModel(
    val id: String,
    val title: String,
    val badgeText: String,
    val skillNodesCount: Int,
    val level: String,
    val imageUrl: String,
    val accentColor: Color
)

@Composable
fun ExploreScreen(
    userName: String,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
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

    val categories = listOf(
        CategoryUiModel(
            "frontend",
            stringResource(R.string.explore_category_frontend),
            Icons.Outlined.Code,
            ExploreBlueContainerColor
        ),
        CategoryUiModel(
            "backend",
            stringResource(R.string.explore_category_backend),
            Icons.Outlined.Storage,
            NeutralSoftSurfaceColor
        ),
        CategoryUiModel(
            "mobile",
            stringResource(R.string.explore_category_mobile),
            Icons.Outlined.Devices,
            ExploreRoseContainerColor
        ),
        CategoryUiModel(
            "devops",
            stringResource(R.string.explore_category_devops),
            Icons.Outlined.Terminal,
            ExploreGreenContainerColor
        ),
        CategoryUiModel(
            "ai",
            stringResource(R.string.explore_category_ai),
            Icons.Outlined.SmartToy,
            ExplorePurpleContainerColor
        )
    )

    val recommendedItems = listOf(
        RecommendedCardUiModel(
            "1",
            "Mastering React & Next.js",
            stringResource(R.string.explore_badge_most_popular),
            48,
            "Expert",
            "", // Placeholder
            PrimaryLight
        ),
        RecommendedCardUiModel(
            "2",
            "AI Engineering Path",
            stringResource(R.string.explore_badge_career_path),
            64,
            "Advanced",
            "", // Placeholder
            DifficultyExpertContentColor
        )
    )

    val popularRoadmaps = listOf(
        RoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_frontend_pro),
            lessonsCount = 120,
            difficultyLabel = stringResource(R.string.roadmap_level_expert),
            difficulty = RoadmapDifficulty.Expert,
            durationLabel = stringResource(R.string.home_roadmap_duration_3_months),
            icon = Icons.Outlined.Code
        ),
        RoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_devops_specialist),
            lessonsCount = 185,
            difficultyLabel = stringResource(R.string.roadmap_level_beginner),
            difficulty = RoadmapDifficulty.Beginner,
            durationLabel = stringResource(R.string.home_roadmap_duration_6_months),
            icon = Icons.Outlined.DataObject
        ),
        RoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_ui_ux_master),
            lessonsCount = 96,
            difficultyLabel = stringResource(R.string.roadmap_level_intermediate),
            difficulty = RoadmapDifficulty.Intermediate,
            durationLabel = stringResource(R.string.home_roadmap_duration_2_months),
            icon = Icons.Outlined.Palette
        ),
        RoadmapCardUiModel(
            title = stringResource(R.string.home_roadmap_title_data_science),
            lessonsCount = 240,
            difficultyLabel = stringResource(R.string.roadmap_level_hard),
            difficulty = RoadmapDifficulty.Hard,
            durationLabel = stringResource(R.string.home_roadmap_duration_4_months),
            icon = Icons.Outlined.Science
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
                    start = 0.dp,
                    end = 0.dp,
                    top = 72.dp,
                    bottom = innerPadding.calculateBottomPadding() + 72.dp
                ),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                item {
                    Header(
                        greetingText = stringResource(R.string.home_greeting, userName),
                        headingText = stringResource(R.string.explore_title),
                        greetingIcon = Icons.Outlined.WbSunny,
                        actionIcon = Icons.Outlined.School,
                        onActionClick = onHeaderActionClick,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                item {
                    ExploreSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onFilterClick = onFilterClick,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                item {
                    CategorySection(
                        categories = categories,
                        onCategoryClick = onCategoryClick,
                        onViewAllClick = onViewAllCategoriesClick
                    )
                }

                item {
                    RecommendedSection(
                        items = recommendedItems,
                        onItemClick = onRecommendedClick
                    )
                }

                item {
                    PopularRoadmapsSection(
                        roadmaps = popularRoadmaps,
                        onRoadmapClick = onRoadmapClick,
                        onSeeAllClick = onSeeAllPopularClick,
                        modifier = Modifier.padding(horizontal = 24.dp)
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
        ExploreScreen(userName = "Minh")
    }
}

@Preview(showBackground = true, name = "Explore Screen - Dark", widthDp = 390, heightDp = 900, backgroundColor = 0xFF000000)
@Composable
private fun ExploreScreenDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        ExploreScreen(userName = "Minh")
    }
}

@Preview(showBackground = true, name = "Search Bar Component")
@Composable
private fun SearchBarPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(16.dp)) {
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
        Box(modifier = Modifier.padding(16.dp)) {
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
