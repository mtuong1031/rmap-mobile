package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun HomeSearchHeader(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeSearchInputHeight),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeSearchIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)
        HomeSearchTextField(
            query = query,
            placeholder = placeholder,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )
        HomeSearchIconButton(
            icon = Icons.Outlined.Tune,
            onClick = onFilterClick,
            iconSize = Dimens.iconMdPlus
        )
    }
}

@Composable
private fun HomeSearchTextField(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RMapTextInput(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(Dimens.iconSmPlus)
            )
        },
        textStyle = MaterialTheme.typography.labelLarge.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        ),
        shape = HomeSearchInputShape,
        height = HomeSearchInputHeight,
        colors = RMapTextInputDefaults.colors(
            borderColor = MaterialTheme.colorScheme.inversePrimary
        )
    )
}

@Composable
private fun HomeSearchIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = Dimens.iconLg
) {
    Box(
        modifier = modifier
            .size(HomeSearchHeaderActionSize)
            .clip(AppShapes.pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun HomeSearchHeaderPreview() {
    RMapTheme {
        HomeSearchHeader(
            query = "",
            placeholder = "Search skills, roadmaps...",
            onQueryChange = {},
            onBackClick = {},
            onFilterClick = {},
            modifier = Modifier.padding(Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun HomeSearchHeaderWithQueryPreview() {
    RMapTheme {
        HomeSearchHeader(
            query = "Android Developer",
            placeholder = "Search skills, roadmaps...",
            onQueryChange = {},
            onBackClick = {},
            onFilterClick = {},
            modifier = Modifier.padding(Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchTextFieldPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingMd)) {
            HomeSearchTextField(
                query = "",
                placeholder = "Search skills, roadmaps...",
                onQueryChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchIconButtonPreview() {
    RMapTheme {
        Row(
            modifier = Modifier.padding(Dimens.spacingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            HomeSearchIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = {}
            )
            HomeSearchIconButton(
                icon = Icons.Outlined.Tune,
                onClick = {},
                iconSize = Dimens.iconMdPlus
            )
        }
    }
}
