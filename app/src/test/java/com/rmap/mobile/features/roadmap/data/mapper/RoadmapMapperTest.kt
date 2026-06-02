package com.rmap.mobile.features.roadmap.data.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.roadmap.data.model.NodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodesListResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapProgressSummaryDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapResponseDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizResponseDto
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import org.junit.Assert.assertEquals
import org.junit.Test

class RoadmapMapperTest {
    private val gson = Gson()

    @Test
    fun `roadmap response maps backend fields to summary`() {
        val roadmap = gson.fromJson(
            """
            {
              "deadlineDate": "2026-07-01",
              "description": "Personalized path",
              "estimatedWeeks": 8,
              "generatedAt": "2026-05-29T00:00:00.000Z",
              "goalName": "Frontend Developer",
              "hoursPerDay": 2,
              "id": "roadmap-1",
              "isTemplate": false,
              "roleCategory": "WEB_DEVELOPMENT",
              "title": "Frontend Developer Roadmap",
              "updatedAt": "2026-05-29T00:00:00.000Z",
              "userId": "user-1"
            }
            """.trimIndent(),
            RoadmapResponseDto::class.java
        )
        val progress = RoadmapProgressSummaryDto(
            roadmapId = "roadmap-1",
            completionPct = 50.0,
            streakDays = 3,
            skillReadinessPct = 40.0,
            nodesTotal = 10,
            nodesCompleted = 5,
            timelineWarning = null
        )

        val summary = roadmap.toSummary(progress)

        assertEquals("roadmap-1", summary.id)
        assertEquals("Frontend Developer Roadmap", summary.title)
        assertEquals(10, summary.totalLessonsCount)
        assertEquals(5, summary.completedLessonsCount)
        assertEquals(LearningDifficulty.Intermediate, summary.difficulty)
        assertEquals("8 weeks", summary.durationLabel)
        assertEquals(LearningTopicIcon.Code, summary.icon)
        assertEquals("web-development", summary.categoryId)
    }

    @Test
    fun `roadmap nodes map to detail sections`() {
        val roadmap = gson.fromJson(
            """
            {
              "deadlineDate": null,
              "description": null,
              "estimatedWeeks": 4,
              "generatedAt": "2026-05-29T00:00:00.000Z",
              "goalName": null,
              "hoursPerDay": null,
              "id": "roadmap-1",
              "isTemplate": false,
              "roleCategory": "WEB_DEVELOPMENT",
              "title": "Frontend Developer Roadmap",
              "updatedAt": "2026-05-29T00:00:00.000Z",
              "userId": "user-1"
            }
            """.trimIndent(),
            RoadmapResponseDto::class.java
        )
        val nodes = gson.fromJson(
            """
            {
              "data": [
                {
                  "id": "group-1",
                  "roadmapId": "roadmap-1",
                  "parentId": null,
                  "skillId": null,
                  "name": "Core Web",
                  "description": null,
                  "nodeType": "GROUP",
                  "estimatedHours": null,
                  "posX": 0,
                  "posY": 0,
                  "progress": null
                },
                {
                  "id": "node-1",
                  "roadmapId": "roadmap-1",
                  "parentId": "group-1",
                  "skillId": "skill-1",
                  "name": "HTML",
                  "description": null,
                  "nodeType": "REQUIRED",
                  "estimatedHours": 5,
                  "posX": 0,
                  "posY": 1,
                  "progress": {
                    "id": "progress-1",
                    "roadmapNodeId": "node-1",
                    "status": "COMPLETED",
                    "startedAt": null,
                    "completedAt": "2026-05-29T00:00:00.000Z",
                    "quizScorePct": null,
                    "quizPassed": null
                  }
                },
                {
                  "id": "node-2",
                  "roadmapId": "roadmap-1",
                  "parentId": "group-1",
                  "skillId": "skill-2",
                  "name": "CSS",
                  "description": null,
                  "nodeType": "REQUIRED",
                  "estimatedHours": 5,
                  "posX": 0,
                  "posY": 2,
                  "progress": {
                    "id": "progress-2",
                    "roadmapNodeId": "node-2",
                    "status": "IN_PROGRESS",
                    "startedAt": "2026-05-29T00:00:00.000Z",
                    "completedAt": null,
                    "quizScorePct": null,
                    "quizPassed": null
                  }
                },
                {
                  "id": "milestone-1",
                  "roadmapId": "roadmap-1",
                  "parentId": null,
                  "skillId": null,
                  "name": "Static Web Page",
                  "description": "Build a responsive static website.",
                  "nodeType": "MILESTONE",
                  "estimatedHours": null,
                  "posX": 0,
                  "posY": 3,
                  "progress": {
                    "id": "progress-3",
                    "roadmapNodeId": "milestone-1",
                    "status": "LOCKED",
                    "startedAt": null,
                    "completedAt": null,
                    "quizScorePct": null,
                    "quizPassed": null
                  }
                }
              ]
            }
            """.trimIndent(),
            RoadmapNodesListResponseDto::class.java
        )

        val detail = roadmap.toDetail(nodes.nodes)

        assertEquals("roadmap-1", detail.id)
        assertEquals("Web Development", detail.categoryLabel)
        assertEquals(1, detail.completedLessons)
        assertEquals(2, detail.totalLessons)
        assertEquals("Core Web", detail.sections.first().title)
        assertEquals("node-1", detail.sections.first().modules.first().id)
        assertEquals("HTML", detail.sections.first().modules.first().title)
        assertEquals(LearningStatus.Completed, detail.sections.first().modules.first().status)
        assertEquals("milestone-1", detail.milestones.single().id)
        assertEquals("Static Web Page", detail.milestones.single().title)
        assertEquals("Build a responsive static website.", detail.milestones.single().description)
        assertEquals(LearningStatus.Locked, detail.milestones.single().status)
        assertEquals("CSS", detail.aiTip?.currentModule)
    }

