package com.rmap.mobile.features.home.data.remote

import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeDashboardSearchResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRecommendationsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateTrendingsResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.TemplateCategoriesResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("dashboard/home")
    suspend fun getDashboardHome(): Response<HomeDashboardResponseDto>

    @GET("dashboard/search")
    suspend fun searchDashboard(
        @Query("query") query: String = "",
        @Query("roadmapPage") roadmapPage: Int = 1,
        @Query("skillPage") skillPage: Int = 1
    ): Response<HomeDashboardSearchResponseDto>

    @GET("templates/recommendations")
    suspend fun getTemplateRecommendations(): Response<HomeTemplateRecommendationsResponseDto>

    @GET("templates/category")
    suspend fun getTemplateCategories(): Response<TemplateCategoriesResponseDto>

    @GET("templates/trendings")
    suspend fun getTemplateTrendings(): Response<HomeTemplateTrendingsResponseDto>
}
