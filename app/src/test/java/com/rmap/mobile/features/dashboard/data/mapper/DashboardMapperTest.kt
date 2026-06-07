package com.rmap.mobile.features.dashboard.data.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.dashboard.data.model.DashboardResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DashboardMapperTest {
    private val gson = Gson()

    @Test
    fun `dashboard response maps roadmap summary and achievements data`() {
        val dto = gson.fromJson(
            """
            {
              "userProfile": {
                "id": "9cc5c757-f7cf-4c1d-bd68-3c73061c436a",
                "email": "thinh@gmail.com",
                "fullName": "Thinh Hoang Duy",
                "role": "user",
                "createdAt": "2026-05-31T08:11:01.885Z"
              },
              "roadmaps": [
                {
                  "roadmapId": "a64e2a27-e1ca-4b34-826b-24c0a605f8eb",
                  "deadlineDate": "2026-07-31",
                  "description": "Backend path",
                  "estimatedWeeks": 18,
                  "goalName": "Backend Intern",
                  "isTemplate": false,
                  "roleCategory": "WEB_DEVELOPMENT",
                  "startedAt": "2026-06-01T03:22:18.055Z",
                  "title": "Backend Intern (Node.js) Roadmap",
                  "completionPct": 10,
                  "streakDays": 5,
                  "skillReadinessPct": 12.2,
                  "nodesTotal": 70,
                  "nodesCompleted": 7,
                  "timelineWarning": null
                },
                {
                  "roadmapId": "behind-roadmap",
                  "deadlineDate": null,
                  "description": "Design path",
                  "estimatedWeeks": null,
                  "goalName": "UX Design Roadmap",
                  "isTemplate": true,
                  "roleCategory": "DESIGN",
                  "startedAt": "2026-05-31T15:29:36.586Z",
                  "title": "UX Design Roadmap",
                  "completionPct": 150,
                  "streakDays": 5,
                  "skillReadinessPct": 101,
                  "nodesTotal": 113,
                  "nodesCompleted": 113,
                  "timelineWarning": {
                    "isBehind": true,
                    "paceDeficitPct": 12.5,
                    "estimatedDelayDays": 3,
                    "message": "Behind pace"
                  }
                }
              ],
              "streakDays": 5,
              "activityRecent": [
                {
                  "activityDate": "2026-06-04",
                  "nodesCompleted": 3
                }
              ],
              "summary": {
                "totalRoadmaps": 9,
                "activeRoadmaps": 9,
                "completedRoadmaps": 0,
                "totalSkills": 925,
                "completedSkills": 21,
                "inProgressSkills": 34,
                "lockedSkills": 870,
                "currentStreak": 5
              },
              "skillCategories": [
                {
                  "category": "WEB_DEVELOPMENT",
                  "label": "Web Development",
                  "totalSkills": 432
                },
                {
                  "category": "DESIGN",
                  "label": "Design",
                  "totalSkills": 187
                }
              ],
              "roadmapStatus": {
                "behindPace": 1,
                "onTrack": 8,
                "completed": 0,
                "notStarted": 0
              }
            }
            """.trimIndent(),
            DashboardResponseDto::class.java
        )

        val dashboard = dto.toDomain()

        assertEquals("Thinh Hoang Duy", dashboard.userProfile.fullName)
        assertEquals(2, dashboard.roadmaps.size)
        assertEquals("Backend Intern (Node.js) Roadmap", dashboard.roadmaps.first().title)
        assertEquals(10.0, dashboard.roadmaps.first().completionPct, 0.0)
        assertEquals(12.2, dashboard.roadmaps.first().skillReadinessPct, 0.0)
        assertNull(dashboard.roadmaps.first().timelineWarning)
        assertEquals(100.0, dashboard.roadmaps.last().completionPct, 0.0)
        assertEquals(100.0, dashboard.roadmaps.last().skillReadinessPct, 0.0)
        assertEquals(true, dashboard.roadmaps.last().timelineWarning?.isBehind)
        assertEquals(21, dashboard.summary.completedSkills)
        assertEquals(432, dashboard.skillCategories.first().totalSkills)
        assertEquals(1, dashboard.roadmapStatus.behindPace)
        assertEquals("2026-06-04", dashboard.activityRecent.first().activityDate)
    }
}
