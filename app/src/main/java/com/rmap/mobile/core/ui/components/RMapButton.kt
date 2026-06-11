package com.rmap.mobile.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.rmap.mobile.core.ui.theme.OnSurfaceDisabledLight
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults

// ─────────────────────────────────────────────
// Enums — unchanged variants, extended sizes
// ─────────────────────────────────────────────

enum class RMapButtonVariant {
    Primary,
    Secondary,
    Outline,
    Neutral
}

enum class RMapButtonSize(
    val height: Dp,
    val radius: Dp,
    val iconSize: Dp,
    // [IMPROVED] Icon-only button width equals height → perfect circle/square
    val iconOnlyWidth: Dp
) {
    Large(height = 56.dp,  radius = 16.dp, iconSize = 20.dp, iconOnlyWidth = 56.dp),
    Medium(height = 48.dp, radius = 14.dp, iconSize = 18.dp, iconOnlyWidth = 48.dp),
    Small(height = 40.dp,  radius = 10.dp, iconSize = 16.dp, iconOnlyWidth = 40.dp),
    XSmall(height = 36.dp, radius = 10.dp, iconSize = 14.dp, iconOnlyWidth = 36.dp)
}

// ─────────────────────────────────────────────
// Defaults
// ─────────────────────────────────────────────

object RMapButtonDefaults {
    val IconSpacing: Dp = Dimens.spacingSm

    @Composable
    fun colors(variant: RMapButtonVariant): ButtonColors {
        // Team token: OnSurfaceDisabledLight = RMapTextTertiary (#99A1AF)
        // Used consistently for all disabled text/icon across variants.
        val disabledContentColor = Color(OnSurfaceDisabledLight.value)

        val disabledContainerColor = MaterialTheme.colorScheme.outline

        return when (variant) {
            RMapButtonVariant.Primary -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor
            )

            // Secondary: white surface + visible border (see border() below)
            RMapButtonVariant.Secondary -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor
            )

