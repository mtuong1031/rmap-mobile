package com.rmap.mobile.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow

private const val RMapSkeletonShimmerDistance = 1_000f

@Composable
fun RMapSkeletonCard(
    modifier: Modifier,
    shape: Shape = AppShapes.card,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .cardShadow(shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun RMapSkeletonBlock(
    modifier: Modifier,
    shape: Shape = AppShapes.pill,
    brush: Brush = rememberRMapSkeletonBrush()
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun rememberRMapSkeletonBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "rmapSkeletonShimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = RMapSkeletonShimmerDistance,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1_100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rmapSkeletonShimmerOffset"
    )
    val baseColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)
    val highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translate - RMapSkeletonShimmerDistance, 0f),
        end = Offset(translate, RMapSkeletonShimmerDistance)
    )
}
