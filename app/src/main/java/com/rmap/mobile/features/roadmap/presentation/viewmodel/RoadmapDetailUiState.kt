package com.rmap.mobile.features.roadmap.presentation.viewmodel

data class RoadmapDetailUiState(
    val roadmapId: String = "",
    val title: String = "",
    val progressFraction: Float = 0f,
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val sections: List<RoadmapModuleSectionUiModel> = emptyList(),
    val aiTip: AiScholarTipUiModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

data class RoadmapModuleSectionUiModel(
    val title: String,
    val modules: List<com.rmap.mobile.features.roadmap.presentation.components.ModuleCardUiModel>
)

data class AiScholarTipUiModel(
    val currentModule: String,
    val recommendedTopic: String,
    val nextModule: String
)
