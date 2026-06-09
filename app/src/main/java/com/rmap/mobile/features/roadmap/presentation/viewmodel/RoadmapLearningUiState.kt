package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.annotation.StringRes

data class RoadmapLearningUiState(
    val roadmapId: String = "",
    val nodeId: String = "",
    val skillId: String = "",
    val nodeTitle: String = "",
    val requirement: RoadmapNodeRequirement = RoadmapNodeRequirement.Required,
    val skill: SkillLearningDetailUiModel? = null,
    val resources: List<SkillLearningResourceUiModel> = emptyList(),
    val isCompleted: Boolean = false,
    val canTakeQuiz: Boolean = false,
    val canMarkCompleted: Boolean = false,
    val isNodeLocked: Boolean = false,
    val isLoading: Boolean = true,
    val isCompleting: Boolean = false,
    val isStartingRoadmapForQuiz: Boolean = false,
    @StringRes val completionBlockedMessageResId: Int? = null,
    @StringRes val errorMessageResId: Int? = null
)

data class SkillLearningDetailUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val estimatedHours: Int?
)

data class SkillLearningResourceUiModel(
    val id: String,
    val title: String,
    val url: String,
    @StringRes val platformLabelResId: Int,
    val rawPlatform: String?,
    val isFree: Boolean,
    @StringRes val levelLabelResId: Int?
)
