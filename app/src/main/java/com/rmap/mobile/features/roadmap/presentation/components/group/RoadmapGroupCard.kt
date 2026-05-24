package com.rmap.mobile.features.roadmap.presentation.components.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapLinearProgress
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.formattedString
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapLockedText
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBorder
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
    modifier: Modifier = Modifier
) {
    val isLocked = group.state == RoadmapGroupState.Locked
    val isCompleted = group.state == RoadmapGroupState.Completed
    val hasExpandableContent = if (isLocked) {
        group.lockedExpandedDescriptionResId != null
    } else {
        group.nodes.isNotEmpty()
    }
    var isExpanded by rememberSaveable(group.id, group.state.name) {
        mutableStateOf(false)
    }
    val toggleExpanded = {
        if (hasExpandableContent) {
            isExpanded = !isExpanded
        }
    }

    RoadmapDecoratedCard(
        modifier = modifier
            .then(
                if (isLocked) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = toggleExpanded
                    )
                } else {
                    Modifier
                }
            ),
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
        if (isLocked) {
            LockedGroupContent(
                group = group,
                isDescriptionExpanded = isExpanded
            )
        } else {
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
                                showDivider = index < group.nodes.lastIndex &&
                                    node.status != RoadmapNodeStatus.InProgress,
                                onActionClick = { onNodeActionClick(node) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedGroupContent(
    group: RoadmapGroupUiModel,
    isDescriptionExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = roadmapLockedText,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            RoadmapPill(
                text = stringResource(R.string.roadmap_detail_locked),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                borderColor = MaterialTheme.colorScheme.outline,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.iconXxs)
                    )
                }
            )
        }

        group.lockedDescriptionResId?.let { resId ->
            Text(
                text = formattedString(resId, group.lockedDescriptionArgs),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = OnSurfacePlaceholderLight
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        RoadmapAccordionVisibility(visible = isDescriptionExpanded) {
            group.lockedExpandedDescriptionResId?.let { resId ->
                Text(
                    text = stringResource(resId),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RoadmapGroupHeader(
    group: RoadmapGroupUiModel,
    isExpanded: Boolean,
    canAccordion: Boolean,
    canToggleExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val toggleModifier = if (canToggleExpanded) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onToggleExpanded
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(RoadmapGroupHeaderHeight)
            .then(toggleModifier)
            .padding(horizontal = Dimens.spacingMdPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (group.state == RoadmapGroupState.Locked) {
                            roadmapLockedText
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = OnSurfacePlaceholderLight,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.roadmap_detail_group_required_complete,
                        group.completedRequiredNodes,
                        group.totalRequiredNodes
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                RoadmapLinearProgress(
                    progress = group.progressFraction,
                    modifier = Modifier.width(GroupProgressWidth),
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                    indicatorColor = if (group.state == RoadmapGroupState.Completed) {
                        roadmapSuccess
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }

        GroupTrailingIndicator(
            state = group.state,
            isExpanded = isExpanded,
            canAccordion = canAccordion
        )
    }
}

@Composable
private fun RoadmapAccordionVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(
                durationMillis = AccordionAnimationMillis,
                easing = FastOutSlowInEasing
            ),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(durationMillis = AccordionFadeAnimationMillis)
        ),
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = AccordionAnimationMillis,
                easing = FastOutSlowInEasing
            ),
            shrinkTowards = Alignment.Top
        ) + fadeOut(
            animationSpec = tween(durationMillis = AccordionFadeAnimationMillis)
        )
    ) {
        content()
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
                lockedDescriptionResId = R.string.roadmap_detail_locked_group_description,
                lockedDescriptionArgs = listOf(
                    "Core Web Fundamentals",
                    "React Fundamentals"
                ),
                lockedExpandedDescriptionResId = R.string.roadmap_detail_framework_ecosystem_description
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

private const val AccordionAnimationMillis = 260
private const val AccordionFadeAnimationMillis = 160
private val RoadmapGroupHeaderHeight = Dimens.iconFrameSize
private val GroupProgressWidth = Dimens.spacingMassive + Dimens.spacingHuge
private val GroupTrailingIndicatorSize = Dimens.spacingXxxl - Dimens.spacingXxs

@Composable
private fun GroupTrailingIndicator(
    state: RoadmapGroupState,
    isExpanded: Boolean,
    canAccordion: Boolean
) {
    val containerColor = if (canAccordion) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else {
        when (state) {
            RoadmapGroupState.Completed -> roadmapSuccessBg
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        }
    }
    val borderColor = if (canAccordion) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        when (state) {
            RoadmapGroupState.Completed -> roadmapSuccessBorder
            else -> MaterialTheme.colorScheme.outlineVariant
        }
    }
    val icon = if (canAccordion) {
        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
    } else {
        when (state) {
            RoadmapGroupState.Completed -> Icons.Default.Check
            RoadmapGroupState.Locked -> Icons.Default.Lock
            RoadmapGroupState.Expanded -> Icons.Default.KeyboardArrowUp
        }
    }
    val tint = if (canAccordion) {
        OnSurfacePlaceholderLight
    } else {
        when (state) {
            RoadmapGroupState.Completed -> roadmapSuccess
            RoadmapGroupState.Locked -> MaterialTheme.colorScheme.onSurfaceVariant
            RoadmapGroupState.Expanded -> OnSurfacePlaceholderLight
        }
    }

    Box(
        modifier = Modifier
            .size(GroupTrailingIndicatorSize)
            .background(containerColor, CircleShape)
            .border(Dimens.borderThin, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(
                if (!canAccordion && state == RoadmapGroupState.Locked) {
                    Dimens.iconXxs
                } else {
                    Dimens.iconSm
                }
            )
        )
    }
}
