package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.CardDividerColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralDisabledColor
import com.rmap.mobile.core.ui.theme.RMapTheme

@Immutable
data class RoadmapTextInputColors(
    val containerColor: Color,
    val contentColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color,
    val borderColor: Color,
    val shadowColor: Color
)

object RoadmapTextInputDefaults {
    val Shape: Shape = AppShapes.searchBar
    val Height: Dp = Dimens.controlXl
    val BorderWidth: Dp = Dimens.borderThin
    val ShadowElevationSmall: Dp = Dimens.cardElevationPressed
    val ShadowElevationMedium: Dp = 3.dp
    val ShadowColor: Color = Color(0x1A000000)
    val ContentPadding = PaddingValues(
        start = Dimens.spacingLg,
        end = Dimens.spacingLg
    )
    val LeadingIconSpacing: Dp = Dimens.spacingMd

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        placeholderColor: Color = NeutralDisabledColor,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        borderColor: Color = CardDividerColor,
        shadowColor: Color = ShadowColor
    ): RoadmapTextInputColors {
        return RoadmapTextInputColors(
            containerColor = containerColor,
            contentColor = contentColor,
            placeholderColor = placeholderColor,
            cursorColor = cursorColor,
            borderColor = borderColor,
            shadowColor = shadowColor
        )
    }

    fun border(
        color: Color,
        width: Dp = BorderWidth
    ): BorderStroke {
        return BorderStroke(width = width, color = color)
    }
}

@Composable
fun RoadmapTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    colors: RoadmapTextInputColors = RoadmapTextInputDefaults.colors(),
    shape: Shape = RoadmapTextInputDefaults.Shape,
    width: Dp? = null,
    height: Dp = RoadmapTextInputDefaults.Height,
    contentPadding: PaddingValues = RoadmapTextInputDefaults.ContentPadding,
    leadingIconSpacing: Dp = RoadmapTextInputDefaults.LeadingIconSpacing,
    border: BorderStroke? = RoadmapTextInputDefaults.border(colors.borderColor),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: (@Composable () -> Unit)? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .shadow(
                elevation = RoadmapTextInputDefaults.ShadowElevationSmall,
                shape = shape,
                ambientColor = colors.shadowColor,
                spotColor = colors.shadowColor
            )
            .shadow(
                elevation = RoadmapTextInputDefaults.ShadowElevationMedium,
                shape = shape,
                ambientColor = colors.shadowColor,
                spotColor = colors.shadowColor
            )
            .then(
                if (border != null) {
                    Modifier.border(
                        border = border,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .background(
                color = colors.containerColor,
                shape = shape
            ),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        textStyle = textStyle.copy(
            color = textStyle.color.takeOrElse { colors.contentColor }
        ),
        cursorBrush = SolidColor(colors.cursorColor),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    leadingIcon?.invoke()
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (leadingIcon != null) {
                                Modifier.padding(start = leadingIconSpacing)
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = textStyle.copy(color = colors.placeholderColor),
                            maxLines = 1
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapTextInputPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapTextInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(Dimens.spacingXxl),
            width = 342.dp,
            placeholder = "Search roadmap, skill, or role...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = RoadmapTextInputDefaults.colors().placeholderColor
                )
            }
        )
    }
}
