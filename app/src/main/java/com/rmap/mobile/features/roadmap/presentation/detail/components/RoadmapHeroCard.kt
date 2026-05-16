package com.rmap.mobile.features.roadmap.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryHeroShadowColor
import com.rmap.mobile.core.ui.theme.RMapTheme

private val RoadmapHeroCardShape = AppShapes.heroCard

@Composable
fun RoadmapHeroCard(
    title: String,
    progressFraction: Float,
    completedLessons: Int,
    totalLessons: Int,
    modifier: Modifier = Modifier
) {
    val progressPercent = (progressFraction.coerceIn(0f, 1f) * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(
                elevation = AppCardDefaults.shadowElevation,
                shape = RoadmapHeroCardShape,
                spotColor = PrimaryHeroShadowColor,
                ambientColor = PrimaryHeroShadowColor
            )
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoadmapHeroCardShape
            )
            .padding(Dimens.spacingXlPlus)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
                ) {
                    // Chapter Tag
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                shape = AppShapes.chip
                            )
                            .border(
                                width = Dimens.borderThin,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                shape = AppShapes.chip
                            )
                            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoGraph,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(Dimens.iconXs)
                        )
                        Text(
                            text = stringResource(R.string.roadmap_detail_chapter_tag).uppercase(),
                            style = AppTextStyles.tag.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }

                    // Title
                    Text(
                        text = title,
                        style = AppTextStyles.heroTitle.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // Circular Progress
                Box(
                    modifier = Modifier.size(Dimens.progressIndicatorLg),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.size(Dimens.progressIndicatorLg),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
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
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = stringResource(R.string.roadmap_detail_done_label),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            // Bottom Progress Bar section
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus)) {
                Text(
                    text = stringResource(
                        R.string.roadmap_detail_lessons_completed,
                        completedLessons,
                        totalLessons
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                )

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapHeroCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            RoadmapHeroCard(
                title = "Frontend Pro",
                progressFraction = 0.75f,
                completedLessons = 6,
                totalLessons = 8
            )
        }
    }
}
