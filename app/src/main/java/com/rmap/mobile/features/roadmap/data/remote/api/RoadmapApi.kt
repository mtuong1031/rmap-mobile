package com.rmap.mobile.features.roadmap.data.remote.api

import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapProgressDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapNodesResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.RoadmapsResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.SubmitQuizResponseDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressRequestDto
import com.rmap.mobile.features.roadmap.data.remote.model.UpdateNodeProgressResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface RoadmapApi {
    @GET("roadmaps")
    suspend fun listUserRoadmaps(
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Response<RoadmapsResponseDto>

    @GET("roadmaps/{roadmapId}")
    suspend fun getUserRoadmap(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapDto>

    @GET("roadmaps/{roadmapId}/nodes")
    suspend fun getUserRoadmapNodes(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapNodesResponseDto>

    @GET("roadmaps/{roadmapId}/nodes/{nodeId}")
    suspend fun getRoadmapNodeDetail(
        @Path("roadmapId") roadmapId: String,
        @Path("nodeId") nodeId: String
    ): Response<RoadmapNodeDetailResponseDto>

    @GET("roadmaps/{roadmapId}/nodes/{nodeId}/quiz")
    suspend fun getNodeQuiz(
        @Path("roadmapId") roadmapId: String,
        @Path("nodeId") nodeId: String
    ): Response<RoadmapNodeQuizResponseDto>

    @POST("roadmaps/{roadmapId}/nodes/{nodeId}/quiz/submit")
    suspend fun submitNodeQuiz(
        @Path("roadmapId") roadmapId: String,
        @Path("nodeId") nodeId: String,
        @Body request: SubmitQuizRequestDto
    ): Response<SubmitQuizResponseDto>

    @GET("roadmaps/{roadmapId}/progress")
    suspend fun getUserRoadmapProgress(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapProgressDto>

    @POST("roadmaps/{roadmapId}/start")
    suspend fun startRoadmap(
        @Path("roadmapId") roadmapId: String
    ): Response<Unit>

    @PATCH("roadmaps/{roadmapId}/nodes/{nodeId}/progress")
    suspend fun updateNodeProgress(
        @Path("roadmapId") roadmapId: String,
        @Path("nodeId") nodeId: String,
        @Body request: UpdateNodeProgressRequestDto
    ): Response<UpdateNodeProgressResponseDto>

    @GET("roadmaps/templates")
    suspend fun listTemplates(
        @Query("role_id") roleId: String? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Response<RoadmapsResponseDto>

    @GET("templates")
    suspend fun listLegacyTemplates(
        @Query("roleCategory") roleCategory: String? = null,
        @Query("page") page: Int? = null,
        @Query("perPage") perPage: Int? = null
    ): Response<RoadmapsResponseDto>

    @GET("templates/{templateId}")
    suspend fun getTemplate(
        @Path("templateId") templateId: String
    ): Response<RoadmapDto>

    @GET("templates/{templateId}/nodes")
    suspend fun getTemplateNodes(
        @Path("templateId") templateId: String
    ): Response<RoadmapNodesResponseDto>
}
