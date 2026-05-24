package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnPrimaryContainerLight
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SecondaryLight
import com.rmap.mobile.core.ui.theme.SurfaceContainerHighLight
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus

typealias BookmarkRoadmapCardUiModel = SavedRoadmapCardUiModel

private val SavedRoadmapCardShape = AppShapes.card
private val SavedRoadmapCardShadowColor = Color(0x1A000000)

data class SavedRoadmapCardUiModel(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val categoryIcon: ImageVector,
    val categoryBackgroundColor: Color = PrimaryContainerLight,
    val categoryContentColor: Color = OnPrimaryContainerLight,
    val nodesLabel: String,
    val durationLabel: String,
    val savedAtLabel: String,
    val actionLabel: String,
    val status: LearningStatus = LearningStatus.NotStarted,
    val statusLabel: String = "",
    val progressPercent: Int? = null
)

@Composable
fun SavedRoadmapCard(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    val showProgress = item.status == LearningStatus.InProgress && item.progressPercent != null

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Dimens.cardElevationXs,
                shape = SavedRoadmapCardShape,
                ambientColor = SavedRoadmapCardShadowColor,
                spotColor = SavedRoadmapCardShadowColor
            ),
        shape = SavedRoadmapCardShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = Dimens.borderThin,
            color = SurfaceContainerHighLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
        ) {
            TopRow(
                item = item,
                onBookmarkClick = onBookmarkClick,
                onShareClick = onShareClick
            )

            RoadmapInfo(item = item)

            if (showProgress) {
                ProgressSection(
                    label = item.statusLabel,
                    progressPercent = item.progressPercent
                )
            }

            ActionButton(
                text = item.actionLabel,
                filled = showProgress,
                onClick = onActionClick
            )
        }
    }
}

@Composable
private fun TopRow(
    item: SavedRoadmapCardUiModel,
    onBookmarkClick: (() -> Unit)?,
    onShareClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryBadge(
            item = item,
            modifier = Modifier.weight(1f, fill = false)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconAction(
                icon = Icons.Outlined.Bookmark,
                tint = PrimaryLight,
                onClick = onBookmarkClick
            )
            IconAction(
                icon = Icons.Outlined.MoreVert,
                tint = OnSurfacePlaceholderLight,
                onClick = onShareClick
            )
        }
    }
}

@Composable
private fun CategoryBadge(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .widthIn(min = Dimens.controlSm)
            .clip(AppShapes.chip)
            .background(item.categoryBackgroundColor)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.categoryIcon,
            contentDescription = null,
            tint = item.categoryContentColor,
            modifier = Modifier.size(Dimens.iconSm)
        )
        Text(
            text = item.categoryLabel,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            ),
            color = item.categoryContentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun IconAction(
    icon: ImageVector,
    tint: Color,
    onClick: (() -> Unit)?
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .sizeIn(
                minWidth = Dimens.controlMd,
                minHeight = Dimens.controlMd
            )
            .clip(AppShapes.pill)
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
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(Dimens.iconSmPlus)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoadmapInfo(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 22.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        RoadmapMetadata(item = item)

        MetadataItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = SecondaryLight,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            },
            text = item.savedAtLabel
        )
    }
}

@Composable
private fun RoadmapMetadata(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 240.dp) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
            ) {
                MetadataItem(
                    icon = { NodesIcon() },
                    text = item.nodesLabel
                )
                MetadataItem(
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
                MetadataItem(
                    icon = { NodesIcon() },
                    text = item.nodesLabel
                )
                MetadataItem(
                    icon = { ClockIcon() },
                    text = item.durationLabel
                )
            }
        }
    }
}

@Composable
private fun MetadataItem(
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
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp
            ),
            color = SecondaryLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProgressSection(
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
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp
                ),
                color = PrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.End
                ),
                color = OnSurfacePlaceholderLight,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.spacingXsPlus)
                .clip(AppShapes.pill)
                .background(PrimaryContainerLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(Dimens.spacingXsPlus)
                    .clip(AppShapes.pill)
                    .background(PrimaryLight)
            )
        }
    }
}

@Composable
private fun ActionButton(
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
    Canvas(modifier = Modifier.size(Dimens.iconXs)) {
        val strokeWidth = 1.3.dp.toPx()
        val color = SecondaryLight
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
    Canvas(modifier = Modifier.size(Dimens.iconXs)) {
        val strokeWidth = 1.3.dp.toPx()
        val color = SecondaryLight
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

@Composable
fun BookmarkRoadmapCard(
    item: BookmarkRoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    SavedRoadmapCard(
        item = item,
        modifier = modifier,
        onActionClick = onActionClick,
        onShareClick = onShareClick,
        onBookmarkClick = onBookmarkClick
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun SavedRoadmapCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        SavedRoadmapCard(
            item = SavedRoadmapCardUiModel(
                id = "full-stack-development",
                title = "Full Stack Development",
                categoryLabel = "Web Development",
                categoryIcon = Icons.Outlined.Code,
                nodesLabel = "64 Nodes",
                durationLabel = "8 months",
                savedAtLabel = "Last saved yesterday",
                actionLabel = "Continue",
                status = LearningStatus.InProgress,
                statusLabel = "In Progress",
                progressPercent = 45
            ),
            modifier = Modifier.padding(Dimens.spacingLg),
            onActionClick = {},
            onBookmarkClick = {},
            onShareClick = {}
        )
    }
}
