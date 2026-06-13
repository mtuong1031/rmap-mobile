package com.rmap.mobile.core.datarefresh

import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface DynamicDataChange {
    data class RoadmapStarted(val roadmapId: String) : DynamicDataChange
    data class NodeCompleted(val roadmapId: String, val nodeId: String) : DynamicDataChange
    data class NodeProgressChanged(val roadmapId: String, val nodeId: String) : DynamicDataChange
    data class AiRoadmapGenerated(val roadmapId: String) : DynamicDataChange
    data class MilestoneSubmitted(val roadmapId: String, val milestoneId: String) : DynamicDataChange
    data class RoadmapProgressReset(val roadmapId: String) : DynamicDataChange
    data class RoadmapDeleted(val roadmapId: String) : DynamicDataChange
}

enum class DynamicDataTarget {
    Home,
    Dashboard,
    MyRoadmap,
    RoadmapDetail,
    LearningProgress,
    CompletedSkills,
    AiRoadmapLibrary,
    ReminderContext
}

data class DynamicDataRefreshEvent(
    val change: DynamicDataChange,
    val targets: Set<DynamicDataTarget>,
    val roadmapId: String? = null,
    val nodeId: String? = null
)

class DynamicDataRefreshCoordinator(
    private val homeRepository: HomeRepository,
    private val dashboardRepository: DashboardRepository
) {
    private val _refreshEvents = MutableSharedFlow<DynamicDataRefreshEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val refreshEvents: SharedFlow<DynamicDataRefreshEvent> = _refreshEvents.asSharedFlow()

    suspend fun notifyChange(change: DynamicDataChange) {
        val event = change.toRefreshEvent()
        refreshDataTargets(event.targets)
        _refreshEvents.emit(event)
    }

    private suspend fun refreshDataTargets(targets: Set<DynamicDataTarget>) = coroutineScope {
        val jobs = buildList {
            if (DynamicDataTarget.Home in targets) {
                add(async { runCatching { homeRepository.refreshHomeContent() } })
            }
            if (targets.any { it in dashboardBackedTargets }) {
                add(async { runCatching { dashboardRepository.refreshDashboard() } })
            }
        }

        jobs.awaitAll()
    }

    private fun DynamicDataChange.toRefreshEvent(): DynamicDataRefreshEvent {
        return when (this) {
            is DynamicDataChange.RoadmapStarted -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.RoadmapDetail,
                    DynamicDataTarget.LearningProgress,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId
            )

            is DynamicDataChange.NodeCompleted -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.RoadmapDetail,
                    DynamicDataTarget.LearningProgress,
                    DynamicDataTarget.CompletedSkills,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId,
                nodeId = nodeId
            )

            is DynamicDataChange.NodeProgressChanged -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.RoadmapDetail,
                    DynamicDataTarget.LearningProgress,
                    DynamicDataTarget.CompletedSkills,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId,
                nodeId = nodeId
            )

            is DynamicDataChange.AiRoadmapGenerated -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.AiRoadmapLibrary,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId
            )

            is DynamicDataChange.MilestoneSubmitted -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.RoadmapDetail
                ),
                roadmapId = roadmapId,
                nodeId = milestoneId
            )

            is DynamicDataChange.RoadmapProgressReset -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.RoadmapDetail,
                    DynamicDataTarget.LearningProgress,
                    DynamicDataTarget.CompletedSkills,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId
            )

            is DynamicDataChange.RoadmapDeleted -> DynamicDataRefreshEvent(
                change = this,
                targets = setOf(
                    DynamicDataTarget.Home,
                    DynamicDataTarget.Dashboard,
                    DynamicDataTarget.MyRoadmap,
                    DynamicDataTarget.AiRoadmapLibrary,
                    DynamicDataTarget.ReminderContext
                ),
                roadmapId = roadmapId
            )
        }
    }

    private companion object {
        val dashboardBackedTargets = setOf(
            DynamicDataTarget.Dashboard,
            DynamicDataTarget.MyRoadmap,
            DynamicDataTarget.LearningProgress,
            DynamicDataTarget.CompletedSkills,
            DynamicDataTarget.AiRoadmapLibrary,
            DynamicDataTarget.ReminderContext
        )
    }
}
