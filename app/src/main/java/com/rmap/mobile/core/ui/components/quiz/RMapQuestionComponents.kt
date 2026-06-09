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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SuccessContainerDark
import com.rmap.mobile.core.ui.theme.SuccessContainerLight
import com.rmap.mobile.core.ui.theme.SuccessDark
import com.rmap.mobile.core.ui.theme.SuccessLight

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = eyebrow,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    trailingHeaderContent?.invoke(this)
                }
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
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
                tint = if (isSystemInDarkTheme()) SuccessDark else SuccessLight,
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
    val isDark = isSystemInDarkTheme()
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primaryContainer
        RMapQuestionOptionState.Correct -> if (isDark) SuccessContainerDark else SuccessContainerLight
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.errorContainer
        RMapQuestionOptionState.Default -> MaterialTheme.colorScheme.surface
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.surfaceContainerLow
    }
}

@Composable
private fun RMapQuestionOptionState.borderColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primary
        RMapQuestionOptionState.Correct -> (if (isDark) SuccessDark else SuccessLight).copy(alpha = OptionBorderAlpha)
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.error.copy(alpha = OptionBorderAlpha)
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.outlineVariant
    }
}

@Composable
private fun RMapQuestionOptionState.contentColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        RMapQuestionOptionState.Correct -> if (isDark) SuccessDark else SuccessLight
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.onErrorContainer
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Selected,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun RMapQuestionOptionState.markerContainerColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.primary
        RMapQuestionOptionState.Correct -> if (isDark) SuccessContainerDark else SuccessContainerLight
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.errorContainer
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
}

@Composable
private fun RMapQuestionOptionState.markerContentColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        RMapQuestionOptionState.Selected -> MaterialTheme.colorScheme.onPrimary
        RMapQuestionOptionState.Default,
        RMapQuestionOptionState.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
        RMapQuestionOptionState.Correct -> if (isDark) SuccessDark else SuccessLight
        RMapQuestionOptionState.Incorrect -> MaterialTheme.colorScheme.error
    }
}

private fun RMapQuestionOptionState.isReviewEmphasis(): Boolean {
    return this == RMapQuestionOptionState.Correct || this == RMapQuestionOptionState.Incorrect
}

private const val QuestionCardSlideDurationMillis = 260
private const val OptionBorderAlpha = 0.45f

@Composable
fun RMapQuestionInlineError(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.chip)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(Dimens.spacingMd),
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun RMapQuestionPagerScaffold(
    currentQuestionIndex: Int,
    questionProgressText: String,
    answeredText: String,
    progressFraction: Float,
    errorText: String?,
    previousText: String,
    nextText: String,
    finalText: String,
    isFirst: Boolean,
    isLast: Boolean,
    isNextEnabled: Boolean,
    isBusy: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinal: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (questionIndex: Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        RMapQuestionProgressHeader(
            progressText = questionProgressText,
            answeredText = answeredText,
            progressFraction = progressFraction
        )

        RMapAnimatedQuestionPager(
            currentQuestionIndex = currentQuestionIndex
        ) { questionIndex ->
            content(questionIndex)
        }

        if (errorText != null) {
            RMapQuestionInlineError(message = errorText)
        }

        RMapQuestionNavigationActions(
            previousText = previousText,
            nextText = nextText,
            finalText = finalText,
            isFirst = isFirst,
            isLast = isLast,
            enabled = isNextEnabled,
            busy = isBusy,
            onPrevious = onPrevious,
            onNext = onNext,
            onFinal = onFinal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionProgressHeaderPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapQuestionProgressHeader(
            progressText = "Question 1 of 5",
            answeredText = "0/5 answered",
            progressFraction = 0.2f,
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionInlineErrorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapQuestionInlineError(
            message = "Please select an option before continuing.",
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionOptionRowPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            modifier = Modifier.padding(Dimens.spacingLg)
        ) {
            RMapQuestionOptionRow(
                markerText = "A",
                label = "Default Option",
                state = RMapQuestionOptionState.Default,
                enabled = true,
                onClick = {}
            )
            RMapQuestionOptionRow(
                markerText = "B",
                label = "Selected Option",
                state = RMapQuestionOptionState.Selected,
                enabled = true,
                onClick = {}
            )
            RMapQuestionOptionRow(
                markerText = "C",
                label = "Correct Option",
                state = RMapQuestionOptionState.Correct,
                enabled = true,
                onClick = {}
            )
            RMapQuestionOptionRow(
                markerText = "D",
                label = "Incorrect Option",
                state = RMapQuestionOptionState.Incorrect,
                enabled = true,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionNavigationActionsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapQuestionNavigationActions(
            previousText = "Previous",
            nextText = "Next",
            finalText = "Submit",
            isFirst = false,
            isLast = false,
            enabled = true,
            busy = false,
            onPrevious = {},
            onNext = {},
            onFinal = {},
            modifier = Modifier.padding(Dimens.spacingLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionCardScaffoldPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapQuestionCardScaffold(
            eyebrow = "Question 1",
            prompt = "What is the capital of France?",
            modifier = Modifier.padding(Dimens.spacingLg)
        ) {
            Text("Content inside the card", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RMapQuestionPagerScaffoldPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        RMapQuestionPagerScaffold(
            currentQuestionIndex = 0,
            questionProgressText = "Question 1 of 5",
            answeredText = "0/5 answered",
            progressFraction = 0.2f,
            errorText = "Please select an option",
            previousText = "Previous",
            nextText = "Next",
            finalText = "Submit",
            isFirst = true,
            isLast = false,
            isNextEnabled = true,
            isBusy = false,
            onPrevious = {},
            onNext = {},
            onFinal = {},
            modifier = Modifier.padding(Dimens.spacingLg)
        ) {
            RMapQuestionCardScaffold(
                eyebrow = "Question 1",
                prompt = "What is the capital of France?"
            ) {
                RMapQuestionOptionRow(
                    markerText = "A",
                    label = "Paris",
                    state = RMapQuestionOptionState.Selected,
                    enabled = true,
                    onClick = {}
                )
            }
        }
    }
}
