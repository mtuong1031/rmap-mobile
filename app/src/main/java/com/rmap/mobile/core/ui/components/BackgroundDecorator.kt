package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AccentBlueColor
import com.rmap.mobile.core.ui.theme.AccentCyanColor
import com.rmap.mobile.core.ui.theme.AccentPurpleGlowColor
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun rememberBackgroundScrollOffsetY(listState: LazyListState): Float {
    val scrollY by remember(listState) {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val avgItemHeight = if (visibleItems.isNotEmpty()) {
                visibleItems.sumOf { it.size } / visibleItems.size
            } else {
                0
            }
            (
                (listState.firstVisibleItemIndex * avgItemHeight) +
                    listState.firstVisibleItemScrollOffset
                ).toFloat()
        }
    }
    return scrollY
}

@Composable
fun BackgroundDecorator(
    scrollOffsetY: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.graphicsLayer {
            translationY = -scrollOffsetY
        }
    ) {
        SoftGlow(
            offsetX = 129.dp,
            offsetY = 0.dp,
            size = 320.dp,
            tint = AccentBlueColor,
            alpha = 0.15f
        )
        SoftGlow(
            offsetX = (-78).dp,
            offsetY = 510.92188.dp,
            size = 280.dp,
            tint = AccentPurpleGlowColor,
            alpha = 0.14f
        )
        SoftGlow(
            offsetX = 168.dp,
            offsetY = 1232.78906.dp,
            size = 320.dp,
            tint = AccentCyanColor,
            alpha = 0.15f
        )
    }
}

@Composable
private fun SoftGlow(
    offsetX: Dp,
    offsetY: Dp,
    size: Dp,
    tint: Color,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .blur(radius = 190.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tint.copy(alpha = alpha),
                        tint.copy(alpha = 0f)
                    ),
                    radius = Float.POSITIVE_INFINITY
                ),
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun BackgroundDecoratorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        BackgroundDecorator()
    }
}
