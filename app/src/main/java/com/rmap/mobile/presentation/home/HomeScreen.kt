package com.rmap.mobile.presentation.home

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
import com.rmap.mobile.presentation.navigation.NavBarDestination
import com.rmap.mobile.presentation.navigation.RMapNavigationBar
import com.rmap.mobile.presentation.ui.components.BackgroundDecorator
import com.rmap.mobile.presentation.ui.components.Header
import com.rmap.mobile.presentation.ui.components.HighlightStatCardDefaults
import com.rmap.mobile.presentation.ui.components.HighlightStatCardRow
import com.rmap.mobile.presentation.ui.components.HighlightStatItemUiModel
import com.rmap.mobile.presentation.ui.components.RoadmapCard
import com.rmap.mobile.presentation.ui.components.RoadmapCardUiModel
import com.rmap.mobile.presentation.ui.components.RoadmapDifficulty
import com.rmap.mobile.presentation.ui.components.RoadmapStatCardRow
import com.rmap.mobile.presentation.ui.components.RoadmapStatItemUiModel
import com.rmap.mobile.presentation.ui.components.rememberBackgroundScrollOffsetY
import com.rmap.mobile.presentation.ui.theme.RMapTheme

@Composable
fun HomeScreen(
    userName: String,
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
                    HighlightStatCardRow(
                        items = highlightItems,
                        horizontalSpacing = 14.dp,
                        onItemClick = onHighlightItemClick
                    )
                }

                item {
                    LearningProgressCard(
                        progressFraction = 0f,
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

@Composable
private fun LearningProgressCard(
    progressFraction: Float,
    onPrimaryIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val normalizedProgress = progressFraction.coerceIn(0f, 1f)
    val progressPercent = (normalizedProgress * 100).toInt()
    val totalLessons = 107
    val completedLessons = 1

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0x66006FFF),
                ambientColor = Color(0x66006FFF)
            )
            .background(
                color = Color(0xFF3875B7),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0x26FFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoGraph,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_progress_title_line_1),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_title_line_2),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPrimaryIconClick
                        )
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { normalizedProgress },
                        modifier = Modifier.size(76.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 6.dp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.home_progress_percent_short,
                                progressPercent
                            ),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_done),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.home_progress_percent_complete, progressPercent),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 28.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Text(
                    text = stringResource(
                        R.string.home_progress_lessons_completed,
                        completedLessons,
                        totalLessons
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
private fun TrendingRoadmapsHeader(
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.roadmap_trending_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3142)
            )
        )

        Text(
            text = stringResource(R.string.roadmap_see_all),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSeeAllClick
                )
                .background(Color.Transparent, RoundedCornerShape(8.dp))
                .padding(horizontal = 2.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.3.sp
            )
        )
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
