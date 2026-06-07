package com.rmap.mobile.features.myroadmap.presentation.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MyRoadmapUiStateTest {
    @Test
    fun `active filter shows started incomplete roadmaps`() {
        val state = state(
            selectedFilter = MyRoadmapFilter.Active,
            roadmaps = listOf(
                roadmap(id = "active", startedAt = "2026-06-01T00:00:00Z", completionPercent = 25),
                roadmap(id = "not-started", startedAt = null, completionPercent = 0),
                roadmap(id = "completed", startedAt = "2026-06-01T00:00:00Z", completionPercent = 100)
            )
        )

        assertEquals(listOf("active"), state.visibleRoadmaps.map { it.id })
    }

    @Test
    fun `all filter shows every roadmap`() {
        val state = state(
            selectedFilter = MyRoadmapFilter.All,
            roadmaps = listOf(
                roadmap(id = "active"),
                roadmap(id = "completed", completionPercent = 100)
            )
        )

        assertEquals(listOf("active", "completed"), state.visibleRoadmaps.map { it.id })
    }

    @Test
    fun `completed and behind filters use completion and timeline warning`() {
        val completedState = state(
            selectedFilter = MyRoadmapFilter.Completed,
            roadmaps = listOf(
                roadmap(id = "active", completionPercent = 99),
                roadmap(id = "completed", completionPercent = 100)
            )
        )
        val behindState = state(
            selectedFilter = MyRoadmapFilter.Behind,
            roadmaps = listOf(
                roadmap(id = "on-track", isBehind = false),
                roadmap(id = "behind", isBehind = true)
            )
        )

        assertEquals(listOf("completed"), completedState.visibleRoadmaps.map { it.id })
        assertEquals(listOf("behind"), behindState.visibleRoadmaps.map { it.id })
    }

    @Test
    fun `empty states are represented by empty visible roadmaps`() {
        val noRoadmapState = state(roadmaps = emptyList())
        val filteredEmptyState = state(
            selectedFilter = MyRoadmapFilter.Behind,
            roadmaps = listOf(roadmap(id = "on-track", isBehind = false))
        )

        assertTrue(noRoadmapState.visibleRoadmaps.isEmpty())
        assertTrue(filteredEmptyState.visibleRoadmaps.isEmpty())
    }

    private fun state(
        selectedFilter: MyRoadmapFilter = MyRoadmapFilter.Active,
        roadmaps: List<MyRoadmapCardUiModel>
    ): MyRoadmapUiState {
        return MyRoadmapUiState(
            selectedFilter = selectedFilter,
            roadmaps = roadmaps
        )
    }

    private fun roadmap(
        id: String,
        startedAt: String? = "2026-06-01T00:00:00Z",
        completionPercent: Int = 10,
        isBehind: Boolean = false
    ): MyRoadmapCardUiModel {
        return MyRoadmapCardUiModel(
            id = id,
            title = "Roadmap $id",
            categoryKey = "WEB_DEVELOPMENT",
            categoryLabel = "Web Development",
            isTemplate = false,
            completionPercent = completionPercent,
            nodesCompleted = 1,
            nodesTotal = 10,
            deadlineDate = null,
            estimatedWeeks = null,
            startedAt = startedAt,
            isBehind = isBehind
        )
    }
}
