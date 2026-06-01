package com.rmap.mobile.features.home.presentation.components.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.components.RMapSkeletonBlock
import com.rmap.mobile.core.ui.components.RMapSkeletonCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

private val HomeSkeletonHeroHeight = 318.dp
private val HomeSkeletonStatHeight = 146.dp
private val HomeSkeletonWarningHeight = 92.dp
private val HomeSkeletonRecommendationWidth = 260.dp
private val HomeSkeletonRecommendationHeight = 286.dp
private val HomeSkeletonCategoryHeight = 44.dp
private val HomeSkeletonTrendingHeight = 154.dp
private val HomeSkeletonRailWidth = 72.dp

@Composable
fun HomeHeroSectionSkeleton(
    sectionTitle: String,
    sectionHorizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(
            text = sectionTitle,
            modifier = Modifier.padding(horizontal = sectionHorizontalPadding)
        )

        RMapSkeletonCard(
            modifier = Modifier
                .padding(horizontal = sectionHorizontalPadding)
                .fillMaxWidth()
                .height(HomeSkeletonHeroHeight),
            shape = AppShapes.heroCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .width(132.dp)
                            .height(14.dp),
                        shape = AppShapes.pill
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .width(76.dp)
                            .height(28.dp),
                        shape = AppShapes.pill
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .height(24.dp),
                        shape = AppShapes.pill
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth(0.58f)
                            .height(24.dp),
                        shape = AppShapes.pill
                    )
                }

                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(174.dp)
                        .height(38.dp),
                    shape = AppShapes.button
                )

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RMapSkeletonBlock(
                            modifier = Modifier
                                .width(118.dp)
                                .height(14.dp),
                            shape = AppShapes.pill
                        )
                        RMapSkeletonBlock(
                            modifier = Modifier
                                .width(42.dp)
                                .height(14.dp),
                            shape = AppShapes.pill
                        )
                    }
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        shape = AppShapes.pill
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = AppShapes.button
                )

                RMapSkeletonBlock(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(186.dp)
                        .height(14.dp),
                    shape = AppShapes.pill
                )
            }
        }
    }
}

@Composable
fun HomeStatCardRowSkeleton(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = Dimens.spacingMd
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) {
            RMapSkeletonCard(
                modifier = Modifier
                    .weight(1f)
                    .height(HomeSkeletonStatHeight),
                shape = AppShapes.card
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.statCardVerticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    RMapSkeletonBlock(
                        modifier = Modifier.size(48.dp),
                        shape = AppShapes.button
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .width(58.dp)
                            .height(22.dp),
                        shape = AppShapes.pill
                    )
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .width(72.dp)
                            .height(12.dp),
                        shape = AppShapes.pill
                    )
                }
            }
        }
    }
}

@Composable
fun HomePaceAlertCardSkeleton(
    modifier: Modifier = Modifier
) {
    RMapSkeletonCard(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeSkeletonWarningHeight),
        shape = AppShapes.card
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(16.dp),
                shape = AppShapes.pill
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.64f)
                    .height(16.dp),
                shape = AppShapes.pill
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .width(112.dp)
                    .height(14.dp),
                shape = AppShapes.pill
            )
        }
    }
}

@Composable
fun HomeRecommendedRoadmapsSectionSkeleton(
    title: String,
    modifier: Modifier = Modifier,
    sectionHorizontalPadding: Dp = Dimens.spacingScreenHorizontal
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = sectionHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            RMapSectionTitle(text = title)
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(14.dp),
                shape = AppShapes.pill
            )
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Spacer(modifier = Modifier.width(sectionHorizontalPadding))
            repeat(2) {
                HomeRecommendationCardSkeleton()
            }
            Spacer(modifier = Modifier.width(sectionHorizontalPadding))
        }
    }
}

@Composable
fun HomeCategoriesSectionSkeleton(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(
            text = title,
            subtitle = subtitle
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(2) {
                        HomeCategoryCardSkeleton(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTrendingRoadmapsSectionSkeleton(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(text = title)
        repeat(3) {
            HomeTrendingRoadmapCardSkeleton()
        }
    }
}

@Composable
private fun HomeRecommendationCardSkeleton() {
    RMapSkeletonCard(
        modifier = Modifier
            .width(HomeSkeletonRecommendationWidth)
            .height(HomeSkeletonRecommendationHeight),
        shape = AppShapes.heroCard
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier.size(56.dp),
                    shape = AppShapes.button
                )
                RMapSkeletonBlock(
                    modifier = Modifier.size(32.dp),
                    shape = AppShapes.pill
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(92.dp)
                        .height(18.dp),
                    shape = AppShapes.chip
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(54.dp)
                        .height(18.dp),
                    shape = AppShapes.chip
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    shape = AppShapes.pill
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(20.dp),
                    shape = AppShapes.pill
                )
            }

            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(14.dp),
                shape = AppShapes.pill
            )

            Spacer(modifier = Modifier.weight(1f))

            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .height(16.dp),
                shape = AppShapes.pill
            )
        }
    }
}

@Composable
private fun HomeCategoryCardSkeleton(
    modifier: Modifier = Modifier
) {
    RMapSkeletonCard(
        modifier = modifier.height(HomeSkeletonCategoryHeight),
        shape = AppShapes.iconContainerLarge
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingLgPlus, vertical = Dimens.spacingSmPlus),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSkeletonBlock(
                modifier = Modifier.size(16.dp),
                shape = AppShapes.pill
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp),
                shape = AppShapes.pill
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .width(24.dp)
                    .height(16.dp),
                shape = AppShapes.chip
            )
        }
    }
}

@Composable
private fun HomeTrendingRoadmapCardSkeleton() {
    RMapSkeletonCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(HomeSkeletonTrendingHeight),
        shape = AppShapes.heroCard
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(HomeSkeletonRailWidth)
                    .height(HomeSkeletonTrendingHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = Dimens.spacingXl)
                        .width(34.dp)
                        .height(24.dp),
                    shape = AppShapes.pill
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(118.dp)
                        .height(22.dp),
                    shape = AppShapes.chip
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(22.dp),
                    shape = AppShapes.pill
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .height(14.dp),
                    shape = AppShapes.pill
                )
                Spacer(modifier = Modifier.weight(1f))
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(14.dp),
                    shape = AppShapes.pill
                )
            }
        }
    }
}