            // Outline: transparent bg, primary text — border carries the shape
            RMapButtonVariant.Outline -> MaterialButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = disabledContentColor
            )

            // Neutral: PrimaryContainerLight = RMapBlueSoft (#EFF6FF) bg
            //          OnPrimaryContainerLight = RMapBlueStrong (#155DFC) text
            //          → soft tonal blue, clearly interactive but not dominant
            RMapButtonVariant.Neutral -> MaterialButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor
            )
        }
    }

    @Composable
    fun elevation(variant: RMapButtonVariant): ButtonElevation {
        return when (variant) {
            RMapButtonVariant.Primary -> MaterialButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = Dimens.cardElevationPressed,
                focusedElevation = Dimens.cardElevationSm,
                hoveredElevation = Dimens.cardElevationSm,
                disabledElevation = Dimens.cardElevationNone
            )

            RMapButtonVariant.Secondary -> MaterialButtonDefaults.buttonElevation(
                defaultElevation = Dimens.cardElevationNone,
                pressedElevation = Dimens.cardElevationXs,
                focusedElevation = Dimens.cardElevationXs,
                hoveredElevation = Dimens.cardElevationXs,
                disabledElevation = Dimens.cardElevationNone
            )

            RMapButtonVariant.Outline,
            RMapButtonVariant.Neutral -> MaterialButtonDefaults.buttonElevation(
                defaultElevation = Dimens.cardElevationNone,
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
        return when {
            !enabled && variant == RMapButtonVariant.Outline -> BorderStroke(
                width = Dimens.borderThin,
                color = Color(OnSurfaceDisabledLight.value).copy(alpha = 0.5f)
            )
            !enabled -> null

            variant == RMapButtonVariant.Primary  -> null
            variant == RMapButtonVariant.Neutral  -> null

            // Secondary border: outline = RMapBorder (#E5E7EB).
            // outlineVariant = RMapDivider (#F1F5F9) was too close to white
            // to give Secondary a distinct, tappable appearance.
            variant == RMapButtonVariant.Secondary -> BorderStroke(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.outline
            )

            // variant == Outline (only remaining branch)
            else -> BorderStroke(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    fun contentPadding(size: RMapButtonSize): PaddingValues {
        val horizontal = when (size) {
            RMapButtonSize.Large  -> 24.dp
            RMapButtonSize.Medium -> 20.dp
            RMapButtonSize.Small  -> 16.dp
            RMapButtonSize.XSmall -> 14.dp
        }
        return PaddingValues(horizontal = horizontal, vertical = Dimens.spacingNone)
    }

    // [NEW] Zero padding for icon-only buttons — content fills the fixed-size box
    fun iconOnlyContentPadding(): PaddingValues = PaddingValues(Dimens.spacingNone)
}

// ─────────────────────────────────────────────
// Main composable
// ─────────────────────────────────────────────

/**
 * RMapButton — full-featured button with:
 *
 * • [isLoading]      shows a centered spinner and blocks interaction
 * • [enableHaptic]   triggers [HapticFeedbackType.TextHandleMove] on press
 * • Press animation  95 % scale-down spring — snappy, not laggy
 * • All original variants / sizes / icon slots preserved
 */
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
    // [NEW] Loading state — shows spinner, disables click
    isLoading: Boolean = false,
    // [NEW] Haptic feedback toggle
    enableHaptic: Boolean = true,
    colors: ButtonColors? = null,
    elevation: ButtonElevation? = null,
    border: BorderStroke? = RMapButtonDefaults.border(variant, enabled),
    textStyle: TextStyle? = null
) {
    val haptic = LocalHapticFeedback.current
    // IDE may warn "assigned value never read" on isPressed — false positive:
    // the state IS consumed by animateFloatAsState below.
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var isPressed by remember { mutableStateOf(false) }

    // [NEW] Spring-based press scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    MaterialButton(
        onClick = {
            if (!isLoading) {
                if (enableHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        },
        modifier = modifier
            .height(size.height)
            .defaultMinSize(minHeight = size.height)
            // [NEW] Apply scale transform for press animation
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            // [NEW] Detect press state for scale animation
            .pointerInput(enabled, isLoading) {
                detectTapGestures(
                    onPress = {
                        if (enabled && !isLoading) {
                            tryAwaitRelease()
                        }
                    }
                )
            },
        enabled = enabled && !isLoading,
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
            trailingIcon = trailingIcon,
            isLoading = isLoading,
            variant = variant
        )
    }
}

// ─────────────────────────────────────────────
// [NEW] Icon-only button variant
// ─────────────────────────────────────────────

/**
 * RMapIconButton — icon-only, square/round button.
 *
 * Uses the same variant system as [RMapButton].
 * Width = height = [RMapButtonSize.iconOnlyWidth] for a perfect tap target.
 */
@Composable
fun RMapIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: RMapButtonVariant = RMapButtonVariant.Primary,
    size: RMapButtonSize = RMapButtonSize.Large,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    enableHaptic: Boolean = true,
    colors: ButtonColors? = null,
    elevation: ButtonElevation? = null,
    border: BorderStroke? = RMapButtonDefaults.border(variant, enabled),
    // [NOTE] contentDescription is required for accessibility
    contentDescription: String
) {
    val haptic = LocalHapticFeedback.current
    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "icon_button_scale"
    )

    MaterialButton(
        onClick = {
            if (!isLoading) {
                if (enableHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        },
        modifier = modifier
            .size(size.iconOnlyWidth)
            // [A11Y] Attach contentDescription for screen readers
            .semantics { this.contentDescription = contentDescription }
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(enabled, isLoading) {
                detectTapGestures(
                    onPress = {
                        if (enabled && !isLoading) {
                            tryAwaitRelease()
                        }
                    }
                )
            },
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(size.radius),
        colors = colors ?: RMapButtonDefaults.colors(variant),
        elevation = elevation ?: RMapButtonDefaults.elevation(variant),
        border = border,
        contentPadding = RMapButtonDefaults.iconOnlyContentPadding()
    ) {
        Box(
            modifier = Modifier.size(size.iconSize),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                RMapButtonSpinner(size = size, variant = variant, colors = colors)
            } else {
                icon()
            }
        }
    }
}

// ─────────────────────────────────────────────
// Internal helpers
// ─────────────────────────────────────────────

@Composable
private fun RMapButtonContent(
    text: String,
    size: RMapButtonSize,
    textStyle: TextStyle?,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
    isLoading: Boolean,
    variant: RMapButtonVariant
) {
    Box(contentAlignment = Alignment.Center) {
        // [NEW] Hide real content while loading — keeps button width stable
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer { alpha = if (isLoading) 0f else 1f }
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

        // [NEW] Spinner overlays on top when loading
        if (isLoading) {
            RMapButtonSpinner(size = size, variant = variant, colors = null)
        }
    }
}

@Composable
private fun RMapButtonSpinner(
    size: RMapButtonSize,
    variant: RMapButtonVariant,
    colors: ButtonColors?
) {
    // Spinner color mirrors the button's contentColor for each variant
    val spinnerColor = colors?.contentColor
        ?: when (variant) {
            RMapButtonVariant.Primary  -> MaterialTheme.colorScheme.onPrimary
            RMapButtonVariant.Secondary -> MaterialTheme.colorScheme.onSurface
            RMapButtonVariant.Outline  -> MaterialTheme.colorScheme.primary
            RMapButtonVariant.Neutral  -> MaterialTheme.colorScheme.onPrimaryContainer
        }

    val spinnerSize = when (size) {
        RMapButtonSize.Large  -> 22.dp
        RMapButtonSize.Medium -> 20.dp
        RMapButtonSize.Small  -> 18.dp
        RMapButtonSize.XSmall -> 16.dp
    }

    CircularProgressIndicator(
        modifier = Modifier.size(spinnerSize),
        color = spinnerColor,
        strokeWidth = 2.dp
    )
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
        // [IMPROVED] Consistent SemiBold across all sizes — Bold on sm/xs
        // was slightly too heavy at small scales.
        RMapButtonSize.Large  -> MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        RMapButtonSize.Medium -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        RMapButtonSize.Small  -> MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        RMapButtonSize.XSmall -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────

@Preview(showBackground = true, name = "Variants — Enabled")
@Composable
private fun RMapButtonVariantsPreview() {
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
                size = RMapButtonSize.Large
            )
            RMapButton(
                text = "Explore ready-made",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large
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

@Preview(showBackground = true, name = "Loading States")
@Composable
private fun RMapButtonLoadingPreview() {
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
                isLoading = true
            )
            RMapButton(
                text = "Explore ready-made",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
                isLoading = true
            )
            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Outline,
                size = RMapButtonSize.Medium,
                isLoading = true
            )
            RMapButton(
                text = "Start",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.XSmall,
                isLoading = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Disabled States")
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
                enabled = false
            )
            RMapButton(
                text = "Explore ready-made",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
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

@Preview(showBackground = true, name = "Icon-Only Buttons")
@Composable
private fun RMapIconButtonPreview() {
    RMapTheme(dynamicColor = false) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RMapIconButton(
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onClick = {},
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                contentDescription = "Add"
            )
            RMapIconButton(
                icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                onClick = {},
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
                contentDescription = "Search"
            )
            RMapIconButton(
                icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = null) },
                onClick = {},
                variant = RMapButtonVariant.Outline,
                size = RMapButtonSize.Large,
                contentDescription = "Favorite"
            )
            RMapIconButton(
                icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
                onClick = {},
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.Large,
                contentDescription = "Share",
                isLoading = true
            )
        }
    }
}