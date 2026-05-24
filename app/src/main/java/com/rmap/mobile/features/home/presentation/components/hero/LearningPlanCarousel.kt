package com.rmap.mobile.features.home.presentation.components.hero

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

private val HomeHeroCarouselDotHeight = 6.dp
private val HomeHeroCarouselActiveDotWidth = 18.dp
private val HomeHeroCarouselInactiveDotWidth = 6.dp
private const val HomeHeroCarouselCardWidthFraction = 0.9f

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
internal fun LearningPlanCarousel(
    modifier: Modifier = Modifier,
    roadmaps: List<HomeLearningPlanUiModel>,
    continueText: String,
    nextUnlockPrefix: String,
    sectionHorizontalPadding: Dp,
    onContinueClick: (HomeLearningPlanUiModel) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { roadmaps.size })
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )
    val cardWidth = LocalConfiguration.current.screenWidthDp.dp * HomeHeroCarouselCardWidthFraction
    val titleTextStyle = learningPlanTitleTextStyle()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val titleMaxWidthPx = with(density) {
        (cardWidth - Dimens.spacingLg * 2).roundToPx()
    }
    val titleLineCounts = remember(roadmaps, textMeasurer, titleTextStyle, titleMaxWidthPx) {
        roadmaps.associate { roadmap ->
            val lineCount = textMeasurer.measure(
                text = AnnotatedString(roadmap.skillTitle),
                style = titleTextStyle,
                constraints = Constraints(maxWidth = titleMaxWidthPx)
            ).lineCount
            roadmap.id to lineCount.coerceAtMost(HomeHeroSkillTitleMaxLines)
        }
    }
    val maxTitleLineCount = titleLineCounts.values.maxOrNull() ?: 1
    val titleLineHeight = with(density) { titleTextStyle.lineHeight.toDp() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        HorizontalPager(
            state = pagerState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = sectionHorizontalPadding,
                end = sectionHorizontalPadding
            ),
            pageSize = PageSize.Fixed(cardWidth),
            pageSpacing = Dimens.spacingMd
        ) { page ->
            roadmaps.getOrNull(page)?.let { roadmap ->
                val titleLineCount = titleLineCounts[roadmap.id] ?: maxTitleLineCount
                val titleLineGap = (maxTitleLineCount - titleLineCount).coerceAtLeast(0)
                val progressBottomSpacing = Dimens.spacingXxl + (titleLineHeight.value * titleLineGap).dp

                LearningPlanCard(
                    modifier = Modifier.fillMaxWidth(),
                    roadmap = roadmap,
                    variant = LearningPlanCardVariant.Carousel,
                    continueText = continueText,
                    nextUnlockPrefix = nextUnlockPrefix,
                    onContinueClick = onContinueClick,
                    progressBottomSpacing = progressBottomSpacing
                )
            }
        }

        LearningPlanCarouselDots(
            pageCount = roadmaps.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
        )
    }
}

@Composable
private fun LearningPlanCarouselDots(
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
                HomeHeroCarouselActiveDotWidth
            } else {
                HomeHeroCarouselInactiveDotWidth
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
                    .height(HomeHeroCarouselDotHeight)
                    .clip(AppShapes.pill)
                    .background(dotColor)
            )
        }
    }
}
