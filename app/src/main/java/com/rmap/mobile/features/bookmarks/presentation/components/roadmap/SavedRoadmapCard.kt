package com.rmap.mobile.features.bookmarks.presentation.components.roadmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
        ) {
            SavedRoadmapTopRow(
                item = item,
                onBookmarkClick = onBookmarkClick
            )

            SavedRoadmapInfo(item = item)

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
