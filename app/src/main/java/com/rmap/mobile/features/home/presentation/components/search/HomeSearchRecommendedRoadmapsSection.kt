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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchRecommendedRoadmapsSection(
    title: String,
    roadmaps: List<HomeSearchRoadmapItemUiModel>,
    metadataSeparatorText: String,
    onRoadmapClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    onBookmarkClick: (HomeSearchRoadmapItemUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HomeSearchSectionHeader(title = title)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            roadmaps.forEach { item ->
                HomeSearchRecommendRoadmap(
                    item = item,
                    metadataSeparatorText = metadataSeparatorText,
                    onClick = { onRoadmapClick(item) },
                    onBookmarkClick = { onBookmarkClick(item) }
                )
            }
        }
    }
}

@Composable
fun HomeSearchRecommendRoadmap(
    item: HomeSearchRoadmapItemUiModel,
    metadataSeparatorText: String,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
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

        HomeSearchRoadmapSaveButton(
            isSaved = item.isSaved,
            onClick = onBookmarkClick
        )
    }
}

@Composable
private fun HomeSearchRoadmapSaveButton(
    isSaved: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSaved) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val iconColor = if (isSaved) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val icon = if (isSaved) {
        Icons.Filled.Bookmark
    } else {
        Icons.Outlined.BookmarkBorder
    }

    Box(
        modifier = Modifier
            .size(HomeSearchRoadmapTrailingSize)
            .clip(AppShapes.pill)
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(Dimens.iconSm)
        )
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
private fun HomeSearchRecommendedRoadmapsSectionPreview() {
    RMapTheme {
        HomeSearchRecommendedRoadmapsSection(
            title = "Recommended Roadmaps",
            roadmaps = listOf(
                HomeSearchRoadmapItemUiModel(
                    id = "1",
                    title = "React Fundamentals",
                    categoryLabel = "Web Development",
                    metadataText = "4 weeks",
                    isSaved = true,
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
            onRoadmapClick = {},
            onBookmarkClick = {},
            modifier = Modifier.padding(Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchRoadmapResultCardPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingMd)) {
            HomeSearchRecommendRoadmap(
                item = HomeSearchRoadmapItemUiModel(
                    id = "1",
                    title = "React Fundamentals",
                    categoryLabel = "Web Development",
                    metadataText = "4 weeks",
                    isSaved = true,
                    style = HomeSearchRoadmapItemDefaults.reactStyle()
                ),
                metadataSeparatorText = "•",
                onClick = {},
                onBookmarkClick = {}
            )
        }
    }
}
