package com.rmap.mobile.features.roadmap.data.remote.api

import com.rmap.mobile.features.roadmap.data.remote.model.SkillDetailDto
import com.rmap.mobile.features.roadmap.data.remote.model.SkillResourcesResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SkillApi {
    @GET("skills/{skillId}")
    suspend fun getSkill(
        @Path("skillId") skillId: String
    ): Response<SkillDetailDto>

    @GET("skills/{skillId}/resources")
    suspend fun getSkillResources(
        @Path("skillId") skillId: String
    ): Response<SkillResourcesResponseDto>
}
