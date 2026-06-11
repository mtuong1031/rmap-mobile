package com.rmap.mobile.features.profile.data.remote

import com.rmap.mobile.features.profile.data.model.ProfileIdentityResponseDto
import com.rmap.mobile.features.profile.data.model.ProfileActivityResponseDto
import com.rmap.mobile.features.profile.data.model.UpdateUserProfileRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileApi {
    @GET("users/me/activity")
    suspend fun getActivity(): Response<ProfileActivityResponseDto>

    @PATCH("users/me")
    suspend fun updateProfile(
        @Body request: UpdateUserProfileRequestDto
    ): Response<ProfileIdentityResponseDto>

    @GET("users/me/integrations")
    suspend fun getIntegrations(): Response<List<com.rmap.mobile.features.profile.data.model.UserIntegrationDto>>

    @retrofit2.http.DELETE("users/me/integrations/{provider}")
    suspend fun disconnectIntegration(
        @retrofit2.http.Path("provider") provider: String
    ): Response<Unit>
}
