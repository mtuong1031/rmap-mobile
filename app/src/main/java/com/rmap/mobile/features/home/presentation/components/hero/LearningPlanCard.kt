package com.rmap.mobile.features.home.presentation.components.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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

private val HomeHeroContentPadding = 20.dp
private val HomeHeroProgressHeight = 8.dp
internal const val HomeHeroSkillTitleMaxLines = 2

@Composable
internal fun LearningPlanCard(
    modifier: Modifier = Modifier,
    roadmap: HomeLearningPlanUiModel,
    variant: LearningPlanCardVariant,
    continueText: String,
    nextUnlockPrefix: String,
    onContinueClick: (HomeLearningPlanUiModel) -> Unit,
    progressBottomSpacing: Dp = Dimens.spacingXxl,
) {
    val progressPercentage = getProgressPercentage(
        completedRequiredNodes = roadmap.completedRequiredNodes,
        totalRequiredNodes = roadmap.totalRequiredNodes,
        progressPercentage = roadmap.progressPercentage
    )
    val progressFraction = progressPercentage.toFloat() / 100f
    val contentPadding = when (variant) {
        LearningPlanCardVariant.Large -> HomeHeroContentPadding
        LearningPlanCardVariant.Carousel -> Dimens.spacingLg
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            HomeHeroTitleRow(
                roadmapTitle = roadmap.roadmapTitle,
                skillTitle = roadmap.skillTitle,
                chapterText = roadmap.chapterText
            )

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            HomeHeroSkillMeta(
                requiredSkillText = roadmap.requiredSkillText,
                timeLeftText = roadmap.timeLeftText
            )

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            HomeHeroProgress(
                completedRequiredNodes = roadmap.completedRequiredNodes,
                totalRequiredNodes = roadmap.totalRequiredNodes,
                progressPercentText = "$progressPercentage%",
                progressFraction = progressFraction
            )

            Spacer(modifier = Modifier.height(progressBottomSpacing))

            RMapButton(
                text = continueText,
                onClick = { onContinueClick(roadmap) },
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            )

            roadmap.nextUnlockText?.takeIf { it.isNotBlank() }?.let { nextUnlockText ->
                Spacer(modifier = Modifier.height(Dimens.spacingLg))

                Text(
                    text = buildAnnotatedString {
                        append(nextUnlockPrefix)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                            append(nextUnlockText)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HomeHeroTitleRow(
    roadmapTitle: String,
    skillTitle: String,
    chapterText: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = roadmapTitle.uppercase(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            chapterText?.takeIf { it.isNotBlank() }?.let { label ->
                Text(
                    text = label,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = AppShapes.pill
                        )
                        .border(
                            width = Dimens.borderThin,
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = AppShapes.pill
                        )
                        .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1
                )
            }
        }

        Text(
            text = skillTitle,
            style = learningPlanTitleTextStyle(),
            maxLines = HomeHeroSkillTitleMaxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun learningPlanTitleTextStyle() = MaterialTheme.typography.titleLarge.copy(
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onSurface
)

@Composable
private fun HomeHeroSkillMeta(
    requiredSkillText: String,
    timeLeftText: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                shape = RoundedCornerShape(Dimens.cardRadiusMd)
            )
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.TrackChanges,
                contentDescription = null,
                tint = Color(0xFFFE9A00),
                modifier = Modifier.size(Dimens.iconXs)
            )
            Text(
                text = requiredSkillText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        timeLeftText?.takeIf { it.isNotBlank() }?.let { label ->
            Text(
                text = stringResource(R.string.separator_bullet),
                modifier = Modifier.offset(y = (-0.5).dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                )
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HomeHeroProgress(
    completedRequiredNodes: Int,
    totalRequiredNodes: Int,
    progressPercentText: String,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val normalizedProgress = progressFraction.coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.home_hero_progress_format,
                    completedRequiredNodes,
                    totalRequiredNodes
                ),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = progressPercentText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1
            )
        }

        LinearProgressIndicator(
            progress = { normalizedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(HomeHeroProgressHeight)
                .clip(AppShapes.pill),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LearningPlanCardLargePreview() {
    RMapTheme {
        LearningPlanCard(
            modifier = Modifier.padding(16.dp),
            roadmap = HomeLearningPlanUiModel(
                id = "1",
                roadmapTitle = "Android Development Android Development Android Development",
                skillTitle = "Jetpack Compose Basics",
                chapterText = "Chapter 3",
                requiredSkillText = "Kotlin Intermediate",
                timeLeftText = "2h 30m left",
                completedRequiredNodes = 5,
                totalRequiredNodes = 10,
                nextUnlockText = "Advanced State Management"
            ),
            variant = LearningPlanCardVariant.Large,
            continueText = "Continue Learning",
            nextUnlockPrefix = "Next unlock: ",
            onContinueClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LearningPlanCardCarouselPreview() {
    RMapTheme {
        LearningPlanCard(
            modifier = Modifier.padding(16.dp),
            roadmap = HomeLearningPlanUiModel(
                id = "2",
                roadmapTitle = "Frontend Web",
                skillTitle = "React Hooks in Depth",
                chapterText = "Part 2",
                requiredSkillText = "JavaScript ES6+",
                timeLeftText = "45m left",
                completedRequiredNodes = 8,
                totalRequiredNodes = 12,
                nextUnlockText = "Custom Hooks"
            ),
            variant = LearningPlanCardVariant.Carousel,
            continueText = "Resume",
            nextUnlockPrefix = "Unlocks: ",
            onContinueClick = {}
        )
    }
}
