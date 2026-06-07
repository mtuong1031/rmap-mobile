package com.rmap.mobile.features.profile.presentation.components.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun ProfileContentSkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberRMapSkeletonBrush()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        ProfileHeaderSkeleton(brush = brush)
        ProfileSectionSkeleton(brush = brush, rowCount = 3)
        ProfileAchievementSkeleton(brush = brush)
        ProfileWeeklyActivitySkeleton(brush = brush)
        ProfileSettingsSkeleton(brush = brush)
    }
}

@Composable
private fun ProfileHeaderSkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier.size(104.dp),
            shape = AppShapes.heroCard,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .width(168.dp)
                .height(24.dp),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .width(218.dp)
                .height(18.dp),
            shape = AppShapes.pill,
            brush = brush
        )
    }
}

@Composable
private fun ProfileSectionSkeleton(
    brush: Brush,
    rowCount: Int,
    modifier: Modifier = Modifier
) {
    RMapSkeletonCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            ProfileSectionHeaderSkeleton(brush = brush)
            repeat(rowCount) {
                ProfileRoadmapRowSkeleton(brush = brush)
            }
        }
    }
}

@Composable
private fun ProfileAchievementSkeleton(
    brush: Brush
) {
    RMapSkeletonCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                ProfileSectionHeaderSkeleton(
                    brush = brush,
                    modifier = Modifier.weight(1f)
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(54.dp)
                        .height(18.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                repeat(2) {
                    RMapSkeletonBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = AppShapes.iconContainerLarge,
                        brush = brush
                    )
                }
            }

            repeat(2) {
                ProfileAchievementRowSkeleton(brush = brush)
            }
        }
    }
}

@Composable
private fun ProfileWeeklyActivitySkeleton(
    brush: Brush
) {
    RMapSkeletonCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                ProfileSectionHeaderSkeleton(
                    brush = brush,
                    modifier = Modifier.weight(1f)
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .width(88.dp)
                        .height(26.dp),
                    shape = AppShapes.small,
                    brush = brush
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(7) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                    ) {
                        RMapSkeletonBlock(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            brush = brush
                        )
                        RMapSkeletonBlock(
                            modifier = Modifier
                                .width(22.dp)
                                .height(12.dp),
                            shape = AppShapes.pill,
                            brush = brush
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSettingsSkeleton(
    brush: Brush
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier
                .padding(start = Dimens.spacingSm)
                .width(162.dp)
                .height(22.dp),
            shape = AppShapes.pill,
            brush = brush
        )

        RMapSkeletonCard(
            modifier = Modifier.fillMaxWidth(),
            shape = AppShapes.card
        ) {
            Column(modifier = Modifier.padding(Dimens.spacingSm)) {
                repeat(4) {
                    ProfileSettingsRowSkeleton(brush = brush)
                }
            }
        }

        RMapSkeletonCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = AppShapes.button
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = Dimens.spacingXl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier.size(Dimens.iconXxl),
                    shape = CircleShape,
                    brush = brush
                )
                RMapSkeletonBlock(
                    modifier = Modifier
                        .padding(start = Dimens.spacingMd)
                        .width(86.dp)
                        .height(18.dp),
                    shape = AppShapes.pill,
                    brush = brush
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionHeaderSkeleton(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
    ) {
        RMapSkeletonBlock(
            modifier = Modifier
                .width(146.dp)
                .height(20.dp),
            shape = AppShapes.pill,
            brush = brush
        )
        RMapSkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .height(16.dp),
            shape = AppShapes.pill,
            brush = brush
        )
    }
}

@Composable
private fun ProfileRoadmapRowSkeleton(
    brush: Brush
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSkeletonBlock(
                modifier = Modifier.size(Dimens.controlSm),
                shape = AppShapes.iconContainerLarge,
                brush = brush
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                RMapSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.74f)
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
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp),
                shape = AppShapes.pill,
                brush = brush
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .width(36.dp)
                    .height(14.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}

@Composable
private fun ProfileAchievementRowSkeleton(
    brush: Brush
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .padding(horizontal = Dimens.spacingXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RMapSkeletonBlock(
            modifier = Modifier.size(Dimens.controlLg),
            shape = AppShapes.iconContainerLarge,
            brush = brush
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(18.dp),
                shape = AppShapes.pill,
                brush = brush
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(14.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
        RMapSkeletonBlock(
            modifier = Modifier.size(Dimens.iconSm),
            shape = CircleShape,
            brush = brush
        )
    }
}

@Composable
private fun ProfileSettingsRowSkeleton(
    brush: Brush
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RMapSkeletonBlock(
            modifier = Modifier.size(Dimens.iconXxl),
            shape = CircleShape,
            brush = brush
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(16.dp),
                shape = AppShapes.pill,
                brush = brush
            )
            RMapSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.36f)
                    .height(12.dp),
                shape = AppShapes.pill,
                brush = brush
            )
        }
    }
}
