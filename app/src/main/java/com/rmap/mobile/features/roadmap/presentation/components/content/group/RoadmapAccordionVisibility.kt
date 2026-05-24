package com.rmap.mobile.features.roadmap.presentation.components.content.group

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun RoadmapAccordionVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(
                durationMillis = AccordionAnimationMillis,
                easing = FastOutSlowInEasing
            ),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(durationMillis = AccordionFadeAnimationMillis)
        ),
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = AccordionAnimationMillis,
                easing = FastOutSlowInEasing
            ),
            shrinkTowards = Alignment.Top
        ) + fadeOut(
            animationSpec = tween(durationMillis = AccordionFadeAnimationMillis)
        )
    ) {
        content()
    }
}

private const val AccordionAnimationMillis = 260
private const val AccordionFadeAnimationMillis = 160
