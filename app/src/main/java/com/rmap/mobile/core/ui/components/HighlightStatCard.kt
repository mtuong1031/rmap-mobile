package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.RMapTheme

private val HighlightStatCardShape = RoundedCornerShape(22.dp)

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
    val CardMinWidth: Dp = 108.dp
    val CardMinHeight: Dp = 128.dp
    val IconContainerSize: Dp = 40.dp
    val IconSize: Dp = 20.dp
    val ContentSpacing: Dp = 8.dp

    @Composable
    fun streakStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconColor = Color(0xFFF59E0B),
            iconContainerColor = Color(0xFFFFFBEB)
        )
    }

    @Composable
    fun goalStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconContainerColor = Color(0xFFECFDF5),
            iconColor = Color(0xFF10B981),
        )
    }

    @Composable
    fun completedStyle(): HighlightStatCardStyle {
        return HighlightStatCardStyle(
            iconContainerColor = Color(0xFFEFF6FF),
            iconColor = Color(0xFF298CF7),
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

    Surface(
        modifier = modifier
            .defaultMinSize(
                minWidth = HighlightStatCardDefaults.CardMinWidth,
                minHeight = HighlightStatCardDefaults.CardMinHeight
            )
            .shadow(
                elevation = 24.dp,
                shape = HighlightStatCardDefaults.CardShape,
                ambientColor = Color(0x0F000000),
                spotColor = Color(0x0F000000)
            )
            .then(clickableModifier),
        shape = HighlightStatCardDefaults.CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, style.iconColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
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
                textAlign = TextAlign.Center,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
            )


            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                ),
                textAlign = TextAlign.Center,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HighlightStatCardRow(
    items: List<HighlightStatItemUiModel>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 12.dp,
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

