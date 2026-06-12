package com.rmap.mobile.features.dashboard.domain.repository

import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DashboardRepository {
    fun observeDashboard(): Flow<Result<Dashboard>> = flow {
        emit(getDashboard())
    }

    suspend fun getDashboard(): Result<Dashboard>

    suspend fun refreshDashboard(): Result<Dashboard> = getDashboard()
}
