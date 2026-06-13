package com.rmap.mobile.features.widget.domain.model

enum class ContinueLearningWidgetState {
    SignedOut,
    Empty,
    Active
}

data class ContinueLearningWidgetSnapshot(
    val state: ContinueLearningWidgetState,
    val roadmapId: String? = null,
    val roadmapTitle: String? = null,
    val currentSkillTitle: String? = null,
    val chapterLabel: String? = null,
    val progressPercent: Int = 0,
    val completedNodes: Int = 0,
    val totalNodes: Int = 0,
    val streakDays: Int = 0,
    val isBehind: Boolean = false
) {
    companion object {
        val SignedOut = ContinueLearningWidgetSnapshot(
            state = ContinueLearningWidgetState.SignedOut
        )
    }
}
