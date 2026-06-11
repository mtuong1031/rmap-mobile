package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapSkeletonBlock
import com.rmap.mobile.core.ui.components.RMapSkeletonCard
import com.rmap.mobile.core.ui.components.rememberRMapSkeletonBrush
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

private const val ExploreSkeletonCategoryColumnCount = 4
private const val ExploreSkeletonCategoryRowCount = 2
private val ExploreSkeletonSearchHeight = 72.dp
private val ExploreSkeletonCategoryIconSize = 72.dp
private val ExploreSkeletonRoadmapHeight = 76.dp

@Composable
fun ExploreContentSkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberRMapSkeletonBrush()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
    ) {
        ExploreCategorySectionSkeleton(brush = brush)
        ExploreRoadmapLibrarySkeleton(brush = brush)
    }
}

@Composable
fun ExploreSearchBarSkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberRMapSkeletonBrush()

    RMapSkeletonCard(
        modifier = modifier
            .fillMaxWidth()
            .height(ExploreSkeletonSearchHeight),
        shape = AppShapes.heroCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingXl, vertical = Dimens.spacingLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSkeletonBlock(
                modifier = Modifier.size(28.dp),
                shape = AppShapes.pill,
                brush = brush
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(24.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}

@Composable
private fun ExploreCategorySectionSkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        ExploreSectionHeaderSkeleton(
            brush = brush,
            modifier = Modifier.padding(horizontal = Dimens.spacingScreenHorizontal)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingScreenHorizontal),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            repeat(ExploreSkeletonCategoryRowCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)
                ) {
                    repeat(ExploreSkeletonCategoryColumnCount) {
                        ExploreCategoryItemSkeleton(
                            brush = brush,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            RMapSkeletonBlock(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(58.dp)
                    .height(6.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}

@Composable
private fun ExploreCategoryItemSkeleton(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier.size(ExploreSkeletonCategoryIconSize),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(14.dp),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.58f)
                .height(12.dp),
            shape = AppShapes.pill,
            brush = brush
        )
    }
}

@Composable
private fun ExploreRoadmapLibrarySkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        ExploreSectionHeaderSkeleton(brush = brush)

        repeat(4) {
            ExploreRoadmapRowSkeleton(brush = brush)
        }
    }
}

@Composable
private fun ExploreSectionHeaderSkeleton(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier
                .width(148.dp)
                .height(24.dp),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(16.dp),
            shape = AppShapes.pill,
            brush = brush
        )
    }
}

@Composable
fun ExploreRoadmapRowSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberRMapSkeletonBrush()
) {
    RMapSkeletonCard(
        modifier = modifier
            .fillMaxWidth()
            .height(ExploreSkeletonRoadmapHeight),
        shape = AppShapes.card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.84f)
                        .height(18.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(14.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
            }

            RMapSkeletonBlock(
                modifier = Modifier.size(24.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}
