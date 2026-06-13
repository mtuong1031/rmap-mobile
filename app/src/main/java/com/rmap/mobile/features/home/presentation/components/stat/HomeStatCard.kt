package com.rmap.mobile.features.home.presentation.components.stat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.LocalRMapSemanticColors
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeStatIconContainerSize = 48.dp
private val HomeStatIconSize = 22.dp

@Immutable
data class HomeStatCardStyle(
    val iconContainerColor: Color,
    val accentColor: Color,
    val borderColor: Color
)

@Immutable
data class HomeStatItemUiModel(
    val valueText: String,
    val labelText: String,
    val icon: ImageVector,
    val style: HomeStatCardStyle
)

object HomeStatCardDefaults {
    val CardShape = AppShapes.card
    val IconContainerShape = AppShapes.button
    val IconContainerSize: Dp = HomeStatIconContainerSize
    val IconSize: Dp = HomeStatIconSize
    val ContentPadding: Dp = Dimens.statCardVerticalPadding
    val ContentSpacing: Dp = Dimens.spacingMd

    @Composable
    fun roadmapStyle(): HomeStatCardStyle {
        val infoColors = LocalRMapSemanticColors.current.info
        return HomeStatCardStyle(
            iconContainerColor = infoColors.container,
            accentColor = infoColors.accent,
            borderColor = infoColors.border
        )
    }

    @Composable
    fun streakStyle(): HomeStatCardStyle {
        val warningColors = LocalRMapSemanticColors.current.warning
        return HomeStatCardStyle(
            iconContainerColor = warningColors.container,
            accentColor = warningColors.accent,
            borderColor = warningColors.border
        )
    }

    @Composable
    fun readinessStyle(): HomeStatCardStyle {
        val successColors = LocalRMapSemanticColors.current.success
        return HomeStatCardStyle(
            iconContainerColor = successColors.container,
            accentColor = successColors.accent,
            borderColor = successColors.border
        )
    }
}

@Composable
fun HomeStatCard(
    valueText: String,
    labelText: String,
    style: HomeStatCardStyle,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .cardShadow(shape = HomeStatCardDefaults.CardShape)
            .clip(HomeStatCardDefaults.CardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = style.borderColor,
                shape = HomeStatCardDefaults.CardShape
            )
            .then(clickableModifier)
            .padding(HomeStatCardDefaults.ContentPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HomeStatCardDefaults.ContentSpacing)
        ) {
            Box(
                modifier = Modifier
                    .size(HomeStatCardDefaults.IconContainerSize)
                    .background(
                        color = style.iconContainerColor,
                        shape = HomeStatCardDefaults.IconContainerShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(HomeStatCardDefaults.IconSize),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeStatCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeStatCard(
                valueText = "75%",
                labelText = "Roadmap",
                style = HomeStatCardDefaults.roadmapStyle(),
                icon = {}
            )
        }
    }
}
