package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapDetailHeroProgressCard
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapDetailTopBar
import com.rmap.mobile.features.roadmap.presentation.components.detail.RoadmapNextActionBar
import com.rmap.mobile.features.roadmap.presentation.components.group.RoadmapGroupCard
import com.rmap.mobile.features.roadmap.presentation.components.group.RoadmapMilestoneCard
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapLocalSearchSection
import com.rmap.mobile.features.roadmap.presentation.components.search.defaultRoadmapQuickFilters
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapDetailUiState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel
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
    onMoreClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSearchFocus: () -> Unit = {},
    onSearchFocusChange: (Boolean) -> Unit = {},
    onSearchClearClick: () -> Unit = {},
    onSearchBackClick: () -> Unit = {},
    onNodeActionClick: (RoadmapNodeUiModel) -> Unit = {},
    onGroupClick: (RoadmapGroupUiModel) -> Unit = {},
    onMilestoneClick: (RoadmapMilestoneUiModel) -> Unit = {}
) {
    val listState = rememberLazyListState()

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
                Text(
                    text = stringResource(uiState.errorMessageResId),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.spacingXxl),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    )
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
                        start = Dimens.spacingXxl,
                        top = Dimens.controlXl + Dimens.spacingXs,
                        end = Dimens.spacingXxl,
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

                    uiState.groups
                        .filter { it.state == RoadmapGroupState.Expanded }
                        .forEach { group ->
                            item {
                                RoadmapDetailSectionSpacer()
                                RoadmapGroupCard(
                                    group = group,
                                    onNodeActionClick = onNodeActionClick
                                )
                            }
                        }

                    uiState.milestones
                        .filter { it.state == RoadmapMilestoneState.Available }
                        .forEach { milestone ->
                            item {
                                RoadmapDetailSectionSpacer()
                                RoadmapMilestoneCard(
                                    milestone = milestone,
                                    onClick = { onMilestoneClick(milestone) }
                                )
                            }
                        }

                    uiState.groups
                        .filter { it.state == RoadmapGroupState.Completed }
                        .forEach { group ->
                            item {
                                RoadmapDetailSectionSpacer()
                                RoadmapGroupCard(
                                    group = group,
                                    onNodeActionClick = onNodeActionClick
                                )
                            }
                        }

                    uiState.groups
                        .filter { it.state == RoadmapGroupState.Locked }
                        .forEach { group ->
                            item {
                                RoadmapDetailSectionSpacer()
                                RoadmapGroupCard(
                                    group = group,
                                    onNodeActionClick = onNodeActionClick
                                )
                            }
                        }

                    uiState.milestones
                        .filter { it.state == RoadmapMilestoneState.Locked }
                        .forEach { milestone ->
                            item {
                                RoadmapDetailSectionSpacer()
                                RoadmapMilestoneCard(
                                    milestone = milestone,
                                    onClick = { onMilestoneClick(milestone) }
                                )
                            }
                        }
                }

                RoadmapNextActionBar(
                    nextActionTitle = uiState.nextActionTitle,
                    onContinueClick = onContinueClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            start = Dimens.spacingXxl,
                            end = Dimens.spacingXxl,
                            bottom = Dimens.spacingXl
                        )
                )
            }
        }

        if (!uiState.isSearchActive) {
            RoadmapDetailTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick,
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

private val RoadmapDetailContentBottomPadding =
    Dimens.controlXl + Dimens.spacingScreenBottomCompact + Dimens.spacingXl

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
                        titleResId = R.string.roadmap_detail_milestone_landing_title,
                        descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                        state = RoadmapMilestoneState.Available
                    )
                ),
                isLoading = false
            ),
            onBackClick = {}
        )
    }
}
