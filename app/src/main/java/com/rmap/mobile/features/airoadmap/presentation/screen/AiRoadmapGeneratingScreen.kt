package com.rmap.mobile.features.airoadmap.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapCardDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationPhase
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapPreviewData
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState

@Composable
internal fun AiRoadmapGeneratingScreen(
    uiState: AiRoadmapUiState,
    onExploreClick: () -> Unit,
    onViewRoadmapClick: () -> Unit,
    onCancelGeneration: () -> Unit
) {
    val isSucceeded = uiState.generationStatus.phase == AiRoadmapGenerationPhase.Succeeded
    var isCancelConfirmationVisible by remember { mutableStateOf(false) }

    if (isCancelConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { isCancelConfirmationVisible = false },
            title = { Text(text = stringResource(R.string.ai_roadmap_cancel_confirmation_title)) },
            text = { Text(text = stringResource(R.string.ai_roadmap_cancel_confirmation_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        isCancelConfirmationVisible = false
                        onCancelGeneration()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.ai_roadmap_cancel_confirmation_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { isCancelConfirmationVisible = false }) {
                    Text(text = stringResource(R.string.ai_roadmap_cancel_confirmation_dismiss))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        GeneratingHeader(
            title = if (isSucceeded) {
                stringResource(R.string.ai_roadmap_notification_ready_title)
            } else {
                stringResource(R.string.ai_roadmap_generating_header_title)
            },
            body = if (isSucceeded) {
                stringResource(R.string.ai_roadmap_notification_ready_body, uiState.topic)
            } else {
                stringResource(R.string.ai_roadmap_generating_header_body)
            }
        )

        GenerationProgressCard(
            status = uiState.generationStatus,
            progressText = stringResource(
                R.string.ai_roadmap_generation_progress,
                uiState.generationStatus.progressPercent
            )
        )

        RoadmapInputSummaryCard(uiState = uiState)

        QuizInsightCard(questions = uiState.questions)

        GeneratingActionButtons(
            exploreText = stringResource(R.string.ai_roadmap_explore_while_generating),
            viewRoadmapText = stringResource(R.string.home_roadmap_view_action),
            cancelText = stringResource(R.string.ai_roadmap_cancel_generation),
            noteText = stringResource(R.string.ai_roadmap_generating_background_note),
            isSucceeded = isSucceeded,
            onExploreClick = onExploreClick,
            onViewRoadmapClick = onViewRoadmapClick,
            onCancelClick = { isCancelConfirmationVisible = true }
        )
    }
}

@Composable
private fun GeneratingHeader(
    title: String,
    body: String
) {
    RMapCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.largeCard,
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = HeaderGradientAlpha)
            )
        ),
        border = RMapCardDefaults.themedBorder(
            color = MaterialTheme.colorScheme.primary.copy(alpha = HeaderBorderAlpha)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.iconFrameSize)
                    .clip(AppShapes.iconFrameInner)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = HeaderIconSurfaceAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.iconXxl),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun GenerationProgressCard(
    status: AiRoadmapGenerationStatus,
    progressText: String
) {
    val progress = status.progressPercent.coerceIn(0, 100)
    val currentStage = status.stageLabel.ifBlank { generationStageFallback(progress) }
    val steps = generationSteps(progress)

    RMapCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.card,
        shadowElevation = Dimens.cardElevationXs
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = stringResource(R.string.ai_roadmap_generation_progress_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = currentStage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ProgressBarHeight),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeCap = StrokeCap.Round
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
                steps.forEach { step ->
                    GenerationStepRow(step = step)
                }
            }
        }
    }
}

