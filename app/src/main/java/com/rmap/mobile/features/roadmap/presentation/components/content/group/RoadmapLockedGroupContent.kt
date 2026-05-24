package com.rmap.mobile.features.roadmap.presentation.components.content.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.OnSurfacePlaceholderLight
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.components.common.formattedString
import com.rmap.mobile.features.roadmap.presentation.components.common.roadmapLockedText
import com.rmap.mobile.features.roadmap.presentation.viewmodel.RoadmapGroupUiModel

@Composable
internal fun RoadmapLockedGroupContent(
    group: RoadmapGroupUiModel,
    isDescriptionExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = roadmapLockedText,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            RoadmapPill(
                text = stringResource(R.string.roadmap_detail_locked),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                borderColor = MaterialTheme.colorScheme.outline,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Dimens.iconXxs)
                    )
                }
            )
        }

        group.lockedDescriptionResId?.let { resId ->
            Text(
                text = formattedString(resId, group.lockedDescriptionArgs),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = OnSurfacePlaceholderLight
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        RoadmapAccordionVisibility(visible = isDescriptionExpanded) {
            group.lockedExpandedDescriptionResId?.let { resId ->
                Text(
                    text = stringResource(resId),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
