package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.CardBorderColor
import com.rmap.mobile.core.ui.theme.CardShadowColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

object AppCardDefaults {
    val shape = AppShapes.largeCard
    val borderWidth: Dp = Dimens.borderThin
    val borderColor = CardBorderColor
    val shadowElevation: Dp = Dimens.cardElevationSm
    val shadowColor = CardShadowColor

    fun border(
        color: Color = borderColor,
        width: Dp = borderWidth
    ): BorderStroke {
        return BorderStroke(width = width, color = color)
    }
}

fun Modifier.appCardShadow(
    shape: Shape = AppCardDefaults.shape,
    elevation: Dp = AppCardDefaults.shadowElevation,
    ambientColor: Color = AppCardDefaults.shadowColor,
    spotColor: Color = AppCardDefaults.shadowColor
): Modifier {
    return shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = ambientColor,
        spotColor = spotColor
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF)
@Composable
private fun AppCardDefaultsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapCard(
            item = RoadmapCardUiModel(
                title = "Frontend Pro",
                lessonsCount = 120,
                difficultyLabel = "Expert",
                difficulty = RoadmapDifficulty.Expert,
                durationLabel = "3 months",
                icon = Icons.Outlined.Code
            )
        )
    }
}
