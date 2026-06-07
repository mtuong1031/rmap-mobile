package com.rmap.mobile.core.ui.components.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens

enum class RMapQuestionOptionState {
    Default,
    Selected,
    Correct,
    Incorrect,
    Neutral
}

@Composable
fun RMapQuestionProgressHeader(
    progressText: String,
    answeredText: String,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progressText,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = answeredText,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        LinearProgressIndicator(
            progress = { progressFraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}

@Composable
fun RMapAnimatedQuestionPager(
    currentQuestionIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable (questionIndex: Int) -> Unit
) {
    AnimatedContent(
        targetState = currentQuestionIndex,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            (
                slideInHorizontally(animationSpec = tween(QuestionCardSlideDurationMillis)) { width ->
                    direction * width
                } + fadeIn(animationSpec = tween(QuestionCardSlideDurationMillis))
            ).togetherWith(
                slideOutHorizontally(animationSpec = tween(QuestionCardSlideDurationMillis)) { width ->
                    -direction * width
                } + fadeOut(animationSpec = tween(QuestionCardSlideDurationMillis))
            )
        },
        label = "RMapQuestionPagerTransition"
    ) { questionIndex ->
        content(questionIndex)
    }
}

@Composable
fun RMapQuestionCardScaffold(
    eyebrow: String,
    prompt: String,
    modifier: Modifier = Modifier,
    trailingHeaderContent: (@Composable RowScope.() -> Unit)? = null,
    bodyContent: @Composable ColumnScope.() -> Unit
) {
    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.largeCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = eyebrow,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                trailingHeaderContent?.invoke(this)
            }

            bodyContent()
        }
    }
}

@Composable
fun RMapQuestionOptionRow(
    markerText: String,
    label: String,
    state: RMapQuestionOptionState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = AppShapes.button
    val containerColor = state.containerColor()
    val borderColor = state.borderColor()
    val contentColor = state.contentColor()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor, shape)
            .border(Dimens.borderThin, borderColor, shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RMapQuestionOptionMarker(
            markerText = markerText,
            state = state
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = contentColor,
                    fontWeight = if (state.isReviewEmphasis()) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
                )
            )
            supportingContent?.invoke(this)
        }
    }
}

@Composable
fun RMapQuestionNavigationActions(
    previousText: String,
    nextText: String,
    finalText: String,
    isFirst: Boolean,
    isLast: Boolean,
    enabled: Boolean,
    busy: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        RMapButton(
            text = previousText,
            onClick = onPrevious,
            modifier = Modifier.weight(1f),
            variant = RMapButtonVariant.Secondary,
            size = RMapButtonSize.Medium,
            enabled = !isFirst && !busy
        )
        RMapButton(
            text = if (isLast) finalText else nextText,
            onClick = if (isLast) onFinal else onNext,
            modifier = Modifier.weight(1f),
            size = RMapButtonSize.Medium,
            enabled = enabled && !busy
        )
    }
}

@Composable
private fun RMapQuestionOptionMarker(
    markerText: String,
    state: RMapQuestionOptionState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.controlSm)
            .clip(AppShapes.iconContainer)
            .background(state.markerContainerColor()),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            RMapQuestionOptionState.Correct -> Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(Dimens.iconLg)
            )

            RMapQuestionOptionState.Incorrect -> Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Dimens.iconLg)
            )

            RMapQuestionOptionState.Default,
            RMapQuestionOptionState.Selected,
            RMapQuestionOptionState.Neutral -> Text(
                text = markerText,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = state.markerContentColor(),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun RMapQuestionOptionState.containerColor(): Color {
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primaryContainer
        RMapQuestionOptionState.Correct -> MaterialTheme.colorScheme.tertiaryContainer
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.errorContainer
        RMapQuestionOptionState.Default -> MaterialTheme.colorScheme.surface
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.surfaceContainerLow
    }
}

@Composable
private fun RMapQuestionOptionState.borderColor(): Color {
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primary
        RMapQuestionOptionState.Correct -> MaterialTheme.colorScheme.tertiary.copy(alpha = OptionBorderAlpha)
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.error.copy(alpha = OptionBorderAlpha)
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.outlineVariant
    }
}

@Composable
private fun RMapQuestionOptionState.contentColor(): Color {
    return when (this) {
        RMapQuestionOptionState.Correct -> MaterialTheme.colorScheme.onTertiaryContainer
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.onErrorContainer
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Selected,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun RMapQuestionOptionState.markerContainerColor(): Color {
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primary
        RMapQuestionOptionState.Correct -> MaterialTheme.colorScheme.tertiaryContainer
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.errorContainer
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
}

@Composable
private fun RMapQuestionOptionState.markerContentColor(): Color {
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.onPrimary
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
        RMapQuestionOptionState.Correct -> MaterialTheme.colorScheme.tertiary
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.error
    }
}

private fun RMapQuestionOptionState.isReviewEmphasis(): Boolean {
    return this == RMapQuestionOptionState.Correct || this == RMapQuestionOptionState.Incorrect
}

private const val QuestionCardSlideDurationMillis = 260
private const val OptionBorderAlpha = 0.45f
