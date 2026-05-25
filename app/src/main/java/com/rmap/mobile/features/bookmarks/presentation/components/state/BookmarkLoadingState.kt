package com.rmap.mobile.features.bookmarks.presentation.components.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarkLoadingState(modifier: Modifier = Modifier) {
    BookmarkStateContainer(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMassive),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkLoadingStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkLoadingState(modifier = Modifier.padding(Dimens.spacingXxl))
    }
}
