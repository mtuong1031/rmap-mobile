package com.rmap.mobile.features.airoadmap.presentation.viewmodel

sealed class AiRoadmapEvent {
    data class NavigateToRoadmapDetail(val roadmapId: String) : AiRoadmapEvent()
}
