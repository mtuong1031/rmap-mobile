package com.rmap.mobile.features.bookmarks.data.repository

import com.rmap.mobile.features.bookmarks.data.local.BookmarkDao
import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.mapper.newRoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.mapper.newSkillBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.mapper.toBookmark
import com.rmap.mobile.features.bookmarks.data.mapper.toDomain
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmark
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmark
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.roadmap.domain.model.containsLearningItem
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomBookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val roadmapRepository: RoadmapRepository,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis
) : BookmarkRepository {
    override fun observeSavedRoadmaps(): Flow<List<RoadmapBookmark>> {
        return bookmarkDao.observeRoadmapBookmarks()
            .map { entities -> hydrateRoadmaps(entities) }
    }

    override fun observeSavedSkills(): Flow<List<SkillBookmark>> {
        return bookmarkDao.observeSkillBookmarks()
            .map { entities -> hydrateSkills(entities) }
    }

    override suspend fun getSavedRoadmaps(): Result<List<RoadmapBookmark>> {
        return runCatching {
            hydrateRoadmaps(bookmarkDao.getRoadmapBookmarks())
        }
    }

    override suspend fun getSavedSkills(): Result<List<SkillBookmark>> {
        return runCatching {
            hydrateSkills(bookmarkDao.getSkillBookmarks())
        }
    }

    override suspend fun saveRoadmap(roadmapId: String): Result<Unit> {
        return runCatching {
            val exists = roadmapRepository.searchRoadmaps("").getOrThrow()
                .any { roadmap -> roadmap.id == roadmapId }
            require(exists) { "Roadmap not found" }

            val nowMillis = currentTimeMillis()
            val existingEntity = bookmarkDao.getRoadmapBookmark(roadmapId)
            bookmarkDao.upsertRoadmapBookmark(
                newRoadmapBookmarkEntity(
                    roadmapId = roadmapId,
                    existingEntity = existingEntity,
                    nowMillis = nowMillis
                )
            )
        }
    }

    override suspend fun deleteRoadmap(roadmapId: String): Result<Unit> {
        return runCatching {
            bookmarkDao.deleteRoadmapBookmark(roadmapId)
        }
    }

    override suspend fun isRoadmapSaved(roadmapId: String): Result<Boolean> {
        return runCatching {
            bookmarkDao.isRoadmapSaved(roadmapId)
        }
    }

    override suspend fun saveSkill(
        skillId: String,
        roadmapId: String
    ): Result<Unit> {
        return runCatching {
            val roadmapDetail = roadmapRepository.getRoadmapDetail(roadmapId).getOrThrow()
            require(roadmapDetail.containsLearningItem(skillId)) { "Skill not found" }

            val nowMillis = currentTimeMillis()
            val existingEntity = bookmarkDao.getSkillBookmark(skillId)
            bookmarkDao.upsertSkillBookmark(
                newSkillBookmarkEntity(
                    skillId = skillId,
                    roadmapId = roadmapId,
                    existingEntity = existingEntity,
                    nowMillis = nowMillis
                )
            )
        }
    }

    override suspend fun deleteSkill(skillId: String): Result<Unit> {
        return runCatching {
            bookmarkDao.deleteSkillBookmark(skillId)
        }
    }

    override suspend fun isSkillSaved(skillId: String): Result<Boolean> {
        return runCatching {
            bookmarkDao.isSkillSaved(skillId)
        }
    }

    private suspend fun hydrateRoadmaps(
        entities: List<RoadmapBookmarkEntity>
    ): List<RoadmapBookmark> {
        if (entities.isEmpty()) return emptyList()

        val roadmaps = roadmapRepository.searchRoadmaps("").getOrThrow()
            .associateBy { it.id }

        return entities.mapNotNull { entity ->
            roadmaps[entity.roadmapId]?.toBookmark(entity)
        }
    }

    private suspend fun hydrateSkills(
        entities: List<SkillBookmarkEntity>
    ): List<SkillBookmark> {
        if (entities.isEmpty()) return emptyList()

        val details = entities
            .map { it.roadmapId }
            .distinct()
            .mapNotNull { roadmapId ->
                roadmapRepository.getRoadmapDetail(roadmapId).getOrNull()
            }
            .associateBy { it.id }

        return entities.mapNotNull { entity ->
            details[entity.roadmapId]?.let { detail -> entity.toDomain(detail) }
        }
    }
}
