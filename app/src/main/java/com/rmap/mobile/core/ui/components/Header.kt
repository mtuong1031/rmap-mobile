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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val HeadingSpacing: Dp = Dimens.spacingXxl
    val GreetingSpacing: Dp = Dimens.spacingSm
    val SectionSpacing: Dp = Dimens.spacingXsPlus
    val TextMaxWidth: Dp = 274.dp
}

@Composable
fun Header(
    greetingText: String,
    headingText: String,
    modifier: Modifier = Modifier,
    greetingIcon: ImageVector = HeaderMapIcon,
    actionIcon: ImageVector = Icons.Outlined.Person,
    actionContentDescription: String? = null,
    actionVerticalAlignment: Alignment.Vertical = Alignment.Top,
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = actionVerticalAlignment
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
                .then(actionModifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = actionContentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(HeaderDefaults.ActionIconSize)
            )
        }
    }
}

private val HeaderMapIcon: ImageVector =
    ImageVector.Builder(
        name = "HeaderMapIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(14.106f, 5.553f)
            curveToRelative(0.563f, 0.281f, 1.225f, 0.281f, 1.788f, 0f)
            lineToRelative(3.659f, -1.83f)
            curveTo(20.217f, 3.392f, 21f, 3.874f, 21f, 4.618f)
            verticalLineToRelative(12.764f)
            curveToRelative(0f, 0.379f, -0.214f, 0.725f, -0.553f, 0.894f)
            lineToRelative(-4.553f, 2.277f)
            curveToRelative(-0.563f, 0.281f, -1.225f, 0.281f, -1.788f, 0f)
            lineToRelative(-4.212f, -2.106f)
            curveToRelative(-0.563f, -0.281f, -1.225f, -0.281f, -1.788f, 0f)
            lineToRelative(-3.659f, 1.83f)
            curveTo(3.783f, 20.608f, 3f, 20.126f, 3f, 19.382f)
            verticalLineTo(6.618f)
            curveToRelative(0f, -0.379f, 0.214f, -0.725f, 0.553f, -0.894f)
            lineToRelative(4.553f, -2.277f)
            curveToRelative(0.563f, -0.281f, 1.225f, -0.281f, 1.788f, 0f)
            lineToRelative(4.212f, 2.106f)
            close()
            moveTo(15f, 5.764f)
            verticalLineToRelative(15f)
            moveTo(9f, 3.236f)
            verticalLineToRelative(15f)
        }
    }.build()

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
