package com.rmap.mobile.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeRoadmapCardWidth = 260.dp
private val HomeRoadmapCardContentPadding = 20.dp
private val HomeRoadmapIconContainerSize = 56.dp
private val HomeRoadmapIconSize = 26.dp
private val HomeRoadmapBookmarkButtonSize = 32.dp
private val HomeRoadmapBookmarkIconSize = 16.dp
private val HomeRoadmapMetaIconSize = 14.dp
private val HomeRoadmapCtaIconSize = 14.dp
private val HomeRoadmapBadgeShape = RoundedCornerShape(6.dp)

@Immutable
data class HomeRoadmapCardStyle(
    val iconContainerColor: Color,
    val iconColor: Color,
    val badgeContainerColor: Color,
    val badgeContentColor: Color
)

@Immutable
data class HomeRoadmapCardUiModel(
    val id: String,
    val categoryLabel: String,
    val title: String,
    val nodesText: String,
    val durationText: String,
    val actionText: String,
    val icon: ImageVector,
    val style: HomeRoadmapCardStyle
)

object HomeRoadmapCardDefaults {
    val CardWidth: Dp = HomeRoadmapCardWidth
    val CardShape = AppShapes.heroCard
    val IconContainerShape = AppShapes.button

    @Composable
    fun webDevelopmentStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconColor = MaterialTheme.colorScheme.primary,
            badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
            badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    @Composable
    fun interviewStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = MaterialTheme.colorScheme.tertiary,
            badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
            badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    fun designStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = Color(0xFFFDF2F8),
            iconColor = Color(0xFFE60076),
            badgeContainerColor = Color(0xFFFDF2F8),
            badgeContentColor = Color(0xFFE60076)
        )
    }
}

@Composable
fun HomeRecommendedRoadmapsSection(
    title: String,
    subtitle: String,
    roadmaps: List<HomeRoadmapCardUiModel>,
    metadataSeparatorText: String,
    modifier: Modifier = Modifier,
    bookmarkContentDescription: String? = null,
    onRoadmapClick: ((HomeRoadmapCardUiModel) -> Unit)? = null,
    onBookmarkClick: ((HomeRoadmapCardUiModel) -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = { roadmaps.size })
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )
    val titleTextStyle = homeRoadmapTitleTextStyle()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val titleMaxWidthPx = with(density) {
        (HomeRoadmapCardDefaults.CardWidth - HomeRoadmapCardContentPadding * 2).roundToPx()
    }
    val titleLineCounts = remember(roadmaps, textMeasurer, titleTextStyle, titleMaxWidthPx) {
        roadmaps.associate { item ->
            val lineCount = textMeasurer.measure(
                text = AnnotatedString(item.title),
                style = titleTextStyle,
                constraints = Constraints(maxWidth = titleMaxWidthPx)
            ).lineCount
            item.id to lineCount
        }
    }
    val maxTitleLineCount = titleLineCounts.values.maxOrNull() ?: 1
    val titleLineHeight = with(density) { titleTextStyle.lineHeight.toDp() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(
            text = title,
            subtitle = subtitle,
            modifier = Modifier.padding(horizontal = Dimens.spacingLg)
        )

        HorizontalPager(
            state = pagerState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = Dimens.spacingScreenHorizontal,
                end = Dimens.spacingScreenHorizontal
            ),
            pageSize = PageSize.Fixed(HomeRoadmapCardDefaults.CardWidth),
            pageSpacing = Dimens.spacingLg
        ) { page ->
            roadmaps.getOrNull(page)?.let { item ->
                val titleLineCount = titleLineCounts[item.id] ?: maxTitleLineCount
                val titleLineGap = (maxTitleLineCount - titleLineCount).coerceAtLeast(0)
                val metadataBottomSpacing = Dimens.spacingLg + (titleLineHeight.value * titleLineGap).dp

                HomeRoadmapCard(
                    item = item,
                    metadataSeparatorText = metadataSeparatorText,
                    metadataBottomSpacing = metadataBottomSpacing,
                    bookmarkContentDescription = bookmarkContentDescription,
                    onClick = onRoadmapClick?.let { callback ->
                        { callback(item) }
                    },
                    onBookmarkClick = onBookmarkClick?.let { callback ->
                        { callback(item) }
                    },
                )
            }
        }
    }
}

