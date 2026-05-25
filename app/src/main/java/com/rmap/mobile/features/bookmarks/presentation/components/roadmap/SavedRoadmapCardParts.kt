package com.rmap.mobile.features.bookmarks.presentation.components.roadmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTextStyles
import com.rmap.mobile.features.bookmarks.presentation.components.colors

@Composable
internal fun SavedRoadmapTopRow(
    item: SavedRoadmapCardUiModel,
    onBookmarkClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            SavedRoadmapCategoryBadge(item = item)
        }

        SavedRoadmapIconAction(
            icon = Icons.Outlined.Bookmark,
            tint = MaterialTheme.colorScheme.primary,
            onClick = onBookmarkClick
        )
    }
}

@Composable
private fun SavedRoadmapCategoryBadge(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    val categoryColors = item.categoryStyle.colors()

    Row(
        modifier = modifier
            .widthIn(min = Dimens.controlSm)
            .clip(AppShapes.chip)
            .background(categoryColors.containerColor)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.categoryIcon,
            contentDescription = null,
            tint = categoryColors.contentColor,
            modifier = Modifier.size(Dimens.iconSm)
        )
        Text(
            text = item.categoryLabel,
            style = BookmarkTextStyles.badge,
            color = categoryColors.contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SavedRoadmapIconAction(
    icon: ImageVector,
    tint: Color,
    onClick: (() -> Unit)?
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(Dimens.controlMd)
            .clip(AppShapes.pill)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.content_description_bookmark),
            tint = tint,
            modifier = Modifier.size(Dimens.iconMd)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SavedRoadmapInfo(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = item.title,
            style = BookmarkTextStyles.cardTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        SavedRoadmapMetadata(item = item)

        SavedRoadmapMetadataItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            },
            text = item.savedAtLabel
        )
    }
}

@Composable
private fun SavedRoadmapMetadata(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 240.dp) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
            ) {
                SavedRoadmapMetadataItem(
                    icon = { NodesIcon() },
                    text = item.nodesLabel
                )
                SavedRoadmapMetadataItem(
                    icon = { ClockIcon() },
                    text = item.durationLabel
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SavedRoadmapMetadataItem(
                    icon = { NodesIcon() },
                    text = item.nodesLabel
                )
                SavedRoadmapMetadataItem(
                    icon = { ClockIcon() },
                    text = item.durationLabel
                )
            }
        }
    }
}

@Composable
private fun SavedRoadmapMetadataItem(
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            text = text,
            style = BookmarkTextStyles.metadata,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun SavedRoadmapProgress(
    label: String,
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    val progress = (progressPercent / 100f).coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = BookmarkTextStyles.progressLabel,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$progressPercent%",
                style = BookmarkTextStyles.progressLabel.copy(
                    textAlign = TextAlign.End
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.spacingXsPlus)
                .clip(AppShapes.pill)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(Dimens.spacingXsPlus)
                    .clip(AppShapes.pill)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
internal fun SavedRoadmapActionButton(
    text: String,
    filled: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    RMapButton(
        text = text,
        onClick = { onClick?.invoke() },
        modifier = modifier.fillMaxWidth(),
        variant = if (filled) RMapButtonVariant.Primary else RMapButtonVariant.Outline,
        size = RMapButtonSize.Medium
    )
}

@Composable
private fun NodesIcon() {
    val color = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.size(Dimens.iconXs)) {
        val strokeWidth = 1.3.dp.toPx()
        drawRect(
            color = color,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, strokeWidth),
            end = Offset(size.width * 0.5f, size.height - strokeWidth),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(strokeWidth, size.height * 0.5f),
            end = Offset(size.width - strokeWidth, size.height * 0.5f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
private fun ClockIcon() {
    val color = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.size(Dimens.iconXs)) {
        val strokeWidth = 1.3.dp.toPx()
        drawCircle(
            color = color,
            radius = (size.minDimension - strokeWidth * 2) / 2,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - size.height * 0.23f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + size.width * 0.18f, center.y + size.height * 0.12f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
