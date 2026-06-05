package com.rmap.mobile.features.roadmap.data.mapper

import com.google.gson.Gson
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionEnvelopeDto
import com.rmap.mobile.features.roadmap.data.remote.model.NodeProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapWithNodesDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapsResponseDto
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionStatus
import com.rmap.mobile.features.roadmap.domain.model.RoadmapContentItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoadmapMapperTest {
    @Test
    fun `toDomain maps roadmap tree and progress statuses`() {
        val detail = roadmapWithNodes().toDomain(roadmapProgress())

        assertEquals("roadmap-1", detail.id)
        assertEquals("Frontend Developer Roadmap", detail.title)
        assertEquals("Frontend Developer", detail.roleName)
        assertEquals(2, detail.completedLessons)
        assertEquals(5, detail.totalLessons)
        assertEquals(2, detail.sections.size)

        val firstSection = detail.sections.first()
        assertEquals("Frontend Foundations", firstSection.title)
        assertEquals(3, firstSection.modules.size)

        val foundations = firstSection.modules.first()
        assertEquals("node-root", foundations.id)
        assertEquals("Frontend Foundations", foundations.title)
        assertEquals(LearningStatus.Completed, foundations.status)

        val css = firstSection.modules[1]
        assertEquals("node-css", css.id)
        assertEquals("CSS Flexbox", css.title)
        assertEquals(LearningRequirement.Required, css.requirement)
        assertEquals(LearningStatus.Completed, css.status)
        assertEquals(3, css.resourcesCount)

        val react = firstSection.modules[2]
        assertEquals("node-react", react.id)
        assertEquals(LearningRequirement.Optional, react.requirement)
        assertEquals(LearningStatus.InProgress, react.status)
        assertEquals(1, react.subLessons.size)
        assertEquals("node-state", react.subLessons.first().id)
        assertEquals(LearningStatus.Locked, react.subLessons.first().status)
    }

    @Test
    fun `toDomain handles root nodes without children`() {
        val detail = roadmapWithNodes().toDomain(roadmapProgress())

        val secondSection = detail.sections[1]

        assertEquals("API Basics", secondSection.title)
        assertEquals(1, secondSection.modules.size)
        assertEquals("node-api", secondSection.modules.first().id)
        assertTrue(secondSection.modules.first().subLessons.isEmpty())
    }

    @Test
    fun `dto supports backend camelCase roadmap list response`() {
        val response = Gson().fromJson(
            """
            {
              "data": [
                {
                  "id": "roadmap-1",
                  "userId": null,
                  "roleId": "role-frontend",
                  "roleName": "Frontend Developer",
                  "title": "Frontend Developer Roadmap",
                  "description": "Personalized roadmap",
                  "isTemplate": true,
                  "createdAt": "2026-05-30T00:00:00Z"
                }
              ],
              "meta": {
                "page": 1,
                "perPage": 5,
                "total": 1,
                "totalPages": 1
              }
            }
            """.trimIndent(),
            RoadmapsResponseDto::class.java
        )

        val roadmap = response.data.orEmpty().single()
        assertEquals("role-frontend", roadmap.roleId)
        assertEquals("Frontend Developer", roadmap.roleName)
        assertEquals(null, roadmap.roleCategory)
        assertEquals(true, roadmap.isTemplate)
        assertEquals(5, response.meta?.perPage)
        assertEquals(1, response.meta?.totalPages)
    }

    @Test
    fun `toDomain maps backend camelCase detail and progress response`() {
        val gson = Gson()
        val roadmap = gson.fromJson(
            """
            {
              "id": "roadmap-1",
              "roleId": "role-frontend",
              "roleName": "Frontend Developer",
              "title": "Frontend Developer Roadmap",
              "nodes": [
                {
                  "id": "node-root",
                  "roadmapId": "roadmap-1",
                  "skillId": "skill-root",
                  "skillName": "Frontend Foundations",
                  "relationType": "REQUIRED",
                  "sortOrder": 1,
                  "children": [
                    {
                      "id": "node-css",
                      "roadmapId": "roadmap-1",
                      "skillId": "skill-css",
                      "skillName": "CSS Flexbox",
                      "skillEstimatedHours": 6,
                      "parentNodeId": "node-root",
                      "relationType": "OPTIONAL",
                      "sortOrder": 1,
                      "children": []
                    }
                  ]
                }
              ]
            }
            """.trimIndent(),
            RoadmapWithNodesDto::class.java
        )
        val progress = gson.fromJson(
            """
            {
              "roadmapId": "roadmap-1",
              "totalNodes": 2,
              "completedNodes": 1,
              "inProgressNodes": 1,
              "completionPercentage": 50.0,
              "nodes": [
                {
                  "roadmapNodeId": "node-root",
                  "skillId": "skill-root",
                  "skillName": "Frontend Foundations",
                  "status": "COMPLETED"
                },
                {
                  "roadmapNodeId": "node-css",
                  "skillId": "skill-css",
                  "skillName": "CSS Flexbox",
                  "status": "IN_PROGRESS"
                }
              ]
            }
            """.trimIndent(),
            RoadmapProgressDto::class.java
        )

        val detail = roadmap.toDomain(progress)

        assertEquals("roadmap-1", detail.id)
        assertEquals(2, detail.totalLessons)
        assertEquals(1, detail.completedLessons)
        assertEquals(LearningStatus.Completed, detail.sections.first().modules.first().status)
        assertEquals(LearningRequirement.Optional, detail.sections.first().modules[1].requirement)
        assertEquals(LearningStatus.InProgress, detail.sections.first().modules[1].status)
    }

    @Test
    fun `toDomain maps actual backend template detail and flat nodes response`() {
        val gson = Gson()
        val template = gson.fromJson(
            """
            {
              "deadlineDate": null,
              "description": "Learn what backend development is and how to become a backend developer.",
              "estimatedWeeks": null,
              "generatedAt": "2026-05-31T07:06:44.227Z",
              "goalName": "Backend Developer Roadmap: What is Backend Development?",
              "hoursPerDay": null,
              "id": "7f3e209e-3781-4ed3-a263-7d0c9e546c1a",
              "isTemplate": true,
              "roleCategory": "WEB_DEVELOPMENT",
              "title": "Backend Developer Roadmap: What is Backend Development?",
              "updatedAt": "2026-05-31T07:06:44.227Z",
              "userId": null
            }
            """.trimIndent(),
            RoadmapDto::class.java
        )
        val nodes = gson.fromJson(
            """
            {
              "nodes": [
                {
                  "description": null,
                  "estimatedHours": null,
                  "roadmap_node_id": "group-1",
                  "name": "Backend Foundations",
                  "nodeType": "GROUP",
                  "parent_node_id": null,
                  "posX": 0,
                  "posY": 0,
                  "roadmap_id": "7f3e209e-3781-4ed3-a263-7d0c9e546c1a",
                  "skill_id": null
                },
                {
                  "description": "Learn backend API basics.",
                  "estimatedHours": 4,
                  "roadmap_node_id": "node-api",
                  "skill_name": "API Design",
                  "nodeType": "REQUIRED",
                  "parent_node_id": "group-1",
                  "posX": 0,
                  "posY": 100,
                  "roadmap_id": "7f3e209e-3781-4ed3-a263-7d0c9e546c1a",
                  "skill_id": "skill-api"
                }
              ]
            }
            """.trimIndent(),
            RoadmapNodesResponseDto::class.java
        )

        val detail = template
            .toRoadmapWithNodes(nodes.nodes.orEmpty())
            .toDomain(RoadmapProgressDto(roadmapId = "7f3e209e-3781-4ed3-a263-7d0c9e546c1a"))

        assertEquals("7f3e209e-3781-4ed3-a263-7d0c9e546c1a", detail.id)
        assertEquals("Backend Developer Roadmap: What is Backend Development?", detail.title)
        assertEquals("Web Development", detail.roleName)
        assertEquals(2, detail.totalLessons)
        assertEquals("Backend Foundations", detail.sections.single().title)
        assertEquals("API Design", detail.sections.single().modules.single().title)
        assertEquals("skill-api", detail.sections.single().modules.single().skillId)
        assertEquals(LearningStatus.NotStarted, detail.sections.single().modules.single().status)
        assertEquals(4, detail.sections.single().modules.single().estimatedHours)
    }

    @Test
    fun `toDomain locks later groups until previous required nodes are completed`() {
        val roadmap = RoadmapWithNodesDto(
            id = "roadmap-1",
            roleName = "Backend Developer",
            title = "Backend Roadmap",
            nodes = listOf(
                RoadmapNodeDto(
                    id = "group-1",
                    skillName = "Backend Foundations",
                    nodeType = "GROUP",
                    sortOrder = 1
                ),
                RoadmapNodeDto(
                    id = "node-http",
                    skillId = "skill-http",
                    skillName = "HTTP Basics",
                    nodeType = "REQUIRED",
                    parentNodeId = "group-1",
                    sortOrder = 1
                ),
                RoadmapNodeDto(
                    id = "group-2",
                    skillName = "API Development",
                    nodeType = "GROUP",
                    sortOrder = 2
                ),
                RoadmapNodeDto(
                    id = "node-api",
                    skillId = "skill-api",
                    skillName = "REST API",
                    nodeType = "REQUIRED",
                    parentNodeId = "group-2",
                    sortOrder = 1
                )
            )
        )

        val detail = roadmap.toDomain(RoadmapProgressDto(roadmapId = "roadmap-1"))

        assertEquals(LearningStatus.NotStarted, detail.sections[0].modules.single().status)
        assertEquals(LearningStatus.Locked, detail.sections[1].modules.single().status)
    }

    @Test
    fun `toDomain preserves backend order for groups and milestones`() {
        val roadmap = RoadmapWithNodesDto(
            id = "roadmap-1",
            roleName = "Backend Developer",
            title = "Professional Node.js",
            nodes = listOf(
                RoadmapNodeDto(
                    id = "group-internet",
                    skillName = "Internet Fundamentals",
                    nodeType = "GROUP",
                    sortOrder = 1
                ),
                RoadmapNodeDto(
                    id = "group-node",
                    skillName = "Node.js Environment",
                    nodeType = "GROUP",
                    sortOrder = 2
                ),
                RoadmapNodeDto(
                    id = "milestone-api",
                    skillName = "Basic API Server",
                    nodeType = "MILESTONE",
                    sortOrder = 3
                ),
                RoadmapNodeDto(
                    id = "group-data",
                    skillName = "Data Management",
                    nodeType = "GROUP",
                    sortOrder = 4
                ),
                RoadmapNodeDto(
                    id = "group-security",
                    skillName = "Web Security",
                    nodeType = "GROUP",
                    sortOrder = 5
                ),
                RoadmapNodeDto(
                    id = "milestone-secure",
                    skillName = "Secure API Project",
                    nodeType = "MILESTONE",
                    sortOrder = 6
                )
            )
        )

        val detail = roadmap.toDomain(
            RoadmapProgressDto(
                roadmapId = "roadmap-1",
                nodes = listOf(
                    NodeProgressDto(roadmapNodeId = "milestone-api", status = "in_progress"),
                    NodeProgressDto(roadmapNodeId = "milestone-secure", status = "locked")
                )
            )
        )

        val orderedTitles = detail.contentItems.map { item ->
            when (item) {
                is RoadmapContentItem.Group -> item.section.title
                is RoadmapContentItem.Milestone -> item.milestone.title
            }
        }

        assertEquals(
            listOf(
                "Internet Fundamentals",
                "Node.js Environment",
                "Basic API Server",
                "Data Management",
                "Web Security",
                "Secure API Project"
            ),
            orderedTitles
        )
        assertEquals(4, detail.sections.size)
        assertEquals(2, detail.milestones.size)
        assertEquals(LearningStatus.InProgress, detail.milestones.first().status)
        assertEquals(LearningStatus.Locked, detail.milestones.last().status)
    }

    @Test
    fun `toMilestoneDetail maps backend milestone detail response`() {
        val response = Gson().fromJson(
            """
            {
              "node": {
                "id": "milestone-api",
                "roadmapId": "roadmap-1",
                "parentId": null,
                "skillId": null,
                "name": "Basic API Server",
                "description": "Construct a raw Node.js HTTP server.",
                "nodeType": "MILESTONE",
                "estimatedHours": null,
                "posX": 0,
                "posY": 400,
                "resourcesCount": 0,
                "progress": {
                  "id": "progress-1",
                  "roadmapNodeId": "milestone-api",
                  "status": "IN_PROGRESS",
                  "startedAt": "2026-06-01T00:00:00.000Z",
                  "completedAt": null,
                  "quizScorePct": null,
                  "quizPassed": null
                }
              },
              "skill": null,
              "resources": null,
              "prerequisites": [],
              "latestSubmission": {
                "id": "submission-1",
                "repoUrl": "https://github.com/example/rmap-test",
                "testSuiteId": "suite-1",
                "status": "ERROR",
                "outputLog": "[error]\nspawn docker ENOENT",
                "passRatePct": null,
                "passedTests": null,
                "testResults": null,
                "totalTests": null,
                "attemptNumber": 6,
                "createdAt": "2026-06-01T00:00:00.000Z",
                "completedAt": null
              },
              "milestoneTestSuite": {
                "generatedAt": "2026-06-01T00:00:00.000Z",
                "id": "suite-1",
                "passThresholdPct": 80,
                "status": "READY",
                "summary": "Verifies the implementation of a manual HTTP server.",
                "testCases": [
                  {
                    "name": "Starts server",
                    "description": "The project starts an HTTP server."
                  }
                ],
                "title": "Raw Node.js API Server Evaluation"
              }
            }
            """.trimIndent(),
            RoadmapNodeDetailResponseDto::class.java
        )

        val detail = response.toMilestoneDetail()

        assertEquals("roadmap-1", detail.roadmapId)
        assertEquals("milestone-api", detail.nodeId)
        assertEquals("Basic API Server", detail.title)
        assertEquals(LearningStatus.InProgress, detail.status)
        assertEquals("Raw Node.js API Server Evaluation", detail.testSuite?.title)
        assertEquals(80, detail.testSuite?.passThresholdPercent)
        assertEquals(1, detail.testSuite?.testCases?.size)
        assertEquals("https://github.com/example/rmap-test", detail.latestSubmission?.repoUrl)
        assertEquals(MilestoneSubmissionStatus.Error, detail.latestSubmission?.status)
        assertEquals(6, detail.latestSubmission?.attemptNumber)
    }

    @Test
    fun `milestone submission envelope maps running submission`() {
        val response = Gson().fromJson(
            """
            {
              "submission": {
                "id": "submission-2",
                "repoUrl": "https://github.com/example/rmap-test",
                "testSuiteId": "suite-1",
                "status": "RUNNING",
                "outputLog": null,
                "passRatePct": null,
                "passedTests": null,
                "testResults": null,
                "totalTests": null,
                "attemptNumber": 7,
                "createdAt": "2026-06-01T00:00:00.000Z",
                "completedAt": null
              }
            }
            """.trimIndent(),
            MilestoneSubmissionEnvelopeDto::class.java
        )

        val submission = response.toDomain()

        assertEquals("submission-2", submission.id)
        assertEquals(MilestoneSubmissionStatus.Running, submission.status)
        assertEquals(7, submission.attemptNumber)
    }

    private fun roadmapWithNodes(): RoadmapWithNodesDto {
        return RoadmapWithNodesDto(
            id = "roadmap-1",
            roleId = "role-frontend",
            roleName = "Frontend Developer",
            title = "Frontend Developer Roadmap",
            description = "Personalized roadmap",
            isTemplate = false,
            createdAt = "2026-05-30T00:00:00Z",
            nodes = listOf(
                RoadmapNodeDto(
                    id = "node-root",
                    roadmapId = "roadmap-1",
                    skillId = "skill-root",
                    skillName = "Frontend Foundations",
                    relationType = "required",
                    sortOrder = 1,
                    children = listOf(
                        RoadmapNodeDto(
                            id = "node-css",
                            roadmapId = "roadmap-1",
                            skillId = "skill-css",
                            skillName = "CSS Flexbox",
                            skillEstimatedHours = 6,
                            resourcesCount = 3,
                            parentNodeId = "node-root",
                            relationType = "required",
                            sortOrder = 1,
                            children = emptyList()
                        ),
                        RoadmapNodeDto(
                            id = "node-react",
                            roadmapId = "roadmap-1",
                            skillId = "skill-react",
                            skillName = "React",
                            skillEstimatedHours = 12,
                            parentNodeId = "node-root",
                            relationType = "optional",
                            sortOrder = 2,
                            children = listOf(
                                RoadmapNodeDto(
                                    id = "node-state",
                                    roadmapId = "roadmap-1",
                                    skillId = "skill-state",
                                    skillName = "State management",
                                    parentNodeId = "node-react",
                                    relationType = "required",
                                    sortOrder = 1,
                                    children = emptyList()
                                )
                            )
                        )
                    )
                ),
                RoadmapNodeDto(
                    id = "node-api",
                    roadmapId = "roadmap-1",
                    skillId = "skill-api",
                    skillName = "API Basics",
                    relationType = "required",
                    sortOrder = 2,
                    children = emptyList()
                )
            )
        )
    }

    private fun roadmapProgress(): RoadmapProgressDto {
        return RoadmapProgressDto(
            roadmapId = "roadmap-1",
            totalNodes = 5,
            completedNodes = 2,
            inProgressNodes = 1,
            completionPercentage = 40f,
            nodes = listOf(
                NodeProgressDto(
                    roadmapNodeId = "node-root",
                    skillId = "skill-root",
                    skillName = "Frontend Foundations",
                    status = "completed"
                ),
                NodeProgressDto(
                    roadmapNodeId = "node-css",
                    skillId = "skill-css",
                    skillName = "CSS Flexbox",
                    status = "completed"
                ),
                NodeProgressDto(
                    roadmapNodeId = "node-react",
                    skillId = "skill-react",
                    skillName = "React",
                    status = "in_progress"
                ),
                NodeProgressDto(
                    roadmapNodeId = "node-state",
                    skillId = "skill-state",
                    skillName = "State management",
                    status = "locked"
                ),
                NodeProgressDto(
                    roadmapNodeId = "node-api",
                    skillId = "skill-api",
                    skillName = "API Basics",
                    status = "locked"
                )
            )
        )
    }
}
