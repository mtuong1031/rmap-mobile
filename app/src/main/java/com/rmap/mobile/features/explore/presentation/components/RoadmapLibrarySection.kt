package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.features.explore.presentation.viewmodel.ExploreRoadmapCardUiModel

@Composable
fun RoadmapLibrarySection(
    roadmaps: List<ExploreRoadmapCardUiModel>,
    selectedCategoryName: String?,
    totalCount: Int,
    onRoadmapClick: (ExploreRoadmapCardUiModel) -> Unit,
    isFetchingMore: Boolean,
    onSeeMoreClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onSeeLessClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasMore = roadmaps.size < totalCount
    val canToggleAll = totalCount > 10
    val isShowingAll = canToggleAll && !hasMore

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSectionTitle(
                text = stringResource(R.string.explore_roadmap_library_title),
                subtitle = selectedCategoryName?.let {
                    stringResource(R.string.explore_roadmap_library_filtered_subtitle, it)
                } ?: stringResource(R.string.explore_roadmap_library_subtitle),
                modifier = Modifier.weight(1f)
            )

            if (canToggleAll) {
                Text(
                    text = stringResource(
                        if (isShowingAll) {
                            R.string.explore_roadmap_library_see_less
                        } else {
                            R.string.roadmap_see_all
                        }
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = if (isShowingAll) {
                            onSeeLessClick
                        } else {
                            onSeeAllClick
                        }
                    ),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        if (roadmaps.isEmpty()) {
            RoadmapLibraryEmptyState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                roadmaps.forEach { roadmap ->
                    ExploreRoadmapCard(
                        item = roadmap,
                        onClick = { onRoadmapClick(roadmap) }
                    )
                }

                if (isFetchingMore) {
                    repeat(3) {
                        ExploreRoadmapRowSkeleton()
                    }
                } else if (hasMore) {
                    RoadmapLibrarySeeMoreButton(
                        remainingCount = totalCount - roadmaps.size,
                        onClick = onSeeMoreClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreRoadmapCard(
    item: ExploreRoadmapCardUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cardShadow(shape = AppShapes.card)
            .clip(AppShapes.card)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = AppShapes.card
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.categoryLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RoadmapLibrarySeeMoreButton(
    remainingCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(vertical = Dimens.spacingMd),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.explore_roadmap_library_see_more, remainingCount.coerceAtMost(10)),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun RoadmapLibraryEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = AppShapes.heroCard
            )
            .padding(Dimens.spacingXxl),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.explore_roadmap_library_empty),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}