    @Test
    fun `node detail response maps backend fields to learning node detail`() {
        val response = gson.fromJson(
            """
            {
              "node": {
                "id": "node-1",
                "roadmapId": "roadmap-1",
                "parentId": "group-1",
                "skillId": "skill-1",
                "name": "Domain Name",
                "description": "Learn how a domain points to a server.",
                "nodeType": "REQUIRED",
                "estimatedHours": 2,
                "posX": 0,
                "posY": 1,
                "progress": {
                  "id": "progress-1",
                  "roadmapNodeId": "node-1",
                  "status": "IN_PROGRESS",
                  "startedAt": "2026-05-29T00:00:00.000Z",
                  "completedAt": null,
                  "quizScorePct": null,
                  "quizPassed": null
                }
              },
              "skill": {
                "id": "skill-1",
                "name": "Domain Name",
                "description": "DNS and domain basics.",
                "defaultEstimatedHours": 3,
                "roleCategory": "WEB_DEVELOPMENT"
              },
              "resources": [
                {
                  "id": 1,
                  "title": "MDN domain guide",
                  "url": "https://developer.mozilla.org/",
                  "resourceType": "ARTICLE",
                  "isFree": true,
                  "isPrimary": true
                }
              ],
              "prerequisites": [
                {
                  "skillId": "skill-html",
                  "skillName": "HTML"
                }
              ],
              "latestSubmission": null
            }
            """.trimIndent(),
            NodeDetailResponseDto::class.java
        )

        val detail = response.toDomain()

        assertEquals("roadmap-1", detail.roadmapId)
        assertEquals("node-1", detail.nodeId)
        assertEquals("Domain Name", detail.title)
        assertEquals("Learn how a domain points to a server.", detail.description)
        assertEquals(2, detail.estimatedHours)
        assertEquals(LearningStatus.InProgress, detail.status)
        assertEquals(LearningRequirement.Required, detail.requirement)
        assertEquals("MDN domain guide", detail.resources.single().title)
        assertEquals("HTML", detail.prerequisites.single().skillName)
    }

    @Test
    fun `quiz response and submit response map to quiz domain models`() {
        val quizResponse = gson.fromJson(
            """
            {
              "nodeId": "node-1",
              "skillId": "skill-1",
              "questions": [
                {
                  "id": "question-1",
                  "questionText": "What does DNS resolve?",
                  "optionA": "Domain names",
                  "optionB": "CSS rules",
                  "optionC": "Gradle tasks",
                  "optionD": "Image assets"
                }
              ]
            }
            """.trimIndent(),
            RoadmapNodeQuizResponseDto::class.java
        )
        val submitResponse = gson.fromJson(
            """
            {
              "scorePct": 80,
              "passed": true,
              "correctCount": 4,
              "totalQuestions": 5,
              "results": [
                {
                  "questionId": "question-1",
                  "selectedOption": "a",
                  "correctOption": "a",
                  "isCorrect": true
                }
              ],
              "nodeProgress": {
                "id": "progress-1",
                "roadmapNodeId": "node-1",
                "status": "COMPLETED",
                "startedAt": "2026-05-29T00:00:00.000Z",
                "completedAt": "2026-05-29T00:05:00.000Z",
                "quizScorePct": 80,
                "quizPassed": true
              },
              "unlockedNodes": ["node-2"],
              "suggestion": null
            }
            """.trimIndent(),
            SubmitQuizResponseDto::class.java
        )

        val quiz = quizResponse.toDomain()
        val request = listOf(
            NodeQuizAnswer(
                questionId = "question-1",
                selectedOption = "a"
            )
        ).toSubmitQuizRequestDto()
        val result = submitResponse.toDomain()

        assertEquals("node-1", quiz.nodeId)
        assertEquals("What does DNS resolve?", quiz.questions.single().text)
        assertEquals("A", quiz.questions.single().options.first().key)
        assertEquals("A", request.answers.single().selectedOption)
        assertEquals(80, result.scorePercent)
        assertEquals(true, result.passed)
        assertEquals("A", result.questionResults.single().selectedOption)
        assertEquals("node-2", result.unlockedNodeIds.single())
    }
}
