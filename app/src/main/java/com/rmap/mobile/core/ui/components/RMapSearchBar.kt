package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun RMapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    colors: RMapTextInputColors = RMapTextInputDefaults.colors(),
    shape: Shape = RMapTextInputDefaults.Shape,
    height: Dp = RMapTextInputDefaults.Height,
    contentPadding: PaddingValues = RMapTextInputDefaults.ContentPadding,
    leadingIcon: (@Composable () -> Unit)? = {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = colors.placeholderColor,
            modifier = Modifier.size(Dimens.iconMd)
        )
    },
    trailingIcon: (@Composable () -> Unit)? = null,
    showClearButton: Boolean = true,
    enabled: Boolean = true,
    border: BorderStroke? = RMapTextInputDefaults.border(colors.borderColor),
    leadingIconSpacing: Dp = RMapTextInputDefaults.LeadingIconSpacing,
    trailingIconSpacing: Dp = RMapTextInputDefaults.LeadingIconSpacing
) {
    val textInputModifier = if (readOnly) {
        Modifier.fillMaxWidth()
    } else {
        modifier.then(
            focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
        )
    }

    if (readOnly) {
        Box(modifier = modifier) {
            RMapTextInput(
                value = query,
                onValueChange = onQueryChange,
                modifier = textInputModifier,
                placeholder = placeholder,
                enabled = enabled,
                readOnly = true,
                textStyle = textStyle,
                colors = colors,
                shape = shape,
                height = height,
                contentPadding = contentPadding,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                showClearButton = showClearButton,
                border = border,
                leadingIconSpacing = leadingIconSpacing,
                trailingIconSpacing = trailingIconSpacing
            )

            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        )
                )
            }
        }
    } else {
        RMapTextInput(
            value = query,
            onValueChange = onQueryChange,
            modifier = textInputModifier,
            placeholder = placeholder,
            enabled = enabled,
            readOnly = false,
            textStyle = textStyle,
            colors = colors,
            shape = shape,
            height = height,
            contentPadding = contentPadding,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            showClearButton = showClearButton,
            border = border,
            leadingIconSpacing = leadingIconSpacing,
            trailingIconSpacing = trailingIconSpacing
        )
    }
}

@Preview(showBackground = true, name = "RMap Search Bar - Active", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapSearchBarActivePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapSearchBar(
            query = "Kotlin",
            onQueryChange = {},
            modifier = Modifier.padding(Dimens.spacingXxl),
            placeholder = "Search roadmap..."
        )
    }
}

@Preview(showBackground = true, name = "RMap Search Bar - ReadOnly", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapSearchBarReadOnlyPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.padding(Dimens.spacingXxl),
            placeholder = "Search roadmap...",
            readOnly = true,
            onClick = {}
        )
    }
}
