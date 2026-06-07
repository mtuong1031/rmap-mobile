package com.rmap.mobile.features.home.presentation.components.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

private val HomeRecommendCarouselDotHeight = 6.dp
private val HomeRecommendCarouselActiveDotWidth = 18.dp
private val HomeRecommendCarouselInactiveDotWidth = 6.dp

@Composable
fun HomeRecommendedRoadmapsSection(
    title: String,
    subtitle: String,
    roadmaps: List<HomeRoadmapCardUiModel>,
    metadataSeparatorText: String,
    starterBadgeText: String,
    modifier: Modifier = Modifier,
    onRoadmapClick: ((HomeRoadmapCardUiModel) -> Unit)? = null
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
            item.id to lineCount.coerceAtMost(HomeRoadmapTitleMaxLines)
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            HorizontalPager(
                state = pagerState,
                flingBehavior = flingBehavior,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingScreenHorizontal,
                    end = Dimens.spacingScreenHorizontal
                ),
                pageSize = PageSize.Fixed(HomeRoadmapCardDefaults.CardWidth),
                pageSpacing = Dimens.spacingMd
            ) { page ->
                roadmaps.getOrNull(page)?.let { item ->
                    val titleLineCount = titleLineCounts[item.id] ?: maxTitleLineCount
                    val titleLineGap = (maxTitleLineCount - titleLineCount).coerceAtLeast(0)
                    val metadataBottomSpacing = Dimens.spacingLg + (titleLineHeight.value * titleLineGap).dp

                    HomeRoadmapCard(
                        item = item,
                        metadataSeparatorText = metadataSeparatorText,
                        starterBadgeText = starterBadgeText,
                        modifier = Modifier.fillMaxWidth(),
                        metadataBottomSpacing = metadataBottomSpacing,
                        onClick = onRoadmapClick?.let { callback -> { callback(item) } },
                    )
                }
            }

            HomeRecommendCarouselDots(
                pageCount = roadmaps.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
            )
        }
    }
}

@Composable
private fun HomeRecommendCarouselDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val dotWidth = if (isSelected) {
                HomeRecommendCarouselActiveDotWidth
            } else {
                HomeRecommendCarouselInactiveDotWidth
            }
            val dotColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.spacingXs)
                    .width(dotWidth)
                    .height(HomeRecommendCarouselDotHeight)
                    .clip(AppShapes.pill)
                    .background(dotColor)
            )
        }
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
                starterBadgeText = "Starter",
                roadmaps = listOf(
                    HomeRoadmapCardUiModel(
                        id = "react-fundamentals",
                        categoryLabel = "Web Development",
                        title = "Frontend Starter",
                        nodesText = "24 nodes",
                        durationText = "4 weeks",
                        actionText = "View roadmap",
                        icon = Icons.Outlined.Code,
                        style = HomeRoadmapCardDefaults.webDevelopmentStyle(),
                        isBeginner = true
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
