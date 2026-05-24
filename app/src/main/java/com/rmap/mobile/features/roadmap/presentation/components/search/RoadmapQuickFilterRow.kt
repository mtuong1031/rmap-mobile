package com.rmap.mobile.features.roadmap.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
fun RoadmapQuickFilterRow(
    filters: List<RoadmapQuickFilterUiModel>,
    onFilterClick: (RoadmapQuickFilterUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        filters.chunked(QuickFilterMaxItemsPerRow).forEach { rowFilters ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                rowFilters.forEach { filter ->
                    RoadmapQuickFilterChip(
                        filter = filter,
                        onClick = { onFilterClick(filter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapQuickFilterChip(
    filter: RoadmapQuickFilterUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor = if (filter.selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (filter.selected) {
        MaterialTheme.colorScheme.inversePrimary
    } else {
        MaterialTheme.colorScheme.outline
    }
    val contentColor = if (filter.selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = stringResource(filter.labelResId),
        modifier = modifier
            .widthIn(min = QuickFilterChipMinWidth, max = QuickFilterChipMaxWidth)
            .heightIn(min = QuickFilterChipMinHeight)
            .cardShadow(shape = AppShapes.pill)
            .background(containerColor, AppShapes.pill)
            .border(Dimens.borderThin, borderColor, AppShapes.pill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        style = MaterialTheme.typography.labelMedium.copy(
            color = contentColor,
            fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private val QuickFilterChipMinHeight = Dimens.spacingXxxl + Dimens.spacingXxs
private val QuickFilterChipMinWidth = Dimens.iconFrameSize
private val QuickFilterChipMaxWidth =
    Dimens.iconFrameSize + Dimens.spacingMassive + Dimens.spacingHuge + Dimens.spacingXs
private const val QuickFilterMaxItemsPerRow = 3

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapQuickFilterRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapQuickFilterRow(
            filters = defaultRoadmapQuickFilters(),
            onFilterClick = {},
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}
