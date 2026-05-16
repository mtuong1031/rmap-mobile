package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.InfoContainerColor
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.StatusSuccessContainerColor
import com.rmap.mobile.core.ui.theme.StatusSuccessContentColor
import com.rmap.mobile.core.ui.theme.StatusWarningContainerColor
import com.rmap.mobile.core.ui.theme.StatusWarningContentColor
import com.rmap.mobile.core.ui.theme.PrimaryLight

private val HighlightStatCardShape = AppShapes.statCard

@Immutable
data class HighlightStatCardStyle(
    val iconContainerColor: Color,
    val iconColor: Color,
)

@Immutable
data class HighlightStatItemUiModel(
    val valueText: String,
    val labelText: String,
    val icon: ImageVector,
    val style: HighlightStatCardStyle
)

object HighlightStatCardDefaults {
    val CardShape = HighlightStatCardShape
    val CardMinWidth: Dp = Dimens.statCardMinWidth
    val CardMinHeight: Dp = Dimens.statCardMinHeight
    val IconContainerSize: Dp = Dimens.controlSm
    val IconSize: Dp = Dimens.iconMd
    val ContentSpacing: Dp = Dimens.spacingSm

    @Composable
    fun streakStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconColor = StatusWarningContentColor,
            iconContainerColor = StatusWarningContainerColor
        )
    }

    @Composable
    fun goalStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconContainerColor = StatusSuccessContainerColor,
            iconColor = StatusSuccessContentColor,
        )
    }

    @Composable
    fun completedStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconContainerColor = InfoContainerColor,
            iconColor = PrimaryLight,
        )
    }
}

@Composable
fun HighlightStatCard(
    valueText: String,
    labelText: String,
    modifier: Modifier = Modifier,
    style: HighlightStatCardStyle = HighlightStatCardDefaults.streakStyle(),
    onClick: (() -> Unit)? = null,
    icon: @Composable BoxScope.() -> Unit,
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

    AppCard(
        modifier = modifier
            .defaultMinSize(
                minWidth = HighlightStatCardDefaults.CardMinWidth,
                minHeight = HighlightStatCardDefaults.CardMinHeight
            )
            .then(clickableModifier),
        shape = HighlightStatCardDefaults.CardShape,
        border = AppCardDefaults.border(color = style.iconColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = Dimens.spacingLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HighlightStatCardDefaults.ContentSpacing)
        ) {
            Box(
                modifier = Modifier
                    .size(HighlightStatCardDefaults.IconContainerSize)
                    .background(
                        color = style.iconContainerColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(HighlightStatCardDefaults.IconSize),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }


            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )


            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HighlightStatCardRow(
    items: List<HighlightStatItemUiModel>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = Dimens.spacingMd,
    onItemClick: ((index: Int, item: HighlightStatItemUiModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            HighlightStatCard(
                modifier = Modifier.weight(1f),
                valueText = item.valueText,
                labelText = item.labelText,
                style = item.style,
                onClick = onItemClick?.let { callback ->
                    {
                        callback(index, item)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.style.iconColor,
                        modifier = Modifier.size(HighlightStatCardDefaults.IconSize)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 500)
@Composable
private fun HighlightStatCardRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        HighlightStatCardRow(
            items = listOf(
                HighlightStatItemUiModel(
                    valueText = "2 days",
                    labelText = "Streak",
                    icon = Icons.Outlined.LocalFireDepartment,
                    style = HighlightStatCardDefaults.streakStyle()
                ),
                HighlightStatItemUiModel(
                    valueText = "2/3",
                    labelText = "Today's Goal",
                    icon = Icons.Outlined.TrackChanges,
                    style = HighlightStatCardDefaults.goalStyle()
                ),
                HighlightStatItemUiModel(
                    valueText = "5",
                    labelText = "Completed",
                    icon = Icons.Outlined.EmojiEvents,
                    style = HighlightStatCardDefaults.completedStyle()
                )
            )
        )
    }
}
