package com.rmap.mobile.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomeCategoryIconSize = 16.dp

@Immutable
data class HomeCategoryItemUiModel(
    val id: String,
    val label: String,
    val countText: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

object HomeCategoryCardDefaults {
    val Shape = AppShapes.iconContainerLarge
}

@Composable
fun HomeCategoryCardRow(
    items: List<HomeCategoryItemUiModel>,
    modifier: Modifier = Modifier,
    onItemClick: ((index: Int, item: HomeCategoryItemUiModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            HomeCategoryCard(
                item = item,
                onClick = onItemClick?.let { callback ->
                    {
                        callback(index, item)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.width(Dimens.spacingMdPlus))
    }
}

@Composable
fun HomeCategoryCard(
    item: HomeCategoryItemUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val containerColor = if (item.selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (item.selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val countContainerColor = if (item.selected) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val countContentColor = if (item.selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.secondary
    }
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
            .cardShadow(shape = HomeCategoryCardDefaults.Shape)
            .clip(HomeCategoryCardDefaults.Shape)
            .background(
                color = containerColor,
                shape = HomeCategoryCardDefaults.Shape
            )
            .border(
                width = Dimens.borderThin,
                color = if (item.selected) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer,
                shape = HomeCategoryCardDefaults.Shape
            )
            .then(clickModifier)
            .padding(horizontal = Dimens.spacingLgPlus, vertical = Dimens.spacingSmPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(HomeCategoryIconSize)
        )

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )

        HomeCategoryCountBadge(
            countText = item.countText,
            containerColor = countContainerColor,
            contentColor = countContentColor
        )
    }
}

@Composable
private fun HomeCategoryCountBadge(
    countText: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = containerColor,
                shape = AppShapes.chip
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingXxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = countText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeCategoryCardRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeCategoryCardRow(
                items = listOf(
                    HomeCategoryItemUiModel(
                        id = "on-click",
                        label = "On Click",
                        countText = "24",
                        icon = Icons.Outlined.Public,
                        selected = true
                    ),
                    HomeCategoryItemUiModel(
                        id = "normal",
                        label = "Normal",
                        countText = "18",
                        icon = Icons.Outlined.PhoneAndroid
                    ),
                    HomeCategoryItemUiModel(
                        id = "devops",
                        label = "DevOps",
                        countText = "12",
                        icon = Icons.Outlined.Settings
                    ),
                    HomeCategoryItemUiModel(
                        id = "data",
                        label = "Data",
                        countText = "8",
                        icon = Icons.Outlined.Storage
                    ),
                    HomeCategoryItemUiModel(
                        id = "design",
                        label = "Design",
                        countText = "15",
                        icon = Icons.Outlined.Palette
                    ),
                    HomeCategoryItemUiModel(
                        id = "ai",
                        label = "AI",
                        countText = "9",
                        icon = Icons.Outlined.SmartToy
                    )
                )
            )
        }
    }
}
