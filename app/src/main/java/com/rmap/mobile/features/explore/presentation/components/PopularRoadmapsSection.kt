package com.rmap.mobile.features.explore.presentation.components

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCard
import com.rmap.mobile.features.home.presentation.components.trending.TrendingRoadmapCardUiModel

private val PopularRoadmapCarouselDotHeight = 6.dp
private val PopularRoadmapCarouselActiveDotWidth = 18.dp
private val PopularRoadmapCarouselInactiveDotWidth = 6.dp
private const val PopularRoadmapCarouselCardWidthFraction = 0.9f

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun PopularRoadmapsSection(
    roadmaps: List<TrendingRoadmapCardUiModel>,
    onRoadmapClick: (TrendingRoadmapCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { roadmaps.size })
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )
    val cardWidth = LocalConfiguration.current.screenWidthDp.dp * PopularRoadmapCarouselCardWidthFraction

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(
            text = stringResource(R.string.explore_popular_title),
            subtitle = stringResource(R.string.explore_popular_subtitle),
            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
        )

        HorizontalPager(
            state = pagerState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = Dimens.spacingScreenHorizontal,
                end = Dimens.spacingScreenHorizontal
            ),
            pageSize = PageSize.Fixed(cardWidth),
            pageSpacing = Dimens.spacingMd
        ) { page ->
            roadmaps.getOrNull(page)?.let { roadmap ->
                TrendingRoadmapCard(
                    item = roadmap,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onRoadmapClick(roadmap) }
                )
            }
        }

        PopularRoadmapCarouselDots(
            pageCount = roadmaps.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
        )
    }
}

@Composable
private fun PopularRoadmapCarouselDots(
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
                PopularRoadmapCarouselActiveDotWidth
            } else {
                PopularRoadmapCarouselInactiveDotWidth
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
                    .height(PopularRoadmapCarouselDotHeight)
                    .clip(AppShapes.pill)
                    .background(dotColor)
            )
        }
    }
}
