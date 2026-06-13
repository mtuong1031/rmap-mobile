package com.rmap.mobile.features.airoadmap.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapSetupContent
import com.rmap.mobile.features.airoadmap.presentation.components.AiRoadmapPreviewData
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapUiState

@Composable
internal fun AiRoadmapSetupScreen(
    uiState: AiRoadmapUiState,
    onTopicChange: (String) -> Unit,
    onDeadlineClick: () -> Unit,
    onDailyStudyHoursChange: (Float) -> Unit,
    onSubmitSetup: () -> Unit
) {
    AiRoadmapSetupContent(
        topic = uiState.topic,
        deadlineText = uiState.deadlineEpochMillis?.toDisplayDate()
            ?: stringResource(R.string.ai_roadmap_deadline_placeholder),
        dailyStudyHours = uiState.dailyStudyHours,
        isSubmitEnabled = uiState.isSetupSubmitEnabled,
        isLoading = uiState.isLoadingQuestions,
        errorText = null,
        guideTitle = stringResource(R.string.ai_roadmap_setup_guide_title),
        guideBody = stringResource(R.string.ai_roadmap_setup_guide_body),
        topicLabel = stringResource(R.string.ai_roadmap_topic_label),
        topicPlaceholder = stringResource(R.string.ai_roadmap_topic_text),
        suggestionsLabel = stringResource(R.string.ai_roadmap_suggestions_label),
        suggestedTopics = stringArrayResource(R.array.ai_roadmap_topic_suggestions).toList(),
        deadlineLabel = stringResource(R.string.ai_roadmap_deadline_label),
        deadlineSupportingText = stringResource(R.string.ai_roadmap_deadline_supporting_text),
        dailyHoursLabel = stringResource(R.string.ai_roadmap_daily_hours_label),
        dailyHoursSupportingText = stringResource(R.string.ai_roadmap_daily_hours_supporting_text),
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
        onDeadlineClick = onDeadlineClick,
        onDailyStudyHoursChange = onDailyStudyHoursChange,
        onSubmitClick = onSubmitSetup
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupScreen(
            uiState = AiRoadmapPreviewData.setupState,
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitSetup = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Setup - Preparing", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupScreenPreparingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupScreen(
            uiState = AiRoadmapPreviewData.setupLoadingState,
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitSetup = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Roadmap Setup - Error", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupScreenErrorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupScreen(
            uiState = AiRoadmapPreviewData.setupErrorState,
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitSetup = {}
        )
    }
}
