package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun ExploreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.exploreSearchBarHeight),
        contentAlignment = Alignment.Center
    ) {
        RMapTextInput(
            value = query,
            onValueChange = onQueryChange,
            placeholder = stringResource(R.string.explore_search_placeholder),
            textStyle = MaterialTheme.typography.bodyLarge,
            height = Dimens.exploreSearchBarHeight,
            contentPadding = PaddingValues(
                start = Dimens.spacingLg,
                end = Dimens.controlSm + Dimens.spacingXl
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = RMapTextInputDefaults.colors().placeholderColor,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Dimens.spacingSm)
                .size(Dimens.controlSm)
                .clip(AppShapes.iconContainer)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onFilterClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = stringResource(R.string.content_description_filter),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconSmPlus)
            )
        }
    }
}

@Preview(showBackground = true, name = "Explore Search Bar", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ExploreSearchBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ExploreSearchBar(
            query = "",
            onQueryChange = {},
            onFilterClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
