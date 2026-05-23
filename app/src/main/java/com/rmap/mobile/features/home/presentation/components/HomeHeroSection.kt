package com.rmap.mobile.features.home.presentation.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.icons.RMapIcons
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

private val HomeHeroContentPadding = 20.dp
private val HomeHeroProgressHeight = 10.dp
private val HomeHeroEmptyCardHorizontalPadding = 32.dp
private val HomeHeroEmptyIconTileSize = 72.dp

@Composable
fun HomeHeroSection(
    modifier: Modifier = Modifier,
    sectionTitle: String,
    roadmapTitle: String,
    skillTitle: String,
    chapterText: String,
    requiredSkillText: String,
    timeLeftText: String,
    progressText: String,
    progressPercentText: String,
    progressFraction: Float,
    continueText: String,
    nextUnlockPrefix: String,
    nextUnlockText: String,
    onContinueClick: () -> Unit,
    hasInProgressRoadmap: Boolean = true,
    onCreateRoadmapWithAiClick: () -> Unit = {},
    onExploreReadyMadeClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapSectionTitle(text = sectionTitle)

        if (hasInProgressRoadmap) {
            HomeHeroCard(
                roadmapTitle = roadmapTitle,
                skillTitle = skillTitle,
                chapterText = chapterText,
                requiredSkillText = requiredSkillText,
                timeLeftText = timeLeftText,
                progressText = progressText,
                progressPercentText = progressPercentText,
                progressFraction = progressFraction,
                continueText = continueText,
                nextUnlockPrefix = nextUnlockPrefix,
                nextUnlockText = nextUnlockText,
                onContinueClick = onContinueClick
            )
        } else {
            HomeHeroEmptyRoadmapCard(
                onCreateRoadmapWithAiClick = onCreateRoadmapWithAiClick,
                onExploreReadyMadeClick = onExploreReadyMadeClick
            )
        }
    }
}

@Composable
private fun HomeHeroEmptyRoadmapCard(
    onCreateRoadmapWithAiClick: () -> Unit,
    onExploreReadyMadeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HomeHeroEmptyCardHorizontalPadding,
                    vertical = Dimens.spacingHuge
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(HomeHeroEmptyIconTileSize)
                    .clip(RoundedCornerShape(Dimens.cardRadiusXl))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = Dimens.borderThin,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(Dimens.cardRadiusXl)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RMapIcons.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconXxl)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            Text(
                text = stringResource(R.string.home_empty_roadmap_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSmPlus))

            Text(
                text = stringResource(R.string.home_empty_roadmap_description),
                modifier = Modifier.widthIn(max = 278.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spacingHuge))

            RMapButton(
                text = stringResource(R.string.home_empty_roadmap_create_ai),
                onClick = onCreateRoadmapWithAiClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            )

            Spacer(modifier = Modifier.height(Dimens.spacingMd))

            RMapButton(
                text = stringResource(R.string.home_empty_roadmap_explore),
                onClick = onExploreReadyMadeClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            )
        }
    }
}

@Composable
private fun HomeHeroCard(
    roadmapTitle: String,
    skillTitle: String,
    chapterText: String,
    requiredSkillText: String,
    timeLeftText: String,
    progressText: String,
    progressPercentText: String,
    progressFraction: Float,
    continueText: String,
    nextUnlockPrefix: String,
    nextUnlockText: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HomeHeroContentPadding)
        ) {
            HomeHeroTitleRow(
                roadmapTitle = roadmapTitle,
                skillTitle = skillTitle,
                chapterText = chapterText
            )

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            HomeHeroSkillMeta(
                requiredSkillText = requiredSkillText,
                timeLeftText = timeLeftText
            )

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            HomeHeroProgress(
                progressText = progressText,
                progressPercentText = progressPercentText,
                progressFraction = progressFraction
            )

            Spacer(modifier = Modifier.height(Dimens.spacingXxl))

            RMapButton(
                text = continueText,
                onClick = onContinueClick,
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

            Spacer(modifier = Modifier.height(Dimens.spacingLg))

            Text(
                text = buildAnnotatedString {
                    append(nextUnlockPrefix)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                        append(nextUnlockText)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
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

@Composable
private fun HomeHeroTitleRow(
    roadmapTitle: String,
    skillTitle: String,
    chapterText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = roadmapTitle.uppercase(),
                style = AppTextStyles.tag.copy(
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.275.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = chapterText,
                modifier = Modifier
                    .padding(start = Dimens.spacingMd)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = AppShapes.pill
                    )
                    .border(
                        width = Dimens.borderThin,
                        color = Color(0xFFBEDBFF),
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

        Text(
            text = skillTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HomeHeroSkillMeta(
    requiredSkillText: String,
    timeLeftText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
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

        Text(
            text = stringResource(R.string.separator_bullet),
            modifier = Modifier.offset(y = (-0.5).dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 19.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD1D5DC)
            )
        )

        Text(
            text = timeLeftText,
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

@Composable
private fun HomeHeroProgress(
    progressText: String,
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
                text = progressText,
                style = MaterialTheme.typography.labelMedium.copy(
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

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeHeroSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeHeroSection(
                sectionTitle = "Today's Learning Plan",
                roadmapTitle = "Frontend Pro",
                skillTitle = "Asynchronous JS",
                chapterText = "Chapter 1/6",
                requiredSkillText = "Required Skill",
                timeLeftText = "25 min left",
                progressText = "6 of 8 required nodes complete",
                progressPercentText = "75%",
                progressFraction = 0.75f,
                continueText = "Continue",
                nextUnlockPrefix = "Next unlock: ",
                nextUnlockText = "DOM Manipulation",
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeHeroSectionEmptyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeHeroSection(
                sectionTitle = "Today's Learning Plan",
                roadmapTitle = "",
                skillTitle = "",
                chapterText = "",
                requiredSkillText = "",
                timeLeftText = "",
                progressText = "",
                progressPercentText = "",
                progressFraction = 0f,
                continueText = "",
                nextUnlockPrefix = "",
                nextUnlockText = "",
                hasInProgressRoadmap = false,
                onContinueClick = {}
            )
        }
    }
}
