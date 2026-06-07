package com.rmap.mobile.features.airoadmap.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rmap.mobile.R
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapFormError
import java.text.DateFormat
import java.util.Date

@Composable
internal fun AiRoadmapFormError?.toMessage(): String? {
    return when (this) {
        AiRoadmapFormError.TopicRequired -> stringResource(R.string.ai_roadmap_error_topic_required)
        AiRoadmapFormError.DeadlineRequired -> stringResource(R.string.ai_roadmap_error_deadline_required)
        AiRoadmapFormError.DeadlineInPast -> stringResource(R.string.ai_roadmap_error_deadline_past)
        AiRoadmapFormError.QuestionsLoadFailed -> stringResource(R.string.ai_roadmap_error_questions)
        AiRoadmapFormError.AnswerAllQuestions -> stringResource(R.string.ai_roadmap_error_answer_all)
        AiRoadmapFormError.CustomAnswerRequired -> stringResource(R.string.ai_roadmap_error_custom_answer_required)
        AiRoadmapFormError.GenerationFailed -> stringResource(R.string.ai_roadmap_error_generation)
        null -> null
    }
}

internal fun Long.toDisplayDate(): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(this))
}
