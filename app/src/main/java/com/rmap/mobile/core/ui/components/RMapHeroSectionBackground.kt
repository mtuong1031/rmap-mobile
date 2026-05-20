package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

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
fun RMapHeroSectionBackground(
    modifier: Modifier = Modifier,
    scrollOffsetY: Float = 0f
) {
    val shape = AppShapes.heroCard

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = -scrollOffsetY
            }
            .cardShadow(shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = Color(0x80F9FAFB),
                shape = shape
            )
    ) {
        TopBubble(
            endInset = Dimens.borderThin,
            topInset = Dimens.spacingNone,
            bubbleSize = 160.dp
        )

        BottomBubble(
            startInset = (-1).dp,
            bottomOverflow = 6.5.dp,
            bubbleSize = 128.dp
        )
    }
}

@Composable
private fun BoxScope.TopBubble(
    endInset: Dp,
    topInset: Dp,
    bubbleSize: Dp
) {
    val shape = RoundedCornerShape(bottomStart = bubbleSize)

    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = -endInset, y = topInset)
            .size(bubbleSize)
            .clip(shape)
            .drawWithCache {
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x1A2B7FFF),
                        Color.Transparent
                    ),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height)
                )

                onDrawBehind {
                    drawRect(brush = brush)
                }
            }
    )
}

@Composable
private fun BoxScope.BottomBubble(
    startInset: Dp,
    bottomOverflow: Dp,
    bubbleSize: Dp
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = startInset, y = bottomOverflow)
            .size(bubbleSize)
            .background(
                color = Color(0x0D00BC7D),
                shape = RoundedCornerShape(topEnd = bubbleSize)
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 390)
@Composable
private fun RMapHeroSectionBackgroundPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.size(width = 342.dp, height = 327.5.dp)) {
            RMapHeroSectionBackground(modifier = Modifier.matchParentSize())
        }
    }
}
