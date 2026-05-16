package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.CardDividerStrongColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowBareColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowMediumColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowSubtleColor
import com.rmap.mobile.core.ui.theme.CardPrimaryGlowStrongColor
import com.rmap.mobile.core.ui.theme.DifficultyBeginnerContainerColor
import com.rmap.mobile.core.ui.theme.DifficultyExpertContainerColor
import com.rmap.mobile.core.ui.theme.DifficultyExpertContentColor
import com.rmap.mobile.core.ui.theme.DifficultyHardContainerColor
import com.rmap.mobile.core.ui.theme.DifficultyIntermediateContainerColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryBlueOverlayFaintColor
import com.rmap.mobile.core.ui.theme.PrimaryBlueOverlaySoftColor
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.StatusCompletedContentColor
import com.rmap.mobile.core.ui.theme.StatusHardContentColor

private val RoadmapCardShape = AppCardDefaults.shape
private val RoadmapIconFrameShape = AppShapes.card
private val RoadmapIconContainerShape = AppShapes.iconFrameInner

enum class RoadmapDifficulty(
    val backgroundColor: Color,
    val textColor: Color
) {
    Expert(
        backgroundColor = DifficultyExpertContainerColor,
        textColor = DifficultyExpertContentColor
    ),
    Beginner(
        backgroundColor = DifficultyBeginnerContainerColor,
        textColor = StatusCompletedContentColor
    ),
    Intermediate(
        backgroundColor = DifficultyIntermediateContainerColor,
        textColor = PrimaryLight
    ),
    Hard(
        backgroundColor = DifficultyHardContainerColor,
        textColor = StatusHardContentColor
    )
}

data class RoadmapCardUiModel(
    val title: String,
    val lessonsCount: Int,
    val difficultyLabel: String,
    val difficulty: RoadmapDifficulty,
    val durationLabel: String,
    val icon: ImageVector
)

@Composable
fun RoadmapCard(
    item: RoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .then(clickModifier),
        shape = RoadmapCardShape,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLgPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoadmapIconFrame(icon = item.icon)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(Dimens.iconSm)
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.roadmap_lessons_count,
                                    item.lessonsCount
                                ),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                HorizontalDivider(Modifier, thickness = Dimens.borderThin, color = CardDividerStrongColor)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DifficultyBadge(
                        text = item.difficultyLabel,
                        difficulty = item.difficulty
                    )

                    Text(
                        text = item.durationLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = Dimens.cardGlowOffsetX, y = Dimens.cardGlowOffsetY)
                .size(Dimens.cardGlowSize)
                .blur(radius = Dimens.cardGlowBlur)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CardPrimaryGlowStrongColor,
                            CardPrimaryGlowMediumColor,
                            CardPrimaryGlowSubtleColor,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun RoadmapIconFrame(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(Dimens.iconFrameSize)
            .border(
                width = Dimens.borderMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoadmapIconFrameShape
            )
            .padding(
                horizontal = Dimens.iconFramePadding,
                vertical = Dimens.iconFramePadding
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlueOverlaySoftColor,
                            PrimaryBlueOverlayFaintColor
                        )
                    ),
                    shape = RoadmapIconContainerShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconXxl)
            )
        }
    }
}

@Composable
private fun DifficultyBadge(
    text: String,
    difficulty: RoadmapDifficulty
) {
    Box(
        modifier = Modifier
            .background(
                color = difficulty.backgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = difficulty.textColor,
                letterSpacing = 0.325.sp
            ),
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun TrendingRoadmapsSectionPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapCard(
            item = RoadmapCardUiModel(
                title = "UI/UX Master",
                lessonsCount = 96,
                difficultyLabel = "Intermediate",
                difficulty = RoadmapDifficulty.Intermediate,
                durationLabel = "2 months",
                icon = Icons.Outlined.Search
            ),
        )
    }
}
