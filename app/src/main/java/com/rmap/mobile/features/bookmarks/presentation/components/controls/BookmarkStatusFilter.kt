package com.rmap.mobile.features.bookmarks.presentation.components.controls

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapButtonDefaults
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.bookmarks.presentation.components.BookmarkTextStyles
import androidx.compose.material3.Button as MaterialButton

@Composable
fun BookmarkStatusFilter(
    filters: List<String>,
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val safeSelectedIndex = selectedIndex.coerceIn(0, (filters.size - 1).coerceAtLeast(0))
    val selectedLabel = filters.getOrNull(safeSelectedIndex).orEmpty()

    Box(modifier = modifier) {
        BookmarkStatusFilterButton(
            text = selectedLabel.toBookmarkStatusButtonLabel(),
            onClick = { expanded = true },
            modifier = Modifier.width(144.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filters.forEachIndexed { index, label ->
                val selected = index == safeSelectedIndex
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            style = BookmarkTextStyles.filterChipLabel,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        expanded = false
                        onFilterSelected(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun BookmarkStatusFilterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val variant = RMapButtonVariant.Neutral
    val size = RMapButtonSize.XSmall

    MaterialButton(
        onClick = onClick,
        modifier = modifier
            .height(size.height)
            .defaultMinSize(minHeight = size.height),
        shape = RoundedCornerShape(size.radius),
        colors = RMapButtonDefaults.colors(variant),
        elevation = RMapButtonDefaults.elevation(variant),
        border = RMapButtonDefaults.border(variant, enabled = true),
        contentPadding = PaddingValues(Dimens.spacingNone)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Dimens.spacingSmPlus)
                    .size(size.iconSize)
            )

            Text(
                text = text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.spacingSmPlus,
                        end = Dimens.spacingXxl
                    ),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun String.toBookmarkStatusButtonLabel(): String {
    return uppercase()
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkStatusFilterPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        BookmarkStatusFilter(
            filters = listOf("All", "In Progress", "Not Started", "Completed"),
            selectedIndex = selectedIndex,
            onFilterSelected = { selectedIndex = it },
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
