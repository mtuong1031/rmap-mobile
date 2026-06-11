package com.rmap.mobile.features.roadmap.presentation.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapInk
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapPrimaryAction

@Composable
fun RoadmapNextActionBar(
    nextActionTitle: String,
    primaryAction: RoadmapPrimaryAction,
    onContinueClick: () -> Unit,
    isPreviewMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = if (isDarkTheme) {
            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.96f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        },
        borderColor = if (isDarkTheme) {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier.padding(
                start = Dimens.spacingLg,
                top = Dimens.spacingSmPlus,
                end = Dimens.spacingSmPlus,
                bottom = Dimens.spacingSmPlus
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
            ) {
                Text(
                    text = stringResource(R.string.roadmap_detail_next_action_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else OnSurfacePlaceholderLight,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Text(
                    text = if (isPreviewMode) {
                        stringResource(R.string.roadmap_detail_preview_action_description)
                    } else {
                        roadmapNextActionTitle(
                            primaryAction = primaryAction,
                            nextActionTitle = nextActionTitle
                        )
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else roadmapInk,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacingMd))

            RMapButton(
                text = if (isPreviewMode) {
                    stringResource(R.string.roadmap_detail_preview_action)
                } else {
                    stringResource(
                        when (primaryAction) {
                            RoadmapPrimaryAction.StartLearning -> R.string.roadmap_detail_action_start_learning
                            RoadmapPrimaryAction.ContinueLearning -> R.string.roadmap_detail_action_continue
                        }
                    )
                },
                onClick = onContinueClick,
                modifier = Modifier.widthIn(min = NextActionButtonMinWidth),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.XSmall,
                colors = if (isDarkTheme) {
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = androidx.compose.ui.graphics.Color(0xFF0F172A)
                    )
                } else null,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isDarkTheme) androidx.compose.ui.graphics.Color(0xFF0F172A) else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(RMapButtonSize.XSmall.iconSize)
                    )
                }
            )
        }
    }
}

@Composable
private fun roadmapNextActionTitle(
    primaryAction: RoadmapPrimaryAction,
    nextActionTitle: String
): String {
    return when (primaryAction) {
        RoadmapPrimaryAction.StartLearning -> stringResource(R.string.roadmap_detail_action_start_learning)
        RoadmapPrimaryAction.ContinueLearning -> stringResource(
            R.string.roadmap_detail_continue_title,
            nextActionTitle
        )
    }
}

private val NextActionButtonMinWidth = Dimens.recommendedCardGlowOffset + Dimens.categoryIconContainerSize
