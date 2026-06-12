package com.rmap.mobile.features.profile.data.repository

import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.profile.data.model.ProfileActivityResponseDto
import com.rmap.mobile.features.profile.data.model.ProfileIdentityResponseDto
import com.rmap.mobile.features.profile.data.model.UpdateUserProfileRequestDto
import com.rmap.mobile.features.profile.data.remote.ProfileApi
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ProfileRepositoryImplTest {
    @Test
    fun `updateProfile maps successful response to domain`() = kotlinx.coroutines.test.runTest {
        val api = FakeProfileApi(
            updateResponse = Response.success(
                ProfileIdentityResponseDto(
                    id = "learner",
                    email = "learner@example.com",
                    fullName = "Updated Learner",
                    avatarUrl = "https://api.dicebear.com/10.x/adventurer/svg?seed=updated",
                    role = "user",
                    createdAt = "2026-05-28T00:00:00Z"
                )
            )
        )
        val repository = ProfileRepositoryImpl(
            profileApi = api,
            sessionManager = SessionManager(clearSessionStorage = {})
        )

        val result = repository.updateProfile(
            fullName = "Updated Learner",
            avatarUrl = "https://api.dicebear.com/10.x/adventurer/svg?seed=updated"
        )

        assertTrue(result.isSuccess)
        assertEquals("Updated Learner", result.getOrThrow().fullName)
        assertEquals("https://api.dicebear.com/10.x/adventurer/svg?seed=updated", api.updateRequest?.avatarUrl)
    }

    @Test
    fun `updateProfile omits blank avatar url`() = kotlinx.coroutines.test.runTest {
        val api = FakeProfileApi(
            updateResponse = Response.success(
                ProfileIdentityResponseDto(
                    id = "learner",
                    email = "learner@example.com",
                    fullName = "Updated Learner",
                    avatarUrl = null,
                    role = "user",
                    createdAt = "2026-05-28T00:00:00Z"
                )
            )
        )
        val repository = ProfileRepositoryImpl(
            profileApi = api,
            sessionManager = SessionManager(clearSessionStorage = {})
        )

        repository.updateProfile(
            fullName = "Updated Learner",
            avatarUrl = ""
        )

        assertNull(api.updateRequest?.avatarUrl)
    }

    @Test
    fun `updateProfile returns failure when api fails`() = kotlinx.coroutines.test.runTest {
        val api = FakeProfileApi(
            updateResponse = Response.error(
                422,
                """{"message":"fullName must be longer than or equal to 2 characters"}""".toResponseBody()
            )
        )
        val repository = ProfileRepositoryImpl(
            profileApi = api,
            sessionManager = SessionManager(clearSessionStorage = {})
        )

        val result = repository.updateProfile(
            fullName = "A",
            avatarUrl = ""
        )

        assertTrue(result.isFailure)
    }

    private class FakeProfileApi(
        private val updateResponse: Response<ProfileIdentityResponseDto>
    ) : ProfileApi {
        var updateRequest: UpdateUserProfileRequestDto? = null
            private set

        override suspend fun getActivity(): Response<ProfileActivityResponseDto> {
            error("Not used")
        }

        override suspend fun updateProfile(
            request: UpdateUserProfileRequestDto
        ): Response<ProfileIdentityResponseDto> {
            updateRequest = request
            return updateResponse
        }

        override suspend fun getIntegrations(): Response<List<com.rmap.mobile.features.profile.data.model.UserIntegrationDto>> =
            Response.success(emptyList())

        override suspend fun disconnectIntegration(provider: String): Response<Unit> = Response.success(Unit)
    }
}
