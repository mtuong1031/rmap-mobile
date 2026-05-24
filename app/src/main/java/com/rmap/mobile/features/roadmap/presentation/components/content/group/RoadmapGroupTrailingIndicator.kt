package com.rmap.mobile.features.roadmap.presentation.components.content.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccess
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapSuccessBorder
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupState

@Composable
internal fun RoadmapGroupTrailingIndicator(
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

private val GroupTrailingIndicatorSize = Dimens.spacingXxxl - Dimens.spacingXxs
