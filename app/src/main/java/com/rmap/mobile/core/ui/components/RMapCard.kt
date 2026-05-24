package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

object RMapCardDefaults {
    val shape = AppShapes.largeCard
    val borderWidth: Dp = Dimens.borderThin
    val borderColor = Color(0x80F9FAFB)
    val shadowElevation: Dp = Dimens.cardElevationSm
    val shadowColor = Color(0x0F000000)

    @Composable
    fun containerColor(): Color {
        return MaterialTheme.colorScheme.surface
    }

    @Composable
    fun themedBorderColor(): Color {
        return MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)
    }

    @Composable
    fun themedShadowColor(): Color {
        return Color(0x14000000)
    }

    fun border(
        color: Color = borderColor,
        width: Dp = borderWidth
    ): BorderStroke {
        return BorderStroke(width = width, color = color)
    }

    @Composable
    fun themedBorder(
        color: Color = themedBorderColor(),
        width: Dp = borderWidth
    ): BorderStroke {
        return BorderStroke(width = width, color = color)
    }
}

@Composable
fun RMapCard(
    modifier: Modifier = Modifier,
    shape: Shape = RMapCardDefaults.shape,
    color: Color = RMapCardDefaults.containerColor(),
    border: BorderStroke? = RMapCardDefaults.themedBorder(),
    shadowElevation: Dp = RMapCardDefaults.shadowElevation,
    shadowColor: Color = RMapCardDefaults.themedShadowColor(),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.rmapCardShadow(
            shape = shape,
            elevation = shadowElevation,
            ambientColor = shadowColor,
            spotColor = shadowColor
        ),
        shape = shape,
        color = color,
        border = border,
        content = content
    )
}

fun Modifier.rmapCardShadow(
    shape: Shape = RMapCardDefaults.shape,
    elevation: Dp = RMapCardDefaults.shadowElevation,
    ambientColor: Color = RMapCardDefaults.shadowColor,
    spotColor: Color = RMapCardDefaults.shadowColor
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
private fun RMapCardDefaultsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RoadmapCard(
            item = RoadmapCardUiModel(
                id = "frontend-pro",
                title = "Frontend Pro",
                totalLessonsCount = 120,
                difficultyLabel = "Expert",
                difficulty = RoadmapDifficulty.Expert,
                durationLabel = "3 months",
                icon = Icons.Outlined.Code
            )
        )
    }
}
