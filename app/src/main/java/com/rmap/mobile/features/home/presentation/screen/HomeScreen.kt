package com.rmap.mobile.features.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.TrackChanges
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
import com.rmap.mobile.core.ui.components.Header
import com.rmap.mobile.core.ui.components.HighlightStatCardDefaults
import com.rmap.mobile.core.ui.components.HighlightStatCardRow
import com.rmap.mobile.core.ui.components.HighlightStatItemUiModel
import com.rmap.mobile.core.ui.components.RoadmapCard
import com.rmap.mobile.core.ui.components.RoadmapCardUiModel
import com.rmap.mobile.core.ui.components.RoadmapStatCardRow
import com.rmap.mobile.core.ui.components.RoadmapStatItemUiModel
import com.rmap.mobile.core.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.home.presentation.components.LearningProgressCard
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapsHeader
import com.rmap.mobile.features.home.presentation.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
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

    val greetingText = stringResource(R.string.home_greeting, uiState.userName)
    val headingText = stringResource(R.string.home_heading_ready_to_learn)

    val highlightItems = listOf(
        HighlightStatItemUiModel(
            valueText = stringResource(R.string.home_streak_value, uiState.streakDays),
            labelText = stringResource(R.string.home_streak_label),
            icon = Icons.Outlined.LocalFireDepartment,
            style = HighlightStatCardDefaults.streakStyle()
        ),
        HighlightStatItemUiModel(
            valueText = stringResource(R.string.home_goal_value, uiState.todayGoalCompleted, uiState.todayGoalTotal),
            labelText = stringResource(R.string.home_goal_label),
            icon = Icons.Outlined.TrackChanges,
            style = HighlightStatCardDefaults.goalStyle()
        ),
        HighlightStatItemUiModel(
            valueText = uiState.completedRoadmaps.toString(),
            labelText = stringResource(R.string.home_completed_label),
            icon = Icons.Outlined.EmojiEvents,
            style = HighlightStatCardDefaults.completedStyle()
        )
    )

    val roadmapStats = listOf(
        RoadmapStatItemUiModel(
            valueText = uiState.totalLessons.toString(),
            labelText = stringResource(R.string.home_roadmap_total_lessons_label),
            icon = Icons.AutoMirrored.Outlined.MenuBook
        ),
        RoadmapStatItemUiModel(
            valueText = uiState.completedLessons.toString(),
            labelText = stringResource(R.string.home_roadmap_completed_lessons_label),
            icon = Icons.Outlined.CheckCircle
        ),
        RoadmapStatItemUiModel(
            valueText = (uiState.totalLessons - uiState.completedLessons).coerceAtLeast(0).toString(),
            labelText = stringResource(R.string.home_roadmap_remaining_lessons_label),
            icon = Icons.Outlined.Schedule
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
                    HighlightStatCardRow(
                        items = highlightItems,
                        horizontalSpacing = Dimens.spacingMdPlus,
                        onItemClick = onHighlightItemClick
                    )
                }

                item {
                    LearningProgressCard(
                        progressFraction = uiState.progressFraction,
                        completedLessons = uiState.completedLessons,
                        totalLessons = uiState.totalLessons,
                        onPrimaryIconClick = {}
                    )
                }

                item {
                    RoadmapStatCardRow(
                        items = roadmapStats,
                        horizontalSpacing = Dimens.spacingMdPlus,
                        onItemClick = onRoadmapStatItemClick
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
                        TrendingRoadmapsHeader(onSeeAllClick = onSeeAllClick)
                        uiState.trendingRoadmaps.forEach { item ->
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
            uiState = HomeUiState(
                userName = "Thinh",
                progressFraction = 0.42f,
                completedLessons = 45,
                totalLessons = 107,
                streakDays = 2,
                todayGoalCompleted = 1,
                todayGoalTotal = 3,
                completedRoadmaps = 1
            ),
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {},
            onSeeAllClick = {}
        )
    }
}
