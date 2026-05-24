package com.rmap.mobile.features.roadmap.presentation.components.content.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapLinearProgress
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapLockedText
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel

@Composable
internal fun RoadmapGroupHeader(
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

        RoadmapGroupTrailingIndicator(
            state = group.state,
            isExpanded = isExpanded,
            canAccordion = canAccordion
        )
    }
}

private val RoadmapGroupHeaderHeight = Dimens.iconFrameSize
private val GroupProgressWidth = Dimens.spacingMassive + Dimens.spacingHuge
