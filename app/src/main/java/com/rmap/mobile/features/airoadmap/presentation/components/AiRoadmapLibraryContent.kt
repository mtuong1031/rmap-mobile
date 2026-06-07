package com.rmap.mobile.features.airoadmap.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiGeneratedRoadmapUiModel

@Composable
fun AiRoadmapLibraryContent(
    roadmaps: List<AiGeneratedRoadmapUiModel>,
    searchQuery: String,
    searchPlaceholder: String,
    createButtonText: String,
    sectionTitle: String,
    sectionSubtitle: String,
    emptyTitle: String,
    emptyBody: String,
    searchEmptyTitle: String,
    searchEmptyBody: String,
    metadataText: @Composable (lessons: Int, weeks: Int) -> String,
    createdAtText: @Composable (createdDaysAgo: Int) -> String,
    seeAllText: String,
    seeLessText: String,
    seeMoreText: @Composable (remainingCount: Int) -> String,
    exploreButtonText: String,
    totalRoadmapCount: Int,
    hasAnyRoadmaps: Boolean,
    isSearching: Boolean,
    canToggleAll: Boolean,
    isShowingAll: Boolean,
    hasMore: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onExploreClick: () -> Unit,
    onRoadmapClick: (String) -> Unit,
    onSeeMoreClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onSeeLessClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        RMapTextInput(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = searchPlaceholder,
            textStyle = MaterialTheme.typography.bodyMedium,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = RMapTextInputDefaults.colors().placeholderColor
                )
            }
        )

        if (roadmaps.isEmpty()) {
            AiRoadmapEmptyHero(
                title = if (isSearching && hasAnyRoadmaps) searchEmptyTitle else emptyTitle,
                body = if (isSearching && hasAnyRoadmaps) searchEmptyBody else emptyBody,
                createButtonText = createButtonText,
                exploreButtonText = exploreButtonText,
                onCreateClick = onCreateClick,
                onExploreClick = onExploreClick
            )
        } else {
            RMapButton(
                text = createButtonText,
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Route,
                        contentDescription = null
                    )
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
                AiGeneratedRoadmapSectionHeader(
                    title = sectionTitle,
                    subtitle = sectionSubtitle,
                    actionText = if (isShowingAll) seeLessText else seeAllText,
                    isActionVisible = canToggleAll,
                    onActionClick = if (isShowingAll) onSeeLessClick else onSeeAllClick
                )

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                    roadmaps.forEach { roadmap ->
                        AiGeneratedRoadmapListItem(
                            title = roadmap.title,
                            metadata = metadataText(roadmap.lessonsCount, roadmap.durationWeeks),
                            createdAtText = createdAtText(roadmap.createdDaysAgo),
                            onClick = { onRoadmapClick(roadmap.id) }
                        )
                    }
                }

                if (hasMore) {
                    AiRoadmapSeeMoreButton(
                        text = seeMoreText(totalRoadmapCount - roadmaps.size),
                        onClick = onSeeMoreClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AiGeneratedRoadmapSectionHeader(
    title: String,
    subtitle: String,
    actionText: String,
    isActionVisible: Boolean,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RMapSectionTitle(
            text = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f)
        )

        if (isActionVisible) {
            Text(
                text = actionText,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onActionClick
                ),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

private val AiRoadmapEmptyContentMaxWidth = 282.dp
private val AiRoadmapEmptyIconContainerShape = RoundedCornerShape(24.dp)
private val AiRoadmapEmptyMinHeight = 420.dp
private val AiRoadmapEmptyMaxHeight = 520.dp
private const val AiRoadmapEmptyHeightRatio = 1.42f

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiRoadmapEmptyHero(
    title: String,
    body: String,
    createButtonText: String,
    exploreButtonText: String,
    onCreateClick: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val horizontalPadding = if (maxWidth < 320.dp) {
            Dimens.spacingXxl
        } else {
            Dimens.spacingHuge
        }
        val cardMinHeight = (maxWidth * AiRoadmapEmptyHeightRatio).coerceIn(
            minimumValue = AiRoadmapEmptyMinHeight,
            maximumValue = AiRoadmapEmptyMaxHeight
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = cardMinHeight)
        ) {
            RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = Dimens.spacingHuge
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = AiRoadmapEmptyContentMaxWidth)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .sizeIn(
                                minWidth = Dimens.controlXl + Dimens.spacingLg,
                                minHeight = Dimens.controlXl + Dimens.spacingLg
                            )
                            .clip(AiRoadmapEmptyIconContainerShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.iconXxl)
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(top = Dimens.spacingXxl)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(top = Dimens.spacingSm)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = Dimens.spacingHuge)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                    ) {
                        RMapButton(
                            text = createButtonText,
                            onClick = onCreateClick,
                            modifier = Modifier.fillMaxWidth(),
                            variant = RMapButtonVariant.Primary,
                            size = RMapButtonSize.Large,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null
                                )
                            }
                        )
                        RMapButton(
                            text = exploreButtonText,
                            onClick = onExploreClick,
                            modifier = Modifier.fillMaxWidth(),
                            variant = RMapButtonVariant.Secondary,
                            size = RMapButtonSize.Large
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiRoadmapSeeMoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun AiGeneratedRoadmapListItem(
    title: String,
    metadata: String,
    createdAtText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Text(
                    text = metadata,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(Dimens.iconSm)
                )
                Text(
                    text = createdAtText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.size(Dimens.spacingXs))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibraryContentPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibraryContent(
            roadmaps = AiRoadmapPreviewData.generatedRoadmaps,
            searchQuery = "",
            searchPlaceholder = "Search AI roadmaps...",
            createButtonText = "Create roadmap with AI",
            sectionTitle = "Generated roadmaps",
            sectionSubtitle = "Roadmaps created from your goals and pace.",
            emptyTitle = "No AI roadmaps yet",
            emptyBody = "Create your first personalized roadmap from your topic, deadline, and learning pace.",
            searchEmptyTitle = "No matching roadmaps",
            searchEmptyBody = "Try a different keyword or create a new roadmap from your current goal.",
            metadataText = { lessons, weeks -> "$lessons lessons, $weeks weeks" },
            createdAtText = { daysAgo -> if (daysAgo == 0) "Generated today" else "$daysAgo days ago" },
            seeAllText = "SEE ALL",
            seeLessText = "SEE LESS",
            seeMoreText = { remainingCount -> "See more $remainingCount" },
            exploreButtonText = "Explore roadmaps",
            totalRoadmapCount = AiRoadmapPreviewData.generatedRoadmaps.size + 2,
            hasAnyRoadmaps = true,
            isSearching = false,
            canToggleAll = true,
            isShowingAll = false,
            hasMore = true,
            onSearchQueryChange = {},
            onCreateClick = {},
            onExploreClick = {},
            onRoadmapClick = {},
            onSeeMoreClick = {},
            onSeeAllClick = {},
            onSeeLessClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Library Content - Empty", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibraryContentEmptyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibraryContent(
            roadmaps = emptyList(),
            searchQuery = "",
            searchPlaceholder = "Search AI roadmaps...",
            createButtonText = "Create roadmap with AI",
            sectionTitle = "Generated roadmaps",
            sectionSubtitle = "Roadmaps created from your goals and pace.",
            emptyTitle = "No AI roadmaps yet",
            emptyBody = "Create your first personalized roadmap from your topic, deadline, and learning pace.",
            searchEmptyTitle = "No matching roadmaps",
            searchEmptyBody = "Try a different keyword or create a new roadmap from your current goal.",
            metadataText = { lessons, weeks -> "$lessons lessons, $weeks weeks" },
            createdAtText = { daysAgo -> if (daysAgo == 0) "Generated today" else "$daysAgo days ago" },
            seeAllText = "SEE ALL",
            seeLessText = "SEE LESS",
            seeMoreText = { remainingCount -> "See more $remainingCount" },
            exploreButtonText = "Explore roadmaps",
            totalRoadmapCount = 0,
            hasAnyRoadmaps = false,
            isSearching = false,
            canToggleAll = false,
            isShowingAll = false,
            hasMore = false,
            onSearchQueryChange = {},
            onCreateClick = {},
            onExploreClick = {},
            onRoadmapClick = {},
            onSeeMoreClick = {},
            onSeeAllClick = {},
            onSeeLessClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Library Content - Search Empty", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibraryContentSearchEmptyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibraryContent(
            roadmaps = emptyList(),
            searchQuery = "Kotlin",
            searchPlaceholder = "Search AI roadmaps...",
            createButtonText = "Create roadmap with AI",
            sectionTitle = "Generated roadmaps",
            sectionSubtitle = "Roadmaps created from your goals and pace.",
            emptyTitle = "No AI roadmaps yet",
            emptyBody = "Create your first personalized roadmap from your topic, deadline, and learning pace.",
            searchEmptyTitle = "No matching roadmaps",
            searchEmptyBody = "Try a different keyword or create a new roadmap from your current goal.",
            metadataText = { lessons, weeks -> "$lessons lessons, $weeks weeks" },
            createdAtText = { daysAgo -> if (daysAgo == 0) "Generated today" else "$daysAgo days ago" },
            seeAllText = "SEE ALL",
            seeLessText = "SEE LESS",
            seeMoreText = { remainingCount -> "See more $remainingCount" },
            exploreButtonText = "Explore roadmaps",
            totalRoadmapCount = AiRoadmapPreviewData.generatedRoadmaps.size,
            hasAnyRoadmaps = true,
            isSearching = true,
            canToggleAll = false,
            isShowingAll = false,
            hasMore = false,
            onSearchQueryChange = {},
            onCreateClick = {},
            onExploreClick = {},
            onRoadmapClick = {},
            onSeeMoreClick = {},
            onSeeAllClick = {},
            onSeeLessClick = {}
        )
    }
}
