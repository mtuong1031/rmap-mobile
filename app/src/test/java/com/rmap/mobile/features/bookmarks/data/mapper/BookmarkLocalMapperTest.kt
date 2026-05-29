package com.rmap.mobile.features.bookmarks.data.mapper

import com.rmap.mobile.features.bookmarks.data.local.RoadmapBookmarkEntity
import com.rmap.mobile.features.bookmarks.data.local.SkillBookmarkEntity
import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.model.toStableLearningId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmarkLocalMapperTest {
    @Test
    fun `new roadmap bookmark entity stores only identity and timestamps`() {
        val entity = newRoadmapBookmarkEntity(
            roadmapId = "frontend-pro",
            existingEntity = null,
            nowMillis = 10L
        )

        assertEquals("frontend-pro", entity.roadmapId)
        assertEquals(10L, entity.savedAtMillis)
        assertEquals(10L, entity.updatedAtMillis)
    }

    @Test
    fun `roadmap bookmark status is derived from backend summary progress`() {
        val entity = RoadmapBookmarkEntity(
            roadmapId = "frontend-pro",
            savedAtMillis = 1L,
            updatedAtMillis = 2L
        )
        val summary = RoadmapSummary(
            id = "frontend-pro",
            title = "Frontend Pro",
            totalLessonsCount = 8,
            completedLessonsCount = 4,
            difficulty = LearningDifficulty.Intermediate,
            durationLabel = "3 months",
            icon = LearningTopicIcon.Code,
            categoryId = "frontend"
        )

        val bookmark = summary.toBookmark(entity)

        assertEquals(LearningStatus.InProgress, bookmark.status)
        assertEquals(summary, bookmark.summary)
        assertEquals(1L, bookmark.savedAtMillis)
        assertEquals(2L, bookmark.updatedAtMillis)
    }

    @Test
    fun `skill bookmark hydrates display data from roadmap detail`() {
        val entity = SkillBookmarkEntity(
            skillId = "async-js",
            roadmapId = "frontend-pro",
            savedAtMillis = 1L,
            updatedAtMillis = 2L
        )

        val bookmark = entity.toDomain(frontendDetail())

        assertEquals("Async JS", bookmark?.title)
        assertEquals("Frontend Pro", bookmark?.parentPathName)
        assertEquals(LearningStatus.InProgress, bookmark?.status)
        assertEquals(LearningTopicIcon.Code, bookmark?.icon)
    }

    @Test
    fun `locked skill status becomes not started in bookmark domain`() {
        val entity = SkillBookmarkEntity(
            skillId = "dom-manipulation",
            roadmapId = "frontend-pro",
            savedAtMillis = 1L,
            updatedAtMillis = 2L
        )

        val bookmark = entity.toDomain(frontendDetail())

        assertEquals(LearningStatus.NotStarted, bookmark?.status)
    }

    @Test
    fun `unknown skill id is skipped during hydration`() {
        val entity = SkillBookmarkEntity(
            skillId = "missing",
            roadmapId = "frontend-pro",
            savedAtMillis = 1L,
            updatedAtMillis = 2L
        )

        assertNull(entity.toDomain(frontendDetail()))
    }

    @Test
    fun `stable learning id matches skill bookmark lookup`() {
        val entity = SkillBookmarkEntity(
            skillId = "Async JS".toStableLearningId(),
            roadmapId = "frontend-pro",
            savedAtMillis = 1L,
            updatedAtMillis = 2L
        )

        val bookmark = entity.toDomain(frontendDetail())

        assertEquals("Async JS", bookmark?.title)
    }

    private fun frontendDetail(): RoadmapDetail {
        return RoadmapDetail(
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
    }
}
