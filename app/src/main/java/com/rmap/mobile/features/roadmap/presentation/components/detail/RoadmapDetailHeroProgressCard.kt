package com.rmap.mobile.features.roadmap.presentation.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapLinearProgress
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapPrimaryAction

@Composable
fun RoadmapDetailHeroProgressCard(
    title: String,
    categoryLabel: String,
    progressFraction: Float,
    completedRequiredNodes: Int,
    totalRequiredNodes: Int,
    nextActionTitle: String,
    primaryAction: RoadmapPrimaryAction,
    nextUnlockTitle: String,
    onContinueClick: () -> Unit,
    isPreviewMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progress = progressFraction.coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()

    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val secondary = MaterialTheme.colorScheme.secondary

    val contentColorMain = if (isDarkTheme) onSurface else onPrimary
    val contentColorSub = if (isDarkTheme) secondary else onPrimary.copy(alpha = 0.9f)
    val pillContainerColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceVariant else onPrimary.copy(alpha = 0.2f)
    val pillContentColor = if (isDarkTheme) onSurface else onPrimary
    val pillBorderColor = if (isDarkTheme) MaterialTheme.colorScheme.outlineVariant else onPrimary.copy(alpha = 0.1f)
    val progressTrackColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceContainerHigh else onPrimary.copy(alpha = 0.22f)
    val progressIndicatorColor = if (isDarkTheme) primary else onPrimary
    val buttonVariant = if (isDarkTheme) RMapButtonVariant.Primary else RMapButtonVariant.Secondary
    val buttonContainerColor = if (isDarkTheme) primary else MaterialTheme.colorScheme.surface
    val buttonContentColor = if (isDarkTheme) onPrimary else primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = RoadmapHeroProgressCardHeight)
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        if (!isDarkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(AppShapes.heroCard)
                    .background(primary.copy(alpha = 0.96f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.roadmap_detail_label_roadmap).uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = contentColorSub,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    RoadmapPill(
                        text = categoryLabel,
                        containerColor = pillContainerColor,
                        contentColor = pillContentColor,
                        borderColor = pillBorderColor
                    )
                }

                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = contentColorMain,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            if (isPreviewMode) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                    RoadmapPill(
                        text = stringResource(R.string.roadmap_detail_preview_mode),
                        containerColor = pillContainerColor,
                        contentColor = pillContentColor,
                        borderColor = pillBorderColor
                    )
                    Text(
                        text = stringResource(R.string.roadmap_detail_preview_description),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = contentColorSub
                        )
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(
                                R.string.roadmap_detail_required_nodes_completed,
                                completedRequiredNodes,
                                totalRequiredNodes
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = contentColorSub
                            )
                        )
                        Text(
                            text = stringResource(R.string.home_progress_percent_short, progressPercent),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isDarkTheme) primary else onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    RoadmapLinearProgress(
                        progress = progress,
                        trackColor = progressTrackColor,
                        indicatorColor = progressIndicatorColor
                    )
                }

                RMapButton(
                    text = roadmapPrimaryActionText(
                        primaryAction = primaryAction,
                        nextActionTitle = nextActionTitle
                    ),
                    onClick = onContinueClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = buttonVariant,
                    size = RMapButtonSize.Medium,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = buttonContentColor,
                            modifier = Modifier.size(RMapButtonSize.Medium.iconSize)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonContainerColor,
                        contentColor = buttonContentColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.cardElevationNone),
                    border = null,
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                NextUnlockText(
                    nextUnlockTitle = nextUnlockTitle,
                    textColor = contentColorSub
                )
            }
        }
    }
}

@Composable
private fun roadmapPrimaryActionText(
    primaryAction: RoadmapPrimaryAction,
    nextActionTitle: String
): String {
    return when (primaryAction) {
        RoadmapPrimaryAction.StartLearning -> stringResource(R.string.roadmap_detail_action_start_learning)
        RoadmapPrimaryAction.ContinueLearning -> stringResource(
            R.string.roadmap_detail_continue_title,
            nextActionTitle
        )
    }
}

@Composable
private fun NextUnlockText(
    nextUnlockTitle: String,
    textColor: androidx.compose.ui.graphics.Color
) {
    if (nextUnlockTitle.isBlank()) return

    val prefix = stringResource(R.string.roadmap_detail_next_unlock, "")
    Text(
        text = buildAnnotatedString {
            append(prefix)
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(nextUnlockTitle)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall.copy(
            color = textColor
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private val RoadmapHeroProgressCardHeight =
    Dimens.recommendedCardHeight + Dimens.profileExperienceIconContainerSize + Dimens.spacingMdPlus

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapDetailHeroProgressCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapDetailHeroProgressCard(
            title = "Frontend Pro",
            categoryLabel = "Web Development",
            progressFraction = 0.75f,
            completedRequiredNodes = 6,
            totalRequiredNodes = 8,
            nextActionTitle = "Asynchronous JS",
            primaryAction = RoadmapPrimaryAction.ContinueLearning,
            nextUnlockTitle = "DOM Manipulation",
            onContinueClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
