package com.rmap.mobile.features.roadmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rmap.mobile.features.roadmap.data.local.entity.TemplateRoadmapEntity

@Dao
interface TemplateRoadmapDao {
    @Query("SELECT * FROM template_roadmaps ORDER BY title ASC")
    suspend fun getAll(): List<TemplateRoadmapEntity>

    @Query("SELECT COUNT(*) FROM template_roadmaps")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roadmaps: List<TemplateRoadmapEntity>)

    @Query("DELETE FROM template_roadmaps")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(roadmaps: List<TemplateRoadmapEntity>) {
        deleteAll()
        insertAll(roadmaps)
    }
}
