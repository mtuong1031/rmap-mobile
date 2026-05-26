package com.rmap.mobile.features.explore.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun ExploreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RMapTextInput(
        value = query,
        onValueChange = onQueryChange,
        placeholder = stringResource(R.string.explore_search_placeholder),
        textStyle = MaterialTheme.typography.bodyLarge,
        height = Dimens.exploreSearchBarHeight,
        contentPadding = PaddingValues(
            start = Dimens.spacingLg,
            end = Dimens.spacingLg
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.exploreSearchBarHeight),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = RMapTextInputDefaults.colors().placeholderColor,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    )
}

@Preview(showBackground = true, name = "Explore Search Bar", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ExploreSearchBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ExploreSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
