package com.rmap.mobile.features.bookmarks.presentation.components.state

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTextStyles

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BookmarkSuggestedDomains(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Text(
            text = stringResource(R.string.bookmarks_empty_suggested_domains),
            style = BookmarkTextStyles.emptyDomainEyebrow,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BookmarkDomainChip(text = stringResource(R.string.bookmarks_empty_domain_web_development))
                BookmarkDomainChip(text = stringResource(R.string.bookmarks_empty_domain_data))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BookmarkDomainChip(text = stringResource(R.string.bookmarks_empty_domain_devops))
                BookmarkDomainChip(text = stringResource(R.string.bookmarks_empty_domain_design))
            }
        }
    }
}

@Composable
private fun BookmarkDomainChip(text: String) {
    Box(
        modifier = Modifier
            .clip(AppShapes.iconContainerLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = AppShapes.iconContainerLarge
            )
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BookmarkTextStyles.domainChip,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
