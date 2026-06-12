package com.rmap.mobile.features.profile.domain.model

data class LearningReminderContext(
    val hasActiveRoadmap: Boolean = false,
    val activeRoadmapTitle: String? = null
)
