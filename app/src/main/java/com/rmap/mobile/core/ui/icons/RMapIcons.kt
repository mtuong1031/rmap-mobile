package com.rmap.mobile.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object RMapIcons {
    val Map: ImageVector =
        ImageVector.Builder(
            name = "RMapMapIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.106f, 5.553f)
                curveToRelative(0.563f, 0.281f, 1.225f, 0.281f, 1.788f, 0f)
                lineToRelative(3.659f, -1.83f)
                curveTo(20.217f, 3.392f, 21f, 3.874f, 21f, 4.618f)
                verticalLineToRelative(12.764f)
                curveToRelative(0f, 0.379f, -0.214f, 0.725f, -0.553f, 0.894f)
                lineToRelative(-4.553f, 2.277f)
                curveToRelative(-0.563f, 0.281f, -1.225f, 0.281f, -1.788f, 0f)
                lineToRelative(-4.212f, -2.106f)
                curveToRelative(-0.563f, -0.281f, -1.225f, -0.281f, -1.788f, 0f)
                lineToRelative(-3.659f, 1.83f)
                curveTo(3.783f, 20.608f, 3f, 20.126f, 3f, 19.382f)
                verticalLineTo(6.618f)
                curveToRelative(0f, -0.379f, 0.214f, -0.725f, 0.553f, -0.894f)
                lineToRelative(4.553f, -2.277f)
                curveToRelative(0.563f, -0.281f, 1.225f, -0.281f, 1.788f, 0f)
                lineToRelative(4.212f, 2.106f)
                close()
                moveTo(15f, 5.764f)
                verticalLineToRelative(15f)
                moveTo(9f, 3.236f)
                verticalLineToRelative(15f)
            }
        }.build()
}
