package com.rmap.mobile.features.roadmap.presentation.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapSearchBar
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun RoadmapLocalSearchSection(
    roadmapTitle: String,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    mode: RoadmapLocalSearchMode = RoadmapLocalSearchMode.Inline,
    quickFilters: List<RoadmapQuickFilterUiModel> = emptyList(),
    onQuickFilterClick: (RoadmapQuickFilterUiModel) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSearchFocusChange: (Boolean) -> Unit = {},
    onClearClick: () -> Unit = { onQueryChange("") },
    inputFocusRequester: FocusRequester? = null
) {
    val isSearchScreenMode = mode != RoadmapLocalSearchMode.Inline
    val showFilterRow = mode != RoadmapLocalSearchMode.Typing && quickFilters.isNotEmpty()
    val textInputHeight = if (isSearchScreenMode) Dimens.controlLg else Dimens.controlXl

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.controlXl),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearchScreenMode) {
                RoadmapSearchIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_back),
                    onClick = onBackClick
                )
            }

            RMapSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onSearchFocusChange(true)
                        } else if (mode != RoadmapLocalSearchMode.Typing) {
                            onSearchFocusChange(false)
                        }
                    },
                focusRequester = inputFocusRequester,
                placeholder = stringResource(
                    if (isSearchScreenMode) {
                        R.string.roadmap_search_placeholder
                    } else {
                        R.string.roadmap_detail_search_placeholder
                    },
                    roadmapTitle
                ),
                height = textInputHeight,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }

        when (mode) {
            RoadmapLocalSearchMode.Inline -> {
                RoadmapSearchingInsideLabel(
                    roadmapTitle = roadmapTitle,
                    modifier = Modifier.padding(start = Dimens.spacingSm)
                )
                if (showFilterRow) {
                    RoadmapQuickFilterRow(
                        filters = quickFilters,
                        onFilterClick = onQuickFilterClick
                    )
                }
            }

            RoadmapLocalSearchMode.Active -> {
                if (showFilterRow) {
                    RoadmapQuickFilterRow(
                        filters = quickFilters,
                        onFilterClick = onQuickFilterClick
                    )
                }
            }

            RoadmapLocalSearchMode.Typing -> {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    thickness = Dimens.borderThin
                )
            }
        }
    }
}

@Composable
private fun RoadmapSearchIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(Dimens.controlSm)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(Dimens.iconLg)
        )
    }
}

@Composable
private fun RoadmapSearchClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(Dimens.iconXxl)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.content_description_clear_search),
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(Dimens.iconSm)
        )
    }
}

@Composable
private fun RoadmapSearchingInsideLabel(
    roadmapTitle: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.roadmap_search_inside_label, roadmapTitle),
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge.copy(
            color = OnSurfacePlaceholderLight,
            fontWeight = FontWeight.Medium
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapLocalSearchSectionInlinePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapLocalSearchSection(
            roadmapTitle = "Frontend Pro",
            query = "",
            onQueryChange = {},
            quickFilters = defaultRoadmapQuickFilters(),
            modifier = Modifier.padding(Dimens.spacingXxl)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapLocalSearchSectionActivePreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapLocalSearchSection(
            roadmapTitle = "Frontend Pro",
            query = "then mai",
            onQueryChange = {},
            mode = RoadmapLocalSearchMode.Active,
            quickFilters = defaultRoadmapQuickFilters(),
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390)
@Composable
private fun RoadmapLocalSearchSectionTypingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapLocalSearchSection(
            roadmapTitle = "Frontend Pro",
            query = "then mai",
            onQueryChange = {},
            mode = RoadmapLocalSearchMode.Typing
        )
    }
}
