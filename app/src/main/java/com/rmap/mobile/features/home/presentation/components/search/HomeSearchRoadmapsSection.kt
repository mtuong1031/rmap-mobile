package com.rmap.mobile.features.home.presentation.components.search

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchRoadmapsSection(
    title: String,
    roadmaps: List<HomeSearchRoadmapItemUiModel>,
    metadataSeparatorText: String,
    seeMoreText: String,
    resultCountText: String,
    canSeeMore: Boolean,
    isLoadingMore: Boolean,
    onRoadmapClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HomeSearchSectionHeader(
            title = title,
            resultCountText = resultCountText
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            roadmaps.forEach { item ->
                HomeSearchRoadmapCard(
                    item = item,
                    metadataSeparatorText = metadataSeparatorText,
                    onClick = { onRoadmapClick(item) }
                )
            }

            if (canSeeMore) {
                RMapButton(
                    text = seeMoreText,
                    onClick = onSeeMoreClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = RMapButtonVariant.Neutral,
                    size = RMapButtonSize.Medium,
                    enabled = !isLoadingMore
                )
            }
        }
    }
}

@Composable
fun HomeSearchRoadmapCard(
    item: HomeSearchRoadmapItemUiModel,
    metadataSeparatorText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cardShadow(shape = HomeSearchRoadmapCardShape)
            .clip(HomeSearchRoadmapCardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = HomeSearchRoadmapCardShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(Dimens.spacingLgPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeSearchRoadmapLeadingIcon(item = item)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Text(
                text = item.title,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.categoryLabel.uppercase(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = item.style.categoryColor
                    )
                )

                Text(
                    text = metadataSeparatorText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )

                Text(
                    text = item.metadataText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }

    }
}

@Composable
private fun HomeSearchRoadmapLeadingIcon(
    item: HomeSearchRoadmapItemUiModel
) {
    Box(
        modifier = Modifier
            .size(HomeSearchRoadmapIconSize)
            .clip(AppShapes.button)
            .background(item.style.iconContainerColor),
        contentAlignment = Alignment.Center
    ) {
        if (item.leadingIcon != null) {
            Icon(
                imageVector = item.leadingIcon,
                contentDescription = null,
                tint = item.style.iconContentColor,
                modifier = Modifier.size(Dimens.iconXl)
            )
        } else {
            Text(
                text = item.leadingText.orEmpty(),
                maxLines = 1,
                modifier = Modifier.widthIn(max = 38.dp),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = item.style.iconContentColor
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun HomeSearchRoadmapsSectionPreview() {
    RMapTheme {
        HomeSearchRoadmapsSection(
            title = "Roadmaps",
            roadmaps = listOf(
                HomeSearchRoadmapItemUiModel(
                    id = "1",
                    title = "React Fundamentals",
                    categoryLabel = "Web Development",
                    metadataText = "4 weeks",
                    style = HomeSearchRoadmapItemDefaults.reactStyle()
                ),
                HomeSearchRoadmapItemUiModel(
                    id = "2",
                    title = "Frontend Starter",
                    categoryLabel = "Web Development",
                    metadataText = "3 weeks",
                    leadingText = "FE",
                    style = HomeSearchRoadmapItemDefaults.starterStyle()
                )
            ),
            metadataSeparatorText = "•",
            seeMoreText = "SEE MORE",
            resultCountText = "2 roadmaps founds",
            canSeeMore = true,
            isLoadingMore = false,
            onRoadmapClick = {},
            onSeeMoreClick = {},
            modifier = Modifier.padding(Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchRoadmapResultCardPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingMd)) {
            HomeSearchRoadmapCard(
                item = HomeSearchRoadmapItemUiModel(
                    id = "1",
                    title = "React Fundamentals",
                    categoryLabel = "Web Development",
                    metadataText = "4 weeks",
                    style = HomeSearchRoadmapItemDefaults.reactStyle()
                ),
                metadataSeparatorText = "•",
                onClick = {}
            )
        }
    }
}
