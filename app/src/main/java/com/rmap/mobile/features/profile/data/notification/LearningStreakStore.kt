package com.rmap.mobile.features.profile.data.notification

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class StreakCheckInResult(
    val streakDays: Int,
    val wasAlreadyCheckedInToday: Boolean
)

class LearningStreakStore(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.US)

    fun currentStreakDays(): Int {
        return sharedPreferences.getInt(KEY_STREAK_DAYS, 0)
    }

    fun markToday(): StreakCheckInResult {
        val today = formatDate(Calendar.getInstance())
        val lastCheckInDate = sharedPreferences.getString(KEY_LAST_CHECK_IN_DATE, null)
        val currentStreak = currentStreakDays()

        if (lastCheckInDate == today) {
            return StreakCheckInResult(
                streakDays = currentStreak.coerceAtLeast(1),
                wasAlreadyCheckedInToday = true
            )
        }

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DATE, -1)
        }
        val nextStreak = if (lastCheckInDate == formatDate(yesterday)) {
            currentStreak + 1
        } else {
            1
        }

        sharedPreferences.edit()
            .putString(KEY_LAST_CHECK_IN_DATE, today)
            .putInt(KEY_STREAK_DAYS, nextStreak)
            .apply()

        return StreakCheckInResult(
            streakDays = nextStreak,
            wasAlreadyCheckedInToday = false
        )
    }

    private fun formatDate(calendar: Calendar): String {
        return dateFormat.format(calendar.time)
    }

    private companion object {
        const val PREFERENCES_NAME = "rmap_learning_streak"
        const val KEY_LAST_CHECK_IN_DATE = "last_check_in_date"
        const val KEY_STREAK_DAYS = "streak_days"
        const val DATE_PATTERN = "yyyy-MM-dd"
    }
}
