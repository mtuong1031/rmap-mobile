package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapDetailHeroProgressCard
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapDetailTopBar
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapNextActionBar
import com.rmap.mobile.features.roadmap.presentation.components.content.RoadmapMilestoneCard
import com.rmap.mobile.features.roadmap.presentation.components.content.group.RoadmapGroupCard
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapLocalSearchSection
import com.rmap.mobile.features.roadmap.presentation.components.search.defaultRoadmapQuickFilters
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailContentUiItem
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailUiState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapPrimaryAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.currentSearchNode
import com.rmap.mobile.features.roadmap.presentation.viewmodel.recentSearchMilestones
import com.rmap.mobile.features.roadmap.presentation.viewmodel.recentSearchNodes
import com.rmap.mobile.features.roadmap.presentation.viewmodel.searchResultGroups
import com.rmap.mobile.features.roadmap.presentation.viewmodel.searchResultMilestones
import com.rmap.mobile.features.roadmap.presentation.viewmodel.searchResultNodes

@Composable
fun RoadmapDetailScreen(
    uiState: RoadmapDetailUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollTarget: RoadmapDetailScrollTarget? = null,
    onScrollTargetHandled: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSearchFocus: () -> Unit = {},
    onSearchFocusChange: (Boolean) -> Unit = {},
    onSearchClearClick: () -> Unit = {},
    onSearchBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onNodeActionClick: (RoadmapNodeUiModel) -> Unit = {},
    onGroupClick: (RoadmapGroupUiModel) -> Unit = {},
    onMilestoneClick: (RoadmapMilestoneUiModel) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val hasStartedLearning = uiState.primaryAction == RoadmapPrimaryAction.ContinueLearning

    LaunchedEffect(scrollTarget, uiState.isLoading, uiState.isSearchActive, uiState.contentItems) {
        val target = scrollTarget ?: return@LaunchedEffect
        if (uiState.isLoading || uiState.isSearchActive) return@LaunchedEffect

        val targetItemIndex = when (target) {
            RoadmapDetailScrollTarget.Hero -> RoadmapDetailHeroItemIndex
            RoadmapDetailScrollTarget.InProgressGroup -> {
                val contentIndex = uiState.contentItems.indexOfFirst { contentItem ->
                    contentItem is RoadmapDetailContentUiItem.Group &&
                        contentItem.group.nodes.any { node -> node.status == RoadmapNodeStatus.InProgress }
                }
                if (contentIndex >= 0) {
                    RoadmapDetailFirstContentItemIndex + contentIndex
                } else {
                    RoadmapDetailHeroItemIndex
                }
            }
        }

        listState.animateScrollToItem(targetItemIndex)
        onScrollTargetHandled()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessageResId != null -> {
                RoadmapDetailMessageState(
                    icon = Icons.Outlined.ErrorOutline,
                    messageResId = uiState.errorMessageResId,
                    actionTextResId = R.string.roadmap_detail_retry,
                    onActionClick = onRetryClick,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.spacingXxl),
                    iconTint = MaterialTheme.colorScheme.error
                )
            }

            uiState.isEmpty -> {
                RoadmapDetailMessageState(
                    icon = Icons.Outlined.Code,
                    messageResId = R.string.roadmap_detail_empty_description,
                    titleResId = R.string.roadmap_detail_empty_title,
                    actionTextResId = R.string.roadmap_detail_retry,
                    onActionClick = onRetryClick,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.spacingXxl)
                )
            }

            uiState.isSearchActive -> {
                RoadmapSearchScreen(
                    roadmapTitle = uiState.title,
                    query = uiState.searchQuery,
                    isInputFocused = uiState.isSearchInputFocused,
                    currentNode = uiState.currentSearchNode(),
                    quickFilters = defaultRoadmapQuickFilters(),
                    recentNodes = uiState.recentSearchNodes(),
                    recentMilestones = uiState.recentSearchMilestones(),
                    resultNodes = uiState.searchResultNodes(),
                    resultGroups = uiState.searchResultGroups(),
                    resultMilestones = uiState.searchResultMilestones(),
                    onBackClick = onSearchBackClick,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchFocusChange = onSearchFocusChange,
                    onSearchClearClick = onSearchClearClick,
                    onCurrentNodeActionClick = onNodeActionClick,
                    onQuickFilterClick = { filter -> onSearchQueryChange(filter.id) },
                    onRecentNodeClick = onNodeActionClick,
                    onRecentMilestoneClick = onMilestoneClick,
                    onNodeActionClick = onNodeActionClick,
                    onGroupClick = onGroupClick,
                    onMilestoneClick = onMilestoneClick
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.spacingScreenHorizontal,
                        top = Dimens.controlXl + Dimens.spacingXs,
                        end = Dimens.spacingScreenHorizontal,
                        bottom = RoadmapDetailContentBottomPadding
                    )
                ) {
                    item {
                        RoadmapDetailHeroProgressCard(
                            title = uiState.title,
                            categoryLabel = uiState.categoryLabel,
                            progressFraction = uiState.progressFraction,
                            completedRequiredNodes = uiState.completedRequiredNodes,
                            totalRequiredNodes = uiState.totalRequiredNodes,
                            nextActionTitle = uiState.nextActionTitle,
                            primaryAction = uiState.primaryAction,
                            nextUnlockTitle = uiState.nextUnlockTitle,
                            onContinueClick = onContinueClick
                        )
                    }

                    item {
                        RoadmapDetailSectionSpacer()
                        RoadmapLocalSearchSection(
                            roadmapTitle = uiState.title,
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onSearchFocusChange = { isFocused ->
                                if (isFocused) {
                                    onSearchFocus()
                                }
                            },
                            onClearClick = onSearchClearClick
                        )
                    }

                    uiState.contentItems.forEach { contentItem ->
                        item {
                            RoadmapDetailSectionSpacer()
                            when (contentItem) {
                                is RoadmapDetailContentUiItem.Group -> {
                                    RoadmapGroupCard(
                                        group = contentItem.group,
                                        onNodeActionClick = onNodeActionClick,
                                        showNodeInlineActions = hasStartedLearning
                                    )
                                }

                                is RoadmapDetailContentUiItem.Milestone -> {
                                    RoadmapMilestoneCard(
                                        milestone = contentItem.milestone,
                                        onClick = { onMilestoneClick(contentItem.milestone) }
                                    )
                                }
                            }
                        }
                    }
                }

                RoadmapNextActionBar(
                    nextActionTitle = uiState.nextActionTitle,
                    primaryAction = uiState.primaryAction,
                    onContinueClick = onContinueClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = Dimens.spacingScreenHorizontal,
                            end = Dimens.spacingScreenHorizontal,
                            bottom = Dimens.spacingXl
                        )
                )
            }
        }

        if (!uiState.isSearchActive) {
            RoadmapDetailTopBar(
                onBackClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RoadmapDetailSectionSpacer() {
    Spacer(modifier = Modifier.height(Dimens.spacingXxl))
}

@Composable
private fun RoadmapDetailMessageState(
    icon: ImageVector,
    @StringRes messageResId: Int,
    modifier: Modifier = Modifier,
    @StringRes titleResId: Int? = null,
    @StringRes actionTextResId: Int? = null,
    onActionClick: () -> Unit = {},
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier
            .widthIn(max = RoadmapDetailMessageMaxWidth)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(Dimens.iconXxl)
        )
        if (titleResId != null) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionTextResId != null) {
            RMapButton(
                text = stringResource(actionTextResId),
                onClick = onActionClick,
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Medium,
                modifier = Modifier.padding(top = Dimens.spacingSm)
            )
        }
    }
}

