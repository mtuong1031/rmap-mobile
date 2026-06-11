package com.rmap.mobile.features.dashboard.data.repository

import com.google.gson.Gson
import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.dashboard.data.local.dao.DashboardCacheDao
import com.rmap.mobile.features.dashboard.data.local.mapper.toDashboard
import com.rmap.mobile.features.dashboard.data.local.mapper.toDashboardCacheEntity
import com.rmap.mobile.features.dashboard.data.mapper.toDomain
import com.rmap.mobile.features.dashboard.data.remote.DashboardApi
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DashboardRepositoryImpl(
    private val dashboardApi: DashboardApi,
    private val sessionManager: SessionManager,
    private val dashboardCacheDao: DashboardCacheDao? = null,
    private val gson: Gson = Gson()
) : DashboardRepository {
    override fun observeDashboard(): Flow<Result<Dashboard>> = flow {
        val cached = dashboardCacheDao?.get()?.toDashboard(gson)
        if (cached != null) {
            emit(Result.success(cached))
        }

        val freshResult = fetchDashboard()
        freshResult.onSuccess { dashboard ->
            dashboardCacheDao?.upsert(dashboard.toDashboardCacheEntity(gson))
            emit(Result.success(dashboard))
        }

        if (freshResult.isFailure && cached == null) {
            emit(freshResult)
        }
    }

    override suspend fun getDashboard(): Result<Dashboard> {
        return observeDashboard().first()
    }

    private suspend fun fetchDashboard(): Result<Dashboard> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            dashboardApi.getDashboard()
        }.toDomainResult { dashboard ->
            dashboard.toDomain()
        }
    }
}
