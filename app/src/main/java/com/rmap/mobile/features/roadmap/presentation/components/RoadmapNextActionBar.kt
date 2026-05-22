package com.rmap.mobile.features.roadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

@Composable
fun RoadmapNextActionBar(
    nextActionTitle: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        borderColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
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
                        color = OnSurfacePlaceholderLight,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.roadmap_detail_next_action_continue, nextActionTitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = roadmapInk,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacingMd))

            RMapButton(
                text = stringResource(R.string.roadmap_detail_action_continue),
                onClick = onContinueClick,
                modifier = Modifier.width(NextActionButtonWidth),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.XSmall,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(RMapButtonSize.XSmall.iconSize)
                    )
                }
            )
        }
    }
}

private val NextActionButtonWidth = Dimens.recommendedCardGlowOffset + Dimens.categoryIconContainerSize
