package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.CardShadowColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.InfoContainerColor
import com.rmap.mobile.core.ui.theme.NeutralBorderColor
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.StatusSuccessContainerColor

@Immutable
data class RoadmapPathCardCornerAccents(
    val topEndColor: Color,
    val bottomStartColor: Color,
    val topEndSize: Dp,
    val bottomStartSize: Dp,
    val topEndOffsetX: Dp,
    val topEndOffsetY: Dp,
    val bottomStartOffsetX: Dp,
    val bottomStartOffsetY: Dp
)

object RoadmapPathCardDefaults {
    val Shape: Shape = AppShapes.largeCard
    val MinHeight: Dp = 288.dp
    val BorderWidth: Dp = Dimens.borderThin
    val BorderColor: Color = NeutralBorderColor
    val ShadowElevation: Dp = Dimens.cardElevationSm
    val ShadowColor: Color = CardShadowColor
    val CornerAccentTopEndSize: Dp = 220.dp
    val CornerAccentBottomStartSize: Dp = 180.dp

    fun border(
        color: Color = BorderColor,
        width: Dp = BorderWidth
    ): BorderStroke {
        return BorderStroke(width = width, color = color)
    }

    fun cornerAccents(
        topEndColor: Color = InfoContainerColor.copy(alpha = 0.5f),
        bottomStartColor: Color = StatusSuccessContainerColor.copy(alpha = 0.55f),
        topEndSize: Dp = CornerAccentTopEndSize,
        bottomStartSize: Dp = CornerAccentBottomStartSize,
        topEndOffsetX: Dp = 86.dp,
        topEndOffsetY: Dp = (-104).dp,
        bottomStartOffsetX: Dp = (-94).dp,
        bottomStartOffsetY: Dp = 84.dp
    ): RoadmapPathCardCornerAccents {
        return RoadmapPathCardCornerAccents(
            topEndColor = topEndColor,
            bottomStartColor = bottomStartColor,
            topEndSize = topEndSize,
            bottomStartSize = bottomStartSize,
            topEndOffsetX = topEndOffsetX,
            topEndOffsetY = topEndOffsetY,
            bottomStartOffsetX = bottomStartOffsetX,
            bottomStartOffsetY = bottomStartOffsetY
        )
    }
}

@Composable
fun RoadmapPathCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoadmapPathCardDefaults.Shape,
    minHeight: Dp = RoadmapPathCardDefaults.MinHeight,
    border: BorderStroke? = RoadmapPathCardDefaults.border(),
    shadowElevation: Dp = RoadmapPathCardDefaults.ShadowElevation,
    shadowColor: Color = RoadmapPathCardDefaults.ShadowColor,
    showCornerAccents: Boolean = true,
    cornerAccents: RoadmapPathCardCornerAccents = RoadmapPathCardDefaults.cornerAccents()
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        shape = shape,
        color = color,
        border = border,
        shadowElevation = shadowElevation,
        shadowColor = shadowColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = minHeight)
        ) {
            if (showCornerAccents) {
                RoadmapPathCardCornerAccentLayer(cornerAccents = cornerAccents)
            }
        }
    }
}

@Composable
private fun BoxScope.RoadmapPathCardCornerAccentLayer(
    cornerAccents: RoadmapPathCardCornerAccents
) {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(
                x = cornerAccents.topEndOffsetX,
                y = cornerAccents.topEndOffsetY
            )
            .size(cornerAccents.topEndSize)
            .background(
                color = cornerAccents.topEndColor,
                shape = CircleShape
            )
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(
                x = cornerAccents.bottomStartOffsetX,
                y = cornerAccents.bottomStartOffsetY
            )
            .size(cornerAccents.bottomStartSize)
            .background(
                color = cornerAccents.bottomStartColor,
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapPathCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapPathCard(modifier = Modifier.padding(Dimens.spacingLg))
    }
}
