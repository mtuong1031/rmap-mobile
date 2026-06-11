package com.rmap.mobile.features.airoadmap.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapHeader
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapPreviewData
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapStepHeader
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapStep
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState
import com.rmap.mobile.navigation.NavBarDestination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import com.rmap.mobile.core.ui.components.RMapNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRoadmapScreen(
    uiState: AiRoadmapUiState,
    selectedDestination: NavBarDestination,
    onDestinationSelected: (NavBarDestination) -> Unit,
    reselectEvent: Flow<NavBarDestination> = emptyFlow(),
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
    onViewGeneratedRoadmap: () -> Unit,
    onExploreClick: () -> Unit,
    onExploreRoadmapsClick: () -> Unit,
    onRoadmapSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(reselectEvent) {
        reselectEvent.collectLatest {
            listState.animateScrollToItem(0)
        }
    }
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
        topBar = {
            if (uiState.step != AiRoadmapStep.Library) {
                AiRoadmapStepHeader(
                    eyebrow = stringResource(R.string.ai_roadmap_eyebrow),
                    title = stringResource(
                        when (uiState.step) {
                            AiRoadmapStep.Generating -> R.string.ai_roadmap_generating_title
                            else -> R.string.ai_roadmap_setup_title
                        }
                    ),
                    description = "",
                    backContentDescription = stringResource(R.string.content_description_back),
                    onBackClick = onBackToLibrary,
                    compact = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(horizontal = Dimens.spacingScreenHorizontal)
                )
            }
        },
        bottomBar = {
            RMapNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        val bottomNavigationPadding = if (uiState.step == AiRoadmapStep.Questions) {
            Dimens.spacingNone
        } else {
            Dimens.floatingNavBarHeight
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.spacingScreenHorizontal,
                top = innerPadding.calculateTopPadding() + if (uiState.step == AiRoadmapStep.Library) {
                    Dimens.spacingScreenTopCompact
                } else {
                    Dimens.spacingMd
                },
                end = Dimens.spacingScreenHorizontal,
                bottom = innerPadding.calculateBottomPadding() +
                    Dimens.spacingScreenBottomCompact +
                    bottomNavigationPadding
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
        ) {
            if (uiState.step == AiRoadmapStep.Library) {
                item {
                    RMapHeader(
                        greetingText = stringResource(R.string.ai_roadmap_library_eyebrow),
                        headingText = stringResource(R.string.ai_roadmap_library_title),
                        greetingIcon = Icons.Outlined.AutoAwesome,
                        actionIcon = Icons.Outlined.Psychology,
                    )
                }
            }

            item {
                when (uiState.step) {
                    AiRoadmapStep.Library -> AiRoadmapLibraryScreen(
                        uiState = uiState,
                        onSearchQueryChange = onSearchQueryChange,
                        onCreateRoadmapClick = onCreateRoadmapClick,
                        onSeeMoreGeneratedRoadmaps = onSeeMoreGeneratedRoadmaps,
                        onSeeAllGeneratedRoadmaps = onSeeAllGeneratedRoadmaps,
                        onSeeLessGeneratedRoadmaps = onSeeLessGeneratedRoadmaps,
                        onExploreRoadmapsClick = onExploreRoadmapsClick,
                        onRoadmapSelected = onRoadmapSelected
                    )

                    AiRoadmapStep.Setup -> AiRoadmapSetupScreen(
                        uiState = uiState,
                        onTopicChange = onTopicChange,
                        onDeadlineClick = { isDatePickerVisible = true },
                        onDailyStudyHoursChange = onDailyStudyHoursChange,
                        onSubmitSetup = onSubmitSetup
                    )

                    AiRoadmapStep.Questions -> AiRoadmapQuestionsScreen(
                        uiState = uiState,
                        onOptionSelected = onOptionSelected,
                        onCustomAnswerChange = onCustomAnswerChange,
                        onPreviousQuestion = onPreviousQuestion,
                        onNextQuestion = onNextQuestion,
                        onGenerateClick = ::requestPermissionThenGenerate
                    )

                    AiRoadmapStep.Generating -> AiRoadmapGeneratingScreen(
                        uiState = uiState,
                        onExploreClick = onExploreClick,
                        onViewRoadmapClick = onViewGeneratedRoadmap,
                        onCancelGeneration = onCancelGeneration
                    )
                }
            }
        }
    }
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
            uiState = AiRoadmapPreviewData.libraryState,
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
            onViewGeneratedRoadmap = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Screen - Setup", backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun AiRoadmapScreenSetupPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapScreen(
            uiState = AiRoadmapPreviewData.setupState,
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
            onViewGeneratedRoadmap = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Screen - Questions", backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun AiRoadmapScreenQuestionsPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapScreen(
            uiState = AiRoadmapPreviewData.questionsState,
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
            onViewGeneratedRoadmap = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Screen - Generating", backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun AiRoadmapScreenGeneratingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapScreen(
            uiState = AiRoadmapPreviewData.generatingState,
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
            onViewGeneratedRoadmap = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Screen - Loading", backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun AiRoadmapScreenLoadingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapScreen(
            uiState = AiRoadmapUiState(isLoadingGeneratedRoadmaps = true),
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
            onViewGeneratedRoadmap = {},
            onExploreClick = {},
            onExploreRoadmapsClick = {},
            onRoadmapSelected = {}
        )
    }
}
