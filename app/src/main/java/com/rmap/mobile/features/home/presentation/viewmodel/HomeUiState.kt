package com.rmap.mobile.features.home.presentation.viewmodel

import com.rmap.mobile.core.ui.components.RoadmapCardUiModel

data class HomeUiState(
    val userName: String = "",
    val progressFraction: Float = 0f,
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val streakDays: Int = 0,
    val todayGoalCompleted: Int = 0,
    val todayGoalTotal: Int = 0,
    val completedRoadmaps: Int = 0,
    val trendingRoadmaps: List<RoadmapCardUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
