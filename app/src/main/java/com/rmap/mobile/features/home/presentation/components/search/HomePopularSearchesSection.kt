package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomePopularSearchesSection(
    title: String,
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        HomeSearchSectionHeader(title = title)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        ) {
            searches.forEach { item ->
                HomePopularSearchChip(
                    text = item,
                    onClick = { onSearchClick(item) }
                )
            }
        }
    }
}

@Composable
private fun HomePopularSearchChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .cardShadow(shape = HomeSearchPopularChipShape)
            .clip(HomeSearchPopularChipShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = HomeSearchPopularChipShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMdPlus, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun HomePopularSearchesSectionPreview() {
    RMapTheme {
        HomePopularSearchesSection(
            title = "Popular Searches",
            searches = listOf("Frontend", "React", "Backend", "DevOps", "AI Engineer"),
            onSearchClick = {},
            modifier = Modifier.padding(Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePopularSearchChipPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingMd)) {
            HomePopularSearchChip(
                text = "React",
                onClick = {}
            )
        }
    }
}
