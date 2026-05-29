package com.rmap.mobile.features.roadmap.presentation.components.content

import androidx.compose.foundation.LocalIndication
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.formattedString
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapDeepBlue
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapFocusedRequirementBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapInk
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBorder
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeRequirement
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeStatus
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeUiModel

private val roadmapNodeFocusedShape = RoundedCornerShape(Dimens.cardRadiusSmPlus)

@Composable
fun RoadmapNodeItem(
    node: RoadmapNodeUiModel,
    showDivider: Boolean,
    onActionClick: () -> Unit,
    onBookmarkClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isFocused = node.status == RoadmapNodeStatus.InProgress
    val isCompleted = node.status == RoadmapNodeStatus.Completed
    val shape = roadmapNodeFocusedShape
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = if (isCompleted) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onActionClick
        )
    } else {
        Modifier
    }
    val itemModifier = if (isFocused) {
        modifier
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSmPlus)
            .cardShadow(shape = shape)
            .background(MaterialTheme.colorScheme.background, shape)
            .border(Dimens.borderThin, MaterialTheme.colorScheme.primary, shape)
            .padding(Dimens.spacingMd)
    } else {
        modifier
            .fillMaxWidth()
            .then(clickModifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingMd)
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
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = node.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = when (node.status) {
                                    RoadmapNodeStatus.InProgress -> roadmapDeepBlue
                                    RoadmapNodeStatus.Locked -> OnSurfacePlaceholderLight
                                    RoadmapNodeStatus.Completed -> roadmapInk
                                },
                                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        NodeTypeBadge(node = node)
                        NodeBookmarkButton(
                            isBookmarked = node.isBookmarked,
                            onClick = onBookmarkClick
                        )
                    }

                    NodeStatusBadge(node = node)
                }

                Text(
                    text = formattedString(node.descriptionResId, node.descriptionArgs),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (node.status == RoadmapNodeStatus.Locked) {
                            OnSurfacePlaceholderLight
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                node.action?.let { action ->
                    if (node.status != RoadmapNodeStatus.Completed) {
                        NodeActionButton(
                            action = action,
                            expanded = isFocused,
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
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
private fun NodeBookmarkButton(
    isBookmarked: Boolean,
    onClick: (() -> Unit)?
) {
    IconButton(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.size(Dimens.controlSm)
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = null,
            tint = if (isBookmarked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(Dimens.iconSm)
        )
    }
}

@Composable
private fun NodeIcon(node: RoadmapNodeUiModel) {
    val containerColor = when (node.status) {
        RoadmapNodeStatus.Completed -> roadmapSuccessBg
        RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.surface
        RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerLow
    }
    val borderColor = when (node.status) {
        RoadmapNodeStatus.Completed -> roadmapSuccessBorder
        RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.inversePrimary
        RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.outlineVariant
    }
    val icon = when (node.status) {
        RoadmapNodeStatus.Completed -> Icons.Default.Check
        RoadmapNodeStatus.Locked -> Icons.Default.Lock
        RoadmapNodeStatus.InProgress -> node.icon
    }
    val tint = when (node.status) {
        RoadmapNodeStatus.Completed -> roadmapSuccess
        RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.primary
        RoadmapNodeStatus.Locked -> OnSurfacePlaceholderLight
    }

    Box(
        modifier = Modifier
            .size(Dimens.iconXxl)
            .background(containerColor, CircleShape)
            .border(Dimens.borderThin, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(Dimens.iconXs)
        )
    }
}

@Composable
private fun NodeTypeBadge(node: RoadmapNodeUiModel) {
    RoadmapPill(
        text = stringResource(
            when (node.requirement) {
                RoadmapNodeRequirement.Required -> R.string.roadmap_detail_status_required
                RoadmapNodeRequirement.Optional -> R.string.roadmap_detail_status_optional
            }
        ),
        containerColor = if (node.status == RoadmapNodeStatus.InProgress) {
            roadmapFocusedRequirementBg
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        contentColor = if (node.requirement == RoadmapNodeRequirement.Optional) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }
    )
}

@Composable
private fun NodeStatusBadge(node: RoadmapNodeUiModel) {
    RoadmapPill(
        text = stringResource(
            when (node.status) {
                RoadmapNodeStatus.Completed -> R.string.roadmap_detail_status_completed
                RoadmapNodeStatus.InProgress -> R.string.roadmap_detail_status_in_progress
                RoadmapNodeStatus.Locked -> R.string.roadmap_detail_locked
            }
        ),
        containerColor = when (node.status) {
            RoadmapNodeStatus.Completed -> roadmapSuccessBg
            RoadmapNodeStatus.InProgress -> MaterialTheme.colorScheme.inversePrimary
            RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        contentColor = when (node.status) {
            RoadmapNodeStatus.Completed -> roadmapSuccess
            RoadmapNodeStatus.InProgress -> roadmapDeepBlue
            RoadmapNodeStatus.Locked -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        dotColor = if (node.status == RoadmapNodeStatus.InProgress) {
            MaterialTheme.colorScheme.primary
        } else {
            null
        }
    )
}

@Composable
private fun NodeActionButton(
    action: RoadmapNodeAction,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(
        when (action) {
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
