package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults

enum class RMapButtonVariant {
    Primary,
    Secondary,
    Outline,
    Neutral
}

enum class RMapButtonSize(
    val height: Dp,
    val radius: Dp,
    val iconSize: Dp
) {
    Large(height = 56.dp, radius = 16.dp, iconSize = 16.dp),
    Medium(height = 48.dp, radius = 14.dp, iconSize = 14.dp),
    Small(height = 40.dp, radius = 10.dp, iconSize = 12.dp),
    XSmall(height = 36.dp, radius = 10.dp, iconSize = 12.dp)
}

object RMapButtonDefaults {
    val IconSpacing: Dp = Dimens.spacingSm

    @Composable
    fun colors(variant: RMapButtonVariant): ButtonColors {
        return when (variant) {
            RMapButtonVariant.Primary -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(OnSurfacePlaceholderLight.value)
            )

            RMapButtonVariant.Secondary -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(OnSurfacePlaceholderLight.value)
            )

            RMapButtonVariant.Outline -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(OnSurfacePlaceholderLight.value)
            )

            RMapButtonVariant.Neutral -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(OnSurfacePlaceholderLight.value)
            )
        }
    }

    @Composable
    fun elevation(variant: RMapButtonVariant): ButtonElevation {
        return when (variant) {
            RMapButtonVariant.Primary -> MaterialButtonDefaults.buttonElevation(
                pressedElevation = Dimens.cardElevationPressed,
                focusedElevation = Dimens.cardElevationSm,
                hoveredElevation = Dimens.cardElevationSm,
                disabledElevation = Dimens.cardElevationNone
            )

            RMapButtonVariant.Secondary -> MaterialButtonDefaults.buttonElevation(
                pressedElevation = Dimens.cardElevationXs,
                focusedElevation = Dimens.cardElevationXs,
                hoveredElevation = Dimens.cardElevationXs,
                disabledElevation = Dimens.cardElevationNone
            )

            RMapButtonVariant.Outline,
            RMapButtonVariant.Neutral -> MaterialButtonDefaults.buttonElevation(
                pressedElevation = Dimens.cardElevationNone,
                focusedElevation = Dimens.cardElevationNone,
                hoveredElevation = Dimens.cardElevationNone,
                disabledElevation = Dimens.cardElevationNone
            )
        }
    }

    @Composable
    fun border(
        variant: RMapButtonVariant,
        enabled: Boolean
    ): BorderStroke? {
        if (!enabled) return null

        return when (variant) {
            RMapButtonVariant.Primary,
            RMapButtonVariant.Neutral -> null

            RMapButtonVariant.Secondary -> BorderStroke(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )

            RMapButtonVariant.Outline -> BorderStroke(
                width = Dimens.borderMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    fun contentPadding(size: RMapButtonSize): PaddingValues {
        val horizontal = when (size) {
            RMapButtonSize.Large -> 24.dp
            RMapButtonSize.Medium -> 20.dp
            RMapButtonSize.Small -> 16.dp
            RMapButtonSize.XSmall -> 14.dp
        }

        return PaddingValues(horizontal = horizontal, vertical = Dimens.spacingNone)
    }
}

@Composable
fun RMapButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: RMapButtonVariant = RMapButtonVariant.Primary,
    size: RMapButtonSize = RMapButtonSize.Large,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: ButtonColors? = null,
    elevation: ButtonElevation? = null,
    border: BorderStroke? = RMapButtonDefaults.border(variant, enabled),
    textStyle: TextStyle? = null
) {
    MaterialButton(
        onClick = onClick,
        modifier = modifier
            .height(size.height)
            .defaultMinSize(minHeight = size.height),
        enabled = enabled,
        shape = RoundedCornerShape(size.radius),
        colors = colors ?: RMapButtonDefaults.colors(variant),
        elevation = elevation ?: RMapButtonDefaults.elevation(variant),
        border = border,
        contentPadding = RMapButtonDefaults.contentPadding(size)
    ) {
        RMapButtonContent(
            text = text,
            size = size,
            textStyle = textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
private fun RMapButtonContent(
    text: String,
    size: RMapButtonSize,
    textStyle: TextStyle?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            RMapButtonIcon(size = size, icon = leadingIcon)
            Spacer(modifier = Modifier.width(RMapButtonDefaults.IconSpacing))
        }

        Text(
            text = text,
            style = textStyle ?: size.textStyle(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(RMapButtonDefaults.IconSpacing))
            RMapButtonIcon(size = size, icon = trailingIcon)
        }
    }
}

@Composable
private fun RMapButtonIcon(
    size: RMapButtonSize,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.size(size.iconSize),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun RMapButtonSize.textStyle(): TextStyle {
    return when (this) {
        RMapButtonSize.Large -> MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold
        )

        RMapButtonSize.Medium -> MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        )

        RMapButtonSize.Small,
        RMapButtonSize.XSmall -> MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RMapButtonPreview() {
    RMapTheme(dynamicColor = false) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RMapButton(
                text = "Continue",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
            )

            RMapButton(
                text = "Explore ready-made",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
            )

            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Outline,
                size = RMapButtonSize.Medium
            )

            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.XSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RMapButtonDisabledPreview() {
    RMapTheme(dynamicColor = false) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RMapButton(
                text = "Continue",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                enabled = false
            )

            RMapButton(
                text = "Explore ready-made",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
                enabled = false
            )

            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Outline,
                size = RMapButtonSize.Medium,
                enabled = false
            )

            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.XSmall,
                enabled = false
            )
        }
    }
}
