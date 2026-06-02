package com.rmap.mobile.features.bookmarks.data.repository

import com.rmap.mobile.features.bookmarks.data.local.BookmarkDao
import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity
import com.rmap.mobile.features.bookmarks.domain.model.RoadmapBookmarkSnapshot
import com.rmap.mobile.features.bookmarks.domain.model.SkillBookmarkSnapshot
import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomBookmarkRepositoryTest {
    private val bookmarkDao = FakeBookmarkDao()
    private val roadmapRepository = FakeRoadmapRepository()
    private val repository = RoomBookmarkRepository(
        bookmarkDao = bookmarkDao,
        roadmapRepository = roadmapRepository,
        currentTimeMillis = { 10L }
    )

    @Test
    fun `save roadmap with known id hydrates saved roadmap`() = runTest {
        val result = repository.saveRoadmap("frontend-pro")

        assertTrue(result.isSuccess)
        val bookmarks = repository.getSavedRoadmaps().getOrThrow()
        assertEquals(1, bookmarks.size)
        assertEquals("frontend-pro", bookmarks.single().summary.id)
    }

    @Test
    fun `save roadmap with unknown id fails without inserting`() = runTest {
        val result = repository.saveRoadmap("missing-roadmap")

        assertTrue(result.isFailure)
        assertFalse(bookmarkDao.isRoadmapSaved("missing-roadmap"))
        assertTrue(repository.getSavedRoadmaps().getOrThrow().isEmpty())
    }

    @Test
    fun `save roadmap snapshot hydrates saved roadmap when id is missing from source repository`() = runTest {
        val result = repository.saveRoadmap(
            RoadmapBookmarkSnapshot(
                roadmapId = "api-roadmap",
                title = "API Roadmap",
                categoryId = "WEB_DEVELOPMENT",
                categoryLabel = "Web Development",
                nodesTotal = 42,
                durationLabel = "Self-paced",
                iconKey = LearningTopicIcon.Code.name
            )
        )

        assertTrue(result.isSuccess)
        val bookmark = repository.getSavedRoadmaps().getOrThrow().single()
        assertEquals("api-roadmap", bookmark.summary.id)
        assertEquals("API Roadmap", bookmark.summary.title)
        assertEquals(42, bookmark.summary.totalLessonsCount)
    }

    @Test
    fun `save skill with known stable id hydrates saved skill`() = runTest {
        val skillId = "Async JS".toStableLearningId()

        val result = repository.saveSkill(
            skillId = skillId,
            roadmapId = "frontend-pro"
        )

        assertTrue(result.isSuccess)
        val bookmarks = repository.getSavedSkills().getOrThrow()
        assertEquals(1, bookmarks.size)
        assertEquals(skillId, bookmarks.single().skillId)
        assertEquals("Async JS", bookmarks.single().title)
        assertEquals("Frontend", bookmarks.single().parentPathName)
    }

    @Test
    fun `save skill with unknown id fails without inserting`() = runTest {
        val result = repository.saveSkill(
            skillId = "missing-skill",
            roadmapId = "frontend-pro"
        )

        assertTrue(result.isFailure)
        assertFalse(bookmarkDao.isSkillSaved("missing-skill"))
        assertTrue(repository.getSavedSkills().getOrThrow().isEmpty())
    }

    @Test
    fun `save skill snapshot hydrates saved skill without roadmap detail match`() = runTest {
        val result = repository.saveSkill(
            SkillBookmarkSnapshot(
                skillId = "api-skill",
                title = "FULL OUTER JOIN",
                categoryId = "LANGUAGES_AND_PLATFORMS",
                categoryLabel = "Languages And Platforms",
                iconKey = LearningTopicIcon.Terminal.name
            )
        )

        assertTrue(result.isSuccess)
        val bookmark = repository.getSavedSkills().getOrThrow().single()
        assertEquals("api-skill", bookmark.skillId)
        assertEquals("FULL OUTER JOIN", bookmark.title)
        assertEquals("Languages And Platforms", bookmark.parentPathName)
        assertEquals(LearningStatus.NotStarted, bookmark.status)
        assertEquals(LearningTopicIcon.Terminal, bookmark.icon)
    }
}

