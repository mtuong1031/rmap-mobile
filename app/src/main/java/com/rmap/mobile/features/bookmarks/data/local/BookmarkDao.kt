package com.rmap.mobile.features.bookmarks.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM roadmap_bookmarks ORDER BY updatedAtMillis DESC")
    fun observeRoadmapBookmarks(): Flow<List<RoadmapBookmarkEntity>>

    @Query("SELECT * FROM skill_bookmarks ORDER BY updatedAtMillis DESC")
    fun observeSkillBookmarks(): Flow<List<SkillBookmarkEntity>>

    @Query("SELECT * FROM roadmap_bookmarks ORDER BY updatedAtMillis DESC")
    suspend fun getRoadmapBookmarks(): List<RoadmapBookmarkEntity>

    @Query("SELECT * FROM skill_bookmarks ORDER BY updatedAtMillis DESC")
    suspend fun getSkillBookmarks(): List<SkillBookmarkEntity>

    @Query("SELECT * FROM roadmap_bookmarks WHERE roadmapId = :roadmapId")
    suspend fun getRoadmapBookmark(roadmapId: String): RoadmapBookmarkEntity?

    @Query("SELECT * FROM skill_bookmarks WHERE skillId = :skillId")
    suspend fun getSkillBookmark(skillId: String): SkillBookmarkEntity?

    @Upsert
    suspend fun upsertRoadmapBookmark(entity: RoadmapBookmarkEntity)

    @Upsert
    suspend fun upsertSkillBookmark(entity: SkillBookmarkEntity)

    @Query("DELETE FROM roadmap_bookmarks WHERE roadmapId = :roadmapId")
    suspend fun deleteRoadmapBookmark(roadmapId: String)

    @Query("DELETE FROM skill_bookmarks WHERE skillId = :skillId")
    suspend fun deleteSkillBookmark(skillId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM roadmap_bookmarks WHERE roadmapId = :roadmapId)")
    suspend fun isRoadmapSaved(roadmapId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM skill_bookmarks WHERE skillId = :skillId)")
    suspend fun isSkillSaved(skillId: String): Boolean
}
