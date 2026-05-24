package com.rmap.mobile.features.roadmap.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.rmap.mobile.core.ui.theme.Dimens

@Composable
internal fun RoadmapLinearProgress(
    progress: Float,
    trackColor: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(Dimens.spacingXsPlus)
            .fillMaxWidth()
            .background(trackColor, CircleShape)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(Dimens.spacingXsPlus)
                .background(indicatorColor, CircleShape)
        )
    }
}
