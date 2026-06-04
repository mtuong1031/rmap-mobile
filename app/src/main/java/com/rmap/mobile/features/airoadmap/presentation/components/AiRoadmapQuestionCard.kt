package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionCardScaffold
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionRow
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionState
import com.rmap.mobile.core.ui.theme.Dimens
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
    RMapQuestionCardScaffold(
        eyebrow = question.skillName,
        prompt = question.prompt,
        modifier = modifier
    ) {
        if (question.options.isNotEmpty()) {
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
            }
        }

        if (question.requiresCustomAnswer) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Text(
                    text = customAnswerLabel,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                RMapTextInput(
                    value = question.customAnswer,
                    onValueChange = { onCustomAnswerChange(question.id, it) },
                    placeholder = customAnswerPlaceholder,
                    singleLine = false,
                    height = 112.dp,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }
    }
}
