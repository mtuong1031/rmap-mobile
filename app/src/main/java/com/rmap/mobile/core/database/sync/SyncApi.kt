package com.rmap.mobile.core.database.sync

import retrofit2.Response
import retrofit2.http.GET

interface SyncApi {
    @GET("sync/version")
    suspend fun getVersions(): Response<SyncVersionDto>
}
