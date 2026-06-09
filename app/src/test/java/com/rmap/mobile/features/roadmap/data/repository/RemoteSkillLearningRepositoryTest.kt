package com.rmap.mobile.features.roadmap.data.repository

import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.roadmap.data.remote.api.SkillApi
import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourceDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourcesResponseDto
import com.rmap.mobile.features.roadmap.domain.model.SkillResourcePlatform
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteSkillLearningRepositoryTest {
    @Test
    fun `getSkillDetail fetches public skill without loading protected resources`() = runTest {
        val api = FakeSkillApi()
        val repository = newRepository(api)

        val result = repository.getSkillDetail("skill-api")

        assertTrue(result.isSuccess)
        assertEquals("REST API", result.getOrThrow().name)
        assertEquals(1, api.getSkillCallCount)
        assertEquals(0, api.getSkillResourcesCallCount)
    }

    @Test
    fun `getSkillLearningContent fetches skill detail and resources`() = runTest {
        val api = FakeSkillApi()
        val repository = newRepository(api)

        val result = repository.getSkillLearningContent("skill-api")

        assertTrue(result.isSuccess)
        val content = result.getOrThrow()
        assertEquals("skill-api", content.skill.id)
        assertEquals("REST API", content.skill.name)
        assertEquals(1, content.resources.size)
        assertEquals("HTTP course", content.resources.single().title)
        assertEquals(SkillResourcePlatform.Youtube, content.resources.single().platform)
        assertEquals(1, api.getSkillCallCount)
        assertEquals(1, api.getSkillResourcesCallCount)
        assertEquals("skill-api", api.lastSkillId)
        assertEquals("skill-api", api.lastResourcesSkillId)
    }

    @Test
    fun `getSkillLearningContent with blank skill id returns failure without calling api`() = runTest {
        val api = FakeSkillApi()
        val repository = newRepository(api)

        val result = repository.getSkillLearningContent(" ")

        assertTrue(result.isFailure)
        assertEquals(0, api.getSkillCallCount)
        assertEquals(0, api.getSkillResourcesCallCount)
    }

    @Test
    fun `getSkillLearningContent returns failure when resources endpoint fails`() = runTest {
        val api = FakeSkillApi().apply {
            resourcesResponse = Response.error(
                500,
                """{"code":50000,"message":"Server error"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.getSkillLearningContent("skill-api")

        assertTrue(result.isFailure)
        assertEquals(1, api.getSkillCallCount)
        assertEquals(1, api.getSkillResourcesCallCount)
    }

    @Test
    fun `getSkillLearningContent returns skill detail with empty resources when resources are not found`() = runTest {
        val api = FakeSkillApi().apply {
            resourcesResponse = Response.error(
                404,
                """{"code":40400,"message":"Resources not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }
        val repository = newRepository(api)

        val result = repository.getSkillLearningContent("skill-api")

        assertTrue(result.isSuccess)
        assertEquals("REST API", result.getOrThrow().skill.name)
        assertTrue(result.getOrThrow().resources.isEmpty())
        assertEquals(1, api.getSkillCallCount)
        assertEquals(1, api.getSkillResourcesCallCount)
    }

    private fun newRepository(api: FakeSkillApi): RemoteSkillLearningRepository {
        return RemoteSkillLearningRepository(
            skillApi = api,
            sessionManager = SessionManager {}
        )
    }

    private class FakeSkillApi : SkillApi {
        var getSkillCallCount = 0
        var getSkillResourcesCallCount = 0
        var lastSkillId: String? = null
        var lastResourcesSkillId: String? = null
        var skillResponse: Response<SkillDetailDto> = Response.success(testSkill)
        var resourcesResponse: Response<SkillResourcesResponseDto> = Response.success(testResources)

        override suspend fun getSkill(skillId: String): Response<SkillDetailDto> {
            getSkillCallCount++
            lastSkillId = skillId
            return skillResponse
        }

        override suspend fun getSkillResources(skillId: String): Response<SkillResourcesResponseDto> {
            getSkillResourcesCallCount++
            lastResourcesSkillId = skillId
            return resourcesResponse
        }
    }

    private companion object {
        val testSkill = SkillDetailDto(
            id = "skill-api",
            name = "REST API",
            description = "Design and consume REST APIs.",
            category = "Backend",
            estimatedHours = 4
        )

        val testResources = SkillResourcesResponseDto(
            data = listOf(
                SkillResourceDto(
                    id = "resource-http",
                    skillId = "skill-api",
                    title = "HTTP course",
                    url = "https://example.com/http",
                    platform = "youtube",
                    isFree = true,
                    levelTag = "fresher"
                )
            )
        )
    }
}
