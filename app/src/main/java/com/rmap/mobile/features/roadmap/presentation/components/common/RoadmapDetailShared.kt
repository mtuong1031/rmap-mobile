package com.rmap.mobile.features.roadmap.presentation.components.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.cardShadow

internal val roadmapInk = Color(0xFF1D293D)
internal val roadmapDeepBlue = Color(0xFF1E3A8A)
internal val roadmapLockedText = Color(0xFF45556C)
internal val roadmapFocusedRequirementBg = Color(0xFFE9F0F9)
internal val roadmapSuccess = Color(0xFF10B981)
internal val roadmapSuccessBg = Color(0xFFECFDF5)
internal val roadmapSuccessBorder = Color(0xFFA7F3D0)
internal val roadmapAmber = Color(0xFFD97706)
internal val roadmapAmberDark = Color(0xFF92400E)
internal val roadmapAmberText = Color(0xFFB45309)
internal val roadmapMilestoneSoftBg = Color(0xFFFFFBEB)
internal val roadmapMilestoneIconBg = Color(0xFFFFF7ED)
internal val roadmapMilestoneLockedBorder = Color(0x99E2E8F0)
internal val roadmapAmberBg = Color(0xFFFEF3C7)
internal val roadmapAmberBorder = Color(0xFFFDE68A)

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

@Composable
internal fun RoadmapPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    dotColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = if (dotColor != null) AppShapes.small else AppShapes.chip
    Row(
        modifier = modifier
            .background(containerColor, shape)
            .then(
                if (borderColor != null) {
                    Modifier.border(Dimens.borderThin, borderColor, shape)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Dimens.spacingXsPlus, vertical = Dimens.spacingXs),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.invoke()
        dotColor?.let {
            Box(
                modifier = Modifier
                    .size(Dimens.spacingXsPlus)
                    .background(it, CircleShape)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun RoadmapLinearProgress(
    progress: Float,
    trackColor: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(Dimens.spacingXsPlus)
            .fillMaxWidth()
            .background(trackColor, CircleShape)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(Dimens.spacingXsPlus)
                .background(indicatorColor, CircleShape)
        )
    }
}

@Composable
internal fun formattedString(
    @StringRes resId: Int,
    args: List<String>
): String {
    return when (args.size) {
        0 -> stringResource(resId)
        1 -> stringResource(resId, args[0])
        2 -> stringResource(resId, args[0], args[1])
        else -> stringResource(resId, *args.toTypedArray())
    }
}
