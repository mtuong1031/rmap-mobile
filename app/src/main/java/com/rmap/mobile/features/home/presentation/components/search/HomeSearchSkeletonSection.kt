package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapSkeletonBlock
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchRoadmapsSkeletonSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 2
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSkeletonBlock(
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
        RMapSkeletonBlock(
            modifier = Modifier.size(HomeSearchRoadmapIconSize),
            shape = AppShapes.button
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(14.dp)
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .height(10.dp)
            )
        }

        RMapSkeletonBlock(
            modifier = Modifier.size(HomeSearchRoadmapTrailingSize),
            shape = AppShapes.pill
        )
    }
}

@Composable
fun HomeSearchSkillsSkeletonSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 2
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier
                .width(64.dp)
                .height(14.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            repeat(itemCount) {
                HomeSearchSkillSkeletonCard()
            }
        }
    }
}

@Composable
private fun HomeSearchSkillSkeletonCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .cardShadow(shape = AppShapes.searchBar)
            .clip(AppShapes.searchBar)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = AppShapes.searchBar
            )
            .padding(Dimens.spacingLgPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(12.dp)
            )
        }

        RMapSkeletonBlock(
            modifier = Modifier
                .width(92.dp)
                .height(32.dp),
            shape = AppShapes.small
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeSearchRoadmapsSkeletonSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        HomeSearchRoadmapsSkeletonSection(
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
