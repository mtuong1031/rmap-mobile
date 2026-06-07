package com.rmap.mobile.features.profile.data.remote

import com.rmap.mobile.features.profile.data.model.ProfileActivityResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("users/me/activity")
    suspend fun getActivity(): Response<ProfileActivityResponseDto>
}
