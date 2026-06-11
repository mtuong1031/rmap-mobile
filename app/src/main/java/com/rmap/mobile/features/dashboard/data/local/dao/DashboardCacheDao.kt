package com.rmap.mobile.features.dashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmap.mobile.features.dashboard.data.local.entity.DashboardCacheEntity

@Dao
interface DashboardCacheDao {
    @Query("SELECT * FROM dashboard_cache WHERE id = 0")
    suspend fun get(): DashboardCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: DashboardCacheEntity)

    @Query("DELETE FROM dashboard_cache")
    suspend fun clear()
}
