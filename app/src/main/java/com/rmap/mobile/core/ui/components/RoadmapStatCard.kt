package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.RMapTheme

private val RoadmapStatCardShape = RoundedCornerShape(22.dp)

@Immutable
data class RoadmapStatItemUiModel(
    val valueText: String,
    val labelText: String,
    val icon: ImageVector
)

object RoadmapStatCardDefaults {
    val CardShape = RoadmapStatCardShape
    val CardMinWidth: Dp = 108.dp
    val CardHeight: Dp = 114.dp
    val IconSize: Dp = 22.dp
    val ContentSpacing: Dp = 8.dp
    val VerticalPadding: Dp = 17.dp
}

@Composable
fun RoadmapStatCard(
    valueText: String,
    labelText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
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
                minWidth = RoadmapStatCardDefaults.CardMinWidth,
                minHeight = RoadmapStatCardDefaults.CardHeight
            )
            .appCardShadow(shape = RoadmapStatCardDefaults.CardShape)
            .then(clickableModifier),
        shape = RoadmapStatCardDefaults.CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = AppCardDefaults.border()
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = RoadmapStatCardDefaults.VerticalPadding
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = RoadmapStatCardDefaults.ContentSpacing,
                alignment = Alignment.CenterVertically
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(RoadmapStatCardDefaults.IconSize)
            )

            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
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
fun RoadmapStatCardRow(
    items: List<RoadmapStatItemUiModel>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 12.dp,
    onItemClick: ((index: Int, item: RoadmapStatItemUiModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            RoadmapStatCard(
                valueText = item.valueText,
                labelText = item.labelText,
                icon = item.icon,
                modifier = Modifier
                    .weight(1f)
                    .height(RoadmapStatCardDefaults.CardHeight),
                onClick = onItemClick?.let { callback ->
                    {
                        callback(index, item)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 500)
@Composable
private fun RoadmapStatCardRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapStatCardRow(
            items = listOf(
                RoadmapStatItemUiModel(
                    valueText = "107",
                    labelText = "Total Lessons",
                    icon = Icons.AutoMirrored.Outlined.MenuBook
                ),
                RoadmapStatItemUiModel(
                    valueText = "1",
                    labelText = "Completed",
                    icon = Icons.Outlined.CheckCircle
                ),
                RoadmapStatItemUiModel(
                    valueText = "106",
                    labelText = "Remaining",
                    icon = Icons.Outlined.Schedule
                )
            )
        )
    }
}
