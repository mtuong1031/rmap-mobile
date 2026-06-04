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
              "userProfile": {
                "id": "user-1",
                "email": "learner@example.com",
                "fullName": "Thinh Duy",
                "role": "frontend_developer",
                "createdAt": "2026-05-28T00:00:00.000Z"
              },
              "roadmaps": [
                {
                  "roadmapId": "roadmap-1",
                  "deadlineDate": "2026-08-31",
                  "description": "Frontend path",
                  "estimatedWeeks": 16,
                  "goalName": "Frontend Developer",
                  "isTemplate": false,
                  "roleCategory": "WEB_DEVELOPMENT",
                  "startedAt": "2026-05-28T00:00:00.000Z",
                  "title": "Frontend Fresher",
                  "completionPct": 75.4,
                  "streakDays": 5,
                  "skillReadinessPct": 32,
                  "nodesTotal": 24,
                  "nodesCompleted": 18,
                  "timelineWarning": null
                },
                {
                  "roadmapId": "roadmap-2",
                  "deadlineDate": null,
                  "description": "Mobile path",
                  "estimatedWeeks": 8,
                  "goalName": "Mobile Developer",
                  "isTemplate": true,
                  "roleCategory": "MOBILE_DEVELOPMENT",
                  "startedAt": null,
                  "title": "Mobile Starter",
                  "completionPct": 150,
                  "streakDays": 0,
                  "skillReadinessPct": 0,
                  "nodesTotal": 12,
                  "nodesCompleted": 0,
                  "timelineWarning": null
                }
              ],
              "streakDays": 5,
              "activityRecent": [
                {
                  "activityDate": "2026-05-29",
                  "nodesCompleted": 2
                }
              ],
              "summary": {
                "totalRoadmaps": 2,
                "activeRoadmaps": 1,
                "completedRoadmaps": 0,
                "totalSkills": 36,
                "completedSkills": 18,
                "inProgressSkills": 2,
                "lockedSkills": 16,
                "currentStreak": 5
              },
              "skillCategories": [
                {
                  "category": "WEB_DEVELOPMENT",
                  "label": "Web Development",
                  "totalSkills": 24
                }
              ],
              "roadmapStatus": {
                "behindPace": 0,
                "onTrack": 1,
                "completed": 0,
                "notStarted": 1
              }
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
        assertEquals(2, profile.roadmaps.size)
        assertEquals(listOf("roadmap-1"), profile.activeRoadmaps.map { it.id })
        assertEquals("Frontend Fresher", profile.activeRoadmaps.first().title)
        assertEquals(75, profile.activeRoadmaps.first().completionPercent)
        assertEquals(100, profile.roadmaps.last().completionPercent)
        assertEquals(1, profile.recentActivity.size)
        assertEquals("2026-05-29", profile.recentActivity.first().activityDate)
        assertEquals(2, profile.recentActivity.first().nodesCompleted)
    }

    @Test
    fun `dashboard response falls back to email name and learner role`() {
        val dto = gson.fromJson(
            """
            {
              "userProfile": {
                "id": "user-1",
                "email": "learner@example.com",
                "fullName": "",
                "role": "",
                "createdAt": "2026-05-28T00:00:00.000Z"
              },
              "roadmaps": [],
              "streakDays": 0,
              "activityRecent": [],
              "summary": {
                "totalRoadmaps": 0,
                "activeRoadmaps": 0,
                "completedRoadmaps": 0,
                "totalSkills": 0,
                "completedSkills": 0,
                "inProgressSkills": 0,
                "lockedSkills": 0,
                "currentStreak": 0
              },
              "skillCategories": [],
              "roadmapStatus": {
                "behindPace": 0,
                "onTrack": 0,
                "completed": 0,
                "notStarted": 0
              }
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
