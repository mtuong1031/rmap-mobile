package com.rmap.mobile.features.bookmarks.presentation.components.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus

@Composable
fun SavedRoadmapCard(
    item: SavedRoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    val showProgress = item.status == LearningStatus.InProgress && item.progressPercent != null

    RMapCard(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLgPlus)
            ) {
                SavedRoadmapTopRow(
                    item = item,
                    onBookmarkClick = onBookmarkClick
                )

                SavedRoadmapInfo(item = item)

                HorizontalDivider(
                    thickness = Dimens.borderThin,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )

                if (showProgress) {
                    SavedRoadmapProgress(
                        label = item.statusLabel,
                        progressPercent = item.progressPercent
                    )
                }

                SavedRoadmapActionButton(
                    text = item.actionLabel,
                    filled = showProgress,
                    onClick = onActionClick
                )
            }

            SavedRoadmapCardGlow()
        }
    }
}

@Composable
private fun SavedRoadmapCardGlow() {
    Box(
        modifier = Modifier
            .offset(x = Dimens.cardGlowOffsetX, y = Dimens.cardGlowOffsetY)
            .size(Dimens.cardGlowSize)
            .blur(radius = Dimens.cardGlowBlur)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
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
