package com.rmap.mobile.core.datarefresh

import com.rmap.mobile.features.dashboard.domain.model.Dashboard
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.home.domain.model.HomeContent
import com.rmap.mobile.features.home.domain.model.HomeSearchResult
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicDataRefreshCoordinatorTest {
    @Test
    fun `roadmap started refreshes home dashboard and emits roadmap refresh target`() = runTest {
        val homeRepository = FakeHomeRepository()
        val dashboardRepository = FakeDashboardRepository()
        val coordinator = DynamicDataRefreshCoordinator(
            homeRepository = homeRepository,
            dashboardRepository = dashboardRepository
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            coordinator.refreshEvents.first()
        }

        coordinator.notifyChange(DynamicDataChange.RoadmapStarted("roadmap-1"))

        assertEquals(1, homeRepository.refreshCount)
        assertEquals(1, dashboardRepository.refreshCount)
        assertTrue(DynamicDataTarget.RoadmapDetail in event.await().targets)
        assertEquals("roadmap-1", event.await().roadmapId)
    }

    @Test
    fun `ai roadmap generated refreshes home dashboard and ai roadmap library target`() = runTest {
        val homeRepository = FakeHomeRepository()
        val dashboardRepository = FakeDashboardRepository()
        val coordinator = DynamicDataRefreshCoordinator(
            homeRepository = homeRepository,
            dashboardRepository = dashboardRepository
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            coordinator.refreshEvents.first()
        }

        coordinator.notifyChange(DynamicDataChange.AiRoadmapGenerated("ai-roadmap-1"))

        assertEquals(1, homeRepository.refreshCount)
        assertEquals(1, dashboardRepository.refreshCount)
        assertTrue(DynamicDataTarget.AiRoadmapLibrary in event.await().targets)
        assertEquals("ai-roadmap-1", event.await().roadmapId)
    }

    @Test
    fun `roadmap progress reset refreshes roadmap detail and completed skills targets`() = runTest {
        val homeRepository = FakeHomeRepository()
        val dashboardRepository = FakeDashboardRepository()
        val coordinator = DynamicDataRefreshCoordinator(
            homeRepository = homeRepository,
            dashboardRepository = dashboardRepository
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            coordinator.refreshEvents.first()
        }

        coordinator.notifyChange(DynamicDataChange.RoadmapProgressReset("roadmap-1"))

        assertEquals(1, homeRepository.refreshCount)
        assertEquals(1, dashboardRepository.refreshCount)
        assertTrue(DynamicDataTarget.RoadmapDetail in event.await().targets)
        assertTrue(DynamicDataTarget.CompletedSkills in event.await().targets)
        assertEquals("roadmap-1", event.await().roadmapId)
    }

    @Test
    fun `roadmap deleted refreshes ai roadmap library target`() = runTest {
        val homeRepository = FakeHomeRepository()
        val dashboardRepository = FakeDashboardRepository()
        val coordinator = DynamicDataRefreshCoordinator(
            homeRepository = homeRepository,
            dashboardRepository = dashboardRepository
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            coordinator.refreshEvents.first()
        }

        coordinator.notifyChange(DynamicDataChange.RoadmapDeleted("ai-roadmap-1"))

        assertEquals(1, homeRepository.refreshCount)
        assertEquals(1, dashboardRepository.refreshCount)
        assertTrue(DynamicDataTarget.AiRoadmapLibrary in event.await().targets)
        assertEquals("ai-roadmap-1", event.await().roadmapId)
    }

    @Test
    fun `refresh event still emits when repository refresh fails`() = runTest {
        val coordinator = DynamicDataRefreshCoordinator(
            homeRepository = FakeHomeRepository(shouldFail = true),
            dashboardRepository = FakeDashboardRepository(shouldFail = true)
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            coordinator.refreshEvents.first()
        }

        coordinator.notifyChange(DynamicDataChange.NodeCompleted("roadmap-1", "node-1"))

        assertEquals("roadmap-1", event.await().roadmapId)
        assertEquals("node-1", event.await().nodeId)
        assertTrue(DynamicDataTarget.CompletedSkills in event.await().targets)
    }
}

private class FakeHomeRepository(
    private val shouldFail: Boolean = false
) : HomeRepository {
    var refreshCount = 0
        private set

    override suspend fun getHomeContent(): Result<HomeContent> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun refreshHomeContent(): Result<HomeContent> {
        refreshCount += 1
        return if (shouldFail) {
            Result.failure(IllegalStateException("Home refresh failed"))
        } else {
            Result.failure(NotImplementedError())
        }
    }

    override suspend fun searchDashboard(
        query: String,
        roadmapPage: Int,
        skillPage: Int
    ): Result<HomeSearchResult> {
        return Result.failure(NotImplementedError())
    }
}

private class FakeDashboardRepository(
    private val shouldFail: Boolean = false
) : DashboardRepository {
    var refreshCount = 0
        private set

    override suspend fun getDashboard(): Result<Dashboard> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun refreshDashboard(): Result<Dashboard> {
        refreshCount += 1
        return if (shouldFail) {
            Result.failure(IllegalStateException("Dashboard refresh failed"))
        } else {
            Result.failure(NotImplementedError())
        }
    }
}
