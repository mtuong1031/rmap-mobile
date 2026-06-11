package com.rmap.mobile.features.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rmap.mobile.features.home.data.local.entity.HomeTrendingRoadmapEntity

@Dao
interface HomeTrendingRoadmapDao {
    @Query("SELECT * FROM home_trending_roadmaps ORDER BY rank ASC")
    suspend fun getAll(): List<HomeTrendingRoadmapEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roadmaps: List<HomeTrendingRoadmapEntity>)

    @Query("DELETE FROM home_trending_roadmaps")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(roadmaps: List<HomeTrendingRoadmapEntity>) {
        deleteAll()
        insertAll(roadmaps)
    }
}
