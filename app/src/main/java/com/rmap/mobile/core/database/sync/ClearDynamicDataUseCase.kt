package com.rmap.mobile.core.database.sync

import com.rmap.mobile.core.database.RMapDatabase

class ClearDynamicDataUseCase(
    private val database: RMapDatabase
) {
    suspend operator fun invoke() {
        database.dashboardCacheDao().clear()
        database.roadmapDetailCacheDao().clearAll()
        database.aiRoadmapCacheDao().clear()
    }
}
