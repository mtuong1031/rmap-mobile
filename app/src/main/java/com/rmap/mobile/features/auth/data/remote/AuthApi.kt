package com.rmap.mobile.features.auth.data.remote

import com.rmap.mobile.features.auth.data.model.AuthMessageResponseDto
import com.rmap.mobile.features.auth.data.model.MobileOAuthRequestDto
import com.rmap.mobile.features.auth.data.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/google/mobile")
    suspend fun loginWithGoogle(
        @Body request: MobileOAuthRequestDto
    ): Response<AuthMessageResponseDto>

    @POST("auth/github/mobile")
    suspend fun loginWithGithub(
        @Body request: com.rmap.mobile.features.auth.data.model.GithubMobileOAuthRequestDto
    ): Response<AuthMessageResponseDto>

    @POST("auth/logout")
    suspend fun logout(): Response<AuthMessageResponseDto>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserDto>
}
