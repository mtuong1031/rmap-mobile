package com.rmap.mobile.features.roadmap.presentation.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapNodeAction

@Composable
fun RoadmapDetailHeroProgressCard(
    title: String,
    categoryLabel: String,
    progressFraction: Float,
    completedRequiredNodes: Int,
    totalRequiredNodes: Int,
    nextActionTitle: String,
    nextAction: RoadmapNodeAction?,
    nextUnlockTitle: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = progressFraction.coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(RoadmapHeroProgressCardHeight)
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(AppShapes.heroCard)
                .background(primary.copy(alpha = 0.96f))
        )

        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)) {
                    Text(
                        text = stringResource(R.string.roadmap_detail_label_roadmap).uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = onPrimary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = onPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                RoadmapPill(
                    text = categoryLabel,
                    containerColor = onPrimary.copy(alpha = 0.2f),
                    contentColor = onPrimary,
                    borderColor = onPrimary.copy(alpha = 0.1f)
                )
            }

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
                            color = onPrimary.copy(alpha = 0.9f)
                        )
                    )
                    Text(
                        text = stringResource(R.string.home_progress_percent_short, progressPercent),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                RoadmapLinearProgress(
                    progress = progress,
                    trackColor = onPrimary.copy(alpha = 0.22f),
                    indicatorColor = onPrimary
                )
            }

            RMapButton(
                text = stringResource(nextAction.titleResId(), nextActionTitle),
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Medium,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(RMapButtonSize.Medium.iconSize)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.cardElevationNone),
                border = null,
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            NextUnlockText(nextUnlockTitle = nextUnlockTitle)
        }
    }
}

@Composable
private fun NextUnlockText(nextUnlockTitle: String) {
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
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private fun RoadmapNodeAction?.titleResId(): Int {
    return when (this) {
        RoadmapNodeAction.StartLearning -> R.string.roadmap_detail_start_learning_title
        RoadmapNodeAction.Review -> R.string.roadmap_detail_review_title
        RoadmapNodeAction.Continue,
        null -> R.string.roadmap_detail_continue_title
    }
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
            nextAction = RoadmapNodeAction.StartLearning,
            nextUnlockTitle = "DOM Manipulation",
            onContinueClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
