package com.rmap.mobile.features.dashboard.domain.repository

import com.rmap.mobile.features.dashboard.domain.model.Dashboard

interface DashboardRepository {
    suspend fun getDashboard(): Result<Dashboard>
}