private class FakeBookmarkDao : BookmarkDao {
    private val roadmaps = MutableStateFlow<List<RoadmapBookmarkEntity>>(emptyList())
    private val skills = MutableStateFlow<List<SkillBookmarkEntity>>(emptyList())

    override fun observeRoadmapBookmarks(): Flow<List<RoadmapBookmarkEntity>> = roadmaps

    override fun observeSkillBookmarks(): Flow<List<SkillBookmarkEntity>> = skills

    override suspend fun getRoadmapBookmarks(): List<RoadmapBookmarkEntity> = roadmaps.value

    override suspend fun getSkillBookmarks(): List<SkillBookmarkEntity> = skills.value

    override suspend fun getRoadmapBookmark(roadmapId: String): RoadmapBookmarkEntity? {
        return roadmaps.value.firstOrNull { it.roadmapId == roadmapId }
    }

    override suspend fun getSkillBookmark(skillId: String): SkillBookmarkEntity? {
        return skills.value.firstOrNull { it.skillId == skillId }
    }

    override suspend fun upsertRoadmapBookmark(entity: RoadmapBookmarkEntity) {
        roadmaps.value = roadmaps.value.filterNot { it.roadmapId == entity.roadmapId } + entity
    }

    override suspend fun upsertSkillBookmark(entity: SkillBookmarkEntity) {
        skills.value = skills.value.filterNot { it.skillId == entity.skillId } + entity
    }

    override suspend fun deleteRoadmapBookmark(roadmapId: String) {
        roadmaps.value = roadmaps.value.filterNot { it.roadmapId == roadmapId }
    }

    override suspend fun deleteSkillBookmark(skillId: String) {
        skills.value = skills.value.filterNot { it.skillId == skillId }
    }

    override suspend fun isRoadmapSaved(roadmapId: String): Boolean {
        return roadmaps.value.any { it.roadmapId == roadmapId }
    }

    override suspend fun isSkillSaved(skillId: String): Boolean {
        return skills.value.any { it.skillId == skillId }
    }
}

private class FakeRoadmapRepository : RoadmapRepository {
    private val summary = RoadmapSummary(
        id = "frontend-pro",
        title = "Frontend Pro",
        totalLessonsCount = 8,
        completedLessonsCount = 4,
        difficulty = LearningDifficulty.Intermediate,
        durationLabel = "3 months",
        icon = LearningTopicIcon.Code,
        categoryId = "frontend"
    )

    private val detail = RoadmapDetail(
        id = "frontend-pro",
        title = "Frontend Pro",
        completedLessons = 1,
        totalLessons = 3,
        sections = listOf(
            LearningModuleSection(
                title = "Core",
                modules = listOf(
                    LearningModule(
                        title = "JavaScript",
                        status = LearningStatus.InProgress,
                        progressPercent = 50,
                        icon = LearningTopicIcon.Code,
                        subLessons = listOf(
                            SubLesson("Async JS", LearningStatus.InProgress),
                            SubLesson("DOM Manipulation", LearningStatus.Locked)
                        )
                    )
                )
            )
        ),
        aiTip = AiScholarTip(
            currentModule = "JavaScript",
            recommendedTopic = "Promises",
            nextModule = "DOM Manipulation"
        )
    )

    override suspend fun getLearningProgress(): Result<LearningProgress> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> {
        return Result.success(listOf(summary))
    }

    override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
        return Result.success(listOf(summary))
    }

    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
        return if (id == detail.id) {
            Result.success(detail)
        } else {
            Result.failure(IllegalArgumentException("Roadmap not found"))
        }
    }
}
