package com.rmap.mobile.features.widget.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.home.domain.model.HomeActiveRoadmap
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeMetrics
import com.rmap.mobile.features.home.domain.model.HomePaceWarning
import com.rmap.mobile.features.home.domain.model.HomePlanNode
import com.rmap.mobile.features.home.domain.model.HomeRoadmapChapter
import com.rmap.mobile.features.home.domain.model.HomeRoadmapProgress
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.repository.ContinueLearningWidgetRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateContinueLearningWidgetUseCaseTest {
    private val repository = FakeContinueLearningWidgetRepository()
    private val useCase = UpdateContinueLearningWidgetUseCase(repository)

    @Test
    fun invoke_savesActiveRoadmapSnapshot_whenUserIsAuthenticated() = runTest {
        val result = useCase(
            authState = authenticatedState(),
            homeContent = homeContent(activeRoadmaps = listOf(activeRoadmap()))
        )

        assertTrue(result.isSuccess)
        assertEquals(
            ContinueLearningWidgetSnapshot(
                state = ContinueLearningWidgetState.Active,
                roadmapId = "roadmap-1",
                roadmapTitle = "Android Developer",
                currentSkillTitle = "Jetpack Compose",
                chapterLabel = "Chapter 2 of 5",
                progressPercent = 63,
                completedNodes = 5,
                totalNodes = 8,
                streakDays = 7,
                isBehind = true
            ),
            repository.storedSnapshot
        )
        assertEquals(1, repository.saveCount)
    }

    @Test
    fun invoke_savesEmptySnapshot_whenUserHasNoActiveRoadmap() = runTest {
        val result = useCase(
            authState = authenticatedState(),
            homeContent = homeContent(activeRoadmaps = emptyList())
        )

        assertTrue(result.isSuccess)
        assertEquals(ContinueLearningWidgetState.Empty, repository.storedSnapshot.state)
        assertEquals(7, repository.storedSnapshot.streakDays)
        assertEquals(1, repository.saveCount)
    }

    @Test
    fun invoke_savesSignedOutSnapshot_whenUserIsUnauthenticated() = runTest {
        repository.storedSnapshot = activeSnapshot()

        val result = useCase(
            authState = AuthState.Unauthenticated,
            homeContent = null
        )

        assertTrue(result.isSuccess)
        assertEquals(ContinueLearningWidgetSnapshot.SignedOut, repository.storedSnapshot)
        assertEquals(1, repository.saveCount)
    }

    @Test
    fun invoke_preservesSnapshot_whileAuthenticationIsBeingChecked() = runTest {
        val existingSnapshot = activeSnapshot()
        repository.storedSnapshot = existingSnapshot

        val result = useCase(
            authState = AuthState.Checking,
            homeContent = null
        )

        assertTrue(result.isSuccess)
        assertSame(existingSnapshot, result.getOrNull())
        assertSame(existingSnapshot, repository.storedSnapshot)
        assertEquals(0, repository.saveCount)
    }

    @Test
    fun invoke_preservesSnapshot_whenAuthenticatedHomeContentIsUnavailable() = runTest {
        val existingSnapshot = activeSnapshot()
        repository.storedSnapshot = existingSnapshot

        val result = useCase(
            authState = authenticatedState(),
            homeContent = null
        )

        assertTrue(result.isSuccess)
        assertSame(existingSnapshot, result.getOrNull())
        assertFalse(repository.storedSnapshot == ContinueLearningWidgetSnapshot.SignedOut)
        assertEquals(0, repository.saveCount)
    }

    private fun authenticatedState(): AuthState.Authenticated {
        return AuthState.Authenticated(
            User(
                id = "user-1",
                email = "learner@example.com",
                fullName = "RMap Learner",
                avatarUrl = null,
                role = "user",
                createdAt = "2026-06-13T00:00:00Z"
            )
        )
    }

    private fun homeContent(activeRoadmaps: List<HomeActiveRoadmap>): HomeContent {
        return HomeContent(
            activeRoadmaps = activeRoadmaps,
            metrics = HomeMetrics(
                roadmapCompletionPct = 62.5,
                streakDays = 7,
                readinessPct = 40.0
            ),
            recommendations = emptyList(),
            beginners = emptyList(),
            trendings = emptyList()
        )
    }

    private fun activeRoadmap(): HomeActiveRoadmap {
        return HomeActiveRoadmap(
            roadmapId = "roadmap-1",
            title = "Android Developer",
            goalName = "Android Engineer",
            roleCategory = "mobile",
            startedAt = "2026-06-01T00:00:00Z",
            currentGroup = null,
            planNode = HomePlanNode(
                id = "node-1",
                name = "Jetpack Compose",
                description = null,
                nodeType = "lesson",
                estimatedHours = 4.0
            ),
            chapter = HomeRoadmapChapter(
                current = 2,
                total = 5,
                label = "Chapter 2 of 5"
            ),
            progress = HomeRoadmapProgress(
                requiredNodesCompleted = 5,
                requiredNodesTotal = 8,
                requiredCompletionPct = 62.5
            ),
            nextUnlock = null,
            paceWarning = HomePaceWarning(
                isBehind = true,
                paceDeficitPct = 10.0,
                estimatedDelayDays = 2,
                message = "Pick up the pace",
                title = "Behind schedule",
                actionLabel = "Continue"
            )
        )
    }

    private fun activeSnapshot(): ContinueLearningWidgetSnapshot {
        return ContinueLearningWidgetSnapshot(
            state = ContinueLearningWidgetState.Active,
            roadmapId = "roadmap-existing",
            roadmapTitle = "Existing roadmap"
        )
    }
}

private class FakeContinueLearningWidgetRepository : ContinueLearningWidgetRepository {
    var storedSnapshot = ContinueLearningWidgetSnapshot.SignedOut
    var saveCount = 0

    override fun getSnapshot(): ContinueLearningWidgetSnapshot = storedSnapshot

    override suspend fun saveSnapshot(
        snapshot: ContinueLearningWidgetSnapshot
    ): Result<Unit> {
        storedSnapshot = snapshot
        saveCount += 1
        return Result.success(Unit)
    }
}
