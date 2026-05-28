package com.rmap.mobile.features.auth.data.remote

import com.rmap.mobile.features.auth.data.model.AuthMessageResponseDto
import com.rmap.mobile.features.auth.data.model.LoginRequestDto
import com.rmap.mobile.features.auth.data.model.RegisterRequestDto
import com.rmap.mobile.features.auth.data.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<AuthMessageResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<UserDto>

    @POST("auth/logout")
    suspend fun logout(): Response<AuthMessageResponseDto>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserDto>
}
