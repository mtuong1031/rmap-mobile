package com.rmap.mobile.features.home.presentation.components.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val TrendingRoadmapRailWidth = 72.dp
private val TrendingRoadmapDecorativeIconSize = 65.dp
private val TrendingRoadmapRankTopPadding = 20.dp
private val TrendingRoadmapContentPadding = 20.dp
private val TrendingRoadmapTrendIconSize = 14.dp
private val TrendingRoadmapArrowSize = 22.dp
private val TrendingRoadmapBadgeShape = RoundedCornerShape(8.dp)

@Immutable
data class TrendingRoadmapCardStyle(
    val railContainerColor: Color,
    val railBorderColor: Color,
    val rankContentColor: Color,
    val categoryContainerColor: Color,
    val categoryContentColor: Color,
    val decorativeIconColor: Color,
    val decorativeIconAlpha: Float,
    val trendContentColor: Color
)

@Immutable
data class TrendingRoadmapCardUiModel(
    val id: String,
    val rankText: String,
    val categoryLabel: String,
    val title: String,
    val metadataText: String,
    val trendText: String,
    val leadingIcon: ImageVector,
    val trendIcon: ImageVector,
    val style: TrendingRoadmapCardStyle
)

object TrendingRoadmapCardDefaults {
    val CardShape = AppShapes.heroCard

    fun primaryStyle(): TrendingRoadmapCardStyle {
        return TrendingRoadmapCardStyle(
            railContainerColor = Color(0x99EFF6FF),
            railBorderColor = Color(0x80EFF6FF),
            rankContentColor = Color(0xFF155DFC),
            categoryContainerColor = Color(0xFFEFF6FF),
            categoryContentColor = Color(0xFF155DFC),
            decorativeIconColor = Color(0xFF2B7FFF),
            decorativeIconAlpha = 0.25f,
            trendContentColor = TrendingRoadmapOrange
        )
    }

    fun neutralStyle(): TrendingRoadmapCardStyle {
        return TrendingRoadmapCardStyle(
            railContainerColor = Color(0xFFF8FAFC),
            railBorderColor = Color(0x80F1F5F9),
            rankContentColor = Color(0xFF45556C),
            categoryContainerColor = Color(0xFFF1F5F9),
            categoryContentColor = Color(0xFF45556C),
            decorativeIconColor = Color(0xFF45556C),
            decorativeIconAlpha = 0.15f,
            trendContentColor = Color(0xFF2B7FFF)
        )
    }

    fun indigoStyle(): TrendingRoadmapCardStyle {
        return TrendingRoadmapCardStyle(
            railContainerColor = Color(0x99EEF2FF),
            railBorderColor = Color(0x80EEF2FF),
            rankContentColor = Color(0xFF4F39F6),
            categoryContainerColor = Color(0xFFEEF2FF),
            categoryContentColor = Color(0xFF4F39F6),
            decorativeIconColor = Color(0xFF4F39F6),
            decorativeIconAlpha = 0.2f,
            trendContentColor = TrendingRoadmapOrange
        )
    }
}

@Composable
fun TrendingRoadmapCard(
    item: TrendingRoadmapCardUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .cardShadow(shape = TrendingRoadmapCardDefaults.CardShape)
            .clip(TrendingRoadmapCardDefaults.CardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = TrendingRoadmapCardDefaults.CardShape
            )
            .then(clickModifier)
    ) {
        TrendingRoadmapRankRail(
            rankText = item.rankText,
            icon = item.leadingIcon,
            style = item.style
        )

        TrendingRoadmapContent(item = item)
    }
}

@Composable
private fun TrendingRoadmapRankRail(
    rankText: String,
    icon: ImageVector,
    style: TrendingRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .width(TrendingRoadmapRailWidth)
            .fillMaxHeight()
            .clipToBounds()
            .background(style.railContainerColor)
            .drawBehind {
                drawLine(
                    color = style.railBorderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = Dimens.borderThin.toPx()
                )
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = TrendingRoadmapRankTopPadding)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
                .padding(horizontal = Dimens.spacingSmPlus, vertical = Dimens.spacingXs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rankText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = style.rankContentColor
                )
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = style.decorativeIconColor.copy(alpha = style.decorativeIconAlpha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = Dimens.spacingXxl, y = Dimens.spacingXxl)
                .size(TrendingRoadmapDecorativeIconSize)
        )
    }
}

@Composable
private fun TrendingRoadmapContent(item: TrendingRoadmapCardUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(TrendingRoadmapContentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
            TrendingRoadmapCategoryBadge(
                text = item.categoryLabel,
                style = item.style
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = item.metadataText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.trendIcon,
                    contentDescription = null,
                    tint = item.style.trendContentColor,
                    modifier = Modifier.size(TrendingRoadmapTrendIconSize)
                )

                Text(
                    text = item.trendText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = item.style.trendContentColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacingMd))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(TrendingRoadmapArrowSize)
            )
        }
    }
}

@Composable
private fun TrendingRoadmapCategoryBadge(
    text: String,
    style: TrendingRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .background(
                color = style.categoryContainerColor,
                shape = TrendingRoadmapBadgeShape
            )
            .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = AppTextStyles.tag.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = style.categoryContentColor
            ),
            maxLines = 1
        )
    }
}

private val TrendingRoadmapOrange = Color(0xFFFF6900)

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
        RMapSectionTitle(
            text = stringResource(R.string.roadmap_trending_title),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = stringResource(R.string.roadmap_see_all).uppercase(),
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
                letterSpacing = AppTextStyles.badge.letterSpacing
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun TrendingRoadmapCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            TrendingRoadmapCard(
                item = TrendingRoadmapCardUiModel(
                    id = "ui-ux-master",
                    rankText = "#1",
                    categoryLabel = "Design",
                    title = "UI/UX Master",
                    metadataText = "96 nodes • 2 months",
                    trendText = "Popular this week",
                    leadingIcon = Icons.Outlined.Palette,
                    trendIcon = Icons.AutoMirrored.Outlined.TrendingUp,
                    style = TrendingRoadmapCardDefaults.primaryStyle()
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun TrendingRoadmapLearnersCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            TrendingRoadmapCard(
                item = TrendingRoadmapCardUiModel(
                    id = "devops-specialist",
                    rankText = "#2",
                    categoryLabel = "DevOps",
                    title = "DevOps Specialist",
                    metadataText = "183 nodes • 6 months",
                    trendText = "2.4k learners",
                    leadingIcon = Icons.Outlined.DataObject,
                    trendIcon = Icons.Outlined.Groups,
                    style = TrendingRoadmapCardDefaults.neutralStyle()
                )
            )
        }
    }
}
