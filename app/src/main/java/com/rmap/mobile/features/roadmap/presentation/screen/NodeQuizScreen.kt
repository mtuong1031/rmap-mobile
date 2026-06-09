package com.rmap.mobile.features.roadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionCardScaffold
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionInlineError
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionRow
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionOptionState
import com.rmap.mobile.core.ui.components.quiz.RMapQuestionPagerScaffold
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SuccessContainerDark
import com.rmap.mobile.core.ui.theme.SuccessContainerLight
import com.rmap.mobile.core.ui.theme.SuccessDark
import com.rmap.mobile.core.ui.theme.SuccessLight
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapDecoratedCard
import com.rmap.mobile.features.roadmap.presentation.components.common.RoadmapPill
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizOptionUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizQuestionUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizQuestionResultUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizResultUiModel
import com.rmap.mobile.features.roadmap.presentation.viewmodel.NodeQuizUiState

@Composable
fun NodeQuizScreen(
    uiState: NodeQuizUiState,
    onBackClick: () -> Unit,
    onOptionSelected: (questionId: String, optionKey: String) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onSubmitClick: () -> Unit,
    onDoneClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            lazyListState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage != null && uiState.questions.isEmpty() -> {
                QuizErrorState(
                    message = uiState.errorMessage.ifBlank {
                        stringResource(R.string.roadmap_quiz_error_fallback)
                    },
                    onRetryClick = onRetryClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.spacingScreenHorizontal,
                        top = Dimens.controlXl + Dimens.spacingLg,
                        end = Dimens.spacingScreenHorizontal,
                        bottom = QuizBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                ) {
                    uiState.result?.let { result ->
                        item {
                            QuizResultSummaryCard(result = result)
                        }
                    }

                    if (uiState.result == null) {
                        uiState.currentQuestion?.let { question ->
                            item {
                                RMapQuestionPagerScaffold(
                                    currentQuestionIndex = uiState.currentQuestionIndex,
                                    questionProgressText = stringResource(
                                        R.string.roadmap_quiz_question_progress,
                                        uiState.currentQuestionIndex + 1,
                                        uiState.questions.size
                                    ),
                                    answeredText = stringResource(
                                        R.string.roadmap_quiz_answered_count,
                                        uiState.answeredQuestionCount,
                                        uiState.questions.size
                                    ),
                                    progressFraction = uiState.progressFraction,
                                    errorText = uiState.errorMessage,
                                    previousText = stringResource(R.string.roadmap_quiz_previous),
                                    nextText = stringResource(R.string.roadmap_quiz_next),
                                    finalText = stringResource(R.string.roadmap_quiz_submit),
                                    isFirst = uiState.isFirstQuestion,
                                    isLast = uiState.isLastQuestion,
                                    isNextEnabled = uiState.isCurrentQuestionAnswered,
                                    isBusy = uiState.isSubmitting,
                                    onPrevious = onPreviousQuestion,
                                    onNext = onNextQuestion,
                                    onFinal = onSubmitClick
                                ) { questionIndex ->
                                    val currentQuestion = uiState.questions.getOrNull(questionIndex) ?: question
                                    QuizQuestionCard(
                                        questionNumber = questionIndex + 1,
                                        question = currentQuestion,
                                        selectedOption = uiState.selectedAnswers[currentQuestion.id],
                                        result = null,
                                        isLocked = uiState.isSubmitting,
                                        onOptionSelected = { optionKey ->
                                            onOptionSelected(currentQuestion.id, optionKey)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        uiState.result.let { result ->
                            item {
                                QuizReviewHeader(
                                    correctCount = result.correctCount,
                                    totalQuestions = result.totalQuestions
                                )
                            }
                        }

                        uiState.errorMessage?.let { message ->
                            item {
                                RMapQuestionInlineError(message = message)
                            }
                        }

                        itemsIndexed(
                            items = uiState.questions,
                            key = { _, question -> question.id }
                        ) { index, question ->
                            val questionResult = uiState.result
                                .questionResults
                                .firstOrNull { result -> result.questionId == question.id }
                            QuizQuestionCard(
                                questionNumber = index + 1,
                                question = question,
                                selectedOption = uiState.selectedAnswers[question.id],
                                result = questionResult,
                                isLocked = true,
                                onOptionSelected = { optionKey ->
                                    onOptionSelected(question.id, optionKey)
                                }
                            )
                        }
                    }
                }

                if (uiState.result != null) {
                    QuizBottomAction(
                        uiState = uiState,
                        onSubmitClick = onSubmitClick,
                        onDoneClick = onDoneClick,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                start = Dimens.spacingScreenHorizontal,
                                end = Dimens.spacingScreenHorizontal,
                                bottom = Dimens.spacingXl
                            )
                    )
                }
            }
        }

        QuizTopBar(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun QuizTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.controlXl)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.spacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(Dimens.controlSm)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_description_back),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.iconLg)
            )
        }
        Text(
            text = stringResource(R.string.roadmap_quiz_title),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuizResultSummaryCard(
    result: NodeQuizResultUiModel,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val accentColor = if (result.passed) {
        if (isDark) SuccessDark else SuccessLight
    } else {
        MaterialTheme.colorScheme.error
    }
    val accentContainerColor = if (result.passed) {
        if (isDark) SuccessContainerDark else SuccessContainerLight
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val onAccentContainerColor = if (result.passed) {
        if (isDark) SuccessDark else SuccessLight
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.card,
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = accentColor.copy(alpha = QuizResultBorderAlpha),
        backgroundBrush = Brush.linearGradient(
            listOf(
                accentContainerColor.copy(alpha = QuizResultContainerAlpha),
                MaterialTheme.colorScheme.surface
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(QuizResultIconContainerSize)
                            .background(accentContainerColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (result.passed) {
                                Icons.Outlined.CheckCircle
                            } else {
                                Icons.Outlined.ErrorOutline
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(Dimens.iconXl)
                        )
                    }

                    RoadmapPill(
                        text = stringResource(
                            if (result.passed) {
                                R.string.roadmap_quiz_passed
                            } else {
                                R.string.roadmap_quiz_try_again
                            }
                        ),
                        containerColor = accentContainerColor,
                        contentColor = onAccentContainerColor
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = stringResource(
                            if (result.passed) {
                                R.string.roadmap_quiz_passed_heading
                            } else {
                                R.string.roadmap_quiz_failed_heading
                            }
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(
                            if (result.passed) {
                                R.string.roadmap_quiz_passed_message
                            } else {
                                R.string.roadmap_quiz_failed_message
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.roadmap_quiz_score_percent_value, result.scorePercent),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = stringResource(
                            R.string.roadmap_quiz_correct_summary,
                            result.correctCount,
                            result.totalQuestions
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(
                            if (result.passed) {
                                R.string.roadmap_quiz_node_completed_summary
                            } else {
                                R.string.roadmap_quiz_node_in_progress_summary
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            result.suggestion?.let { suggestion ->
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
private fun QuizReviewHeader(
    correctCount: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(R.string.roadmap_quiz_review_heading),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f).padding(end = Dimens.spacingSm)
            )
            RoadmapPill(
                text = stringResource(R.string.roadmap_quiz_correct_summary, correctCount, totalQuestions),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = stringResource(R.string.roadmap_quiz_review_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


@Composable
private fun QuizQuestionCard(
    questionNumber: Int,
    question: NodeQuizQuestionUiModel,
    selectedOption: String?,
    result: NodeQuizQuestionResultUiModel?,
    isLocked: Boolean,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RMapQuestionCardScaffold(
        eyebrow = stringResource(R.string.roadmap_quiz_question_number, questionNumber),
        prompt = question.text,
        modifier = modifier,
        trailingHeaderContent = {
            result?.let {
                RoadmapPill(
                    text = stringResource(
                        if (it.isCorrect) {
                            R.string.roadmap_quiz_question_correct
                        } else {
                            R.string.roadmap_quiz_question_needs_review
                        }
                    ),
                    containerColor = if (it.isCorrect) {
                        if (isSystemInDarkTheme()) SuccessContainerDark else SuccessContainerLight
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                    contentColor = if (it.isCorrect) {
                        if (isSystemInDarkTheme()) SuccessDark else SuccessLight
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            question.options.forEach { option ->
                val optionState = result?.optionStateFor(option.key)
                    ?: if (selectedOption == option.key) {
                        RMapQuestionOptionState.Selected
                    } else {
                        RMapQuestionOptionState.Default
                    }
                val statusLabelResId = result?.answerLabelFor(option.key)

                RMapQuestionOptionRow(
                    markerText = option.key,
                    label = option.text,
                    state = optionState,
                    enabled = !isLocked,
                    onClick = { onOptionSelected(option.key) },
                    supportingContent = if (statusLabelResId != null) {
                        {
                            RoadmapPill(
                                text = stringResource(statusLabelResId),
                                containerColor = MaterialTheme.colorScheme.surface.copy(
                                    alpha = QuizOptionLabelContainerAlpha
                                ),
                                contentColor = when (optionState) {
                                    RMapQuestionOptionState.Correct -> {
                                        if (isSystemInDarkTheme()) SuccessDark else SuccessLight
                                    }

                                    RMapQuestionOptionState.Incorrect -> {
                                        MaterialTheme.colorScheme.error
                                    }

                                    RMapQuestionOptionState.Default,
                                    RMapQuestionOptionState.Selected,
                                    RMapQuestionOptionState.Neutral -> {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                }
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Composable
private fun QuizBottomAction(
    uiState: NodeQuizUiState,
    onSubmitClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoadmapDecoratedCard(
        modifier = modifier,
        shape = AppShapes.button,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        borderColor = MaterialTheme.colorScheme.outlineVariant
    ) {
        RMapButton(
            text = if (uiState.result == null) {
                stringResource(R.string.roadmap_quiz_submit)
            } else if (uiState.result.passed) {
                stringResource(R.string.action_done)
            } else {
                stringResource(R.string.roadmap_quiz_review_resources)
            },
            onClick = if (uiState.result == null) onSubmitClick else onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            variant = RMapButtonVariant.Primary,
            size = RMapButtonSize.Medium,
            enabled = uiState.canSubmit || uiState.result != null
        )
    }
}

private fun NodeQuizQuestionResultUiModel.optionStateFor(optionKey: String): RMapQuestionOptionState {
    return when {
        optionKey == correctOption -> RMapQuestionOptionState.Correct
        optionKey == selectedOption && !isCorrect -> RMapQuestionOptionState.Incorrect
        else -> RMapQuestionOptionState.Neutral
    }
}

private fun NodeQuizQuestionResultUiModel.answerLabelFor(optionKey: String): Int? {
    return when {
        optionKey == selectedOption && optionKey == correctOption -> R.string.roadmap_quiz_correct_selected_answer
        optionKey == selectedOption -> R.string.roadmap_quiz_your_answer
        optionKey == correctOption -> R.string.roadmap_quiz_correct_answer
        else -> null
    }
}


@Composable
private fun QuizErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.spacingXxl),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.error
            )
        )
        RMapButton(
            text = stringResource(R.string.action_retry),
            onClick = onRetryClick,
            variant = RMapButtonVariant.Neutral,
            size = RMapButtonSize.Small
        )
    }
}

private val QuizBottomPadding =
    Dimens.controlXl + Dimens.spacingScreenBottomCompact + Dimens.spacingXxl
private val QuizResultIconContainerSize = Dimens.controlXl
private const val QuizResultBorderAlpha = 0.35f
private const val QuizResultContainerAlpha = 0.68f
private const val QuizOptionLabelContainerAlpha = 0.62f

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun NodeQuizScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        NodeQuizScreen(
            uiState = NodeQuizUiState(
                questions = listOf(
                    NodeQuizQuestionUiModel(
                        id = "question-1",
                        text = "What does DNS help browsers do?",
                        options = listOf(
                            NodeQuizOptionUiModel("A", "Resolve a domain name to an address"),
                            NodeQuizOptionUiModel("B", "Style the page"),
                            NodeQuizOptionUiModel("C", "Compile Kotlin"),
                            NodeQuizOptionUiModel("D", "Compress images")
                        )
                    )
                ),
                selectedAnswers = mapOf("question-1" to "A"),
                isLoading = false
            ),
            onBackClick = {},
            onOptionSelected = { _, _ -> },
            onPreviousQuestion = {},
            onNextQuestion = {},
            onSubmitClick = {},
            onDoneClick = {},
            onRetryClick = {}
        )
    }
}
