package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralBorderColor
import com.rmap.mobile.core.ui.theme.RMapTheme
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults
import androidx.compose.material3.FilledTonalButton as M3FilledTonalButton
import androidx.compose.material3.OutlinedButton as M3OutlinedButton
import androidx.compose.material3.TextButton as M3TextButton

object AppButtonDefaults {
    val MinHeight: Dp = Dimens.controlXl
    val Shape: Shape = AppShapes.button
    val IconSize: Dp = Dimens.iconLg
    val IconSpacing: Dp = Dimens.spacingMd

    @Composable
    fun filledButtonColors(): ButtonColors {
        return MaterialButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    @Composable
    fun filledTonalButtonColors(): ButtonColors {
        return MaterialButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    @Composable
    fun outlinedButtonColors(): ButtonColors {
        return MaterialButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun textButtonColors(): ButtonColors {
        return MaterialButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun filledButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.buttonElevation(
            defaultElevation = Dimens.cardElevationSm,
            pressedElevation = Dimens.cardElevationPressed,
            focusedElevation = Dimens.cardElevationSm,
            hoveredElevation = Dimens.cardElevationSm,
            disabledElevation = Dimens.cardElevationNone
        )
    }

    @Composable
    fun filledTonalButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.filledTonalButtonElevation(
            defaultElevation = Dimens.cardElevationXs,
            pressedElevation = Dimens.cardElevationPressed,
            focusedElevation = Dimens.cardElevationXs,
            hoveredElevation = Dimens.cardElevationXs,
            disabledElevation = Dimens.cardElevationNone
        )
    }

    @Composable
    fun outlinedButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.buttonElevation(
            defaultElevation = Dimens.cardElevationNone,
            pressedElevation = Dimens.cardElevationNone,
            focusedElevation = Dimens.cardElevationNone,
            hoveredElevation = Dimens.cardElevationNone,
            disabledElevation = Dimens.cardElevationNone
        )
    }

    @Composable
    fun textButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.buttonElevation(
            defaultElevation = Dimens.cardElevationNone,
            pressedElevation = Dimens.cardElevationNone,
            focusedElevation = Dimens.cardElevationNone,
            hoveredElevation = Dimens.cardElevationNone,
            disabledElevation = Dimens.cardElevationNone
        )
    }

    @Composable
    fun filledTonalBorder(): BorderStroke {
        return BorderStroke(width = Dimens.borderThin, color = NeutralBorderColor)
    }

    @Composable
    fun outlinedBorder(): BorderStroke {
        return BorderStroke(width = Dimens.borderThin, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    }
}

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AppButtonDefaults.Shape,
    colors: ButtonColors = AppButtonDefaults.filledButtonColors(),
    elevation: ButtonElevation = AppButtonDefaults.filledButtonElevation()
) {
    M3Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AppButtonDefaults.MinHeight),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation
    ) {
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun FilledTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AppButtonDefaults.Shape,
    colors: ButtonColors = AppButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation = AppButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke = AppButtonDefaults.filledTonalBorder()
) {
    M3FilledTonalButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AppButtonDefaults.MinHeight),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AppButtonDefaults.Shape,
    colors: ButtonColors = AppButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation = AppButtonDefaults.outlinedButtonElevation(),
    border: BorderStroke = AppButtonDefaults.outlinedBorder()
) {
    M3OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AppButtonDefaults.MinHeight),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = AppButtonDefaults.Shape,
    colors: ButtonColors = AppButtonDefaults.textButtonColors(),
    elevation: ButtonElevation = AppButtonDefaults.textButtonElevation(),
    border: BorderStroke? = null
) {
    M3TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = AppButtonDefaults.MinHeight),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        ButtonContent(
            text = text,
            leadingIcon = leadingIcon
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leadingIcon: (@Composable () -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier.size(AppButtonDefaults.IconSize),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }

            Spacer(modifier = Modifier.width(AppButtonDefaults.IconSpacing))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonsPreview() {
    RMapTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            FilledButton(
                text = "Continue with Facebook",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_facebook),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )
                }
            )

            FilledTonalButton(
                text = "Continue with Google",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_google),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Unspecified
                    )
                }
            )

            OutlinedButton(
                text = "Create Account",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                text = "Need an account?",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
