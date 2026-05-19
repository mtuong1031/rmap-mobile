package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.icons.RMapIcons
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.CardDividerColor
import com.rmap.mobile.core.ui.theme.CardShadowOverlayColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

private val HeaderActionShape = AppShapes.searchBar

object HeaderDefaults {
    val ActionButtonSize: Dp = Dimens.controlLg
    val ActionButtonShape = HeaderActionShape
    val ActionIconSize: Dp = Dimens.iconMdPlus
    val GreetingIconSize: Dp = Dimens.iconSm
    val GreetingSpacing: Dp = Dimens.spacingSm
    val SectionSpacing: Dp = Dimens.spacingXsPlus
    val TextMaxWidth: Dp = 274.dp
}

@Composable
fun Header(
    greetingText: String,
    headingText: String,
    modifier: Modifier = Modifier,
    greetingIcon: ImageVector = RMapIcons.Map,
    actionIcon: ImageVector = Icons.Outlined.Person,
    actionContentDescription: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedActionContentDescription = actionContentDescription
        ?: if (onActionClick != null) {
            stringResource(R.string.content_description_header_action)
        } else {
            null
        }
    val actionSemanticsModifier = if (resolvedActionContentDescription != null) {
        Modifier.semantics {
            contentDescription = resolvedActionContentDescription
            role = Role.Button
        }
    } else {
        Modifier
    }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = HeaderDefaults.TextMaxWidth),
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
                    style = AppTextStyles.headerGreeting.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = headingText,
                style = AppTextStyles.headerTitle.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(HeaderDefaults.ActionButtonSize)
                .shadow(
                    elevation = Dimens.cardElevationPressed,
                    shape = HeaderDefaults.ActionButtonShape,
                    spotColor = CardShadowOverlayColor,
                    ambientColor = CardShadowOverlayColor
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = HeaderDefaults.ActionButtonShape
                )
                .border(
                    width = Dimens.borderThin,
                    color = CardDividerColor,
                    shape = HeaderDefaults.ActionButtonShape
                )
                .then(actionSemanticsModifier)
                .then(actionModifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
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
            headingText = "Ready for your next skill?",
            onActionClick = {}
        )
    }
}
