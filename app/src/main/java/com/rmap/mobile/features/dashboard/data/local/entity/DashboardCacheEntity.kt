package com.rmap.mobile.features.dashboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_cache")
data class DashboardCacheEntity(
    @PrimaryKey val id: Int = DASHBOARD_CACHE_ID,
    val dataJson: String,
    val cachedAtEpoch: Long = System.currentTimeMillis()
)

const val DASHBOARD_CACHE_ID = 0