@Composable
fun HomeRoadmapCard(
    item: HomeRoadmapCardUiModel,
    metadataSeparatorText: String,
    modifier: Modifier = Modifier,
    metadataBottomSpacing: Dp = Dimens.spacingLg,
    bookmarkContentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    onBookmarkClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .width(HomeRoadmapCardDefaults.CardWidth)
            .cardShadow(shape = HomeRoadmapCardDefaults.CardShape)
            .clip(HomeRoadmapCardDefaults.CardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = HomeRoadmapCardDefaults.CardShape
            )
            .then(clickModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = HomeRoadmapCardContentPadding,
                    top = HomeRoadmapCardContentPadding,
                    end = HomeRoadmapCardContentPadding,
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                HomeRoadmapIconTile(
                    icon = item.icon,
                    style = item.style
                )

                HomeRoadmapBookmarkButton(
                    contentDescription = bookmarkContentDescription,
                    onClick = onBookmarkClick
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacingLgPlus))

            HomeRoadmapBadge(
                text = item.categoryLabel,
                style = item.style
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSmPlus))

            Text(
                text = item.title,
                style = homeRoadmapTitleTextStyle()
            )

            Spacer(modifier = Modifier.height(Dimens.spacingMd))

            HomeRoadmapMetadata(
                nodesText = item.nodesText,
                separatorText = metadataSeparatorText,
                durationText = item.durationText
            )

            Spacer(modifier = Modifier.height(metadataBottomSpacing))
        }

        HorizontalDivider(
            thickness = Dimens.borderThin,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(
                start = HomeRoadmapCardContentPadding,
                end = HomeRoadmapCardContentPadding
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(
                    start = HomeRoadmapCardContentPadding,
                    end = HomeRoadmapCardContentPadding,
                    top = Dimens.spacingMdPlus,
                    bottom = HomeRoadmapCardContentPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.actionText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(HomeRoadmapCtaIconSize)
            )
        }
    }
}

@Composable
private fun homeRoadmapTitleTextStyle(): TextStyle {
    return MaterialTheme.typography.titleLarge.copy(
        fontSize = 18.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun HomeRoadmapIconTile(
    icon: ImageVector,
    style: HomeRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .size(HomeRoadmapIconContainerSize)
            .background(
                color = style.iconContainerColor,
                shape = HomeRoadmapCardDefaults.IconContainerShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = style.iconColor,
            modifier = Modifier.size(HomeRoadmapIconSize)
        )
    }
}

@Composable
private fun HomeRoadmapBookmarkButton(
    contentDescription: String?,
    onClick: (() -> Unit)?
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(HomeRoadmapBookmarkButtonSize)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = AppShapes.pill
            )
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.BookmarkBorder,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(HomeRoadmapBookmarkIconSize)
        )
    }
}

@Composable
private fun HomeRoadmapBadge(
    text: String,
    style: HomeRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .background(
                color = style.badgeContainerColor,
                shape = HomeRoadmapBadgeShape
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingMicro),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = style.badgeContentColor
            )
        )
    }
}

@Composable
private fun HomeRoadmapMetadata(
    nodesText: String,
    separatorText: String,
    durationText: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(HomeRoadmapMetaIconSize)
        )

        Text(
            text = nodesText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Text(
            text = separatorText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Text(
            text = durationText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeRecommendedRoadmapsSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeRecommendedRoadmapsSection(
                title = "Recommended for your goal",
                subtitle = "Recommended because you're learning Frontend Pro",
                metadataSeparatorText = "•",
                roadmaps = listOf(
                    HomeRoadmapCardUiModel(
                        id = "react-fundamentals",
                        categoryLabel = "Web Development",
                        title = "Cristiano Ronaldo dos santos",
                        nodesText = "24 nodes",
                        durationText = "4 weeks",
                        actionText = "View roadmap",
                        icon = Icons.Outlined.Code,
                        style = HomeRoadmapCardDefaults.webDevelopmentStyle()
                    ),
                    HomeRoadmapCardUiModel(
                        id = "frontend-interview",
                        categoryLabel = "Web Development",
                        title = "Frontend Interview",
                        nodesText = "12 nodes",
                        durationText = "2 weeks",
                        actionText = "View roadmap",
                        icon = Icons.Outlined.TrackChanges,
                        style = HomeRoadmapCardDefaults.interviewStyle()
                    ),
                    HomeRoadmapCardUiModel(
                        id = "css-architecture",
                        categoryLabel = "Design",
                        title = "CSS Architecture",
                        nodesText = "18 nodes",
                        durationText = "3 weeks",
                        actionText = "View roadmap",
                        icon = Icons.Outlined.Palette,
                        style = HomeRoadmapCardDefaults.designStyle()
                    )
                )
            )
        }
    }
}
