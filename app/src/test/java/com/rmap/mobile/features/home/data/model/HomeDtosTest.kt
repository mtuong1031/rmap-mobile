package com.rmap.mobile.features.home.data.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeDtosTest {
    @Test
    fun `dashboard response accepts fractional estimated hours and nullable goal`() {
        val response = Gson().fromJson(
            """
            {
              "activeRoadmaps": [
                {
                  "roadmapId": "roadmap-1",
                  "title": "Backend Roadmap",
                  "goalName": null,
                  "isTemplate": false,
                  "roleCategory": "WEB_DEVELOPMENT",
                  "startedAt": "2026-06-01T03:22:18.055Z",
                  "currentGroup": null,
                  "planNode": {
                    "id": "node-1",
                    "name": "HTTP",
                    "description": null,
                    "nodeType": "REQUIRED",
                    "estimatedHours": 1.5
                  },
                  "chapter": {"current": 1, "total": 14, "label": "Chapter 1/14"},
                  "progress": {
                    "requiredNodesCompleted": 1,
                    "requiredNodesTotal": 49,
                    "requiredCompletionPct": 2.0
                  },
                  "nextUnlock": null,
                  "paceWarning": null
                }
              ],
              "metrics": {"roadmapCompletionPct": 2.0, "streakDays": 1, "readinessPct": 2.0}
            }
            """.trimIndent(),
            HomeDashboardResponseDto::class.java
        )

        val roadmap = response.activeRoadmaps.single()
        assertNull(roadmap.goalName)
        assertEquals(1.5, roadmap.planNode?.estimatedHours ?: 0.0, 0.0)
    }
}
