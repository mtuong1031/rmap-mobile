package com.rmap.mobile.features.home.domain.repository

import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface HomeRepository {
    val homeContentUpdates: Flow<HomeContent>
        get() = emptyFlow()

    suspend fun getHomeContent(): Result<HomeContent>

    suspend fun refreshHomeContent(): Result<HomeContent> = getHomeContent()

    suspend fun searchDashboard(
        query: String = "",
        roadmapPage: Int = 1,
        skillPage: Int = 1
    ): Result<HomeSearchResult>
}
