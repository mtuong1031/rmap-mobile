package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import com.rmap.mobile.R
import com.rmap.mobile.features.profile.domain.model.LearningReminderContext

class LearningNotificationContentFactory(
    private val context: Context
) {
    fun reminderTitle(reminderContext: LearningReminderContext): String {
        return if (reminderContext.hasActiveRoadmap) {
            context.resources.getStringArray(R.array.notification_learning_titles).pick()
        } else {
            context.resources.getStringArray(R.array.notification_no_roadmap_titles).pick()
        }
    }

    fun reminderBody(
        streakDays: Int,
        reminderContext: LearningReminderContext
    ): String {
        if (!reminderContext.hasActiveRoadmap) {
            return context.resources.getStringArray(R.array.notification_no_roadmap_bodies).pick()
        }

        val roadmapTitle = reminderContext.activeRoadmapTitle
        return if (streakDays > 0) {
            context.getString(R.string.notification_learning_body_with_streak, streakDays)
        } else if (roadmapTitle != null) {
            context.getString(R.string.notification_learning_body_with_roadmap, roadmapTitle)
        } else {
            context.resources.getStringArray(R.array.notification_learning_bodies).pick()
        }
    }

    fun streakCelebrationTitle(result: StreakCheckInResult): String {
        return if (result.wasAlreadyCheckedInToday) {
            context.getString(R.string.notification_streak_already_marked_title)
        } else {
            context.resources.getStringArray(R.array.notification_streak_titles).pick()
        }
    }

    fun streakCelebrationBody(result: StreakCheckInResult): String {
        return if (result.wasAlreadyCheckedInToday) {
            context.getString(R.string.notification_streak_already_marked_body, result.streakDays)
        } else {
            context.getString(R.string.notification_streak_marked_body, result.streakDays)
        }
    }

    private fun Array<String>.pick(): String {
        if (isEmpty()) return ""
        val index = (System.currentTimeMillis() % size).toInt()
        return this[index]
    }
}
