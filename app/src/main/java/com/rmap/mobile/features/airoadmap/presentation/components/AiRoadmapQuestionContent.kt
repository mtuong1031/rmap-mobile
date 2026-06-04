package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rmap.mobile.core.ui.components.quiz.RMapAnimatedQuestionPager
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionNavigationActions
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionProgressHeader
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel

@Composable
fun AiRoadmapQuestionContent(
    question: AiRoadmapQuestionUiModel,
    questions: List<AiRoadmapQuestionUiModel>,
    currentQuestionIndex: Int,
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
        RMapQuestionProgressHeader(
            progressText = questionProgressText,
            answeredText = answeredText,
            progressFraction = progressFraction
        )

        RMapAnimatedQuestionPager(
            currentQuestionIndex = currentQuestionIndex
        ) { questionIndex ->
            AiRoadmapQuestionCard(
                question = questions.getOrNull(questionIndex) ?: question,
                customAnswerLabel = customAnswerLabel,
                customAnswerPlaceholder = customAnswerPlaceholder,
                onOptionSelected = onOptionSelected,
                onCustomAnswerChange = onCustomAnswerChange
            )
        }

        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        RMapQuestionNavigationActions(
            previousText = previousText,
            nextText = nextText,
            finalText = generateText,
            isFirst = isFirstQuestion,
            isLast = isLastQuestion,
            enabled = isCurrentQuestionAnswered,
            busy = false,
            onPrevious = onPreviousClick,
            onNext = onNextClick,
            onFinal = onGenerateClick
        )
    }
}
