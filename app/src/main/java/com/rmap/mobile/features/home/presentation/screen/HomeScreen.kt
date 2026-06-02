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
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.rmap.mobile.features.home.presentation.components.category.HomeCategoryCardGrid
import com.rmap.mobile.features.home.presentation.components.category.HomeCategoryItemUiModel
import com.rmap.mobile.features.home.presentation.components.hero.HomeHeroSection
import com.rmap.mobile.features.home.presentation.components.hero.HomeLearningPlanUiModel
import com.rmap.mobile.features.home.presentation.components.insight.HomeGoalQuizCard
import com.rmap.mobile.features.home.presentation.components.insight.HomePaceAlertCard
import com.rmap.mobile.features.home.presentation.components.loading.HomeCategoriesSectionSkeleton
import com.rmap.mobile.features.home.presentation.components.loading.HomeHeroSectionSkeleton
import com.rmap.mobile.features.home.presentation.components.loading.HomePaceAlertCardSkeleton
import com.rmap.mobile.features.home.presentation.components.loading.HomeRecommendedRoadmapsSectionSkeleton
import com.rmap.mobile.features.home.presentation.components.loading.HomeStatCardRowSkeleton
import com.rmap.mobile.features.home.presentation.components.loading.HomeTrendingRoadmapsSectionSkeleton
import com.rmap.mobile.features.home.presentation.components.recommend.HomeRecommendedRoadmapsSection
import com.rmap.mobile.features.home.presentation.components.recommend.HomeRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.recommend.HomeRoadmapCardUiModel
import com.rmap.mobile.features.home.presentation.components.stat.HomeStatCardDefaults
import com.rmap.mobile.features.home.presentation.components.stat.HomeStatCardRow
import com.rmap.mobile.features.home.presentation.components.stat.HomeStatItemUiModel
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCard
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardDefaults
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapsHeader
import com.rmap.mobile.features.home.presentation.viewmodel.HomeGreetingPeriod
import com.rmap.mobile.features.home.presentation.viewmodel.HomeRecommendedRoadmapState
import com.rmap.mobile.features.home.presentation.viewmodel.HomeUiState
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.toHomeBrowseCategoryLabel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.toImageVector
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
    onContinueLearningClick: () -> Unit = {},
    onContinueLearningPlanClick: ((HomeLearningPlanUiModel) -> Unit)? = null,
    onCreateRoadmapWithAiClick: () -> Unit = {},
    onExploreReadyMadeClick: () -> Unit = {},
    onAdjustPlanClick: () -> Unit = {},
    onDestinationSelected: (NavBarDestination) -> Unit = {},
    onHomeStatItemClick: ((index: Int, item: HomeStatItemUiModel) -> Unit)? = null,
    onCategoryItemClick: ((index: Int, item: HomeCategoryItemUiModel) -> Unit)? = null,
    onRecommendedRoadmapClick: ((HomeRecommendedRoadmapState) -> Unit)? = null,
    onRecommendedRoadmapBookmarkClick: ((HomeRecommendedRoadmapState) -> Unit)? = null,
    onRoadmapClick: ((TrendingRoadmapCardUiModel) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val sectionHorizontalPadding = Dimens.spacingScreenHorizontal

    val greetingText = if (uiState.isAuthenticated && uiState.userName.isNotBlank()) {
        stringResource(
            R.string.home_greeting_authenticated,
            stringResource(uiState.greetingPeriod.labelResId()),
            uiState.userName
        )
    } else {
        stringResource(R.string.home_greeting_guest)
    }
    val greetingVisual = uiState.toGreetingVisual()
    val headingText = stringResource(R.string.home_heading_next_skill)
    val isLoading = uiState.isLoading
    val progressPercent = (uiState.progressFraction.coerceIn(0f, 1f) * 100).toInt()
    val readinessPercent = (uiState.readinessFraction.coerceIn(0f, 1f) * 100).toInt()
    val learningPlans = uiState.learningPlans.map { plan ->
        HomeLearningPlanUiModel(
            id = plan.id,
            roadmapTitle = plan.roadmapTitle,
            skillTitle = plan.skillTitle,
            chapterText = plan.chapterText,
            requiredSkillText = stringResource(R.string.home_hero_required_skill),
            timeLeftText = plan.timeLeftText,
            completedRequiredNodes = plan.completedRequiredNodes,
            totalRequiredNodes = plan.totalRequiredNodes,
            progressPercentage = plan.progressPercentage,
            nextUnlockText = plan.nextUnlockText,
            currentNodeId = plan.currentNodeId,
            startedAtMillis = plan.startedAtMillis
        )
    }
    var focusedLearningPlanId by remember { mutableStateOf<String?>(learningPlans.firstOrNull()?.id) }
    var showAllCategories by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(learningPlans) {
        if (focusedLearningPlanId !in learningPlans.map { it.id }) {
            focusedLearningPlanId = learningPlans.firstOrNull()?.id
        }
    }
    val focusedPaceWarning = uiState.learningPlans
        .firstOrNull { it.id == focusedLearningPlanId }
        ?.paceWarning

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

    val recommendedRoadmaps = uiState.recommendedRoadmaps.toRoadmapCardPairs(
        savedRoadmapIds = uiState.savedRoadmapIds,
        actionText = stringResource(R.string.home_roadmap_view_action)
    )
    val beginnerRoadmaps = uiState.beginnerRoadmaps.toRoadmapCardPairs(
        savedRoadmapIds = uiState.savedRoadmapIds,
        actionText = stringResource(R.string.home_roadmap_view_action)
    )

    val categoryItems = uiState.categories.mapIndexed { index, category ->
        HomeCategoryItemUiModel(
            id = category.id,
            label = category.label,
            countText = category.countText,
            icon = category.icon.toImageVector(),
            selected = false
        )
    }
    val visibleCategoryItems = if (showAllCategories) {
        categoryItems
    } else {
        categoryItems.take(HomeInitialCategoryLimit)
    }
    val shouldShowCategoryToggleAction = categoryItems.size > HomeInitialCategoryLimit

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
                        greetingIcon = greetingVisual.icon,
                        greetingIconTint = greetingVisual.tint,
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

                if (isLoading) {
                    item {
                        HomeHeroSectionSkeleton(
                            sectionTitle = stringResource(R.string.home_learning_plan_title),
                            sectionHorizontalPadding = sectionHorizontalPadding,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        HomeStatCardRowSkeleton(
                            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                        )
                    }

                    item {
                        HomePaceAlertCardSkeleton(
                            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                        )
                    }

                    item {
                        HomeRecommendedRoadmapsSectionSkeleton(
                            title = stringResource(R.string.home_recommended_title)
                        )
                    }

                    item {
                        HomeCategoriesSectionSkeleton(
                            title = stringResource(R.string.home_categories_title),
                            subtitle = stringResource(R.string.home_categories_subtitle),
                            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                        )
                    }

                    item {
                        HomeTrendingRoadmapsSectionSkeleton(
                            title = stringResource(R.string.home_popular_roadmaps_explore_title),
                            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                        )
                    }
                } else {
                    item {
                        HomeHeroSection(
                            sectionTitle = stringResource(R.string.home_learning_plan_title),
                            continueText = stringResource(R.string.home_hero_continue),
                            nextUnlockPrefix = stringResource(R.string.home_hero_next_unlock_prefix),
                            sectionHorizontalPadding = sectionHorizontalPadding,
                            learningPlans = learningPlans,
                            onContinueClick = { item ->
                                onContinueLearningPlanClick?.invoke(item) ?: onContinueLearningClick()
                            },
                            onCreateRoadmapWithAiClick = onCreateRoadmapWithAiClick,
                            onExploreReadyMadeClick = onExploreReadyMadeClick,
                            onFocusedPlanChange = { focusedLearningPlanId = it.id },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.hasInProgressRoadmap) {
                        item {
                            HomeStatCardRow(
                                items = homeStatItems,
                                modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                                horizontalSpacing = Dimens.spacingMd,
                                onItemClick = onHomeStatItemClick
                            )
                        }

                        focusedPaceWarning?.let { warning ->
                            item {
                                HomePaceAlertCard(
                                    message = warning.message,
                                    actionText = warning.actionText,
                                    onActionClick = onAdjustPlanClick,
                                    modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                                )
                            }
                        }

                        if (recommendedRoadmaps.isNotEmpty()) {
                            item {
                                HomeRecommendedRoadmapsSection(
                                    title = stringResource(R.string.home_recommended_title),
                                    subtitle = "Recommended because you're learning Frontend Pro",
                                    roadmaps = recommendedRoadmaps.map { it.second },
                                    metadataSeparatorText = stringResource(R.string.separator_bullet),
                                    starterBadgeText = stringResource(R.string.home_roadmap_starter_badge),
                                    bookmarkContentDescription = stringResource(R.string.content_description_bookmark),
                                    onRoadmapClick = { item ->
                                        recommendedRoadmaps.firstOrNull { it.second.id == item.id }?.first?.let { roadmap ->
                                            onRecommendedRoadmapClick?.invoke(roadmap)
                                        }
                                    },
                                    onBookmarkClick = { item ->
                                        recommendedRoadmaps.firstOrNull { it.second.id == item.id }?.first?.let { roadmap ->
                                            onRecommendedRoadmapBookmarkClick?.invoke(roadmap)
                                        }
                                    },
                                )
                            }
                        }

                        item {
                            HomeCategorySection(
                                title = stringResource(R.string.home_categories_title),
                                subtitle = stringResource(R.string.home_categories_subtitle),
                                items = visibleCategoryItems,
                                shouldShowToggleAction = shouldShowCategoryToggleAction,
                                showAllCategories = showAllCategories,
                                onToggleClick = { showAllCategories = !showAllCategories },
                                onCategoryItemClick = onCategoryItemClick,
                                modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                            )
                        }
                    } else {
                        item {
                            HomeGoalQuizCard(
                                title = stringResource(R.string.home_goal_quiz_title),
                                description = stringResource(R.string.home_goal_quiz_description),
                                actionText = stringResource(R.string.home_goal_quiz_action),
                                onActionClick = onCreateRoadmapWithAiClick,
                                modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                            )
                        }

                        item {
                            HomeCategorySection(
                                title = stringResource(R.string.home_popular_subjects_title),
                                subtitle = null,
                                items = visibleCategoryItems,
                                shouldShowToggleAction = shouldShowCategoryToggleAction,
                                showAllCategories = showAllCategories,
                                onToggleClick = { showAllCategories = !showAllCategories },
                                onCategoryItemClick = onCategoryItemClick,
                                modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
                            )
                        }

                        if (beginnerRoadmaps.isNotEmpty()) {
                            item {
                                HomeRecommendedRoadmapsSection(
                                    title = stringResource(R.string.home_beginner_roadmaps_title),
                                    subtitle = stringResource(R.string.home_beginner_roadmaps_subtitle),
                                    roadmaps = beginnerRoadmaps.map { it.second },
                                    metadataSeparatorText = stringResource(R.string.separator_bullet),
                                    starterBadgeText = stringResource(R.string.home_roadmap_starter_badge),
                                    bookmarkContentDescription = stringResource(R.string.content_description_bookmark),
                                    onRoadmapClick = { item ->
                                        beginnerRoadmaps.firstOrNull { it.second.id == item.id }?.first?.let { roadmap ->
                                            onRecommendedRoadmapClick?.invoke(roadmap)
                                        }
                                    },
                                    onBookmarkClick = { item ->
                                        beginnerRoadmaps.firstOrNull { it.second.id == item.id }?.first?.let { roadmap ->
                                            onRecommendedRoadmapBookmarkClick?.invoke(roadmap)
                                        }
                                    },
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                        ) {
                            TrendingRoadmapsHeader(
                                title = stringResource(R.string.home_popular_roadmaps_explore_title)
                            )
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
}

private fun HomeGreetingPeriod.labelResId(): Int {
    return when (this) {
        HomeGreetingPeriod.Morning -> R.string.home_greeting_period_morning
        HomeGreetingPeriod.Afternoon -> R.string.home_greeting_period_afternoon
        HomeGreetingPeriod.Evening -> R.string.home_greeting_period_evening
        HomeGreetingPeriod.Night -> R.string.home_greeting_period_night
    }
}

private data class HomeGreetingVisual(
    val icon: ImageVector,
    val tint: Color
)

@Composable
private fun HomeCategorySection(
    title: String,
    subtitle: String?,
    items: List<HomeCategoryItemUiModel>,
    shouldShowToggleAction: Boolean,
    showAllCategories: Boolean,
    onToggleClick: () -> Unit,
    onCategoryItemClick: ((index: Int, item: HomeCategoryItemUiModel) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(
            text = title,
            subtitle = subtitle,
            actionText = if (shouldShowToggleAction) {
                stringResource(
                    if (showAllCategories) {
                        R.string.action_see_less
                    } else {
                        R.string.action_see_all
                    }
                )
            } else {
                null
            },
            onActionClick = if (shouldShowToggleAction) {
                onToggleClick
            } else {
                null
            }
        )

        HomeCategoryCardGrid(
            items = items,
            onItemClick = onCategoryItemClick
        )
    }
}

@Composable
private fun HomeUiState.toGreetingVisual(): HomeGreetingVisual {
    if (!isAuthenticated) {
        return HomeGreetingVisual(
            icon = Icons.Outlined.Map,
            tint = MaterialTheme.colorScheme.primary
        )
    }

    return when (greetingPeriod) {
        HomeGreetingPeriod.Morning -> HomeGreetingVisual(
            icon = Icons.Outlined.WbSunny,
            tint = Color(0xFFFE9A00)
        )
        HomeGreetingPeriod.Afternoon -> HomeGreetingVisual(
            icon = Icons.Outlined.WbSunny,
            tint = MaterialTheme.colorScheme.primary
        )
        HomeGreetingPeriod.Evening -> HomeGreetingVisual(
            icon = Icons.Outlined.NightsStay,
            tint = Color(0xFF7C3AED)
        )
        HomeGreetingPeriod.Night -> HomeGreetingVisual(
            icon = Icons.Outlined.NightsStay,
            tint = Color(0xFF4F46E5)
        )
    }
}

private const val HomeInitialCategoryLimit = 6

@Composable
private fun List<HomeRecommendedRoadmapState>.toRoadmapCardPairs(
    savedRoadmapIds: Set<String>,
    actionText: String
): List<Pair<HomeRecommendedRoadmapState, HomeRoadmapCardUiModel>> {
    return map { roadmap ->
        roadmap to HomeRoadmapCardUiModel(
            id = roadmap.id,
            categoryLabel = roadmap.categoryId.toHomeBrowseCategoryLabel(roadmap.categoryLabel),
            title = roadmap.title,
            nodesText = roadmap.nodesText,
            durationText = roadmap.durationText,
            actionText = actionText,
            icon = roadmap.icon.toImageVector(),
            style = roadmap.icon.toHomeRoadmapCardStyle(),
            isBeginner = roadmap.isBeginner,
            isSaved = savedRoadmapIds.contains(roadmap.id)
        )
    }
}

@Composable
private fun LearningTopicIcon.toHomeRoadmapCardStyle() = when (this) {
    LearningTopicIcon.Palette -> HomeRoadmapCardDefaults.designStyle()
    LearningTopicIcon.SmartToy,
    LearningTopicIcon.Science -> HomeRoadmapCardDefaults.interviewStyle()
    else -> HomeRoadmapCardDefaults.webDevelopmentStyle()
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
            onContinueLearningClick = {},
            onAdjustPlanClick = {}
        )
    }
}
