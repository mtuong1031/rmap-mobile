package com.rmap.mobile.features.bookmarks.presentation.components.state

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapCardDefaults

@Composable
internal fun BookmarkStateContainer(
    modifier: Modifier = Modifier,
    showBorder: Boolean = true,
    content: @Composable () -> Unit
) {
    val border = if (showBorder) {
        RMapCardDefaults.themedBorder()
    } else {
        null
    }

    RMapCard(
        modifier = modifier.fillMaxWidth(),
        border = border,
        content = content
    )
}
