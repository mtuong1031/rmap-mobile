package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryBlueShadowColor
import com.rmap.mobile.core.ui.theme.RMapTheme

private val HeaderActionShape = AppShapes.searchBar

object HeaderDefaults {
    val ActionButtonSize: Dp = Dimens.controlXl
    val ActionButtonShape = HeaderActionShape
    val ActionIconSize: Dp = Dimens.iconXl
    val GreetingIconSize: Dp = Dimens.iconSmPlus
    val HeadingSpacing: Dp = Dimens.spacingXxl
    val GreetingSpacing: Dp = Dimens.spacingSm
    val SectionSpacing: Dp = Dimens.spacingSm
}

@Composable
fun Header(
    greetingText: String,
    headingText: String,
    modifier: Modifier = Modifier,
    greetingIcon: ImageVector = Icons.Outlined.WbSunny,
    actionIcon: ImageVector = Icons.Outlined.School,
    onActionClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val actionModifier = if (onActionClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onActionClick
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HeaderDefaults.HeadingSpacing),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(HeaderDefaults.SectionSpacing)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(HeaderDefaults.GreetingSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = greetingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(HeaderDefaults.GreetingIconSize)
                )
                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = headingText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(HeaderDefaults.ActionButtonSize)
                .shadow(
                    elevation = Dimens.cardElevationHeader,
                    spotColor = PrimaryBlueShadowColor,
                    ambientColor = PrimaryBlueShadowColor
                )
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = HeaderDefaults.ActionButtonShape
                )
                .then(actionModifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(HeaderDefaults.ActionIconSize)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 180)
@Composable
private fun HeaderPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Header(
            greetingText = "Good morning, Thinh Hoang Duy",
            headingText = "Ready to learn today with rmap?",
            onActionClick = {}
        )
    }
}
