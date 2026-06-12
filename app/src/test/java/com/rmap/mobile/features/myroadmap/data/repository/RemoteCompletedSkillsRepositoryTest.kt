package com.rmap.mobile.features.myroadmap.data.repository

import com.rmap.mobile.core.network.AppException
import com.rmap.mobile.core.network.NetworkErrorType
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillDto
import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillsPaginationDto
import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillsResponseDto
import com.rmap.mobile.features.myroadmap.data.remote.CompletedSkillsApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteCompletedSkillsRepositoryTest {
    @Test
    fun `completed skills map pagination and deduplicate by skill id`() = runTest {
        val api = FakeCompletedSkillsApi(
            response = Response.success(
                responseDto(
                    skills = listOf(
                        skillDto(id = "skill-1", name = "HTTP"),
                        skillDto(id = "skill-1", name = "HTTP duplicate"),
                        skillDto(id = "skill-2", name = "REST")
                    ),
                    page = 2,
                    totalPages = 3
                )
            )
        )
        val repository = RemoteCompletedSkillsRepository(api, SessionManager {})

        val page = repository.getCompletedSkills("WEB_DEVELOPMENT", page = 2, perPage = 20).getOrThrow()

        assertEquals(listOf("skill-1", "skill-2"), page.skills.map { it.id })
        assertEquals(2, page.page)
        assertTrue(page.hasMore)
    }

    @Test
    fun `repository clamps invalid pagination before calling api`() = runTest {
        val api = FakeCompletedSkillsApi(Response.success(responseDto()))
        val repository = RemoteCompletedSkillsRepository(api, SessionManager {})

        repository.getCompletedSkills("DESIGN", page = 0, perPage = 500)

        assertEquals(1, api.requestedPage)
        assertEquals(100, api.requestedPerPage)
    }

    @Test
    fun `unauthorized response clears session and returns typed failure`() = runTest {
        var clearCount = 0
        val api = FakeCompletedSkillsApi(
            Response.error(
                401,
                """{"code":40100,"message":"Unauthorized"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        )
        val repository = RemoteCompletedSkillsRepository(
            api = api,
            sessionManager = SessionManager { clearCount += 1 }
        )

        val result = repository.getCompletedSkills("DESIGN", page = 1, perPage = 20)

        assertTrue(result.isFailure)
        assertEquals(NetworkErrorType.Unauthorized, (result.exceptionOrNull() as AppException).type)
        assertEquals(1, clearCount)
    }
}

private class FakeCompletedSkillsApi(
    private val response: Response<CompletedSkillsResponseDto>
) : CompletedSkillsApi {
    var requestedPage: Int? = null
        private set
    var requestedPerPage: Int? = null
        private set

    override suspend fun getCompletedSkills(
        category: String,
        page: Int,
        perPage: Int
    ): Response<CompletedSkillsResponseDto> {
        requestedPage = page
        requestedPerPage = perPage
        return response
    }
}

private fun responseDto(
    skills: List<CompletedSkillDto> = listOf(skillDto()),
    page: Int = 1,
    totalPages: Int = 1
): CompletedSkillsResponseDto = CompletedSkillsResponseDto(
    data = skills,
    meta = CompletedSkillsPaginationDto(
        page = page,
        perPage = 20,
        total = skills.size,
        totalPages = totalPages
    )
)

private fun skillDto(
    id: String = "skill-1",
    name: String = "HTTP"
): CompletedSkillDto = CompletedSkillDto(
    skillId = id,
    skillName = name,
    category = "WEB_DEVELOPMENT",
    completedAt = "2026-06-01T03:22:18Z"
)
