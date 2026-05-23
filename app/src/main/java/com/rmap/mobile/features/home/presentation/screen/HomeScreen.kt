package com.rmap.mobile.features.home.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.home.presentation.components.HomeCategoryCardRow
import com.rmap.mobile.features.home.presentation.components.HomeCategoryItemUiModel
import com.rmap.mobile.features.home.presentation.components.HomeHeroSection
import com.rmap.mobile.features.home.presentation.components.HomePaceAlertCard
import com.rmap.mobile.features.home.presentation.components.HomeRecommendedRoadmapsSection
import com.rmap.mobile.features.home.presentation.components.HomeRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.HomeRoadmapCardUiModel
import com.rmap.mobile.features.home.presentation.components.HomeStatCardDefaults
import com.rmap.mobile.features.home.presentation.components.HomeStatCardRow
import com.rmap.mobile.features.home.presentation.components.HomeStatItemUiModel
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapCard
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.home.presentation.components.TrendingRoadmapsHeader
import com.rmap.mobile.features.home.presentation.viewmodel.HomeUiState
import com.rmap.mobile.navigation.NavBarDestination

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    selectedDestination: NavBarDestination = NavBarDestination.Home,
    onHeaderActionClick: () -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onContinueLearningClick: () -> Unit = {},
    onCreateRoadmapWithAiClick: () -> Unit = {},
    onExploreReadyMadeClick: () -> Unit = {},
    onAdjustPlanClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onHomeStatItemClick: ((index: Int, item: HomeStatItemUiModel) -> Unit)? = null,
    onCategoryItemClick: ((index: Int, item: HomeCategoryItemUiModel) -> Unit)? = null,
    onRecommendedRoadmapClick: ((HomeRoadmapCardUiModel) -> Unit)? = null,
    onRecommendedRoadmapBookmarkClick: ((HomeRoadmapCardUiModel) -> Unit)? = null,
    onRoadmapClick: ((TrendingRoadmapCardUiModel) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val sectionHorizontalPadding = Dimens.spacingScreenHorizontal

    val greetingText = "Good morning, ${uiState.userName}"
    val headingText = stringResource(R.string.home_heading_next_skill)
    val progressPercent = (uiState.progressFraction.coerceIn(0f, 1f) * 100).toInt()
    val readinessPercent = if (uiState.todayGoalTotal > 0) {
        ((uiState.todayGoalCompleted.toFloat() / uiState.todayGoalTotal.toFloat()).coerceIn(0f, 1f) * 100).toInt()
    } else {
        0
    }

    val homeStatItems = listOf(
        HomeStatItemUiModel(
            valueText = "$progressPercent%",
            labelText = stringResource(R.string.home_stat_roadmap_label),
            icon = Icons.Outlined.Map,
            style = HomeStatCardDefaults.roadmapStyle()
        ),
        HomeStatItemUiModel(
            valueText = "${uiState.streakDays} days",
            labelText = stringResource(R.string.home_streak_label),
            icon = Icons.Outlined.LocalFireDepartment,
            style = HomeStatCardDefaults.streakStyle()
        ),
        HomeStatItemUiModel(
            valueText = "$readinessPercent%",
            labelText = stringResource(R.string.home_stat_readiness_label),
            icon = Icons.Outlined.TrackChanges,
            style = HomeStatCardDefaults.readinessStyle()
        )
    )

    val recommendedRoadmaps = listOf(
        HomeRoadmapCardUiModel(
            id = "react-fundamentals",
            categoryLabel = "Web Development",
            title = "React Fundamentals",
            nodesText = "24 nodes",
            durationText = "4 weeks",
            actionText = stringResource(R.string.home_roadmap_view_action),
            icon = Icons.Outlined.Code,
            style = HomeRoadmapCardDefaults.webDevelopmentStyle(),
            isBeginner = true
        ),
        HomeRoadmapCardUiModel(
            id = "frontend-interview",
            categoryLabel = "Web Development",
            title = "Frontend Interview",
            nodesText = "12 nodes",
            durationText = "2 weeks",
            actionText = stringResource(R.string.home_roadmap_view_action),
            icon = Icons.Outlined.TrackChanges,
            style = HomeRoadmapCardDefaults.interviewStyle()
        ),
        HomeRoadmapCardUiModel(
            id = "css-architecture",
            categoryLabel = "Design",
            title = "CSS Architecture",
            nodesText = "18 nodes",
            durationText = "3 weeks",
            actionText = stringResource(R.string.home_roadmap_view_action),
            icon = Icons.Outlined.Palette,
            style = HomeRoadmapCardDefaults.designStyle()
        )
    )

    val categoryItems = listOf(
        HomeCategoryItemUiModel(
            id = "on-click",
            label = "On Click",
            countText = 24.toString(),
            icon = Icons.Outlined.Public,
            selected = true
        ),
        HomeCategoryItemUiModel(
            id = "normal",
            label = "Normal",
            countText = 18.toString(),
            icon = Icons.Outlined.PhoneAndroid
        ),
        HomeCategoryItemUiModel(
            id = "devops",
            label = "DevOps",
            countText = 12.toString(),
            icon = Icons.Outlined.Settings
        ),
        HomeCategoryItemUiModel(
            id = "data",
            label = "Data",
            countText = 8.toString(),
            icon = Icons.Outlined.Storage
        ),
        HomeCategoryItemUiModel(
            id = "design",
            label = "Design",
            countText = 15.toString(),
            icon = Icons.Outlined.Palette
        ),
        HomeCategoryItemUiModel(
            id = "ai",
            label = "AI",
            countText = 9.toString(),
            icon = Icons.Outlined.SmartToy
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = Dimens.spacingScreenTopCompact,
                    bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
            ) {
                item {
                    RMapHeader(
                        greetingText = greetingText,
                        headingText = headingText,
                        onActionClick = onHeaderActionClick,
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                    )
                }

                item {
                    Box(modifier = Modifier.padding(horizontal = sectionHorizontalPadding)) {
                        RMapTextInput(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = stringResource(R.string.home_search_placeholder),
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = RMapTextInputDefaults.colors().placeholderColor,
                                )
                            }
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    role = Role.Button,
                                    onClick = onSearchClick
                                )
                        )
                    }
                }

                item {
                    HomeHeroSection(
                        sectionTitle = stringResource(R.string.home_learning_plan_title),
                        roadmapTitle = "Frontend Pro",
                        skillTitle = "Asynchronous Ronaldo Dos Santoss",
                        chapterText = "Chapter 1/6",
                        requiredSkillText = stringResource(R.string.home_hero_required_skill),
                        timeLeftText = "25 min left",
                        progressText = "${uiState.completedLessons} of ${uiState.totalLessons} required nodes complete",
                        progressPercentText = "$progressPercent%",
                        progressFraction = uiState.progressFraction,
                        continueText = stringResource(R.string.home_hero_continue),
                        nextUnlockPrefix = stringResource(R.string.home_hero_next_unlock_prefix),
                        nextUnlockText = "DOM Manipulation",
                        hasInProgressRoadmap = uiState.hasInProgressRoadmap,
                        onContinueClick = onContinueLearningClick,
                        onCreateRoadmapWithAiClick = onCreateRoadmapWithAiClick,
                        onExploreReadyMadeClick = onExploreReadyMadeClick,
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                    )
                }

                item {
                    HomeStatCardRow(
                        items = homeStatItems,
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                        horizontalSpacing = Dimens.spacingMd,
                        onItemClick = onHomeStatItemClick
                    )
                }

                item {
                    HomePaceAlertCard(
                        message = "You are 15% behind your target pace.\nFinish 1 skill node today to back the track.",
                        actionText = stringResource(R.string.home_pace_alert_action),
                        onActionClick = onAdjustPlanClick,
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                    )
                }

                item {
                    HomeRecommendedRoadmapsSection(
                        title = stringResource(R.string.home_recommended_title),
                        subtitle = "Recommended because you're learning Frontend Pro",
                        roadmaps = recommendedRoadmaps,
                        metadataSeparatorText = stringResource(R.string.separator_bullet),
                        starterBadgeText = stringResource(R.string.home_roadmap_starter_badge),
                        bookmarkContentDescription = stringResource(R.string.content_description_bookmark),
                        onRoadmapClick = onRecommendedRoadmapClick,
                        onBookmarkClick = onRecommendedRoadmapBookmarkClick,
                    )
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                    ) {
                        RMapSectionTitle(
                            text = stringResource(R.string.home_categories_title),
                            subtitle = stringResource(R.string.home_categories_subtitle)
                        )

                        HomeCategoryCardRow(
                            items = categoryItems,
                            onItemClick = onCategoryItemClick
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                    ) {
                        TrendingRoadmapsHeader(onSeeAllClick = onSeeAllClick)
                        uiState.trendingRoadmaps.forEach { item ->
                            TrendingRoadmapCard(
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
                userName = "User",
                progressFraction = 0.75f,
                completedLessons = 6,
                totalLessons = 8,
                streakDays = 5,
                todayGoalCompleted = 1,
                todayGoalTotal = 3,
                completedRoadmaps = 1,
                trendingRoadmaps = listOf(
                    TrendingRoadmapCardUiModel(
                        id = "ui-ux-master",
                        rankText = "#1",
                        categoryLabel = "Design",
                        title = "UI/UX Master",
                        metadataText = "96 nodes • 2 months",
                        trendText = "Popular this week",
                        leadingIcon = Icons.Outlined.Palette,
                        trendIcon = Icons.AutoMirrored.Outlined.TrendingUp,
                        style = TrendingRoadmapCardDefaults.primaryStyle()
                    ),
                    TrendingRoadmapCardUiModel(
                        id = "devops-specialist",
                        rankText = "#2",
                        categoryLabel = "DevOps",
                        title = "DevOps Specialist",
                        metadataText = "183 nodes • 6 months",
                        trendText = "2.4k learners",
                        leadingIcon = Icons.Outlined.DataObject,
                        trendIcon = Icons.Outlined.Groups,
                        style = TrendingRoadmapCardDefaults.neutralStyle()
                    ),
                    TrendingRoadmapCardUiModel(
                        id = "data-science",
                        rankText = "#3",
                        categoryLabel = "Data",
                        title = "Data Science",
                        metadataText = "240 nodes • 4 months",
                        trendText = "Trending globally",
                        leadingIcon = Icons.Outlined.Science,
                        trendIcon = Icons.AutoMirrored.Outlined.TrendingUp,
                        style = TrendingRoadmapCardDefaults.indigoStyle()
                    )
                )
            ),
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            onHeaderActionClick = {},
            onSeeAllClick = {},
            onContinueLearningClick = {},
            onAdjustPlanClick = {}
        )
    }
}
