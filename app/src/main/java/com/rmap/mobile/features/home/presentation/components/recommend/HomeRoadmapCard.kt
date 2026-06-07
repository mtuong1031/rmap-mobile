package com.rmap.mobile.features.home.presentation.components.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeRoadmapCardWidth = 260.dp
internal val HomeRoadmapCardContentPadding = 20.dp
private val HomeRoadmapIconContainerSize = 56.dp
private val HomeRoadmapIconSize = 26.dp
private val HomeRoadmapMetaIconSize = 14.dp
private val HomeRoadmapCtaIconSize = 14.dp
private val HomeRoadmapBeginnerCtaIconSize = 20.dp
private val HomeRoadmapBadgeShape = RoundedCornerShape(6.dp)
internal const val HomeRoadmapTitleMaxLines = 2

@Immutable
data class HomeRoadmapCardStyle(
    val iconContainerColor: Color,
    val iconColor: Color,
    val badgeContainerColor: Color,
    val badgeContentColor: Color
)

@Immutable
data class HomeRoadmapCardUiModel(
    val id: String,
    val categoryLabel: String,
    val title: String,
    val nodesText: String,
    val durationText: String,
    val actionText: String,
    val icon: ImageVector,
    val style: HomeRoadmapCardStyle,
    val isBeginner: Boolean = false
)

object HomeRoadmapCardDefaults {
    val CardWidth: Dp = HomeRoadmapCardWidth
    val CardShape = AppShapes.heroCard
    val IconContainerShape = AppShapes.button

    @Composable
    fun webDevelopmentStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconColor = MaterialTheme.colorScheme.primary,
            badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
            badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    @Composable
    fun interviewStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = MaterialTheme.colorScheme.tertiary,
            badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
            badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    fun designStyle(): HomeRoadmapCardStyle {
        return HomeRoadmapCardStyle(
            iconContainerColor = Color(0xFFFDF2F8),
            iconColor = Color(0xFFE60076),
            badgeContainerColor = Color(0xFFFDF2F8),
            badgeContentColor = Color(0xFFE60076)
        )
    }
}

@Composable
fun HomeRoadmapCard(
    item: HomeRoadmapCardUiModel,
    metadataSeparatorText: String,
    starterBadgeText: String,
    modifier: Modifier = Modifier.width(HomeRoadmapCardDefaults.CardWidth),
    metadataBottomSpacing: Dp = Dimens.spacingLg,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .cardShadow(shape = HomeRoadmapCardDefaults.CardShape)
            .clip(HomeRoadmapCardDefaults.CardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = HomeRoadmapCardDefaults.CardShape
            )
            .then(clickModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = HomeRoadmapCardContentPadding,
                    top = HomeRoadmapCardContentPadding,
                    end = HomeRoadmapCardContentPadding,
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                HomeRoadmapIconTile(
                    icon = item.icon,
                    style = item.style
                )

                Spacer(modifier = Modifier.size(HomeRoadmapIconContainerSize))
            }

            Spacer(modifier = Modifier.height(Dimens.spacingLgPlus))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeRoadmapBadge(
                    text = item.categoryLabel,
                    style = item.style
                )

                if (item.isBeginner) {
                    HomeRoadmapStarterBadge(text = starterBadgeText)
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacingSmPlus))

            Text(
                text = item.title,
                style = homeRoadmapTitleTextStyle(),
                maxLines = HomeRoadmapTitleMaxLines,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Dimens.spacingMd))

            HomeRoadmapMetadata(
                nodesText = item.nodesText,
                separatorText = metadataSeparatorText,
                durationText = item.durationText
            )

            Spacer(modifier = Modifier.height(metadataBottomSpacing))
        }

        HorizontalDivider(
            thickness = Dimens.borderThin,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(
                start = HomeRoadmapCardContentPadding,
                end = HomeRoadmapCardContentPadding
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(
                    start = HomeRoadmapCardContentPadding,
                    end = HomeRoadmapCardContentPadding,
                    top = Dimens.spacingMdPlus,
                    bottom = HomeRoadmapCardContentPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.isBeginner) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Text(
                    text = item.actionText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(
                    if (item.isBeginner) {
                        HomeRoadmapBeginnerCtaIconSize
                    } else {
                        HomeRoadmapCtaIconSize
                    }
                )
            )
        }
    }
}

@Composable
internal fun homeRoadmapTitleTextStyle(): TextStyle {
    return MaterialTheme.typography.titleLarge.copy(
        fontSize = 18.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun HomeRoadmapIconTile(
    icon: ImageVector,
    style: HomeRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .size(HomeRoadmapIconContainerSize)
            .background(
                color = style.iconContainerColor,
                shape = HomeRoadmapCardDefaults.IconContainerShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = style.iconColor,
            modifier = Modifier.size(HomeRoadmapIconSize)
        )
    }
}

@Composable
private fun HomeRoadmapBadge(
    text: String,
    style: HomeRoadmapCardStyle
) {
    Box(
        modifier = Modifier
            .background(
                color = style.badgeContainerColor,
                shape = HomeRoadmapBadgeShape
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingMicro),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = style.badgeContentColor
            )
        )
    }
}

@Composable
private fun HomeRoadmapStarterBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFFEAFBF3),
                shape = HomeRoadmapBadgeShape
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingMicro),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = Color(0xFF00A866)
            )
        )
    }
}

@Composable
private fun HomeRoadmapMetadata(
    nodesText: String,
    separatorText: String,
    durationText: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(HomeRoadmapMetaIconSize)
        )

        Text(
            text = nodesText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Text(
            text = separatorText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Text(
            text = durationText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeRoadmapCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeRoadmapCard(
                metadataSeparatorText = "•",
                starterBadgeText = "Starter",
                item = HomeRoadmapCardUiModel(
                    id = "react-fundamentals",
                    categoryLabel = "Web Development",
                    title = "Frontend Starter",
                    nodesText = "24 nodes",
                    durationText = "4 weeks",
                    actionText = "View roadmap",
                    icon = Icons.Outlined.Code,
                    style = HomeRoadmapCardDefaults.webDevelopmentStyle(),
                    isBeginner = true
                )
            )
        }
    }
}
