package com.rmap.mobile.features.home.domain.repository

import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeSearchResult

interface HomeRepository {
    suspend fun getHomeContent(includePersonalDashboard: Boolean = true): Result<HomeContent>
    suspend fun searchDashboard(
        query: String = "",
        roadmapPage: Int = 1,
        skillPage: Int = 1
    ): Result<HomeSearchResult>
}
