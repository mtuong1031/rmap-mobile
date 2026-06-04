package com.rmap.mobile.features.profile.presentation.components.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionCard
import com.rmap.mobile.features.profile.presentation.components.common.ProfileSectionHeader

@Composable
fun ActiveRoadmapsCard(
    title: String,
    subtitle: String,
    showAllLabel: String,
    collapseLabel: String,
    emptyMessage: String,
    items: List<ProfileRoadmapProgressUiModel>,
    isAllRoadmapsVisible: Boolean,
    onShowAllRoadmapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasOverflow = items.size > MAX_COLLAPSED_ROADMAPS
    val visibleItems = if (isAllRoadmapsVisible || !hasOverflow) {
        items
    } else {
        items.take(MAX_COLLAPSED_ROADMAPS)
    }

    ProfileSectionCard(modifier = modifier) {
        ProfileSectionHeader(title = title, subtitle = subtitle)

        if (visibleItems.isEmpty()) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)) {
                visibleItems.forEach { item ->
                    RoadmapProgressRow(item = item)
                }
            }
        }

        AnimatedVisibility(
            visible = hasOverflow,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.chip)
                    .clickable(onClick = onShowAllRoadmapsClick)
                    .padding(vertical = Dimens.spacingSm),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAllRoadmapsVisible) collapseLabel else showAllLabel,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            }
        }
    }
}

@Composable
private fun RoadmapProgressRow(item: ProfileRoadmapProgressUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.controlSm)
                    .clip(AppShapes.iconContainerLarge)
                    .background(item.accentContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.accentColor,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                Text(
                    text = item.remainingTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProfileProgress(
                progress = item.progressPercent / 100f,
                color = item.accentColor,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.profile_progress_percent_short, item.progressPercent),
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = item.accentColor,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

@Composable
private fun LinearProfileProgress(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(AppShapes.pill)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(AppShapes.pill)
                .background(
                    Brush.horizontalGradient(
                        listOf(color, color.copy(alpha = 0.75f))
                    )
                )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ActiveRoadmapsCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ActiveRoadmapsCard(
            title = "Active Roadmaps",
            subtitle = "2 active paths",
            showAllLabel = "Show all 4 roadmaps",
            collapseLabel = "Show less",
            emptyMessage = "No active roadmaps yet",
            items = listOf(
                ProfileRoadmapProgressUiModel(
                    id = "frontend",
                    title = "Frontend Fresher",
                    remainingTime = "4 months left",
                    progressPercent = 75,
                    icon = Icons.Outlined.TrackChanges,
                    accentColor = Color(0xFF2B7FFF),
                    accentContainerColor = Color(0xFFF0F5FE)
                ),
                ProfileRoadmapProgressUiModel(
                    id = "uiux",
                    title = "UI/UX Master",
                    remainingTime = "2 months left",
                    progressPercent = 32,
                    icon = Icons.Outlined.Palette,
                    accentColor = Color(0xFF8B5CF6),
                    accentContainerColor = Color(0xFFF3F0FF)
                )
            ),
            isAllRoadmapsVisible = true,
            onShowAllRoadmapsClick = {},
            modifier = Modifier.padding(Dimens.spacingXl)
        )
    }
}

private const val MAX_COLLAPSED_ROADMAPS = 3
