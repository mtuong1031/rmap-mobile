package com.rmap.mobile.features.bookmarks.presentation.components.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarkSearchBar(
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
        textStyle = MaterialTheme.typography.bodyMedium,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = RMapTextInputDefaults.colors().placeholderColor
            )
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkSearchBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkSearchBar(
            query = "",
            placeholder = "Search saved roadmaps or skills...",
            onQueryChange = {}
        )
    }
}
