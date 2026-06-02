package com.rmap.mobile.features.profile.data.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.profile.data.model.DashboardResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileMapperTest {
    private val gson = Gson()

    @Test
    fun `dashboard response maps backend profile summary to domain profile`() {
        val dto = gson.fromJson(
            """
            {
              "user": {
                "id": "user-1",
                "email": "learner@example.com",
                "fullName": "Thinh Duy",
                "role": "frontend_developer",
                "createdAt": "2026-05-28T00:00:00.000Z"
              },
              "activeRoadmap": {
                "roadmapId": "roadmap-1",
                "completionPct": 75,
                "streakDays": 5,
                "skillReadinessPct": 32,
                "nodesTotal": 24,
                "nodesCompleted": 18,
                "timelineWarning": null
              },
              "streakDays": 5,
              "activityRecent": [
                {
                  "activityDate": "2026-05-29",
                  "nodesCompleted": 2
                }
              ]
            }
            """.trimIndent(),
            DashboardResponseDto::class.java
        )

        val profile = dto.toDomain()

        assertEquals("Thinh", profile.userName)
        assertEquals("Thinh Duy", profile.name)
        assertEquals("Frontend Developer", profile.role)
        assertEquals("", profile.avatarUrl)
        assertEquals(0, profile.xp)
        assertEquals(5, profile.streakDays)
        assertEquals(0, profile.certificates)
    }

    @Test
    fun `dashboard response falls back to email name and learner role`() {
        val dto = gson.fromJson(
            """
            {
              "user": {
                "id": "user-1",
                "email": "learner@example.com",
                "fullName": "",
                "role": "",
                "createdAt": "2026-05-28T00:00:00.000Z"
              },
              "activeRoadmap": null,
              "streakDays": 0,
              "activityRecent": []
            }
            """.trimIndent(),
            DashboardResponseDto::class.java
        )

        val profile = dto.toDomain()

        assertEquals("learner", profile.userName)
        assertEquals("learner", profile.name)
        assertEquals("Learner", profile.role)
    }
}
