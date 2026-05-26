package com.rmap.mobile.features.airoadmap.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.components.RMapNavigationBar
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.domain.model.AiRoadmapGenerationStatus
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapGenerationContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapLibraryContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapQuestionContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapSetupContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapStepHeader
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiGeneratedRoadmapUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapFormError
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionOptionUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapQuestionUiModel
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapStep
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState
import com.rmap.mobile.navigation.NavBarDestination
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRoadmapScreen(
    uiState: AiRoadmapUiState,
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCreateRoadmapClick: () -> Unit,
    onSeeMoreGeneratedRoadmaps: () -> Unit,
    onSeeAllGeneratedRoadmaps: () -> Unit,
    onSeeLessGeneratedRoadmaps: () -> Unit,
    onBackToLibrary: () -> Unit,
    onTopicChange: (String) -> Unit,
    onDeadlineSelected: (Long) -> Unit,
    onDailyStudyHoursChange: (Float) -> Unit,
    onSubmitSetup: () -> Unit,
    onOptionSelected: (questionId: String, optionId: String) -> Unit,
    onCustomAnswerChange: (questionId: String, answer: String) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onSubmitAnswers: () -> Unit,
    onCancelGeneration: () -> Unit,
    onExploreClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    onRoadmapSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var isNotificationPromptVisible by remember { mutableStateOf(false) }
    var shouldGenerateAfterPermission by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (shouldGenerateAfterPermission) {
            shouldGenerateAfterPermission = false
            onSubmitAnswers()
        }
    }

    fun requestPermissionThenGenerate() {
        if (context.hasNotificationPermission()) {
            onSubmitAnswers()
        } else {
            isNotificationPromptVisible = true
        }
    }

    if (isDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.deadlineEpochMillis
        )

        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(onDeadlineSelected)
                        isDatePickerVisible = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_done))
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerVisible = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (isNotificationPromptVisible) {
        AlertDialog(
            onDismissRequest = {
                isNotificationPromptVisible = false
                onSubmitAnswers()
            },
            title = { Text(text = stringResource(R.string.ai_roadmap_permission_title)) },
            text = { Text(text = stringResource(R.string.ai_roadmap_permission_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        isNotificationPromptVisible = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            shouldGenerateAfterPermission = true
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onSubmitAnswers()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.ai_roadmap_permission_allow))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isNotificationPromptVisible = false
                        onSubmitAnswers()
                    }
                ) {
                    Text(text = stringResource(R.string.ai_roadmap_permission_skip))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            RMapNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.spacingScreenHorizontal,
                top = Dimens.spacingScreenTopCompact,
                end = Dimens.spacingScreenHorizontal,
                bottom = innerPadding.calculateBottomPadding() + Dimens.spacingScreenBottomCompact
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
        ) {
            item {
                if (uiState.step == AiRoadmapStep.Library) {
                    RMapHeader(
                        greetingText = stringResource(R.string.ai_roadmap_library_eyebrow),
                        headingText = stringResource(R.string.ai_roadmap_library_title),
                        greetingIcon = Icons.Outlined.AutoAwesome,
                        actionIcon = Icons.Outlined.AutoAwesome
                    )
                } else {
                    AiRoadmapStepHeader(
                        eyebrow = stringResource(R.string.ai_roadmap_eyebrow),
                        title = stringResource(R.string.ai_roadmap_setup_title),
                        description = stringResource(R.string.ai_roadmap_setup_description),
                        backContentDescription = stringResource(R.string.content_description_back),
                        onBackClick = onBackToLibrary,
                        compact = true
                    )
                }
            }

            item {
                when (uiState.step) {
                    AiRoadmapStep.Library -> {
                        AiRoadmapLibraryContent(
                            roadmaps = uiState.visibleGeneratedRoadmaps,
                            searchQuery = uiState.searchQuery,
                            searchPlaceholder = stringResource(R.string.ai_roadmap_library_search_placeholder),
                            createButtonText = stringResource(R.string.ai_roadmap_library_create),
                            sectionTitle = stringResource(R.string.ai_roadmap_library_section_title),
                            sectionSubtitle = stringResource(R.string.ai_roadmap_library_section_subtitle),
                            emptyTitle = stringResource(R.string.ai_roadmap_library_empty_title),
                            emptyBody = stringResource(R.string.ai_roadmap_library_empty_body),
                            searchEmptyTitle = stringResource(R.string.ai_roadmap_library_search_empty_title),
                            searchEmptyBody = stringResource(R.string.ai_roadmap_library_search_empty_body),
                            metadataText = { lessons, weeks ->
                                stringResource(R.string.ai_roadmap_library_metadata, lessons, weeks)
                            },
                            createdAtText = { createdDaysAgo ->
                                if (createdDaysAgo == 0) {
                                    stringResource(R.string.ai_roadmap_created_today)
                                } else {
                                    pluralStringResource(
                                        R.plurals.ai_roadmap_created_days_ago,
                                        createdDaysAgo,
                                        createdDaysAgo
                                    )
                                }
                            },
                            seeAllText = stringResource(R.string.roadmap_see_all),
                            seeLessText = stringResource(R.string.explore_roadmap_library_see_less),
                            seeMoreText = { remainingCount ->
                                stringResource(
                                    R.string.explore_roadmap_library_see_more,
                                    remainingCount.coerceAtMost(AiRoadmapUiState.GENERATED_ROADMAP_PAGE_SIZE)
                                )
                            },
                            exploreButtonText = stringResource(R.string.bookmarks_empty_explore_roadmaps),
                            totalRoadmapCount = uiState.totalGeneratedRoadmapCount,
                            hasAnyRoadmaps = uiState.hasAnyGeneratedRoadmaps,
                            isSearching = uiState.isSearchingGeneratedRoadmaps,
                            canToggleAll = uiState.canToggleAllGeneratedRoadmaps,
                            isShowingAll = uiState.isShowingAllGeneratedRoadmaps,
                            hasMore = uiState.hasMoreGeneratedRoadmaps,
                            onSearchQueryChange = onSearchQueryChange,
                            onCreateClick = onCreateRoadmapClick,
                            onExploreClick = onExploreRoadmapsClick,
                            onRoadmapClick = onRoadmapSelected,
                            onSeeMoreClick = onSeeMoreGeneratedRoadmaps,
                            onSeeAllClick = onSeeAllGeneratedRoadmaps,
                            onSeeLessClick = onSeeLessGeneratedRoadmaps
                        )
                    }

                    AiRoadmapStep.Setup -> {
                        AiRoadmapSetupContent(
                            topic = uiState.topic,
                            deadlineText = uiState.deadlineEpochMillis?.toDisplayDate()
                                ?: stringResource(R.string.ai_roadmap_deadline_placeholder),
                            dailyStudyHours = uiState.dailyStudyHours,
                            isSubmitEnabled = uiState.isSetupSubmitEnabled,
                            isLoading = uiState.isLoadingQuestions,
                            errorText = uiState.formError.toMessage(),
                            topicLabel = stringResource(R.string.ai_roadmap_topic_label),
                            topicPlaceholder = stringResource(R.string.ai_roadmap_topic_placeholder),
                            deadlineLabel = stringResource(R.string.ai_roadmap_deadline_label),
                            dailyHoursLabel = stringResource(R.string.ai_roadmap_daily_hours_label),
                            dailyHoursValueText = stringResource(
                                R.string.ai_roadmap_daily_hours_value,
                                uiState.dailyStudyHours
                            ),
                            submitText = if (uiState.isLoadingQuestions) {
                                stringResource(R.string.ai_roadmap_loading_questions)
                            } else {
                                stringResource(R.string.ai_roadmap_submit_setup)
                            },
                            onTopicChange = onTopicChange,
                            onDeadlineClick = { isDatePickerVisible = true },
                            onDailyStudyHoursChange = onDailyStudyHoursChange,
                            onSubmitClick = onSubmitSetup
                        )
                    }

                    AiRoadmapStep.Questions -> {
                        val question = uiState.currentQuestion
                        if (question != null) {
                            AiRoadmapQuestionContent(
                                question = question,
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
                                errorText = uiState.formError.toMessage(),
                                isFirstQuestion = uiState.isFirstQuestion,
                                isLastQuestion = uiState.isLastQuestion,
                                isCurrentQuestionAnswered = uiState.isCurrentQuestionAnswered,
                                progressFraction = (uiState.currentQuestionIndex + 1).toFloat() /
                                        uiState.questions.size.toFloat(),
                                onOptionSelected = onOptionSelected,
                                onCustomAnswerChange = onCustomAnswerChange,
                                onPreviousClick = onPreviousQuestion,
                                onNextClick = onNextQuestion,
                                onGenerateClick = ::requestPermissionThenGenerate
                            )
                        }
                    }

                    AiRoadmapStep.Generating -> {
                        AiRoadmapGenerationContent(
                            status = uiState.generationStatus,
                            title = stringResource(R.string.ai_roadmap_generating_title),
                            body = stringResource(R.string.ai_roadmap_generating_body),
                            progressText = stringResource(
                                R.string.ai_roadmap_generation_progress,
                                uiState.generationStatus.progressPercent
                            ),
                            exploreText = stringResource(R.string.ai_roadmap_explore_while_generating),
                            cancelText = stringResource(R.string.ai_roadmap_cancel_generation),
                            onExploreClick = onExploreClick,
                            onCancelClick = onCancelGeneration
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiRoadmapFormError?.toMessage(): String? {
    return when (this) {
        AiRoadmapFormError.TopicRequired -> stringResource(R.string.ai_roadmap_error_topic_required)
        AiRoadmapFormError.DeadlineRequired -> stringResource(R.string.ai_roadmap_error_deadline_required)
        AiRoadmapFormError.DeadlineInPast -> stringResource(R.string.ai_roadmap_error_deadline_past)
        AiRoadmapFormError.QuestionsLoadFailed -> stringResource(R.string.ai_roadmap_error_questions)
        AiRoadmapFormError.AnswerAllQuestions -> stringResource(R.string.ai_roadmap_error_answer_all)
        AiRoadmapFormError.GenerationFailed -> stringResource(R.string.ai_roadmap_error_generation)
        null -> null
    }
}

private fun Long.toDisplayDate(): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(this))
}

private fun Context.hasNotificationPermission(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun AiRoadmapScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapScreen(
            uiState = AiRoadmapUiState(
                generatedRoadmaps = listOf(
                    AiGeneratedRoadmapUiModel(
                        id = "ai-android",
                        title = "Android Developer",
                        lessonsCount = 24,
                        durationWeeks = 8,
                        createdDaysAgo = 0
                    )
                ),
                topic = "Android Developer",
                deadlineEpochMillis = System.currentTimeMillis() + 30L * 24L * 60L * 60L * 1000L,
                questions = listOf(
                    AiRoadmapQuestionUiModel(
                        id = "current-level",
                        skillName = "Android Developer",
                        prompt = "How would you describe your current level with Android Developer?",
                        options = listOf(
                            AiRoadmapQuestionOptionUiModel("1", "1", "I am completely new"),
                            AiRoadmapQuestionOptionUiModel("2", "2", "I know the basics"),
                            AiRoadmapQuestionOptionUiModel("3", "3", "I can build small projects"),
                            AiRoadmapQuestionOptionUiModel("4", "4", "I have production experience")
                        )
                    )
                ),
                generationStatus = AiRoadmapGenerationStatus()
            ),
            selectedDestination = NavBarDestination.AiAssistant,
            onDestinationSelected = {},
            onSearchQueryChange = {},
            onCreateRoadmapClick = {},
            onSeeMoreGeneratedRoadmaps = {},
            onSeeAllGeneratedRoadmaps = {},
            onSeeLessGeneratedRoadmaps = {},
            onBackToLibrary = {},
            onTopicChange = {},
            onDeadlineSelected = {},
            onDailyStudyHoursChange = {},
            onSubmitSetup = {},
            onOptionSelected = { _, _ -> },
            onCustomAnswerChange = { _, _ -> },
            onPreviousQuestion = {},
            onNextQuestion = {},
            onSubmitAnswers = {},
            onCancelGeneration = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}
