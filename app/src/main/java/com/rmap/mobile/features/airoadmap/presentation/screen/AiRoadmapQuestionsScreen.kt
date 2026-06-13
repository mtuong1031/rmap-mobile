package com.rmap.mobile.features.airoadmap.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapQuestionContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapPreviewData
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState

@Composable
internal fun AiRoadmapQuestionsScreen(
    uiState: AiRoadmapUiState,
    onOptionSelected: (questionId: String, optionId: String) -> Unit,
    onCustomAnswerChange: (questionId: String, answer: String) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onGenerateClick: () -> Unit
) {
    val question = uiState.currentQuestion ?: return

    AiRoadmapQuestionContent(
        question = question,
        questions = uiState.questions,
        currentQuestionIndex = uiState.currentQuestionIndex,
        questionProgressText = stringResource(
            R.string.ai_roadmap_question_progress,
            uiState.currentQuestionIndex + 1,
            uiState.questions.size
        ),
        answeredText = stringResource(
            R.string.ai_roadmap_answered_count,
            uiState.answeredQuestionCount,
            uiState.questions.size
        ),
        customAnswerLabel = stringResource(R.string.ai_roadmap_custom_answer_label),
        customAnswerPlaceholder = stringResource(R.string.ai_roadmap_custom_answer_placeholder),
        previousText = stringResource(R.string.ai_roadmap_previous),
        nextText = stringResource(R.string.ai_roadmap_next),
        generateText = stringResource(R.string.ai_roadmap_generate),
        errorText = null,
        isFirstQuestion = uiState.isFirstQuestion,
        isLastQuestion = uiState.isLastQuestion,
        isCurrentQuestionAnswered = uiState.isCurrentQuestionAnswered,
        progressFraction = (uiState.currentQuestionIndex + 1).toFloat() /
                uiState.questions.size.toFloat(),
        onOptionSelected = onOptionSelected,
        onCustomAnswerChange = onCustomAnswerChange,
        onPreviousClick = onPreviousQuestion,
        onNextClick = onNextQuestion,
        onGenerateClick = onGenerateClick
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionsScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionsScreen(
            uiState = AiRoadmapPreviewData.questionsState,
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> },
            onPreviousQuestion = {},
            onNextQuestion = {},
            onGenerateClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Questions - Error", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapQuestionsScreenErrorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapQuestionsScreen(
            uiState = AiRoadmapPreviewData.questionsErrorState,
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> },
            onPreviousQuestion = {},
            onNextQuestion = {},
            onGenerateClick = {}
        )
    }
}
