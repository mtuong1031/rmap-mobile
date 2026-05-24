package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapSectionTitle
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarkSectionHeader(
    title: String,
    badgeText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RMapSectionTitle(
            text = title,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .heightIn(min = Dimens.controlSm - Dimens.spacingSm)
                .background(
                    color = PrimaryContainerLight,
                    shape = AppShapes.pill
                )
                .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                ),
                color = PrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkSectionHeaderPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BookmarkSectionHeader(
            title = "Saved Roadmaps",
            badgeText = "3 saved",
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
