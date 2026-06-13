package com.rmap.mobile.features.widget.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.model.WidgetLearningPlan
import com.rmap.mobile.features.widget.domain.repository.ContinueLearningWidgetRepository

class SharedPreferencesContinueLearningWidgetRepository(
    context: Context,
    private val onSnapshotChanged: suspend () -> Unit,
    private val gson: Gson = Gson()
) : ContinueLearningWidgetRepository {
    private val sharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun getSnapshot(): ContinueLearningWidgetSnapshot {
        val json = sharedPreferences.getString(KEY_SNAPSHOT_JSON, null)
        return ContinueLearningWidgetSnapshotCodec.decode(json, gson)
            ?: sharedPreferences.toLegacySnapshot()
    }

    override suspend fun saveSnapshot(
        snapshot: ContinueLearningWidgetSnapshot
    ): Result<Unit> = runCatching {
        if (snapshot == getSnapshot()) return@runCatching

        sharedPreferences.edit()
            .putString(KEY_SNAPSHOT_JSON, ContinueLearningWidgetSnapshotCodec.encode(snapshot, gson))
            .removeLegacySnapshot()
            .apply()

        onSnapshotChanged()
    }

    private fun SharedPreferences.toLegacySnapshot(): ContinueLearningWidgetSnapshot {
        return runCatching {
            val state = getString(KEY_STATE, null)
                ?.let(ContinueLearningWidgetState::valueOf)
                ?: ContinueLearningWidgetState.SignedOut
            val roadmapId = getString(KEY_ROADMAP_ID, null)
            val roadmapTitle = getString(KEY_ROADMAP_TITLE, null)
            val progress = getInt(KEY_PROGRESS_PERCENT, 0).coerceIn(0, 100)
            val legacyPlan = if (
                state == ContinueLearningWidgetState.Active &&
                !roadmapId.isNullOrBlank() &&
                !roadmapTitle.isNullOrBlank()
            ) {
                WidgetLearningPlan(
                    roadmapId = roadmapId,
                    title = roadmapTitle,
                    currentSkillTitle = getString(KEY_CURRENT_SKILL_TITLE, null),
                    chapterLabel = getString(KEY_CHAPTER_LABEL, null),
                    progressPercent = progress,
                    completedNodes = getInt(KEY_COMPLETED_NODES, 0).coerceAtLeast(0),
                    totalNodes = getInt(KEY_TOTAL_NODES, 0).coerceAtLeast(0),
                    isBehind = getBoolean(KEY_IS_BEHIND, false)
                )
            } else {
                null
            }

            ContinueLearningWidgetSnapshot(
                state = when {
                    legacyPlan != null -> ContinueLearningWidgetState.Active
                    state == ContinueLearningWidgetState.Empty -> ContinueLearningWidgetState.Empty
                    else -> ContinueLearningWidgetState.SignedOut
                },
                learningPlans = listOfNotNull(legacyPlan),
                totalActiveRoadmaps = if (legacyPlan == null) 0 else 1,
                roadmapCompletionPercent = progress,
                streakDays = getInt(KEY_STREAK_DAYS, 0).coerceAtLeast(0)
            )
        }.getOrDefault(ContinueLearningWidgetSnapshot.SignedOut)
    }

    private fun SharedPreferences.Editor.removeLegacySnapshot(): SharedPreferences.Editor {
        return remove(KEY_STATE)
            .remove(KEY_ROADMAP_ID)
            .remove(KEY_ROADMAP_TITLE)
            .remove(KEY_CURRENT_SKILL_TITLE)
            .remove(KEY_CHAPTER_LABEL)
            .remove(KEY_PROGRESS_PERCENT)
            .remove(KEY_COMPLETED_NODES)
            .remove(KEY_TOTAL_NODES)
            .remove(KEY_STREAK_DAYS)
            .remove(KEY_IS_BEHIND)
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_continue_learning_widget"
        const val KEY_SNAPSHOT_JSON = "snapshot_json"
        const val KEY_STATE = "state"
        const val KEY_ROADMAP_ID = "roadmap_id"
        const val KEY_ROADMAP_TITLE = "roadmap_title"
        const val KEY_CURRENT_SKILL_TITLE = "current_skill_title"
        const val KEY_CHAPTER_LABEL = "chapter_label"
        const val KEY_PROGRESS_PERCENT = "progress_percent"
        const val KEY_COMPLETED_NODES = "completed_nodes"
        const val KEY_TOTAL_NODES = "total_nodes"
        const val KEY_STREAK_DAYS = "streak_days"
        const val KEY_IS_BEHIND = "is_behind"
    }
}

internal object ContinueLearningWidgetSnapshotCodec {
    fun encode(
        snapshot: ContinueLearningWidgetSnapshot,
        gson: Gson = Gson()
    ): String = gson.toJson(
        PersistedContinueLearningWidgetSnapshot(
            version = SNAPSHOT_VERSION,
            snapshot = snapshot
        )
    )

    fun decode(
        json: String?,
        gson: Gson = Gson()
    ): ContinueLearningWidgetSnapshot? {
        if (json.isNullOrBlank()) return null
        return runCatching {
            val persisted = gson.fromJson(
                json,
                PersistedContinueLearningWidgetSnapshot::class.java
            )
            persisted
                .takeIf { it.version == SNAPSHOT_VERSION }
                ?.snapshot
                ?.normalized()
        }.getOrNull()
    }
}

private fun ContinueLearningWidgetSnapshot.normalized(): ContinueLearningWidgetSnapshot {
    val plans = learningPlans.orEmpty().map { plan ->
        plan.copy(
            progressPercent = plan.progressPercent.coerceIn(0, 100),
            completedNodes = plan.completedNodes.coerceAtLeast(0),
            totalNodes = plan.totalNodes.coerceAtLeast(0)
        )
    }
    return copy(
        state = when {
            state == ContinueLearningWidgetState.SignedOut -> ContinueLearningWidgetState.SignedOut
            plans.isEmpty() -> ContinueLearningWidgetState.Empty
            else -> ContinueLearningWidgetState.Active
        },
        learningPlans = plans,
        totalActiveRoadmaps = totalActiveRoadmaps.coerceAtLeast(plans.size),
        roadmapCompletionPercent = roadmapCompletionPercent.coerceIn(0, 100),
        streakDays = streakDays.coerceAtLeast(0),
        readinessPercent = readinessPercent.coerceIn(0, 100)
    )
}

private data class PersistedContinueLearningWidgetSnapshot(
    val version: Int,
    val snapshot: ContinueLearningWidgetSnapshot
)

private const val SNAPSHOT_VERSION = 2
