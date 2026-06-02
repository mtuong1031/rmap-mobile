package com.rmap.mobile.features.profile.data.remote

import com.rmap.mobile.features.profile.data.model.DashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("dashboard")
    suspend fun getDashboard(): Response<DashboardResponseDto>
}
