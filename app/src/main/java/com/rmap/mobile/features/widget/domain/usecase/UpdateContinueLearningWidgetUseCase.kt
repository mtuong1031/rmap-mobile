package com.rmap.mobile.features.widget.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
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
    val activeRoadmap = activeRoadmaps.firstOrNull()
        ?: return ContinueLearningWidgetSnapshot(
            state = ContinueLearningWidgetState.Empty,
            streakDays = metrics.streakDays.coerceAtLeast(0)
        )

    return activeRoadmap.toWidgetSnapshot(metrics.streakDays)
}

private fun HomeActiveRoadmap.toWidgetSnapshot(streakDays: Int): ContinueLearningWidgetSnapshot {
    return ContinueLearningWidgetSnapshot(
        state = ContinueLearningWidgetState.Active,
        roadmapId = roadmapId,
        roadmapTitle = title,
        currentSkillTitle = planNode?.name ?: currentGroup?.name,
        chapterLabel = chapter?.label,
        progressPercent = progress.requiredCompletionPct.roundToInt().coerceIn(0, 100),
        completedNodes = progress.requiredNodesCompleted.coerceAtLeast(0),
        totalNodes = progress.requiredNodesTotal.coerceAtLeast(0),
        streakDays = streakDays.coerceAtLeast(0),
        isBehind = paceWarning?.isBehind == true
    )
}
