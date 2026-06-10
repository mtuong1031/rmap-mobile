package com.rmap.mobile.features.roadmap.presentation.components.content

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.formattedString
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapDeepBlue
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapInk
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBorder
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

@Composable
fun RoadmapNodeItem(
    modifier: Modifier = Modifier,
    node: RoadmapNodeUiModel,
    showDivider: Boolean,
    onActionClick: () -> Unit,
    showInlineAction: Boolean = true,
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val hasInlineAction = showInlineAction && node.status == RoadmapNodeStatus.NotStarted
    val isRowClickable = node.skillId.isNotBlank()
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = if (isRowClickable) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onActionClick
        )
    } else {
        Modifier
    }
    val itemModifier =
        modifier
            .fillMaxWidth()
            .then(clickModifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingMd)

    val titleColor = when {
        isDarkTheme && node.status != RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurface
        isDarkTheme -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        else -> when (node.status) {
            RoadmapNodeStatus.InProgress -> roadmapDeepBlue
            RoadmapNodeStatus.Locked -> OnSurfacePlaceholderLight
            RoadmapNodeStatus.Completed,
            RoadmapNodeStatus.NotStarted -> roadmapInk
        }
    }

    val subTextColor = when {
        isDarkTheme && node.status != RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurfaceVariant
        isDarkTheme -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        else -> if (node.status == RoadmapNodeStatus.Locked) {
            OnSurfacePlaceholderLight
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Column {
        Row(
            modifier = itemModifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.Top
        ) {
            NodeIcon(node = node)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = node.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = titleColor,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        NodeStatusBadge(node = node)
                    }
                }

                Text(
                    text = formattedString(node.descriptionResId, node.descriptionArgs),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = subTextColor
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                node.action?.let { action ->
                    if (hasInlineAction) {
                        NodeActionButton(
                            action = action,
                            expanded = false,
                            onClick = onActionClick
                        )
                    }
                }
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = NodeDividerStartPadding)
                    .height(Dimens.borderThin)
                    .background(
                        if (isDarkTheme) {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}

@Composable
private fun NodeIcon(node: RoadmapNodeUiModel) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val containerColor = when {
        isDarkTheme && node.status == RoadmapNodeStatus.Completed -> Color(0xFF064E3B)
        isDarkTheme && node.status == RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.primaryContainer
        isDarkTheme && node.status == RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> when (node.status) {
            RoadmapNodeStatus.Completed -> roadmapSuccessBg
            RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.surface
            RoadmapNodeStatus.NotStarted -> MaterialTheme.colorScheme.primaryContainer
            RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerLow
        }
    }
    val icon = when (node.status) {
        RoadmapNodeStatus.Completed -> Icons.Default.Check
        RoadmapNodeStatus.Locked -> Icons.Default.Lock
        RoadmapNodeStatus.InProgress,
        RoadmapNodeStatus.NotStarted -> node.requirement.toNodeRequirementIcon()
    }
    val tint = when {
        isDarkTheme && node.status == RoadmapNodeStatus.Completed -> Color(0xFF34D399)
        isDarkTheme && node.status == RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.primary
        isDarkTheme && node.status == RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        else -> when (node.status) {
            RoadmapNodeStatus.Completed -> CompletedNodeIconTint
            RoadmapNodeStatus.InProgress,
            RoadmapNodeStatus.NotStarted -> node.requirement.toNodeRequirementIconTint()
            RoadmapNodeStatus.Locked -> OnSurfacePlaceholderLight
        }
    }
    val borderColor = when {
        isDarkTheme -> Color.Transparent // Hide border in dark mode for better look
        else -> when (node.status) {
            RoadmapNodeStatus.Completed -> roadmapSuccessBorder
            RoadmapNodeStatus.InProgress -> tint
            RoadmapNodeStatus.NotStarted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.34f)
            RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.outlineVariant
        }
    }

    Box(
        modifier = Modifier
            .size(Dimens.iconXxl)
            .background(containerColor, CircleShape)
            .nodeIconBorder(
                color = borderColor,
                dashed = node.requirement == RoadmapNodeRequirement.Optional
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = node.requirementIconContentDescription(),
            tint = tint,
            modifier = Modifier.size(Dimens.iconXs)
        )
    }
}

private fun RoadmapNodeRequirement.toNodeRequirementIcon() = when (this) {
    RoadmapNodeRequirement.Required -> Icons.Outlined.PushPin
    RoadmapNodeRequirement.Optional -> Icons.Outlined.Extension
}

@Composable
private fun RoadmapNodeUiModel.requirementIconContentDescription(): String? {
    return when (status) {
        RoadmapNodeStatus.InProgress,
        RoadmapNodeStatus.NotStarted -> stringResource(requirement.toNodeRequirementIconContentDescriptionResId())
        RoadmapNodeStatus.Completed,
        RoadmapNodeStatus.Locked -> null
    }
}

private fun RoadmapNodeRequirement.toNodeRequirementIconContentDescriptionResId() = when (this) {
    RoadmapNodeRequirement.Required -> R.string.roadmap_detail_required_skill_icon
    RoadmapNodeRequirement.Optional -> R.string.roadmap_detail_optional_skill_icon
}

@Composable
private fun RoadmapNodeRequirement.toNodeRequirementIconTint() = when (this) {
    RoadmapNodeRequirement.Required -> MaterialTheme.colorScheme.primary
    RoadmapNodeRequirement.Optional -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun NodeStatusBadge(node: RoadmapNodeUiModel) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    RoadmapPill(
        text = stringResource(
            when (node.status) {
                RoadmapNodeStatus.Completed -> R.string.roadmap_detail_status_completed
                RoadmapNodeStatus.InProgress -> R.string.roadmap_detail_status_in_progress
                RoadmapNodeStatus.NotStarted -> R.string.roadmap_detail_status_not_started
                RoadmapNodeStatus.Locked -> R.string.roadmap_detail_locked
            }
        ),
        containerColor = when {
            isDarkTheme && node.status == RoadmapNodeStatus.Completed -> Color(0xFF064E3B)
            isDarkTheme && node.status == RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.primaryContainer
            isDarkTheme && node.status == RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> when (node.status) {
                RoadmapNodeStatus.Completed -> roadmapSuccessBg
                RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.inversePrimary
                RoadmapNodeStatus.NotStarted -> MaterialTheme.colorScheme.primaryContainer
                RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerLow
            }
        },
        contentColor = when {
            isDarkTheme && node.status == RoadmapNodeStatus.Completed -> Color(0xFF34D399)
            isDarkTheme && node.status == RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.onPrimaryContainer
            isDarkTheme && node.status == RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
            else -> when (node.status) {
                RoadmapNodeStatus.Completed -> roadmapSuccess
                RoadmapNodeStatus.InProgress -> roadmapDeepBlue
                RoadmapNodeStatus.NotStarted -> MaterialTheme.colorScheme.primary
                RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        },
        dotColor = if (node.status == RoadmapNodeStatus.InProgress) {
            if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
        } else {
            null
        }
    )
}

private fun Modifier.nodeIconBorder(
    color: Color,
    dashed: Boolean
): Modifier {
    return drawWithContent {
        drawContent()
        val strokeWidth = Dimens.borderMedium.toPx()
        drawCircle(
            color = color,
            radius = size.minDimension / 2f - strokeWidth / 2f,
            style = Stroke(
                width = strokeWidth,
                pathEffect = if (dashed) {
                    PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            Dimens.spacingSm.toPx(),
                            Dimens.spacingXs.toPx()
                        )
                    )
                } else {
                    null
                }
            )
        )
    }
}

@Composable
private fun NodeActionButton(
    action: RoadmapNodeAction,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(
        when (action) {
            RoadmapNodeAction.Start -> R.string.roadmap_detail_action_start_learning
            RoadmapNodeAction.Review -> R.string.roadmap_detail_action_review
            RoadmapNodeAction.Continue -> R.string.roadmap_detail_action_continue
        }
    )

    RMapButton(
        text = label,
        onClick = onClick,
        modifier = if (expanded) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.widthIn(min = NodeActionMinWidth)
        },
        variant = if (expanded) RMapButtonVariant.Primary else RMapButtonVariant.Neutral,
        size = RMapButtonSize.XSmall,
        colors = if (expanded) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.cardElevationNone),
        border = null
    )
}

private val NodeDividerStartPadding = Dimens.controlXl + Dimens.spacingXs
private val NodeActionMinWidth = Dimens.categoryIconContainerSize
private val CompletedNodeIconTint = Color(0xFF059669)
