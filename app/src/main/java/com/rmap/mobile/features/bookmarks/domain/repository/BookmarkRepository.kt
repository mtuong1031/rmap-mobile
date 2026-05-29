package com.rmap.mobile.features.bookmarks.domain.repository

import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun observeSavedRoadmaps(): Flow<List<RoadmapBookmark>>
    fun observeSavedSkills(): Flow<List<SkillBookmark>>

    suspend fun getSavedRoadmaps(): Result<List<RoadmapBookmark>>
    suspend fun getSavedSkills(): Result<List<SkillBookmark>>
    suspend fun saveRoadmap(roadmapId: String): Result<Unit>
    suspend fun deleteRoadmap(roadmapId: String): Result<Unit>
    suspend fun isRoadmapSaved(roadmapId: String): Result<Boolean>
    suspend fun saveSkill(
        skillId: String,
        roadmapId: String
    ): Result<Unit>
    suspend fun deleteSkill(skillId: String): Result<Unit>
    suspend fun isSkillSaved(skillId: String): Result<Boolean>
}
