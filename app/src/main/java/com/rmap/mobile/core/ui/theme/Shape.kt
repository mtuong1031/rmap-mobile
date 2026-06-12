package com.rmap.mobile.core.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppShapes {
    val extraSmall = RoundedCornerShape(Dimens.spacingXs)
    val small = RoundedCornerShape(Dimens.spacingSm)
    val chip = RoundedCornerShape(Dimens.radiusSm)
    val iconContainer = RoundedCornerShape(Dimens.cardRadiusSm)
    val iconContainerLarge = RoundedCornerShape(Dimens.cardRadiusSmPlus)
    val button = RoundedCornerShape(Dimens.cardRadiusMd)
    val snackbar = RoundedCornerShape(Dimens.cardRadiusMd)
    val iconFrameInner = RoundedCornerShape(Dimens.cardRadiusMdPlus)
    val searchBar = RoundedCornerShape(Dimens.cardRadiusLg)
    val statCard = RoundedCornerShape(Dimens.cardRadiusLgPlus)
    val card = RoundedCornerShape(Dimens.cardRadiusXl)
    val heroCard = RoundedCornerShape(Dimens.cardRadiusXxl)
    val largeCard = RoundedCornerShape(Dimens.cardRadiusHuge)
    val pill = RoundedCornerShape(percent = 50)
    val navigationBar = RoundedCornerShape(
        topStart = Dimens.cardRadiusHuge,
        topEnd = Dimens.cardRadiusHuge
    )
    val bottomSheet = RoundedCornerShape(
        topStart = Dimens.spacingMassive,
        topEnd = Dimens.spacingMassive
    )
}

data class AppShadowLayer(
    val offsetX: Dp,
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val color: Color
)

object AppShadows {
    val card = listOf(
        AppShadowLayer(
            offsetX = 0.dp,
            offsetY = 1.dp,
            blur = 3.dp,
            spread = 0.dp,
            color = Color(0x1A000000)
        ),
        AppShadowLayer(
            offsetX = 0.dp,
            offsetY = 1.dp,
            blur = 2.dp,
            spread = (-1).dp,
            color = Color(0x1A000000)
        )
    )
}

fun Modifier.cardShadow(
    shape: Shape = AppShapes.largeCard,
    layers: List<AppShadowLayer> = AppShadows.card
): Modifier {
    return drawBehind {
        layers.forEach { layer ->
            val spreadPx = layer.spread.toPx()
            val shadowSize = Size(
                width = size.width + spreadPx * 2,
                height = size.height + spreadPx * 2
            )
            val shadowOffset = Offset(
                x = layer.offsetX.toPx() - spreadPx,
                y = layer.offsetY.toPx() - spreadPx
            )
            val outline = shape.createOutline(
                size = shadowSize,
                layoutDirection = layoutDirection,
                density = this
            )
            val paint = Paint().apply {
                color = layer.color
                asFrameworkPaint().maskFilter = BlurMaskFilter(
                    layer.blur.toPx(),
                    BlurMaskFilter.Blur.NORMAL
                )
            }

            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(shadowOffset.x, shadowOffset.y)
                when (outline) {
                    is Outline.Rectangle -> canvas.drawRect(outline.rect, paint)
                    is Outline.Rounded -> {
                        val path = Path().apply {
                            addRoundRect(outline.roundRect)
                        }
                        canvas.drawPath(path, paint)
                    }
                    is Outline.Generic -> canvas.drawPath(outline.path, paint)
                }
                canvas.restore()
            }
        }
    }
}

val RMapShapes = Shapes(
    extraSmall = AppShapes.extraSmall,
    small = AppShapes.small,
    medium = AppShapes.iconContainer,
    large = AppShapes.button,
    extraLarge = AppShapes.card
)
