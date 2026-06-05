package com.rmap.mobile.features.roadmap.presentation.components.content.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.content.RoadmapNodeItem
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

@Composable
fun RoadmapGroupCard(
    group: RoadmapGroupUiModel,
    onNodeActionClick: (RoadmapNodeUiModel) -> Unit,
    showNodeInlineActions: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isLocked = group.state == RoadmapGroupState.Locked
    val isCompleted = group.state == RoadmapGroupState.Completed
    val hasExpandableContent = group.nodes.isNotEmpty()
    var isExpanded by rememberSaveable(group.id, group.state.name) {
        mutableStateOf(group.state == RoadmapGroupState.Expanded)
    }
    val toggleExpanded = {
        if (hasExpandableContent) {
            isExpanded = !isExpanded
        }
    }

    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.searchBar,
        containerColor = if (isLocked) {
            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        borderColor = if (isLocked) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        shadow = !isLocked
    ) {
        Column {
            RoadmapGroupHeader(
                group = group,
                isExpanded = isExpanded,
                canAccordion = hasExpandableContent && !isCompleted,
                canToggleExpanded = hasExpandableContent,
                onToggleExpanded = toggleExpanded
            )

            RoadmapAccordionVisibility(visible = group.nodes.isNotEmpty() && isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.surfaceContainerLow))
                        .padding(vertical = Dimens.spacingXs)
                ) {
                    group.nodes.forEachIndexed { index, node ->
                        RoadmapNodeItem(
                            node = node,
                            showDivider = index < group.nodes.lastIndex,
                            onActionClick = { onNodeActionClick(node) },
                            showInlineAction = showNodeInlineActions
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapGroupCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapGroupCard(
            group = RoadmapGroupUiModel(
                id = "core-web",
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
                        descriptionResId = R.string.roadmap_detail_completed_recently
                    ),
                    RoadmapNodeUiModel(
                        id = "async-js",
                        title = "Asynchronous JS",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.InProgress,
                        requirement = RoadmapNodeRequirement.Required,
                        descriptionResId = R.string.roadmap_detail_async_description,
                        action = RoadmapNodeAction.Continue
                    )
                )
            ),
            onNodeActionClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapGroupCardLockedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapGroupCard(
            group = RoadmapGroupUiModel(
                id = "framework-ecosystem",
                title = "Framework Ecosystem",
                completedRequiredNodes = 0,
                totalRequiredNodes = 3,
                progressFraction = 0f,
                state = RoadmapGroupState.Locked,
                nodes = listOf(
                    RoadmapNodeUiModel(
                        id = "react-state",
                        title = "React State",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.Locked,
                        requirement = RoadmapNodeRequirement.Required,
                        descriptionResId = R.string.roadmap_detail_node_locked_description
                    )
                )
            ),
            onNodeActionClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapGroupCardCompletedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapGroupCard(
            group = RoadmapGroupUiModel(
                id = "core-web-completed",
                title = "Core Web Fundamentals",
                completedRequiredNodes = 3,
                totalRequiredNodes = 3,
                progressFraction = 1f,
                state = RoadmapGroupState.Completed,
                nodes = listOf(
                    RoadmapNodeUiModel(
                        id = "html-css",
                        title = "HTML & CSS",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.Completed,
                        requirement = RoadmapNodeRequirement.Required,
                        descriptionResId = R.string.roadmap_detail_completed_recently
                    ),
                    RoadmapNodeUiModel(
                        id = "async-js",
                        title = "Asynchronous JS",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.Completed,
                        requirement = RoadmapNodeRequirement.Required,
                        descriptionResId = R.string.roadmap_detail_completed_recently
                    ),
                    RoadmapNodeUiModel(
                        id = "dom",
                        title = "DOM Manipulation",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.Completed,
                        requirement = RoadmapNodeRequirement.Required,
                        descriptionResId = R.string.roadmap_detail_completed_recently
                    ),
                    RoadmapNodeUiModel(
                        id = "css-animation",
                        title = "CSS Animation",
                        icon = Icons.Outlined.Code,
                        status = RoadmapNodeStatus.Completed,
                        requirement = RoadmapNodeRequirement.Optional,
                        descriptionResId = R.string.roadmap_detail_css_animation_description
                    )
                )
            ),
            onNodeActionClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
