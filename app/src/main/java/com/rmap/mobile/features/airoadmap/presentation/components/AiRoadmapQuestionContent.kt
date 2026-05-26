package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel

@Composable
fun AiRoadmapQuestionContent(
    question: AiRoadmapQuestionUiModel,
    questionProgressText: String,
    answeredText: String,
    customAnswerLabel: String,
    customAnswerPlaceholder: String,
    previousText: String,
    nextText: String,
    generateText: String,
    errorText: String?,
    isFirstQuestion: Boolean,
    isLastQuestion: Boolean,
    isCurrentQuestionAnswered: Boolean,
    progressFraction: Float,
    onOptionSelected: (questionId: String, optionId: String) -> Unit,
    onCustomAnswerChange: (questionId: String, answer: String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = questionProgressText,
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

        AiRoadmapQuestionCard(
            question = question,
            customAnswerLabel = customAnswerLabel,
            customAnswerPlaceholder = customAnswerPlaceholder,
            onOptionSelected = onOptionSelected,
            onCustomAnswerChange = onCustomAnswerChange
        )

        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            RMapButton(
                text = previousText,
                onClick = onPreviousClick,
                modifier = Modifier.weight(1f),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Medium,
                enabled = !isFirstQuestion
            )
            RMapButton(
                text = if (isLastQuestion) generateText else nextText,
                onClick = if (isLastQuestion) onGenerateClick else onNextClick,
                modifier = Modifier.weight(1f),
                size = RMapButtonSize.Medium,
                enabled = isCurrentQuestionAnswered
            )
        }
    }
}
