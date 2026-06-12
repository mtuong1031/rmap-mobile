package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import com.rmap.mobile.features.profile.domain.model.LearningReminderContext
import com.rmap.mobile.features.profile.domain.repository.LearningReminderContextRepository

class SharedPreferencesLearningReminderContextRepository(
    context: Context
) : LearningReminderContextRepository {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getContext(): LearningReminderContext {
        return LearningReminderContext(
            hasActiveRoadmap = sharedPreferences.getBoolean(KEY_HAS_ACTIVE_ROADMAP, false),
            activeRoadmapTitle = sharedPreferences.getString(KEY_ACTIVE_ROADMAP_TITLE, null)
                ?.takeIf { it.isNotBlank() }
        )
    }

    override suspend fun setActiveRoadmap(title: String?) {
        val normalizedTitle = title?.trim()?.takeIf { it.isNotBlank() }
        sharedPreferences.edit()
            .putBoolean(KEY_HAS_ACTIVE_ROADMAP, normalizedTitle != null)
            .putString(KEY_ACTIVE_ROADMAP_TITLE, normalizedTitle)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_learning_reminder_context"
        const val KEY_HAS_ACTIVE_ROADMAP = "has_active_roadmap"
        const val KEY_ACTIVE_ROADMAP_TITLE = "active_roadmap_title"
    }
}
