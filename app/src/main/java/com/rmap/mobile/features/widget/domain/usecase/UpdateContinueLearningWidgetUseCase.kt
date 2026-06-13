package com.rmap.mobile.features.widget.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.model.WidgetLearningPlan
import com.rmap.mobile.features.widget.domain.repository.ContinueLearningWidgetRepository
import kotlin.math.roundToInt

class UpdateContinueLearningWidgetUseCase(
    private val repository: ContinueLearningWidgetRepository
) {
    suspend operator fun invoke(
        authState: AuthState,
        homeContent: HomeContent?
    ): Result<ContinueLearningWidgetSnapshot> {
        val snapshot = when (authState) {
            AuthState.Checking -> return Result.success(repository.getSnapshot())
            AuthState.Unauthenticated -> ContinueLearningWidgetSnapshot.SignedOut
            is AuthState.Authenticated -> {
                homeContent?.toWidgetSnapshot() ?: return Result.success(repository.getSnapshot())
            }
        }

        return repository.saveSnapshot(snapshot).map { snapshot }
    }
}

internal fun HomeContent.toWidgetSnapshot(): ContinueLearningWidgetSnapshot {
    val plans = activeRoadmaps
        .take(MAX_WIDGET_LEARNING_PLANS)
        .map(HomeActiveRoadmap::toWidgetLearningPlan)

    return ContinueLearningWidgetSnapshot(
        state = if (plans.isEmpty()) {
            ContinueLearningWidgetState.Empty
        } else {
            ContinueLearningWidgetState.Active
        },
        learningPlans = plans,
        totalActiveRoadmaps = activeRoadmaps.size,
        roadmapCompletionPercent = metrics.roadmapCompletionPct.toWidgetPercent(),
        streakDays = metrics.streakDays.coerceAtLeast(0),
        readinessPercent = metrics.readinessPct.toWidgetPercent()
    )
}

private fun HomeActiveRoadmap.toWidgetLearningPlan(): WidgetLearningPlan {
    return WidgetLearningPlan(
        roadmapId = roadmapId,
        title = title,
        currentSkillTitle = planNode?.name ?: currentGroup?.name,
        chapterLabel = chapter?.label,
        estimatedHours = planNode?.estimatedHours?.takeIf { it >= 0.0 },
        nextUnlockTitle = nextUnlock?.name,
        progressPercent = progress.requiredCompletionPct.toWidgetPercent(),
        completedNodes = progress.requiredNodesCompleted.coerceAtLeast(0),
        totalNodes = progress.requiredNodesTotal.coerceAtLeast(0),
        isBehind = paceWarning?.isBehind == true
    )
}

private fun Double.toWidgetPercent(): Int = roundToInt().coerceIn(0, 100)

internal const val MAX_WIDGET_LEARNING_PLANS = 5
