package com.rmap.mobile.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.rmap.mobile.core.ui.components.HighlightStatCardDefaults
import com.rmap.mobile.core.ui.components.HighlightStatCardRow
import com.rmap.mobile.core.ui.components.HighlightStatItemUiModel
import com.rmap.mobile.core.ui.components.RoadmapCard
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.RoadmapDifficulty
import com.rmap.mobile.core.ui.components.RoadmapStatCardRow
import com.rmap.mobile.core.ui.components.RoadmapStatItemUiModel
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.home.presentation.components.LearningProgressCard
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapsHeader

@Composable
fun HomeScreen(
    userName: String,
    modifier: Modifier = Modifier,
    progressFraction: Float = 0f,
    selectedDestination: NavBarDestination = NavBarDestination.Home,
    onHeaderActionClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onHighlightItemClick: ((index: Int, item: HighlightStatItemUiModel) -> Unit)? = null,
    onRoadmapStatItemClick: ((index: Int, item: RoadmapStatItemUiModel) -> Unit)? = null,
    onRoadmapClick: ((RoadmapCardUiModel) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val scrollY = rememberBackgroundScrollOffsetY(listState)

    val greetingText = stringResource(R.string.home_greeting, userName)
    val headingText = stringResource(R.string.home_heading_ready_to_learn)

    val highlightItems = listOf(
        HighlightStatItemUiModel(
            valueText = stringResource(R.string.home_streak_value),
            labelText = stringResource(R.string.home_streak_label),
            icon = Icons.Outlined.LocalFireDepartment,
            style = HighlightStatCardDefaults.streakStyle()
        ),
        HighlightStatItemUiModel(
            valueText = stringResource(R.string.home_goal_value),
            labelText = stringResource(R.string.home_goal_label),
            icon = Icons.Outlined.TrackChanges,
            style = HighlightStatCardDefaults.goalStyle()
        ),
        HighlightStatItemUiModel(
            valueText = stringResource(R.string.home_completed_value),
            labelText = stringResource(R.string.home_completed_label),
            icon = Icons.Outlined.EmojiEvents,
            style = HighlightStatCardDefaults.completedStyle()
        )
    )

    val roadmapStats = listOf(
        RoadmapStatItemUiModel(
            valueText = stringResource(R.string.home_roadmap_total_lessons_value),
            labelText = stringResource(R.string.home_roadmap_total_lessons_label),
            icon = Icons.AutoMirrored.Outlined.MenuBook
        ),
        RoadmapStatItemUiModel(
            valueText = stringResource(R.string.home_roadmap_completed_lessons_value),
            labelText = stringResource(R.string.home_roadmap_completed_lessons_label),
            icon = Icons.Outlined.CheckCircle
        ),
        RoadmapStatItemUiModel(
            valueText = stringResource(R.string.home_roadmap_remaining_lessons_value),
            labelText = stringResource(R.string.home_roadmap_remaining_lessons_label),
            icon = Icons.Outlined.Schedule
        )
    )

    val roadmapItems = listOf(
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
                    HighlightStatCardRow(
                        items = highlightItems,
                        horizontalSpacing = 14.dp,
                        onItemClick = onHighlightItemClick
                    )
                }

                item {
                    LearningProgressCard(
                        progressFraction = progressFraction,
                        onPrimaryIconClick = {}
                    )
                }

                item {
                    RoadmapStatCardRow(
                        items = roadmapStats,
                        horizontalSpacing = 14.dp,
                        onItemClick = onRoadmapStatItemClick
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        TrendingRoadmapsHeader(onSeeAllClick = onSeeAllClick)
                        roadmapItems.forEach { item ->
                            RoadmapCard(
                                item = item,
                                onClick = onRoadmapClick?.let { callback ->
                                    { callback(item) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedDestination by remember { mutableStateOf(NavBarDestination.Home) }
        HomeScreen(
            userName = "Thinh",
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {},
            onSeeAllClick = {}
        )
    }
}
