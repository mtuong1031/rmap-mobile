package com.rmap.mobile.features.widget.domain.model

enum class ContinueLearningWidgetState {
    SignedOut,
    Empty,
    Active
}

data class WidgetLearningPlan(
    val roadmapId: String,
    val title: String,
    val currentSkillTitle: String? = null,
    val chapterLabel: String? = null,
    val estimatedHours: Double? = null,
    val nextUnlockTitle: String? = null,
    val progressPercent: Int = 0,
    val completedNodes: Int = 0,
    val totalNodes: Int = 0,
    val isBehind: Boolean = false
)

data class ContinueLearningWidgetSnapshot(
    val state: ContinueLearningWidgetState,
    val learningPlans: List<WidgetLearningPlan> = emptyList(),
    val totalActiveRoadmaps: Int = 0,
    val roadmapCompletionPercent: Int = 0,
    val streakDays: Int = 0,
    val readinessPercent: Int = 0
) {
    companion object {
        val SignedOut = ContinueLearningWidgetSnapshot(
            state = ContinueLearningWidgetState.SignedOut
        )
    }
}
