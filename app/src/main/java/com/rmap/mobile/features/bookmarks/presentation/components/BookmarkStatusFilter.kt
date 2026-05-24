package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnPrimaryLight
import com.rmap.mobile.core.ui.theme.OnSecondaryContainerLight
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun BookmarkStatusFilter(
    filters: List<String>,
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(
            items = filters,
            key = { index, label -> "$index-$label" }
        ) { index, label ->
            BookmarkStatusFilterChip(
                label = label,
                selected = index == selectedIndex,
                onClick = { onFilterSelected(index) }
            )
        }
    }
}

@Composable
private fun BookmarkStatusFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) PrimaryLight else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 180),
        label = "bookmarkStatusFilterBackground"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) OnPrimaryLight else OnSecondaryContainerLight,
        animationSpec = tween(durationMillis = 180),
        label = "bookmarkStatusFilterTextColor"
    )

    Box(
        modifier = Modifier
            .heightIn(min = Dimens.controlSm - Dimens.spacingXs)
            .then(
                if (selected) {
                    Modifier.shadow(
                        elevation = Dimens.spacingXs,
                        shape = AppShapes.pill,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                    )
                } else {
                    Modifier
                }
            )
            .clip(AppShapes.pill)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMdPlus, vertical = Dimens.spacingSm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 19.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            ),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
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