@Composable
private fun GenerationStepRow(step: GenerationStepUiModel) {
    val contentColor = when (step.state) {
        GenerationStepState.Done -> MaterialTheme.colorScheme.primary
        GenerationStepState.Active -> MaterialTheme.colorScheme.tertiary
        GenerationStepState.Upcoming -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val icon = when (step.state) {
        GenerationStepState.Done -> Icons.Outlined.CheckCircle
        GenerationStepState.Active -> Icons.Outlined.HourglassTop
        GenerationStepState.Upcoming -> Icons.Outlined.RadioButtonUnchecked
    }
    val containerColor = when (step.state) {
        GenerationStepState.Done -> MaterialTheme.colorScheme.primaryContainer
        GenerationStepState.Active -> MaterialTheme.colorScheme.tertiaryContainer
        GenerationStepState.Upcoming -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(StepIconContainerSize)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconSmPlus),
                tint = contentColor
            )
        }
        Text(
            text = step.label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (step.state == GenerationStepState.Upcoming) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (step.state == GenerationStepState.Active) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoadmapInputSummaryCard(uiState: AiRoadmapUiState) {
    val items = buildList {
        val topic = uiState.topic.trim()
        if (topic.isNotBlank()) {
            add(
                SummaryItemUiModel(
                    icon = Icons.Outlined.Route,
                    label = stringResource(R.string.ai_roadmap_summary_title_label),
                    value = topic
                )
            )
        }

        add(
            SummaryItemUiModel(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(R.string.ai_roadmap_summary_deadline_label),
                value = uiState.deadlineEpochMillis?.toDisplayDate()
                    ?: stringResource(R.string.ai_roadmap_summary_deadline_not_set)
            )
        )

        add(
            SummaryItemUiModel(
                icon = Icons.Outlined.Schedule,
                label = stringResource(R.string.ai_roadmap_summary_study_time_label),
                value = stringResource(
                    R.string.ai_roadmap_daily_hours_value,
                    uiState.dailyStudyHours
                )
            )
        )

        uiState.roleCategory?.takeIf { it.isNotBlank() }?.let { roleCategory ->
            add(
                SummaryItemUiModel(
                    icon = Icons.Outlined.TrackChanges,
                    label = stringResource(R.string.ai_roadmap_summary_focus_label),
                    value = roleCategory
                )
            )
        }
    }

    if (items.isEmpty()) return

    SectionCard(
        title = stringResource(R.string.ai_roadmap_summary_section_title),
        subtitle = stringResource(R.string.ai_roadmap_summary_section_subtitle),
        icon = Icons.Outlined.School
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
                ) {
                    rowItems.forEach { item ->
                        SummaryItemRow(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItemRow(
    item: SummaryItemUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.iconXs + Dimens.spacingSm)
                .clip(AppShapes.iconContainer)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = SummaryIconContainerAlpha)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.iconXs),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = SummaryValueMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuizInsightCard(questions: List<AiRoadmapQuestionUiModel>) {
    val answeredQuestions = questions.filter { it.hasAnswer }
    if (answeredQuestions.isEmpty()) return

    var isExpanded by remember { mutableStateOf(false) }
    val displayQuestions = if (isExpanded) answeredQuestions else answeredQuestions.take(MaxVisibleQuizAnswers)

    SectionCard(
        title = stringResource(R.string.ai_roadmap_quiz_summary_title),
        subtitle = stringResource(
            R.string.ai_roadmap_quiz_summary_subtitle,
            answeredQuestions.size
        ),
        icon = Icons.Outlined.Psychology
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            displayQuestions.forEach { question ->
                QuizAnswerRow(question = question)
            }

            if (!isExpanded && answeredQuestions.size > MaxVisibleQuizAnswers) {
                val hiddenCount = answeredQuestions.size - MaxVisibleQuizAnswers
                Text(
                    text = stringResource(R.string.ai_roadmap_quiz_summary_more, hiddenCount),
                    modifier = Modifier
                        .clip(AppShapes.pill)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = SummaryChipAlpha))
                        .clickable { isExpanded = true }
                        .padding(
                            horizontal = Dimens.spacingMd,
                            vertical = Dimens.spacingSm
                        ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else if (isExpanded && answeredQuestions.size > MaxVisibleQuizAnswers) {
                Text(
                    text = stringResource(R.string.action_see_less),
                    modifier = Modifier
                        .clip(AppShapes.pill)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = SummaryChipAlpha))
                        .clickable { isExpanded = false }
                        .padding(
                            horizontal = Dimens.spacingLg,
                            vertical = Dimens.spacingSm
                        ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun QuizAnswerRow(question: AiRoadmapQuestionUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.chip)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Dimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)) {
            Text(
                text = "Q: " + question.prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "A: " + question.answerText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = QuizAnswerMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GeneratingActionButtons(
    exploreText: String,
    viewRoadmapText: String,
    cancelText: String,
    noteText: String,
    isSucceeded: Boolean,
    onExploreClick: () -> Unit,
    onViewRoadmapClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        if (!isSucceeded) {
            Text(
                text = noteText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.card)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = ActionNoteContainerAlpha))
                    .padding(Dimens.spacingLg),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
        
        RMapButton(
            text = if (isSucceeded) viewRoadmapText else exploreText,
            onClick = if (isSucceeded) onViewRoadmapClick else onExploreClick,
            modifier = Modifier.fillMaxWidth(),
            variant = RMapButtonVariant.Primary,
            size = RMapButtonSize.Large
        )

        if (!isSucceeded) {
            RMapCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.button)
                    .clickable(onClick = onCancelClick),
                shape = AppShapes.button,
                border = androidx.compose.foundation.BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = Dimens.spacingXl),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.iconXxl)
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(Dimens.iconSm)
                        )
                    }

                    Text(
                        text = cancelText,
                        modifier = Modifier.padding(start = Dimens.spacingMd),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    RMapCard(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.card,
        shadowElevation = Dimens.cardElevationXs
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.controlMd)
                        .clip(AppShapes.iconContainer)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconMd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun generationSteps(progress: Int): List<GenerationStepUiModel> {
    return listOf(
        GenerationStepUiModel(
            label = stringResource(R.string.ai_roadmap_generation_step_goal),
            state = progress.toStepState(activeFrom = 0, doneAt = 25)
        ),
        GenerationStepUiModel(
            label = stringResource(R.string.ai_roadmap_generation_step_skills),
            state = progress.toStepState(activeFrom = 25, doneAt = 50)
        ),
        GenerationStepUiModel(
            label = stringResource(R.string.ai_roadmap_generation_step_milestones),
            state = progress.toStepState(activeFrom = 50, doneAt = 78)
        ),
        GenerationStepUiModel(
            label = stringResource(R.string.ai_roadmap_generation_step_lessons),
            state = progress.toStepState(activeFrom = 78, doneAt = 100)
        )
    )
}

@Composable
private fun generationStageFallback(progress: Int): String {
    return when {
        progress < 25 -> stringResource(R.string.ai_roadmap_generation_step_goal)
        progress < 50 -> stringResource(R.string.ai_roadmap_generation_step_skills)
        progress < 78 -> stringResource(R.string.ai_roadmap_generation_step_milestones)
        else -> stringResource(R.string.ai_roadmap_generation_step_lessons)
    }
}

private fun Int.toStepState(
    activeFrom: Int,
    doneAt: Int
): GenerationStepState {
    return when {
        this >= doneAt -> GenerationStepState.Done
        this >= activeFrom -> GenerationStepState.Active
        else -> GenerationStepState.Upcoming
    }
}

@Immutable
private data class GenerationStepUiModel(
    val label: String,
    val state: GenerationStepState
)

private enum class GenerationStepState {
    Done,
    Active,
    Upcoming
}

@Immutable
private data class SummaryItemUiModel(
    val icon: ImageVector,
    val label: String,
    val value: String
)

private const val HeaderBorderAlpha = 0.16f
private const val HeaderGradientAlpha = 0.48f
private const val HeaderIconSurfaceAlpha = 0.72f
private const val SummaryIconContainerAlpha = 0.72f
private const val SummaryChipAlpha = 0.72f
private const val ActionNoteContainerAlpha = 0.62f
private const val SummaryValueMaxLines = 1
private const val QuizAnswerMaxLines = 2
private const val MaxVisibleQuizAnswers = 3

private val StepIconContainerSize = 30.dp
private val ProgressBarHeight = 8.dp

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun GeneratingHeaderPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            GeneratingHeader(
                title = "Almost done...",
                body = "RMap is turning your answers into a personalized learning path."
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun GenerationProgressCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            GenerationProgressCard(
                status = AiRoadmapPreviewData.generationStatus,
                progressText = "64%"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun RoadmapInputSummaryCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            RoadmapInputSummaryCard(uiState = AiRoadmapPreviewData.generatingState)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun QuizInsightCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            QuizInsightCard(questions = AiRoadmapPreviewData.generatingState.questions)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun GeneratingActionButtonsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            GeneratingActionButtons(
                exploreText = "Explore app",
                viewRoadmapText = "View roadmap",
                cancelText = "Cancel generation",
                noteText = "You can explore the app while generation continues in the background.",
                isSucceeded = false,
                onExploreClick = {},
                onViewRoadmapClick = {},
                onCancelClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapGeneratingScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapGeneratingScreen(
            uiState = AiRoadmapPreviewData.generatingState,
            onExploreClick = {},
            onViewRoadmapClick = {},
            onCancelGeneration = {}
        )
    }
}
