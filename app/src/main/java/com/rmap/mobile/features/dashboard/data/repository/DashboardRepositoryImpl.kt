package com.rmap.mobile.features.dashboard.data.repository

import com.rmap.mobile.core.network.SafeApiCall
import com.rmap.mobile.core.network.toDomainResult
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.features.dashboard.data.mapper.toDomain
import com.rmap.mobile.features.dashboard.data.remote.DashboardApi
import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val dashboardApi: DashboardApi,
    private val sessionManager: SessionManager
) : DashboardRepository {
    override suspend fun getDashboard(): Result<Dashboard> {
        return SafeApiCall.execute(
            onUnauthorized = sessionManager::handleUnauthorized
        ) {
            dashboardApi.getDashboard()
        }.toDomainResult { dashboard ->
            dashboard.toDomain()
        }
    }
}
