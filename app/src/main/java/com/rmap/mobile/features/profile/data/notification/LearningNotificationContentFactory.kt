package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import com.rmap.mobile.R

class LearningNotificationContentFactory(
    private val context: Context
) {
    fun reminderTitle(): String {
        return context.resources.getStringArray(R.array.notification_learning_titles).pick()
    }

    fun reminderBody(streakDays: Int): String {
        return if (streakDays > 0) {
            context.getString(R.string.notification_learning_body_with_streak, streakDays)
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
