package com.rmap.mobile.features.profile.data.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.profile.data.model.ProfileActivityResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileActivityMapperTest {
    private val gson = Gson()

    @Test
    fun `activity response maps streak summary to domain`() {
        val dto = gson.fromJson(
            """
            {
              "streakDays": 5,
              "longestStreak": 7,
              "activity": [
                {
                  "activityDate": "2026-06-01",
                  "nodesCompleted": 2
                },
                {
                  "activityDate": "2026-06-02",
                  "nodesCompleted": 0
                }
              ]
            }
            """.trimIndent(),
            ProfileActivityResponseDto::class.java
        )

        val activity = dto.toDomain()

        assertEquals(5, activity.streakDays)
        assertEquals(7, activity.longestStreak)
        assertEquals(2, activity.activity.size)
        assertEquals("2026-06-01", activity.activity.first().activityDate)
        assertEquals(2, activity.activity.first().nodesCompleted)
    }

    @Test
    fun `activity response supports snake case alternates`() {
        val dto = gson.fromJson(
            """
            {
              "streak_days": 3,
              "longest_streak": 8,
              "activity": [
                {
                  "activity_date": "2026-06-03",
                  "nodes_completed": 4
                }
              ]
            }
            """.trimIndent(),
            ProfileActivityResponseDto::class.java
        )

        val activity = dto.toDomain()

        assertEquals(3, activity.streakDays)
        assertEquals(8, activity.longestStreak)
        assertEquals("2026-06-03", activity.activity.first().activityDate)
        assertEquals(4, activity.activity.first().nodesCompleted)
    }
}
