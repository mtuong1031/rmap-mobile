package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rmap.mobile.core.ui.components.RMapHeader

@Composable
fun BookmarkTopBar(
    greetingText: String,
    headingText: String,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {}
) {
    RMapHeader(
        greetingText = greetingText,
        headingText = headingText,
        modifier = modifier,
        onActionClick = onActionClick
    )
}
