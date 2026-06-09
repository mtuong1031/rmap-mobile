package com.rmap.mobile.features.roadmap.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

@Composable
internal fun RoadmapPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    dotColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .background(containerColor, AppShapes.chip)
            .then(
                if (borderColor != null) {
                    Modifier.border(Dimens.borderThin, borderColor, AppShapes.chip)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.invoke()
        dotColor?.let {
            Box(
                modifier = Modifier
                    .size(Dimens.spacingXsPlus)
                    .background(it, CircleShape)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
