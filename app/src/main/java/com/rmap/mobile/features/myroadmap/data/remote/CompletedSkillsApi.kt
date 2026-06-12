package com.rmap.mobile.features.myroadmap.data.remote

import com.rmap.mobile.features.myroadmap.data.model.CompletedSkillsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CompletedSkillsApi {
    @GET("users/me/skills/completed")
    suspend fun getCompletedSkills(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<CompletedSkillsResponseDto>
}
