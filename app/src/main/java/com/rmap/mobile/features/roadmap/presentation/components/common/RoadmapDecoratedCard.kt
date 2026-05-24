package com.rmap.mobile.features.roadmap.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow

@Composable
internal fun RoadmapDecoratedCard(
    modifier: Modifier = Modifier,
    shape: Shape = AppShapes.searchBar,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = MaterialTheme.colorScheme.outlineVariant,
    shadow: Boolean = true,
    useHeroBackground: Boolean = false,
    backgroundBrush: Brush? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (shadow) Modifier.cardShadow(shape = shape) else Modifier)
            .clip(shape)
            .background(containerColor, shape)
            .then(
                if (borderColor != null) {
                    Modifier.border(Dimens.borderThin, borderColor, shape)
                } else {
                    Modifier
                }
            )
    ) {
        if (useHeroBackground) {
            RMapHeroSectionBackground(modifier = Modifier.matchParentSize())
        }
        if (backgroundBrush != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(backgroundBrush, shape)
            )
        }
        content()
    }
}
