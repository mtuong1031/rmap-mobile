package com.rmap.mobile.features.airoadmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmap.mobile.features.airoadmap.data.local.entity.AiRoadmapCacheEntity

@Dao
interface AiRoadmapCacheDao {
    @Query("SELECT * FROM ai_roadmaps_cache WHERE id = 0")
    suspend fun get(): AiRoadmapCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: AiRoadmapCacheEntity)

    @Query("DELETE FROM ai_roadmaps_cache")
    suspend fun clear()
}
