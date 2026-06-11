package com.rmap.mobile.features.roadmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmap.mobile.features.roadmap.data.local.entity.RoadmapDetailCacheEntity

@Dao
interface RoadmapDetailCacheDao {
    @Query("SELECT * FROM roadmap_detail_cache WHERE roadmapId = :roadmapId")
    suspend fun get(roadmapId: String): RoadmapDetailCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: RoadmapDetailCacheEntity)

    @Query("DELETE FROM roadmap_detail_cache")
    suspend fun clearAll()

    @Query("DELETE FROM roadmap_detail_cache WHERE roadmapId = :roadmapId")
    suspend fun deleteById(roadmapId: String)
}
