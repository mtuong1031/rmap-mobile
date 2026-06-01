package com.rmap.mobile.features.home.data.remote

import com.rmap.mobile.features.home.data.model.HomeDashboardResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateCategoriesResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateRecommendationsResponseDto
import com.rmap.mobile.features.home.data.model.HomeTemplateTrendingsResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface HomeApi {
    @GET("dashboard/home")
    suspend fun getDashboardHome(): Response<HomeDashboardResponseDto>

    @GET("templates/recommendations")
    suspend fun getTemplateRecommendations(): Response<HomeTemplateRecommendationsResponseDto>

    @GET("templates/categories")
    suspend fun getTemplateCategories(): Response<HomeTemplateCategoriesResponseDto>

    @GET("templates/trendings")
    suspend fun getTemplateTrendings(): Response<HomeTemplateTrendingsResponseDto>
}
