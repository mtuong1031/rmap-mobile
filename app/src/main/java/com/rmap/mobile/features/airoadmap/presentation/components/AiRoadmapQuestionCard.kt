package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionCardScaffold
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionRow
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionState
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel

@Composable
fun AiRoadmapQuestionCard(
    question: AiRoadmapQuestionUiModel,
    customAnswerLabel: String,
    customAnswerPlaceholder: String,
    onOptionSelected: (questionId: String, optionId: String) -> Unit,
    onCustomAnswerChange: (questionId: String, answer: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val customOptionPlaceholder = customAnswerPlaceholder.ifBlank { customAnswerLabel }

    RMapQuestionCardScaffold(
        eyebrow = question.skillName,
        prompt = question.prompt,
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            question.options.forEach { option ->
                RMapQuestionOptionRow(
                    markerText = option.numberText,
                    label = option.label,
                    state = if (option.id == question.selectedOptionId) {
                        RMapQuestionOptionState.Selected
                    } else {
                        RMapQuestionOptionState.Default
                    },
                    enabled = true,
                    onClick = { onOptionSelected(question.id, option.id) }
                )
            }

            AiRoadmapCustomAnswerOptionRow(
                markerText = (question.options.size + 1).toString(),
                value = question.customAnswer,
                placeholder = customOptionPlaceholder,
                isSelected = question.isCustomAnswerSelected,
                onValueChange = { onCustomAnswerChange(question.id, it) },
                onClick = { onCustomAnswerChange(question.id, question.customAnswer) }
            )
        }
    }
}

@Composable
private fun AiRoadmapCustomAnswerOptionRow(
    markerText: String,
    value: String,
    placeholder: String,
    isSelected: Boolean,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = if (isSelected) {
        RMapQuestionOptionState.Selected
    } else {
        RMapQuestionOptionState.Default
    }
    val shape = AppShapes.button
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = MaterialTheme.colorScheme.onSurface
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = CUSTOM_ANSWER_PLACEHOLDER_ALPHA)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = shape
            )
            .border(
                width = Dimens.borderThin,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(Dimens.spacingMd),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AiRoadmapQuestionOptionMarker(
            markerText = markerText,
            state = state
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = contentColor,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !isSelected) {
                        onClick()
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = placeholderColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
private fun AiRoadmapQuestionOptionMarker(
    markerText: String,
    state: RMapQuestionOptionState,
    modifier: Modifier = Modifier
) {
    val isSelected = state == RMapQuestionOptionState.Selected

    Box(
        modifier = modifier
            .size(Dimens.controlSm)
            .clip(AppShapes.iconContainer)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = markerText,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private const val CUSTOM_ANSWER_PLACEHOLDER_ALPHA = 0.72f

@Preview(showBackground = true, name = "AI Question Card - Options", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionCardOptionsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionCard(
            question = AiRoadmapPreviewData.optionQuestion,
            customAnswerLabel = "Custom answer",
            customAnswerPlaceholder = "Write your own answer if the options do not fit...",
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "AI Question Card - Custom", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionCardCustomPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionCard(
            question = AiRoadmapPreviewData.customQuestion,
            customAnswerLabel = "Custom answer",
            customAnswerPlaceholder = "Write your own answer if the options do not fit...",
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> }
        )
    }
}
