package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchRecommendedRoadmapsSkeletonSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 2
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HomeSearchSkeletonBlock(
            modifier = Modifier
                .width(104.dp)
                .height(14.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            repeat(itemCount) {
                HomeSearchRoadmapSkeletonCard()
            }
        }
    }
}

@Composable
private fun HomeSearchRoadmapSkeletonCard() {
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
            .padding(Dimens.spacingLgPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeSearchSkeletonBlock(
            modifier = Modifier.size(HomeSearchRoadmapIconSize),
            shape = AppShapes.button
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            HomeSearchSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(14.dp)
            )
            HomeSearchSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .height(10.dp)
            )
        }

        HomeSearchSkeletonBlock(
            modifier = Modifier.size(HomeSearchRoadmapTrailingSize),
            shape = AppShapes.pill
        )
    }
}

@Composable
private fun HomeSearchSkeletonBlock(
    modifier: Modifier,
    shape: Shape = AppShapes.button
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeSearchRecommendedRoadmapsSkeletonSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        HomeSearchRecommendedRoadmapsSkeletonSection(
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
