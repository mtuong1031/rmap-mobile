package com.rmap.mobile.features.roadmap.presentation.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun RoadmapDetailTopBar(
    onBackClick: () -> Unit,
    isTemplate: Boolean,
    onResetProgressClick: () -> Unit,
    onDeleteRoadmapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.controlXl)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.spacingLg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoadmapTopIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_description_back),
            onClick = onBackClick
        )
        Box {
            RoadmapTopIconButton(
                icon = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.content_description_more_options),
                onClick = { isMenuExpanded = true }
            )
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.roadmap_detail_action_reset_progress)) },
                    onClick = {
                        isMenuExpanded = false
                        onResetProgressClick()
                    }
                )
                if (!isTemplate) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.roadmap_detail_action_delete_roadmap)) },
                        onClick = {
                            isMenuExpanded = false
                            onDeleteRoadmapClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapTopIconButton(
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

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapDetailTopBarPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapDetailTopBar(
            onBackClick = {},
            isTemplate = false,
            onResetProgressClick = {},
            onDeleteRoadmapClick = {}
        )
    }
}
