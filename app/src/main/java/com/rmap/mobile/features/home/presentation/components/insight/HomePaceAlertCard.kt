package com.rmap.mobile.features.home.presentation.components.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.LocalRMapSemanticColors
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val HomePaceAlertIconContainerSize = 28.dp
private val HomePaceAlertIconSize = 16.dp
private val HomePaceAlertActionIconSize = 14.dp

@Composable
fun HomePaceAlertCard(
    message: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val warningColors = LocalRMapSemanticColors.current.warning

    Row(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(shape = AppShapes.card)
            .clip(AppShapes.card)
            .background(warningColors.container)
            .border(
                width = Dimens.borderThin,
                color = warningColors.border,
                shape = AppShapes.card
            )
            .padding(Dimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(HomePaceAlertIconContainerSize)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = AppShapes.pill
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = warningColors.accent,
                modifier = Modifier.size(HomePaceAlertIconSize)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = warningColors.content
                )
            )

            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onActionClick
                ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXxs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = warningColors.accent
                    )
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = warningColors.accent,
                    modifier = Modifier.size(HomePaceAlertActionIconSize)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomePaceAlertCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomePaceAlertCard(
                message = "You are 15% behind your target pace.\nFinish 1 skill node today to back the track.",
                actionText = "Adjust plan",
                onActionClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF15151B, widthDp = 390)
@Composable
private fun HomePaceAlertCardDarkPreview() {
    RMapTheme(darkTheme = true, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomePaceAlertCard(
                message = "You are 15% behind your target pace.\nFinish 1 skill node today to back the track.",
                actionText = "Adjust plan",
                onActionClick = {}
            )
        }
    }
}
