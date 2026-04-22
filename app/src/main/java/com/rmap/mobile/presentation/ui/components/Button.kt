package com.rmap.mobile.presentation.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.presentation.ui.theme.RMapTheme
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults
import androidx.compose.material3.FilledTonalButton as M3FilledTonalButton
import androidx.compose.material3.OutlinedButton as M3OutlinedButton
import androidx.compose.material3.TextButton as M3TextButton

object ButtonDefaults {
    val MinHeight: Dp = 56.dp
    val Shape: Shape = RoundedCornerShape(16.dp)
    val IconSize: Dp = 24.dp
    val IconSpacing: Dp = 12.dp

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
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            focusedElevation = 8.dp,
            hoveredElevation = 8.dp,
            disabledElevation = 0.dp
        )
    }

    @Composable
    fun filledTonalButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.filledTonalButtonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 1.dp,
            hoveredElevation = 1.dp,
            disabledElevation = 0.dp
        )
    }

    @Composable
    fun outlinedButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        )
    }

    @Composable
    fun textButtonElevation(): ButtonElevation {
        return MaterialButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        )
    }

    @Composable
    fun filledTonalBorder(): BorderStroke {
        return BorderStroke(width = 1.dp, color = Color(0xFFE5E7EB))
    }

    @Composable
    fun outlinedBorder(): BorderStroke {
        return BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    }
}

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.Shape,
    colors: ButtonColors = ButtonDefaults.filledButtonColors(),
    elevation: ButtonElevation = ButtonDefaults.filledButtonElevation()
) {
    M3Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = ButtonDefaults.MinHeight),
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
    shape: Shape = ButtonDefaults.Shape,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke = ButtonDefaults.filledTonalBorder()
) {
    M3FilledTonalButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = ButtonDefaults.MinHeight),
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
    shape: Shape = ButtonDefaults.Shape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation = ButtonDefaults.outlinedButtonElevation(),
    border: BorderStroke = ButtonDefaults.outlinedBorder()
) {
    M3OutlinedButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = ButtonDefaults.MinHeight),
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
    shape: Shape = ButtonDefaults.Shape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation = ButtonDefaults.textButtonElevation(),
    border: BorderStroke? = null
) {
    M3TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = ButtonDefaults.MinHeight),
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
                modifier = Modifier.size(ButtonDefaults.IconSize),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }

            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
