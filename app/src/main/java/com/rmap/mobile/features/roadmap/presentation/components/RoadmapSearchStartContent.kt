package com.rmap.mobile.features.roadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

@Composable
fun RoadmapSearchStartContent(
    currentNode: RoadmapNodeUiModel?,
    quickFilters: List<RoadmapQuickFilterUiModel>,
    recentNodes: List<RoadmapNodeUiModel>,
    recentMilestones: List<RoadmapMilestoneUiModel>,
    onCurrentNodeActionClick: (RoadmapNodeUiModel) -> Unit,
    onQuickFilterClick: (RoadmapQuickFilterUiModel) -> Unit,
    onRecentNodeClick: (RoadmapNodeUiModel) -> Unit,
    onRecentMilestoneClick: (RoadmapMilestoneUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingXxl),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
    ) {
        currentNode?.let { node ->
            RoadmapSearchSection(title = stringResource(R.string.roadmap_search_section_current_node)) {
                RoadmapSearchCard {
                    RoadmapNodeItem(
                        node = node,
                        showDivider = false,
                        onActionClick = { onCurrentNodeActionClick(node) }
                    )
                }
            }
        }

        if (quickFilters.isNotEmpty()) {
            RoadmapSearchSection(title = stringResource(R.string.roadmap_search_section_quick_filters)) {
                RoadmapQuickFilterRow(
                    filters = quickFilters,
                    onFilterClick = onQuickFilterClick
                )
            }
        }

        if (recentNodes.isNotEmpty() || recentMilestones.isNotEmpty()) {
            RoadmapSearchSection(title = stringResource(R.string.roadmap_search_section_recently_viewed)) {
                RoadmapSearchCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        recentNodes.forEachIndexed { index, node ->
                            RoadmapNodeItem(
                                node = node,
                                showDivider = index < recentNodes.lastIndex || recentMilestones.isNotEmpty(),
                                onActionClick = { onRecentNodeClick(node) }
                            )
                        }
                        recentMilestones.forEach { milestone ->
                            RoadmapMilestoneCompactCard(
                                milestone = milestone,
                                onClick = { onRecentMilestoneClick(milestone) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 900)
@Composable
private fun RoadmapSearchStartContentPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapSearchStartContent(
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
                    id = "dom-manipulation",
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
                    id = "landing-page",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Available
                )
            ),
            onCurrentNodeActionClick = {},
            onQuickFilterClick = {},
            onRecentNodeClick = {},
            onRecentMilestoneClick = {}
        )
    }
}
