package com.rmap.mobile.features.home.presentation.components.stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun HomeStatCardRow(
    items: List<HomeStatItemUiModel>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = Dimens.spacingMd,
    onItemClick: ((index: Int, item: HomeStatItemUiModel) -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            HomeStatCard(
                modifier = Modifier.weight(1f),
                valueText = item.valueText,
                labelText = item.labelText,
                style = item.style,
                onClick = onItemClick?.let { callback ->
                    { callback(index, item) }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.style.accentColor,
                        modifier = Modifier.size(HomeStatCardDefaults.IconSize)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun HomeStatCardRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingXxl)) {
            HomeStatCardRow(
                items = listOf(
                    HomeStatItemUiModel(
                        valueText = "75%",
                        labelText = "Roadmap",
                        icon = Icons.Outlined.Map,
                        style = HomeStatCardDefaults.roadmapStyle()
                    ),
                    HomeStatItemUiModel(
                        valueText = "5 days",
                        labelText = "Streak",
                        icon = Icons.Outlined.LocalFireDepartment,
                        style = HomeStatCardDefaults.streakStyle()
                    ),
                    HomeStatItemUiModel(
                        valueText = "42%",
                        labelText = "Readiness",
                        icon = Icons.Outlined.TrackChanges,
                        style = HomeStatCardDefaults.readinessStyle()
                    )
                )
            )
        }
    }
}
