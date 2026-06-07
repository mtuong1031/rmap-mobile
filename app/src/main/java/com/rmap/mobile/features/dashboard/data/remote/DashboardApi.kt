package com.rmap.mobile.features.dashboard.data.remote

import com.rmap.mobile.features.dashboard.data.model.DashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApi {
    @GET("dashboard")
    suspend fun getDashboard(): Response<DashboardResponseDto>
}
