package com.rmap.mobile.features.home.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun HomeSearchSuggestionChipsRow(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        items(suggestions) { item ->
            HomeSearchSuggestionChip(
                text = item,
                onClick = { onSuggestionClick(item) }
            )
        }
    }
}

@Composable
private fun HomeSearchSuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .cardShadow(shape = HomeSearchSuggestionChipShape)
            .clip(HomeSearchSuggestionChipShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = HomeSearchSuggestionChipShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMdPlus),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun HomeSearchSuggestionChipsRowPreview() {
    RMapTheme {
        HomeSearchSuggestionChipsRow(
            suggestions = listOf("Frontend", "Backend", "React", "AI", "DevOps", "Mobile", "Security", "Cloud"),
            onSuggestionClick = {},
            contentPadding = PaddingValues(horizontal = Dimens.spacingMd)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSearchSuggestionChipPreview() {
    RMapTheme {
        Box(modifier = Modifier.padding(Dimens.spacingMd)) {
            HomeSearchSuggestionChip(
                text = "Frontend",
                onClick = {}
            )
        }
    }
}
