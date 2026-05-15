package com.rmap.mobile.features.roadmap.presentation.detail

data class RoadmapDetailUiState(
    val title: String = "Frontend Pro",
    val progressFraction: Float = 0.75f,
    val completedLessons: Int = 6,
    val totalLessons: Int = 8,
)
