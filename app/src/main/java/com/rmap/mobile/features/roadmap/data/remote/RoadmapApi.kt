package com.rmap.mobile.features.roadmap.data.remote

import com.rmap.mobile.features.roadmap.data.model.PaginatedRoadmapsResponseDto
import com.rmap.mobile.features.roadmap.data.model.NodeDetailResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodeQuizResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapNodesListResponseDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapProgressSummaryDto
import com.rmap.mobile.features.roadmap.data.model.RoadmapResponseDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizRequestDto
import com.rmap.mobile.features.roadmap.data.model.SubmitQuizResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoadmapApi {
    @GET("roadmaps")
    suspend fun listRoadmaps(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = DEFAULT_PAGE_SIZE
    ): Response<PaginatedRoadmapsResponseDto>

    @GET("roadmaps/{roadmapId}")
    suspend fun getRoadmap(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapResponseDto>

    @GET("roadmaps/{roadmapId}/nodes")
    suspend fun listRoadmapNodes(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapNodesListResponseDto>

    @GET("roadmaps/{roadmapId}/progress")
    suspend fun getRoadmapProgress(
        @Path("roadmapId") roadmapId: String
    ): Response<RoadmapProgressSummaryDto>

    @GET("roadmaps/{roadmapId}/nodes/{nodeId}")
    suspend fun getNodeDetail(
        @Path("roadmapId") roadmapId: String,
        @Path("nodeId") nodeId: String
    ): Response<NodeDetailResponseDto>

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

    private companion object {
        const val DEFAULT_PAGE_SIZE = 100
    }
}
