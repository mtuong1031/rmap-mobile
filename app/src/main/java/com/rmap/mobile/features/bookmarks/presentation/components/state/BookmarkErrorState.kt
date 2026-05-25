package com.rmap.mobile.features.bookmarks.presentation.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarkErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    BookmarkStateContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingHuge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Dimens.iconXxl)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkErrorStatePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkErrorState(
            message = "Unable to load bookmarks",
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
