package com.rmap.mobile.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.HeadingTextColor
import com.rmap.mobile.core.ui.theme.PrimaryHeroShadowColor
import com.rmap.mobile.core.ui.theme.RMapTheme

private val LearningProgressCardShape = AppShapes.heroCard

@Composable
fun LearningProgressCard(
    progressFraction: Float,
    onPrimaryIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val normalizedProgress = progressFraction.coerceIn(0f, 1f)
    val progressPercent = (normalizedProgress * 100).toInt()
    val totalLessons = 107
    val completedLessons = 1

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(
                elevation = AppCardDefaults.shadowElevation,
                shape = LearningProgressCardShape,
                spotColor = PrimaryHeroShadowColor,
                ambientColor = PrimaryHeroShadowColor
            )
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = LearningProgressCardShape
            )
            .padding(
                start = Dimens.spacingXlPlus,
                top = Dimens.spacingXlPlus,
                end = Dimens.spacingXlPlus,
                bottom = Dimens.spacingXlPlus
            )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMdPlus)) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.controlLg)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                                shape = AppShapes.iconContainerLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoGraph,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(Dimens.iconLg)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(top = Dimens.spacingXxs),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                    ) {
                        Text(
                            text = stringResource(R.string.home_progress_title_line_1),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_title_line_2),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(Dimens.progressIndicatorLg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPrimaryIconClick
                        )
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { normalizedProgress },
                        modifier = Modifier.size(Dimens.progressIndicatorLg),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                        strokeWidth = Dimens.progressStroke
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.home_progress_percent_short,
                                progressPercent
                            ),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_done),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Text(
                    text = stringResource(R.string.home_progress_percent_complete, progressPercent),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 28.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Text(
                    text = stringResource(
                        R.string.home_progress_lessons_completed,
                        completedLessons,
                        totalLessons
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun TrendingRoadmapsHeader(
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.roadmap_trending_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingTextColor
            )
        )

        Text(
            text = stringResource(R.string.roadmap_see_all),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSeeAllClick
                )
                .background(Color.Transparent, AppShapes.small)
                .padding(horizontal = Dimens.spacingXxs),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.3.sp
            )
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun LearningProgressCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            LearningProgressCard(progressFraction = 0.42f, onPrimaryIconClick = {})
        }
    }
}
