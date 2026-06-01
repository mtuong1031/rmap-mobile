package com.rmap.mobile.features.home.domain.repository

import com.rmap.mobile.features.home.domain.model.HomeContent

interface HomeRepository {
    suspend fun getHomeContent(): Result<HomeContent>
}
