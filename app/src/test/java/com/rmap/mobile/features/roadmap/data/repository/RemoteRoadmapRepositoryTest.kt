package com.rmap.mobile.features.roadmap.data.repository

import com.google.gson.JsonParser
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneSubmissionEnvelopeDto
import com.rmap.mobile.features.roadmap.data.remote.model.MilestoneTestSuiteDto
import com.rmap.mobile.features.roadmap.data.remote.api.RoadmapApi
import com.rmap.mobile.features.roadmap.data.remote.model.NodeProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapsResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitMilestoneSubmissionRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.TemplateCategoryDto
import com.rmap.mobile.features.roadmap.data.remote.model.TemplateCategoriesResponseDto
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteRoadmapRepositoryTest {
    @Test
    fun `getLearningProgress uses first roadmap from backend list endpoint`() = runTest {
        val api = FakeRoadmapApi().apply {
            roadmapsResponse = Response.success(
                RoadmapsResponseDto(
                    data = listOf(RoadmapDto(id = "roadmap-1", title = "Backend Roadmap"))
                )
            )
        }
        val repository = newRepository(api)

        val result = repository.getLearningProgress()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().totalLessons)
        assertEquals(0, result.getOrThrow().completedLessons)
        assertEquals(3, result.getOrThrow().streakDays)
        assertEquals(1, api.listRoadmapsCallCount)
        assertEquals(1, api.getRoadmapProgressCallCount)
        assertEquals(0, api.getRoadmapCallCount)
    }

    @Test
    fun `getLearningProgress returns empty progress when backend list is empty`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.getLearningProgress()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().totalLessons)
        assertEquals(0, result.getOrThrow().completedLessons)
        assertEquals(1, api.listRoadmapsCallCount)
        assertEquals(0, api.getRoadmapProgressCallCount)
    }

    @Test
    fun `getRoadmapDetail success calls roadmap detail nodes and progress endpoints`() = runTest {
        val api = FakeRoadmapApi().apply {
            templateResponse = Response.error(
                404,
                """{"code":40400,"message":"Template not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.getRoadmapDetail("roadmap-1")

        assertTrue(result.isSuccess)
        assertEquals("roadmap-1", result.getOrThrow().id)
        assertEquals(1, api.getRoadmapCallCount)
        assertEquals(1, api.getRoadmapNodesCallCount)
        assertEquals(1, api.getRoadmapProgressCallCount)
        assertEquals(1, api.getTemplateCallCount)
        assertEquals(0, api.getTemplateNodesCallCount)
    }

    @Test
    fun `getRoadmapDetail uses public template before personal endpoints`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.getRoadmapDetail("roadmap-1")

        assertTrue(result.isSuccess)
        assertEquals("roadmap-1", result.getOrThrow().id)
        assertEquals(0, api.getRoadmapCallCount)
        assertEquals(0, api.getRoadmapNodesCallCount)
        assertEquals(0, api.getRoadmapProgressCallCount)
        assertEquals(1, api.getTemplateCallCount)
        assertEquals(1, api.getTemplateNodesCallCount)
    }

    @Test
    fun `getRoadmapDetail returns failure when template and personal detail endpoints fail`() = runTest {
        val api = FakeRoadmapApi().apply {
            templateResponse = Response.error(
                404,
                """{"code":40400,"message":"Template not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
            roadmapResponse = Response.error(
                404,
                """{"code":40400,"message":"Roadmap not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.getRoadmapDetail("missing")

        assertTrue(result.isFailure)
        assertEquals(1, api.getRoadmapCallCount)
        assertEquals(1, api.getRoadmapNodesCallCount)
        assertEquals(1, api.getRoadmapProgressCallCount)
        assertEquals(1, api.getTemplateCallCount)
    }

    @Test
    fun `getRoadmapDetail with blank id returns failure without calling api`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.getRoadmapDetail(" ")

        assertTrue(result.isFailure)
        assertEquals(0, api.getTemplateCallCount)
        assertEquals(0, api.getRoadmapCallCount)
        assertEquals(0, api.getRoadmapProgressCallCount)
    }

    @Test
    fun `getRoadmapDetail returns failure when progress endpoint fails`() = runTest {
        val api = FakeRoadmapApi().apply {
            templateResponse = Response.error(
                404,
                """{"code":40400,"message":"Template not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
            progressResponse = Response.error(
                500,
                """{"code":50000,"message":"Server error"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.getRoadmapDetail("roadmap-1")

        assertTrue(result.isFailure)
        assertEquals(1, api.getRoadmapCallCount)
        assertEquals(1, api.getRoadmapNodesCallCount)
        assertEquals(1, api.getRoadmapProgressCallCount)
        assertEquals(1, api.getTemplateCallCount)
    }

    @Test
    fun `updateNodeProgress calls backend roadmap node progress endpoint`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.updateNodeProgress(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            status = LearningStatus.Completed
        )

        assertTrue(result.isSuccess)
        assertEquals("node-api", result.getOrThrow().nodeId)
        assertEquals(LearningStatus.Completed, result.getOrThrow().status)
        assertTrue(result.getOrThrow().unlockedNodeIds.isEmpty())
        assertEquals(1, api.updateNodeProgressCallCount)
        assertEquals("roadmap-1", api.lastUpdatedRoadmapId)
        assertEquals("node-api", api.lastUpdatedNodeId)
        assertEquals("COMPLETED", api.lastUpdateProgressRequest?.status)
    }

    @Test
    fun `startRoadmap calls backend start endpoint`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.startRoadmap("roadmap-1")

        assertTrue(result.isSuccess)
        assertEquals(1, api.startRoadmapCallCount)
        assertEquals("roadmap-1", api.lastStartedRoadmapId)
    }

    @Test
    fun `getRoadmapNodeLearningContent maps node detail skill resources and quiz state`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.getRoadmapNodeLearningContent(
            roadmapId = "roadmap-1",
            nodeId = "node-api",
            skillId = "skill-api"
        )

        assertTrue(result.isSuccess)
        val content = result.getOrThrow()
        assertEquals("REST API", content.skill.name)
        assertEquals(LearningStatus.InProgress, content.status)
        assertTrue(content.quizPassed)
        assertEquals(3, content.resources.size)
        assertEquals("HTTP video", content.resources[0].title)
        assertEquals(SkillResourcePlatform.Youtube, content.resources[0].platform)
        assertEquals("HTTP course", content.resources[1].title)
        assertEquals(SkillResourcePlatform.Course, content.resources[1].platform)
        assertEquals("HTTP article", content.resources[2].title)
        assertEquals(SkillResourcePlatform.Article, content.resources[2].platform)
        assertEquals(1, api.getRoadmapNodeDetailCallCount)
        assertEquals("roadmap-1", api.lastNodeDetailRoadmapId)
        assertEquals("node-api", api.lastNodeDetailNodeId)
    }

    @Test
    fun `getMilestoneDetail maps milestone suite and latest submission`() = runTest {
        val api = FakeRoadmapApi().apply {
            nodeDetailResponse = Response.success(testMilestoneDetail)
        }
        val repository = newRepository(api)

        val result = repository.getMilestoneDetail(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api"
        )

        assertTrue(result.isSuccess)
        val detail = result.getOrThrow()
        assertEquals("Basic API Server", detail.title)
        assertEquals(80, detail.testSuite?.passThresholdPercent)
        assertEquals("https://github.com/example/rmap-test", detail.latestSubmission?.repoUrl)
        assertEquals(1, api.getRoadmapNodeDetailCallCount)
        assertEquals("milestone-api", api.lastNodeDetailNodeId)
    }

    @Test
    fun `submitMilestone calls backend milestone submission endpoint`() = runTest {
        val api = FakeRoadmapApi()
        val repository = newRepository(api)

        val result = repository.submitMilestone(
            roadmapId = "roadmap-1",
            milestoneId = "milestone-api",
            repoUrl = " https://github.com/example/rmap-test "
        )

        assertTrue(result.isSuccess)
        assertEquals("submission-1", result.getOrThrow().id)
        assertEquals(1, api.submitMilestoneCallCount)
        assertEquals("roadmap-1", api.lastSubmittedMilestoneRoadmapId)
        assertEquals("milestone-api", api.lastSubmittedMilestoneId)
        assertEquals("https://github.com/example/rmap-test", api.lastSubmitMilestoneRequest?.repoUrl)
    }

    @Test
    fun `getTrendingRoadmaps hydrates summaries from backend template endpoints`() = runTest {
        val api = FakeRoadmapApi().apply {
            templatesResponse = Response.success(
                RoadmapsResponseDto(
                    data = listOf(
                        RoadmapDto(
                            id = "roadmap-1",
                            roleCategory = "WEB_DEVELOPMENT",
                            title = "Backend Roadmap"
                        )
                    )
                )
            )
        }
        val repository = newRepository(api)

        val result = repository.getTrendingRoadmaps()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().single().totalLessonsCount)
        assertEquals(1, result.getOrThrow().single().skillNodesCount)
        assertEquals(1, api.listTemplatesCallCount)
        assertEquals(1, api.getTemplateNodesCallCount)
        assertEquals(0, api.listRoadmapsCallCount)
        assertEquals(0, api.getRoadmapCallCount)
        assertEquals(0, api.getRoadmapProgressCallCount)
    }

    @Test
    fun `getExploreCategories fetches and maps from listTemplateCategories endpoint`() = runTest {
        val api = FakeRoadmapApi().apply {
            templateCategoriesResponse = Response.success(
                TemplateCategoriesResponseDto(
                    total = 1,
                    categories = listOf(
                        TemplateCategoryDto(
                            category = "WEB_DEVELOPMENT",
                            label = "Web Development",
                            templatesCount = 9,
                            shortLabel = "Web"
                        )
                    )
                )
            )
        }
        val repository = newRepository(api)

        val result = repository.getExploreCategories()

        assertTrue(result.isSuccess)
        val categories = result.getOrThrow()
        assertEquals(1, categories.size)
        val category = categories.single()
        assertEquals("WEB_DEVELOPMENT", category.id)
        assertEquals("Web Development", category.name)
        assertEquals("Web", category.shortName)
        assertEquals(9, category.roadmapCount)
        assertEquals(1, api.listTemplateCategoriesCallCount)
        assertEquals(0, api.listTemplatesCallCount)
    }

    @Test
    fun `getExploreCategories falls back to public templates when categories endpoint is missing`() = runTest {
        val api = FakeRoadmapApi().apply {
            templateCategoriesResponse = Response.error(
                404,
                "{}".toResponseBody("application/json".toMediaType())
            )
            templatesResponse = Response.success(
                RoadmapsResponseDto(
                    data = listOf(
                        RoadmapDto(
                            id = "template-1",
                            roleCategory = "WEB_DEVELOPMENT",
                            title = "Backend Roadmap"
                        )
                    )
                )
            )
        }
        val repository = newRepository(api)

        val result = repository.getExploreCategories()

        assertTrue(result.isSuccess)
        val category = result.getOrThrow().single()
        assertEquals("web-development", category.id)
        assertEquals("Web Development", category.name)
        assertEquals(1, category.roadmapCount)
        assertEquals(1, api.listTemplateCategoriesCallCount)
        assertEquals(1, api.listTemplatesCallCount)
    }


    private fun newRepository(api: FakeRoadmapApi): RemoteRoadmapRepository {
        return RemoteRoadmapRepository(
            roadmapApi = api,
            sessionManager = SessionManager {}
        )
    }

    private class FakeRoadmapApi : RoadmapApi {
        var getRoadmapCallCount = 0
        var getRoadmapNodesCallCount = 0
        var getRoadmapNodeDetailCallCount = 0
        var getRoadmapProgressCallCount = 0
        var startRoadmapCallCount = 0
        var updateNodeProgressCallCount = 0
        var submitMilestoneCallCount = 0
        var listRoadmapsCallCount = 0
        var listTemplatesCallCount = 0
        var getTemplateCallCount = 0
        var getTemplateNodesCallCount = 0
        var listTemplateCategoriesCallCount = 0
        var lastStartedRoadmapId: String? = null
        var lastNodeDetailRoadmapId: String? = null
        var lastNodeDetailNodeId: String? = null
        var lastUpdatedRoadmapId: String? = null
        var lastUpdatedNodeId: String? = null
        var lastSubmittedMilestoneRoadmapId: String? = null
        var lastSubmittedMilestoneId: String? = null
        var lastUpdateProgressRequest: UpdateNodeProgressRequestDto? = null
        var lastSubmitMilestoneRequest: SubmitMilestoneSubmissionRequestDto? = null
        var roadmapResponse: Response<RoadmapDto> = Response.success(testRoadmap)
        var roadmapNodesResponse: Response<RoadmapNodesResponseDto> = Response.success(testNodes)
        var nodeDetailResponse: Response<RoadmapNodeDetailResponseDto> = Response.success(testNodeDetail)
        var nodeQuizResponse: Response<RoadmapNodeQuizResponseDto> = Response.success(testNodeQuiz)
        var submitNodeQuizResponse: Response<SubmitQuizResponseDto> = Response.success(testSubmitQuizResponse)
        var submitMilestoneResponse: Response<MilestoneSubmissionEnvelopeDto> =
            Response.success(testMilestoneSubmissionEnvelope)
        var progressResponse: Response<RoadmapProgressDto> = Response.success(testProgress)
        var startRoadmapResponse: Response<Unit> = Response.success(Unit)
        var updateNodeProgressResponse: Response<UpdateNodeProgressResponseDto> =
            Response.success(testUpdateNodeProgressResponse)
        var roadmapsResponse: Response<RoadmapsResponseDto> =
            Response.success(RoadmapsResponseDto(data = emptyList()))
        var templatesResponse: Response<RoadmapsResponseDto> =
            Response.success(RoadmapsResponseDto(data = listOf(testRoadmap)))
        var templateResponse: Response<RoadmapDto> = Response.success(testRoadmap.copy(isTemplate = true))
        var templateNodesResponse: Response<RoadmapNodesResponseDto> = Response.success(testNodes)
        var templateCategoriesResponse: Response<TemplateCategoriesResponseDto> =
            Response.success(TemplateCategoriesResponseDto(total = 0, categories = emptyList()))


        override suspend fun listUserRoadmaps(
            page: Int?,
            perPage: Int?
        ): Response<RoadmapsResponseDto> {
            listRoadmapsCallCount++
            return roadmapsResponse
        }

        override suspend fun getUserRoadmap(roadmapId: String): Response<RoadmapDto> {
            getRoadmapCallCount++
            return roadmapResponse
        }

        override suspend fun getUserRoadmapNodes(roadmapId: String): Response<RoadmapNodesResponseDto> {
            getRoadmapNodesCallCount++
            return roadmapNodesResponse
        }

        override suspend fun getRoadmapNodeDetail(
            roadmapId: String,
            nodeId: String
        ): Response<RoadmapNodeDetailResponseDto> {
            getRoadmapNodeDetailCallCount++
            lastNodeDetailRoadmapId = roadmapId
            lastNodeDetailNodeId = nodeId
            return nodeDetailResponse
        }

        override suspend fun getNodeQuiz(
            roadmapId: String,
            nodeId: String
        ): Response<RoadmapNodeQuizResponseDto> {
            return nodeQuizResponse
        }

        override suspend fun submitNodeQuiz(
            roadmapId: String,
            nodeId: String,
            request: SubmitQuizRequestDto
        ): Response<SubmitQuizResponseDto> {
            return submitNodeQuizResponse
        }

        override suspend fun submitMilestoneSubmission(
            roadmapId: String,
            nodeId: String,
            request: SubmitMilestoneSubmissionRequestDto
        ): Response<MilestoneSubmissionEnvelopeDto> {
            submitMilestoneCallCount++
            lastSubmittedMilestoneRoadmapId = roadmapId
            lastSubmittedMilestoneId = nodeId
            lastSubmitMilestoneRequest = request
            return submitMilestoneResponse
        }

        override suspend fun getUserRoadmapProgress(roadmapId: String): Response<RoadmapProgressDto> {
            getRoadmapProgressCallCount++
            return progressResponse
        }

        override suspend fun startRoadmap(roadmapId: String): Response<Unit> {
            startRoadmapCallCount++
            lastStartedRoadmapId = roadmapId
            return startRoadmapResponse
        }

        override suspend fun updateNodeProgress(
            roadmapId: String,
            nodeId: String,
            request: UpdateNodeProgressRequestDto
        ): Response<UpdateNodeProgressResponseDto> {
            updateNodeProgressCallCount++
            lastUpdatedRoadmapId = roadmapId
            lastUpdatedNodeId = nodeId
            lastUpdateProgressRequest = request
            return updateNodeProgressResponse
        }

        override suspend fun listTemplates(
            roleCategory: String?,
            page: Int?,
            perPage: Int?
        ): Response<RoadmapsResponseDto> {
            listTemplatesCallCount++
            return templatesResponse
        }

        override suspend fun listLegacyTemplates(
            roleCategory: String?,
            page: Int?,
            perPage: Int?
        ): Response<RoadmapsResponseDto> {
            listTemplatesCallCount++
            return templatesResponse
        }

        override suspend fun getTemplate(templateId: String): Response<RoadmapDto> {
            getTemplateCallCount++
            return templateResponse
        }

        override suspend fun listTemplateCategories(): Response<TemplateCategoriesResponseDto> {
            listTemplateCategoriesCallCount++
            return templateCategoriesResponse
        }

        override suspend fun getTemplateNodes(templateId: String): Response<RoadmapNodesResponseDto> {
            getTemplateNodesCallCount++
            return templateNodesResponse
        }
    }

    private companion object {
        val testRoadmap = RoadmapDto(
            id = "roadmap-1",
            roleId = "role-1",
            roleName = "Backend Developer",
            title = "Backend Roadmap"
        )

        val testNodes = RoadmapNodesResponseDto(
            nodes = listOf(
                RoadmapNodeDto(
                    id = "node-api",
                    roadmapId = "roadmap-1",
                    skillId = "skill-api",
                    skillName = "API Basics",
                    relationType = "required",
                    sortOrder = 1,
                    children = emptyList()
                )
            )
        )

        val testProgress = RoadmapProgressDto(
            roadmapId = "roadmap-1",
            totalNodes = 1,
            completedNodes = 0,
            inProgressNodes = 1,
            streakDays = 3,
            nodes = listOf(
                NodeProgressDto(
                    roadmapNodeId = "node-api",
                    skillId = "skill-api",
                    skillName = "API Basics",
                    status = "in_progress"
                )
            )
        )

        val testUpdateNodeProgressResponse = UpdateNodeProgressResponseDto(
            roadmapNodeId = "node-api",
            status = "completed"
        )

        val testNodeQuiz = RoadmapNodeQuizResponseDto(
            nodeId = "node-api",
            skillId = "skill-api",
            questions = emptyList()
        )

        val testSubmitQuizResponse = SubmitQuizResponseDto(
            scorePct = 0f,
            passed = false,
            correctCount = 0,
            totalQuestions = 0,
            results = emptyList(),
            unlockedNodes = emptyList()
        )

        val testNodeDetail = RoadmapNodeDetailResponseDto(
            data = RoadmapNodeDetailDto(
                node = RoadmapNodeDto(
                    id = "node-api",
                    roadmapId = "roadmap-1",
                    skillId = "skill-api",
                    skillName = "REST API"
                ),
                progress = NodeProgressDto(
                    roadmapNodeId = "node-api",
                    status = "IN_PROGRESS",
                    quizPassed = true
                ),
                skill = SkillDetailDto(
                    id = "skill-api",
                    name = "REST API",
                    description = "Design APIs.",
                    category = "Backend",
                    estimatedHours = 4
                ),
                resources = JsonParser.parseString(
                    """
                    [
                      {
                        "id": "resource-video",
                        "skill_id": "skill-api",
                        "title": "HTTP video",
                        "url": "https://example.com/http-video",
                        "resourceType": "YOUTUBE",
                        "isFree": true,
                        "levelTag": "fresher"
                      },
                      {
                        "id": "resource-course",
                        "skill_id": "skill-api",
                        "title": "HTTP course",
                        "url": "https://example.com/http",
                        "resourceType": "COURSE",
                        "is_free": true,
                        "level_tag": "fresher"
                      },
                      {
                        "id": "resource-article",
                        "skillId": "skill-api",
                        "title": "HTTP article",
                        "url": "https://example.com/http-article",
                        "resourceType": "ARTICLE",
                        "isFree": true,
                        "levelTag": "fresher"
                      }
                    ]
                    """.trimIndent()
                )
            )
        )

        val testMilestoneSubmission = MilestoneSubmissionDto(
            id = "submission-1",
            repoUrl = "https://github.com/example/rmap-test",
            testSuiteId = "suite-1",
            status = "ERROR",
            outputLog = "[error]\nspawn docker ENOENT",
            passRatePct = null,
            passedTests = null,
            totalTests = null,
            attemptNumber = 6,
            createdAt = "2026-06-01T00:00:00Z",
            completedAt = null
        )

        val testMilestoneSubmissionEnvelope = MilestoneSubmissionEnvelopeDto(
            submission = testMilestoneSubmission
        )

        val testMilestoneDetail = RoadmapNodeDetailResponseDto(
            data = RoadmapNodeDetailDto(
                node = RoadmapNodeDto(
                    id = "milestone-api",
                    roadmapId = "roadmap-1",
                    skillName = "Basic API Server",
                    description = "Build a raw Node.js HTTP API.",
                    nodeType = "MILESTONE"
                ),
                progress = NodeProgressDto(
                    roadmapNodeId = "milestone-api",
                    status = "IN_PROGRESS"
                ),
                latestSubmission = testMilestoneSubmission,
                milestoneTestSuite = MilestoneTestSuiteDto(
                    id = "suite-1",
                    title = "Raw Node.js API Server Evaluation",
                    summary = "Verifies the implementation of a manual HTTP server.",
                    passThresholdPct = 80,
                    status = "READY",
                    testCases = emptyList()
                )
            )
        )

    }
}