private val RoadmapDetailContentBottomPadding =
    Dimens.controlXl + Dimens.spacingScreenBottomCompact + Dimens.spacingXl
private val RoadmapDetailMessageMaxWidth = 320.dp
private const val RoadmapDetailHeroItemIndex = 0
private const val RoadmapDetailFirstContentItemIndex = 2

enum class RoadmapDetailScrollTarget {
    Hero,
    InProgressGroup
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 1250)
@Composable
private fun RoadmapDetailScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapDetailScreen(
            uiState = RoadmapDetailUiState(
                title = "Frontend Pro",
                categoryLabel = "Web Development",
                progressFraction = 0.75f,
                completedLessons = 6,
                totalLessons = 8,
                completedRequiredNodes = 6,
                totalRequiredNodes = 8,
                nextActionTitle = "Asynchronous JS",
                primaryAction = RoadmapPrimaryAction.ContinueLearning,
                nextUnlockTitle = "DOM Manipulation",
                groups = listOf(
                    RoadmapGroupUiModel(
                        id = "core",
                        title = "Core Web Fundamentals",
                        completedRequiredNodes = 2,
                        totalRequiredNodes = 3,
                        progressFraction = 0.67f,
                        state = RoadmapGroupState.Expanded,
                        nodes = listOf(
                            RoadmapNodeUiModel(
                                id = "html-css",
                                title = "HTML & CSS",
                                icon = Icons.Outlined.Code,
                                status = RoadmapNodeStatus.Completed,
                                requirement = RoadmapNodeRequirement.Required,
                                descriptionResId = R.string.roadmap_detail_completed_recently,
                                action = RoadmapNodeAction.Review
                            )
                        )
                    ),
                    RoadmapGroupUiModel(
                        id = "core-completed",
                        title = "Core Web Fundamentals",
                        completedRequiredNodes = 3,
                        totalRequiredNodes = 3,
                        progressFraction = 1f,
                        state = RoadmapGroupState.Completed,
                        nodes = listOf(
                            RoadmapNodeUiModel(
                                id = "html-css-completed",
                                title = "HTML & CSS",
                                icon = Icons.Outlined.Code,
                                status = RoadmapNodeStatus.Completed,
                                requirement = RoadmapNodeRequirement.Required,
                                descriptionResId = R.string.roadmap_detail_completed_recently
                            ),
                            RoadmapNodeUiModel(
                                id = "async-js-completed",
                                title = "Asynchronous JS",
                                icon = Icons.Outlined.Code,
                                status = RoadmapNodeStatus.Completed,
                                requirement = RoadmapNodeRequirement.Required,
                                descriptionResId = R.string.roadmap_detail_completed_recently
                            ),
                            RoadmapNodeUiModel(
                                id = "css-animation-completed",
                                title = "CSS Animation",
                                icon = Icons.Outlined.Code,
                                status = RoadmapNodeStatus.Completed,
                                requirement = RoadmapNodeRequirement.Optional,
                                descriptionResId = R.string.roadmap_detail_css_animation_description
                            )
                        )
                    )
                ),
                milestones = listOf(
                    RoadmapMilestoneUiModel(
                        id = "landing-page",
                        title = "Build your first landing page",
                        description = "Apply HTML, CSS, and JavaScript basics in a portfolio-ready project.",
                        state = RoadmapMilestoneState.Available
                    )
                ),
                isLoading = false
            ),
            onBackClick = {}
        )
    }
}
