package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapSkeletonBlock
import com.rmap.mobile.core.ui.components.RMapSkeletonCard
import com.rmap.mobile.core.ui.components.rememberRMapSkeletonBrush
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

private val AiRoadmapSkeletonSearchHeight = 72.dp
private val AiRoadmapSkeletonCreateButtonHeight = 56.dp
private val AiRoadmapSkeletonRowHeight = 106.dp

@Composable
fun AiRoadmapLibrarySkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberRMapSkeletonBrush()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        AiRoadmapSearchSkeleton(brush = brush)
        AiRoadmapCreateButtonSkeleton(brush = brush)
        AiRoadmapGeneratedListSkeleton(brush = brush)
    }
}

@Preview(showBackground = true, name = "AI Library Skeleton", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapLibrarySkeletonPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapLibrarySkeleton(
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}

@Composable
private fun AiRoadmapSearchSkeleton(
    brush: Brush
) {
    RMapSkeletonCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(AiRoadmapSkeletonSearchHeight),
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
                    .fillMaxWidth(0.78f)
                    .height(22.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}

@Composable
private fun AiRoadmapCreateButtonSkeleton(
    brush: Brush
) {
    RMapSkeletonBlock(
        modifier = Modifier
            .fillMaxWidth()
            .height(AiRoadmapSkeletonCreateButtonHeight),
        shape = AppShapes.button,
        brush = brush
    )
}

@Composable
private fun AiRoadmapGeneratedListSkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        AiRoadmapSectionHeaderSkeleton(brush = brush)

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
            repeat(4) {
                AiRoadmapGeneratedRowSkeleton(brush = brush)
            }
        }
    }
}

@Composable
private fun AiRoadmapSectionHeaderSkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier
                .width(174.dp)
                .height(24.dp),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(16.dp),
            shape = AppShapes.pill,
            brush = brush
        )
    }
}

@Composable
private fun AiRoadmapGeneratedRowSkeleton(
    brush: Brush
) {
    RMapSkeletonCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(AiRoadmapSkeletonRowHeight),
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
                        .fillMaxWidth(0.52f)
                        .height(14.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.38f)
                        .height(12.dp),
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
