package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.components.quiz.RMapAnimatedQuestionPager
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionNavigationActions
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionProgressHeader
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
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
                modifier = Modifier
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

@Preview(showBackground = true, name = "AI Questions Content - Answered", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionContentPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionContent(
            question = AiRoadmapPreviewData.optionQuestion,
            questions = listOf(AiRoadmapPreviewData.optionQuestion, AiRoadmapPreviewData.customQuestion),
            currentQuestionIndex = 0,
            questionProgressText = "Question 1 of 2",
            answeredText = "1/2 answered",
            customAnswerLabel = "Custom answer",
            customAnswerPlaceholder = "Write your own answer if the options do not fit...",
            previousText = "Previous",
            nextText = "Next",
            generateText = "Generate",
            errorText = null,
            isFirstQuestion = true,
            isLastQuestion = false,
            isCurrentQuestionAnswered = true,
            progressFraction = 0.5f,
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> },
            onPreviousClick = {},
            onNextClick = {},
            onGenerateClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Questions Content - Error", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionContentErrorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionContent(
            question = AiRoadmapPreviewData.customQuestion,
            questions = listOf(AiRoadmapPreviewData.optionQuestion, AiRoadmapPreviewData.customQuestion),
            currentQuestionIndex = 1,
            questionProgressText = "Question 2 of 2",
            answeredText = "1/2 answered",
            customAnswerLabel = "Custom answer",
            customAnswerPlaceholder = "Write your own answer if the options do not fit...",
            previousText = "Previous",
            nextText = "Next",
            generateText = "Generate",
            errorText = "Answer every question before continuing.",
            isFirstQuestion = false,
            isLastQuestion = true,
            isCurrentQuestionAnswered = false,
            progressFraction = 1f,
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> },
            onPreviousClick = {},
            onNextClick = {},
            onGenerateClick = {}
        )
    }
}
