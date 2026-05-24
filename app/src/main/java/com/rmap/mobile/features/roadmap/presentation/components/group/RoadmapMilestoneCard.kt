package com.rmap.mobile.features.roadmap.presentation.components.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.RoadmapSearchCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmber
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberBorder
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberDark
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapAmberText
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapInk
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneIconBg
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneLockedBorder
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapMilestoneSoftBg
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneState
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapMilestoneUiModel

@Composable
fun RoadmapMilestoneCard(
    milestone: RoadmapMilestoneUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = milestone.state == RoadmapMilestoneState.Locked

    RoadmapDecoratedCard(
        modifier = modifier,
        borderColor = roadmapAmberBorder,
        useHeroBackground = true,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                roadmapMilestoneSoftBg.copy(alpha = 0.96f),
                roadmapAmberBg.copy(alpha = 0.98f)
            )
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = roadmapAmber.copy(alpha = 0.18f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimens.spacingSm)
                .size(MilestoneDecorIconSize)
                .rotate(12f)
        )

        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RoadmapPill(
                text = stringResource(R.string.roadmap_detail_milestone_label),
                containerColor = roadmapAmberBg,
                contentColor = roadmapAmber,
                borderColor = roadmapAmberBorder,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = roadmapAmber,
                        modifier = Modifier.size(Dimens.iconXxs)
                    )
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
                Text(
                    text = stringResource(milestone.titleResId),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = roadmapAmberDark,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(milestone.descriptionResId),
                    style = MaterialTheme.typography.labelMedium.copy(color = roadmapAmberText),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            RMapButton(
                text = stringResource(
                    if (isLocked) R.string.roadmap_detail_locked else R.string.roadmap_detail_action_view_project
                ),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Small,
                leadingIcon = if (isLocked) {
                    {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(RMapButtonSize.Small.iconSize)
                        )
                    }
                } else {
                    null
                },
                trailingIcon = if (!isLocked) {
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = roadmapAmberText,
                            modifier = Modifier.size(RMapButtonSize.Small.iconSize)
                        )
                    }
                } else {
                    null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLocked) 0.86f else 0.68f),
                    contentColor = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else roadmapAmberText
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.cardElevationNone),
                border = if (isLocked) BorderStroke(Dimens.borderThin, roadmapMilestoneLockedBorder) else null,
                enabled = !isLocked
            )
        }
    }
}

@Composable
fun RoadmapMilestoneCompactCard(
    milestone: RoadmapMilestoneUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(Dimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconXxl)
                .background(roadmapMilestoneIconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = roadmapAmber,
                modifier = Modifier.size(Dimens.iconXs)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
        ) {
            Text(
                text = stringResource(milestone.titleResId),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = roadmapInk,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.roadmap_search_recent_milestone_project),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfacePlaceholderLight
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val MilestoneDecorIconSize = Dimens.iconFrameSize + Dimens.spacingMd

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCardAvailablePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapMilestoneCard(
                milestone = RoadmapMilestoneUiModel(
                    id = "landing-page",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Available
                ),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCompactCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapSearchCard {
                RoadmapMilestoneCompactCard(
                    milestone = RoadmapMilestoneUiModel(
                        id = "landing-page-compact",
                        titleResId = R.string.roadmap_detail_milestone_landing_title,
                        descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                        state = RoadmapMilestoneState.Available
                    ),
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapMilestoneCardLockedPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            RoadmapMilestoneCard(
                milestone = RoadmapMilestoneUiModel(
                    id = "landing-page-locked",
                    titleResId = R.string.roadmap_detail_milestone_landing_title,
                    descriptionResId = R.string.roadmap_detail_milestone_landing_description,
                    state = RoadmapMilestoneState.Locked
                ),
                onClick = {}
            )
        }
    }
}
