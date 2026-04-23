package com.rmap.mobile.presentation.explore

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.navigation.RMapNavigationBar
import com.rmap.mobile.presentation.ui.components.BackgroundDecorator
import com.rmap.mobile.presentation.ui.components.Header
import com.rmap.mobile.presentation.ui.components.RoadmapCard
import com.rmap.mobile.presentation.ui.components.RoadmapCardUiModel
import com.rmap.mobile.presentation.ui.components.RoadmapDifficulty
import com.rmap.mobile.presentation.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.presentation.ui.theme.RMapTheme

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
    selectedDestination: NavBarDestination = NavBarDestination.Explore,
    onHeaderActionClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllCategoriesClick: () -> Unit = {},
    onSeeAllPopularClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onCategoryClick: (CategoryUiModel) -> Unit = {},
    onRecommendedClick: (RecommendedCardUiModel) -> Unit = {},
    onRoadmapClick: (RoadmapCardUiModel) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        CategoryUiModel(
            "frontend",
            stringResource(R.string.explore_category_frontend),
            Icons.Outlined.Code,
            Color(0xFFEBF5FF)
        ),
        CategoryUiModel(
            "backend",
            stringResource(R.string.explore_category_backend),
            Icons.Outlined.Storage,
            Color(0xFFF3F4F6)
        ),
        CategoryUiModel(
            "mobile",
            stringResource(R.string.explore_category_mobile),
            Icons.Outlined.Devices,
            Color(0xFFFFF1F2)
        ),
        CategoryUiModel(
            "devops",
            stringResource(R.string.explore_category_devops),
            Icons.Outlined.Terminal,
            Color(0xFFF0FDF4)
        ),
        CategoryUiModel(
            "ai",
            stringResource(R.string.explore_category_ai),
            Icons.Outlined.SmartToy,
            Color(0xFFF5F3FF)
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
            Color(0xFF298CF7)
        ),
        RecommendedCardUiModel(
            "2",
            "AI Engineering Path",
            stringResource(R.string.explore_badge_career_path),
            64,
            "Advanced",
            "", // Placeholder
            Color(0xFF9810FA)
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
                        onQueryChange = { searchQuery = it },
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

@Composable
private fun ExploreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x0A000000)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(R.string.explore_search_placeholder),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onFilterClick)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    categories: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.explore_categories_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = stringResource(R.string.explore_view_all),
                modifier = Modifier.clickable(onClick = onViewAllClick),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category = category, onClick = { onCategoryClick(category) })
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(category.backgroundColor, CircleShape)
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun RecommendedSection(
    items: List<RecommendedCardUiModel>,
    onItemClick: (RecommendedCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.explore_recommended_title),
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                RecommendedCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun RecommendedCard(
    item: RecommendedCardUiModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(170.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = item.accentColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        item.accentColor.copy(alpha = 0.9f),
                        item.accentColor
                    )
                )
            )
    ) {
        // Decorative glow
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = item.badgeText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 26.sp
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.explore_card_stats, item.skillNodesCount, item.level),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 0.2.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PopularRoadmapsSection(
    roadmaps: List<RoadmapCardUiModel>,
    onRoadmapClick: (RoadmapCardUiModel) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.explore_popular_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = stringResource(R.string.roadmap_see_all),
                modifier = Modifier.clickable(onClick = onSeeAllClick),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        roadmaps.forEach { roadmap ->
            RoadmapCard(
                item = roadmap,
                onClick = { onRoadmapClick(roadmap) }
            )
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
                    Color(0xFF298CF7)
                ),
                onClick = {}
            )
        }
    }
}
