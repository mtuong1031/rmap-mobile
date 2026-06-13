package com.rmap.mobile.features.widget.data.repository

import android.content.Context
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.repository.ContinueLearningWidgetRepository

class SharedPreferencesContinueLearningWidgetRepository(
    context: Context,
    private val onSnapshotChanged: suspend () -> Unit
) : ContinueLearningWidgetRepository {
    private val sharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun getSnapshot(): ContinueLearningWidgetSnapshot {
        return runCatching {
            ContinueLearningWidgetSnapshot(
                state = sharedPreferences.getString(KEY_STATE, null)
                    ?.let(ContinueLearningWidgetState::valueOf)
                    ?: ContinueLearningWidgetState.SignedOut,
                roadmapId = sharedPreferences.getString(KEY_ROADMAP_ID, null),
                roadmapTitle = sharedPreferences.getString(KEY_ROADMAP_TITLE, null),
                currentSkillTitle = sharedPreferences.getString(KEY_CURRENT_SKILL_TITLE, null),
                chapterLabel = sharedPreferences.getString(KEY_CHAPTER_LABEL, null),
                progressPercent = sharedPreferences.getInt(KEY_PROGRESS_PERCENT, 0),
                completedNodes = sharedPreferences.getInt(KEY_COMPLETED_NODES, 0),
                totalNodes = sharedPreferences.getInt(KEY_TOTAL_NODES, 0),
                streakDays = sharedPreferences.getInt(KEY_STREAK_DAYS, 0),
                isBehind = sharedPreferences.getBoolean(KEY_IS_BEHIND, false)
            )
        }.getOrDefault(ContinueLearningWidgetSnapshot.SignedOut)
    }

    override suspend fun saveSnapshot(
        snapshot: ContinueLearningWidgetSnapshot
    ): Result<Unit> = runCatching {
        if (snapshot == getSnapshot()) return@runCatching

        sharedPreferences.edit()
            .putString(KEY_STATE, snapshot.state.name)
            .putString(KEY_ROADMAP_ID, snapshot.roadmapId)
            .putString(KEY_ROADMAP_TITLE, snapshot.roadmapTitle)
            .putString(KEY_CURRENT_SKILL_TITLE, snapshot.currentSkillTitle)
            .putString(KEY_CHAPTER_LABEL, snapshot.chapterLabel)
            .putInt(KEY_PROGRESS_PERCENT, snapshot.progressPercent)
            .putInt(KEY_COMPLETED_NODES, snapshot.completedNodes)
            .putInt(KEY_TOTAL_NODES, snapshot.totalNodes)
            .putInt(KEY_STREAK_DAYS, snapshot.streakDays)
            .putBoolean(KEY_IS_BEHIND, snapshot.isBehind)
            .apply()

        onSnapshotChanged()
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_continue_learning_widget"
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
