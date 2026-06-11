package com.rmap.mobile.features.roadmap.presentation.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .wrapContentHeight()
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(AppShapes.heroCard)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primary,
                            Color(0xFF1E40AF)
                        )
                    ),
                    alpha = 0.88f
                )
                .border(
                    width = 1.dp,
                    color = onPrimary.copy(alpha = 0.15f),
                    shape = AppShapes.heroCard
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLgPlus),
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
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            color = onPrimary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
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

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val progressText = buildAnnotatedString {
                        val text = stringResource(
                            R.string.roadmap_detail_required_nodes_completed,
                            completedRequiredNodes,
                            totalRequiredNodes
                        )
                        val completedStr = completedRequiredNodes.toString()
                        val totalStr = totalRequiredNodes.toString()
                        val completedIndex = text.indexOf(completedStr)
                        val totalIndex = text.indexOf(totalStr)
                        if (completedIndex != -1 && totalIndex != -1) {
                            append(text.substring(0, completedIndex))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onPrimary)) {
                                append(completedStr)
                            }
                            append(text.substring(completedIndex + completedStr.length, totalIndex))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onPrimary)) {
                                append(totalStr)
                            }
                            append(text.substring(totalIndex + totalStr.length))
                        } else {
                            append(text)
                        }
                    }
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = onPrimary.copy(alpha = 0.85f)
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
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                ),
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
    val prefix = stringResource(R.string.roadmap_detail_next_unlock, "")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Icon(
            imageVector = Icons.Outlined.Key,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = buildAnnotatedString {
                append(prefix)
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(nextUnlockTitle)
                }
            },
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

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
