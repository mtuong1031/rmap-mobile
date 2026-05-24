package com.rmap.mobile.features.roadmap.presentation.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.RoadmapSearchCard
import com.rmap.mobile.features.roadmap.presentation.components.RoadmapSearchSection
import com.rmap.mobile.features.roadmap.presentation.components.group.RoadmapMilestoneCompactCard
import com.rmap.mobile.features.roadmap.presentation.components.group.RoadmapNodeItem
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

@Composable
fun RoadmapSearchResultsContent(
    nodes: List<RoadmapNodeUiModel>,
    groups: List<RoadmapGroupUiModel>,
    milestones: List<RoadmapMilestoneUiModel>,
    onNodeActionClick: (RoadmapNodeUiModel) -> Unit,
    onGroupClick: (RoadmapGroupUiModel) -> Unit,
    onMilestoneClick: (RoadmapMilestoneUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingXxl),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
    ) {
        if (nodes.isNotEmpty()) {
            RoadmapSearchSection(
                title = stringResource(R.string.roadmap_search_section_skill_lessons)
            ) {
                nodes.forEach { node ->
                    RoadmapSearchCard {
                        RoadmapNodeItem(
                            node = node,
                            showDivider = false,
                            onActionClick = { onNodeActionClick(node) }
                        )
                    }
                }
            }
        }

        if (groups.isNotEmpty()) {
            RoadmapSearchSection(
                title = stringResource(R.string.roadmap_search_section_groups)
            ) {
                groups.forEach { group ->
                    RoadmapSearchGroupResultCard(
                        group = group,
                        onClick = { onGroupClick(group) }
                    )
                }
            }
        }

        if (milestones.isNotEmpty()) {
            RoadmapSearchSection(
                title = stringResource(R.string.roadmap_search_section_milestones)
            ) {
                milestones.forEach { milestone ->
                    RoadmapSearchCard {
                        RoadmapMilestoneCompactCard(
                            milestone = milestone,
                            onClick = { onMilestoneClick(milestone) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoadmapSearchGroupResultCard(
    group: RoadmapGroupUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    RoadmapSearchCard(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.roadmap_detail_group_required_complete,
                        group.completedRequiredNodes,
                        group.totalRequiredNodes
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = Dimens.spacingMd)
                    .size(Dimens.iconXxl)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconSm)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapSearchResultsContentPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapSearchResultsContent(
            nodes = listOf(
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
            groups = listOf(
                RoadmapGroupUiModel(
                    id = "dom",
                    title = "DOM",
                    completedRequiredNodes = 2,
                    totalRequiredNodes = 3,
                    progressFraction = 0.67f,
                    state = RoadmapGroupState.Expanded
                )
            ),
            milestones = listOf(
                RoadmapMilestoneUiModel(
                    id = "landing-page",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Locked
                )
            ),
            onNodeActionClick = {},
            onGroupClick = {},
            onMilestoneClick = {}
        )
    }
}
