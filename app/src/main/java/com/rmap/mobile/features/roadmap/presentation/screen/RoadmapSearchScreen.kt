package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapLocalSearchMode
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapLocalSearchSection
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapQuickFilterUiModel
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapSearchResultsContent
import com.rmap.mobile.features.roadmap.presentation.components.search.RoadmapSearchStartContent
import com.rmap.mobile.features.roadmap.presentation.components.search.defaultRoadmapQuickFilters
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

@Composable
fun RoadmapSearchScreen(
    roadmapTitle: String,
    query: String,
    isInputFocused: Boolean,
    currentNode: RoadmapNodeUiModel?,
    quickFilters: List<RoadmapQuickFilterUiModel>,
    recentNodes: List<RoadmapNodeUiModel>,
    recentMilestones: List<RoadmapMilestoneUiModel>,
    resultNodes: List<RoadmapNodeUiModel>,
    resultGroups: List<RoadmapGroupUiModel>,
    resultMilestones: List<RoadmapMilestoneUiModel>,
    onBackClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onSearchClearClick: () -> Unit,
    onCurrentNodeActionClick: (RoadmapNodeUiModel) -> Unit,
    onQuickFilterClick: (RoadmapQuickFilterUiModel) -> Unit,
    onRecentNodeClick: (RoadmapNodeUiModel) -> Unit,
    onRecentMilestoneClick: (RoadmapMilestoneUiModel) -> Unit,
    onNodeActionClick: (RoadmapNodeUiModel) -> Unit,
    onGroupClick: (RoadmapGroupUiModel) -> Unit,
    onMilestoneClick: (RoadmapMilestoneUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchFocusRequester = remember { FocusRequester() }
    val searchMode = if (isInputFocused) {
        RoadmapLocalSearchMode.Typing
    } else {
        RoadmapLocalSearchMode.Active
    }
    val headerQuickFilters = if (isInputFocused) emptyList() else quickFilters
    val startContentQuickFilters = if (isInputFocused) quickFilters else emptyList()

    LaunchedEffect(isInputFocused) {
        if (isInputFocused) {
            searchFocusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = Dimens.spacingNone,
                bottom = Dimens.spacingScreenBottomCompact
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
        ) {
            item {
                RoadmapLocalSearchSection(
                    roadmapTitle = roadmapTitle,
                    query = query,
                    onQueryChange = onSearchQueryChange,
                    mode = searchMode,
                    quickFilters = headerQuickFilters,
                    onQuickFilterClick = onQuickFilterClick,
                    onBackClick = onBackClick,
                    onSearchFocusChange = onSearchFocusChange,
                    onClearClick = onSearchClearClick,
                    inputFocusRequester = searchFocusRequester,
                    modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
                )
            }

            item {
                if (query.isBlank()) {
                    RoadmapSearchStartContent(
                        currentNode = currentNode,
                        quickFilters = startContentQuickFilters,
                        recentNodes = recentNodes,
                        recentMilestones = recentMilestones,
                        onCurrentNodeActionClick = onCurrentNodeActionClick,
                        onQuickFilterClick = onQuickFilterClick,
                        onRecentNodeClick = onRecentNodeClick,
                        onRecentMilestoneClick = onRecentMilestoneClick
                    )
                } else {
                    RoadmapSearchResultsContent(
                        nodes = resultNodes,
                        groups = resultGroups,
                        milestones = resultMilestones,
                        onNodeActionClick = onNodeActionClick,
                        onGroupClick = onGroupClick,
                        onMilestoneClick = onMilestoneClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 900)
@Composable
private fun RoadmapSearchScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapSearchScreen(
            roadmapTitle = "Frontend Pro",
            query = "",
            isInputFocused = false,
            currentNode = RoadmapNodeUiModel(
                id = "async-js",
                title = "Asynchronous JS",
                icon = Icons.Outlined.Code,
                status = RoadmapNodeStatus.InProgress,
                requirement = RoadmapNodeRequirement.Required,
                descriptionResId = R.string.roadmap_detail_async_description,
                action = RoadmapNodeAction.Continue
            ),
            quickFilters = defaultRoadmapQuickFilters(),
            recentNodes = listOf(
                RoadmapNodeUiModel(
                    id = "html-css",
                    title = "HTML & CSS",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Completed,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_completed_recently,
                    action = RoadmapNodeAction.Review
                ),
                RoadmapNodeUiModel(
                    id = "dom-manipulation-recent",
                    title = "DOM Manipulation",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_unlock_by_completing,
                    descriptionArgs = listOf("Asynchronous JS")
                )
            ),
            recentMilestones = listOf(
                RoadmapMilestoneUiModel(
                    id = "landing-page-recent",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Available
                )
            ),
            resultNodes = listOf(
                RoadmapNodeUiModel(
                    id = "dom-manipulation",
                    title = "DOM Manipulation",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_unlock_by_completing,
                    descriptionArgs = listOf("Asynchronous JS")
                ),
                RoadmapNodeUiModel(
                    id = "browser-dom-basics",
                    title = "Browser DOM Basics",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Optional,
                    descriptionResId = R.string.skill_part_of_format,
                    descriptionArgs = listOf("Core Web Fundamentals")
                )
            ),
            resultGroups = listOf(
                RoadmapGroupUiModel(
                    id = "dom",
                    title = "DOM",
                    completedRequiredNodes = 2,
                    totalRequiredNodes = 3,
                    progressFraction = 0.67f,
                    state = RoadmapGroupState.Expanded
                )
            ),
            resultMilestones = listOf(
                RoadmapMilestoneUiModel(
                    id = "landing-page",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Locked
                )
            ),
            onBackClick = {},
            onSearchQueryChange = {},
            onSearchFocusChange = {},
            onSearchClearClick = {},
            onCurrentNodeActionClick = {},
            onQuickFilterClick = {},
            onRecentNodeClick = {},
            onRecentMilestoneClick = {},
            onNodeActionClick = {},
            onGroupClick = {},
            onMilestoneClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 900)
@Composable
private fun RoadmapSearchScreenResultsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapSearchScreen(
            roadmapTitle = "Frontend Pro",
            query = "dom",
            isInputFocused = true,
            currentNode = null,
            quickFilters = defaultRoadmapQuickFilters(),
            recentNodes = emptyList(),
            recentMilestones = emptyList(),
            resultNodes = listOf(
                RoadmapNodeUiModel(
                    id = "dom-manipulation",
                    title = "DOM Manipulation",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Required,
                    descriptionResId = R.string.roadmap_detail_unlock_by_completing,
                    descriptionArgs = listOf("Asynchronous JS")
                ),
                RoadmapNodeUiModel(
                    id = "browser-dom-basics",
                    title = "Browser DOM Basics",
                    icon = Icons.Outlined.Code,
                    status = RoadmapNodeStatus.Locked,
                    requirement = RoadmapNodeRequirement.Optional,
                    descriptionResId = R.string.skill_part_of_format,
                    descriptionArgs = listOf("Core Web Fundamentals")
                )
            ),
            resultGroups = listOf(
                RoadmapGroupUiModel(
                    id = "dom",
                    title = "DOM",
                    completedRequiredNodes = 2,
                    totalRequiredNodes = 3,
                    progressFraction = 0.67f,
                    state = RoadmapGroupState.Expanded
                )
            ),
            resultMilestones = listOf(
                RoadmapMilestoneUiModel(
                    id = "landing-page",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Locked
                )
            ),
            onBackClick = {},
            onSearchQueryChange = {},
            onSearchFocusChange = {},
            onSearchClearClick = {},
            onCurrentNodeActionClick = {},
            onQuickFilterClick = {},
            onRecentNodeClick = {},
            onRecentMilestoneClick = {},
            onNodeActionClick = {},
            onGroupClick = {},
            onMilestoneClick = {}
        )
    }
}
