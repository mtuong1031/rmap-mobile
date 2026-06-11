package com.rmap.mobile.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

object RMapCardDefaults {
    val shape = AppShapes.largeCard
    val borderWidth: Dp = Dimens.borderThin
    val borderColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.secondaryContainer
    val shadowElevation: Dp = Dimens.cardElevationSm

    @Composable
    fun containerColor(): Color {
        return MaterialTheme.colorScheme.surface
    }

    @Composable
    fun themedBorderColor(): Color {
        return MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)
    }

    @Composable
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
    brush: Brush? = null,
    border: BorderStroke? = RMapCardDefaults.themedBorder(),
    shadowElevation: Dp = RMapCardDefaults.shadowElevation,
    content: @Composable () -> Unit
) {
    val cardModifier = if (shadowElevation > 0.dp) {
        modifier.cardShadow(shape = shape)
    } else {
        modifier
    }

    Surface(
        modifier = cardModifier,
        shape = shape,
        color = if (brush == null) color else Color.Transparent,
        border = border
    ) {
        val finalContent: @Composable () -> Unit = {
            if (brush != null) {
                Box(modifier = Modifier.background(brush = brush, shape = shape)) {
                    content()
                }
            } else {
                content()
            }
        }
        finalContent()
    }
}

@Deprecated(
    message = "Use Modifier.cardShadow from core.ui.theme for consistent app shadows.",
    replaceWith = ReplaceWith(
        expression = "this.cardShadow(shape = shape)",
        imports = ["com.rmap.mobile.core.ui.theme.cardShadow"]
    )
)

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
