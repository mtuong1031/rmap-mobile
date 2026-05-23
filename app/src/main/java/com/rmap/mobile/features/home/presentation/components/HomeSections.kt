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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

private val LearningProgressCardShape = AppShapes.heroCard

@Composable
fun LearningProgressCard(
    progressFraction: Float,
    completedLessons: Int,
    totalLessons: Int,
    onPrimaryIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val normalizedProgress = progressFraction.coerceIn(0f, 1f)
    val progressPercent = (normalizedProgress * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(
                elevation = AppCardDefaults.shadowElevation,
                shape = LearningProgressCardShape,
                spotColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f),
                ambientColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
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
                            text = "Learning",
                            style = AppTextStyles.progressTitle.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Progress",
                            style = AppTextStyles.progressTitle.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Keep up the great work!",
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
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Text(
                    text = "$progressPercent% Complete",
                    style = AppTextStyles.progressValue.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Text(
                    text = "$completedLessons of $totalLessons lessons completed",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun LearningProgressCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            LearningProgressCard(
                progressFraction = 0.42f,
                completedLessons = 45,
                totalLessons = 107,
                onPrimaryIconClick = {}
            )
        }
    }
}
