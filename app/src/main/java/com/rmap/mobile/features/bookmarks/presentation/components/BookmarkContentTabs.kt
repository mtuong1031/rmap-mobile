package com.rmap.mobile.features.bookmarks.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SecondaryLight
import com.rmap.mobile.core.ui.theme.SurfaceLight

@Composable
fun BookmarkTypeTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BookmarkContentTabs(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

@Composable
fun BookmarkContentTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(min = Dimens.controlLg)
            .shadow(
                elevation = Dimens.cardElevationXs,
                shape = AppShapes.button,
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
            )
            .clip(AppShapes.button)
            .background(SurfaceLight.copy(alpha = 0.6f))
            .padding(Dimens.spacingXsPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingNone),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, label ->
            BookmarkContentTab(
                label = label,
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BookmarkContentTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textColor by animateColorAsState(
        targetValue = if (selected) PrimaryLight else SecondaryLight,
        animationSpec = tween(durationMillis = 180),
        label = "bookmarkContentTabTextColor"
    )

    Box(
        modifier = modifier
            .heightIn(min = Dimens.controlLg)
            .then(
                if (selected) {
                    Modifier.shadow(
                        elevation = Dimens.cardElevationXs,
                        shape = AppShapes.iconContainerLarge,
                        ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
                        spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                    )
                } else {
                    Modifier
                }
            )
            .clip(AppShapes.iconContainerLarge)
            .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            ),
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun BookmarkContentTabsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        BookmarkContentTabs(
            tabs = listOf("Saved Roadmaps", "Saved Skills"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}
