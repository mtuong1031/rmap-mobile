package com.rmap.mobile.features.airoadmap.data.remote

import com.rmap.mobile.features.airoadmap.data.model.GenerateRoadmapRequestDto
import com.rmap.mobile.features.airoadmap.data.model.GenerateRoadmapResponseDto
import com.rmap.mobile.features.airoadmap.data.model.OnboardingQuizRequestDto
import com.rmap.mobile.features.airoadmap.data.model.OnboardingQuizResponseDto
import com.rmap.mobile.features.airoadmap.data.model.PaginatedRoadmapsResponseDto
import com.rmap.mobile.features.airoadmap.data.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.airoadmap.data.model.RoadmapResponseDto
import com.rmap.mobile.features.airoadmap.data.model.TemplateRoadmapNodesResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AiRoadmapApi {
    @POST("onboarding/quiz")
    suspend fun createQuiz(
        @Body request: OnboardingQuizRequestDto
    ): Response<OnboardingQuizResponseDto>

    @GET("roadmaps")
    suspend fun listRoadmaps(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 20
    ): Response<PaginatedRoadmapsResponseDto>

    @POST("roadmaps/generate")
    suspend fun generateRoadmap(
        @Body request: GenerateRoadmapRequestDto
    ): Response<GenerateRoadmapResponseDto>

    @GET("roadmaps/{roadmapId}")
    suspend fun getRoadmap(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapResponseDto>

    @GET("roadmaps/{roadmapId}/nodes")
    suspend fun listRoadmapNodes(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapNodesResponseDto>

    @GET("templates")
    suspend fun listTemplates(
        @Query("roleCategory") roleCategory: String? = null,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 20
    ): Response<PaginatedRoadmapsResponseDto>

    @GET("templates/{templateId}")
    suspend fun getTemplate(
        @Path("templateId") templateId: String
    ): Response<RoadmapResponseDto>

    @GET("templates/{templateId}/nodes")
    suspend fun listTemplateNodes(
        @Path("templateId") templateId: String,
        @Query("nodeType") nodeType: String? = null
    ): Response<TemplateRoadmapNodesResponseDto>
}
