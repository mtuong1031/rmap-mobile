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

    @Test
    fun `search filters roadmaps by title or category ignoring case`() {
        val titleMatch = state(
            selectedFilter = MyRoadmapFilter.All,
            searchQuery = "backend",
            roadmaps = listOf(
                roadmap(id = "backend"),
                roadmap(id = "mobile", categoryLabel = "Mobile Development")
            )
        )
        val categoryMatch = state(
            selectedFilter = MyRoadmapFilter.All,
            searchQuery = "MOBILE",
            roadmaps = listOf(
                roadmap(id = "backend"),
                roadmap(id = "mobile", categoryLabel = "Mobile Development")
            )
        )

        assertEquals(listOf("backend"), titleMatch.visibleRoadmaps.map { it.id })
        assertEquals(listOf("mobile"), categoryMatch.visibleRoadmaps.map { it.id })
    }

    @Test
    fun `active roadmaps prioritize behind then deadline then completion`() {
        val state = state(
            selectedFilter = MyRoadmapFilter.Active,
            roadmaps = listOf(
                roadmap(id = "on-track", deadlineDate = "2026-06-15", completionPercent = 80),
                roadmap(id = "behind-later", deadlineDate = "2026-07-01", isBehind = true),
                roadmap(id = "behind-near-low", deadlineDate = "2026-06-20", completionPercent = 20, isBehind = true),
                roadmap(id = "behind-near-high", deadlineDate = "2026-06-20", completionPercent = 70, isBehind = true)
            )
        )

        assertEquals(
            listOf("behind-near-high", "behind-near-low", "behind-later", "on-track"),
            state.visibleRoadmaps.map { it.id }
        )
    }

    @Test
    fun `filters calculates counts correctly and orders All first`() {
        val state = MyRoadmapUiState(
            roadmaps = listOf(
                roadmap(id = "active-1", startedAt = "2026-06-01T00:00:00Z", completionPercent = 25, isBehind = false),
                roadmap(id = "active-2", startedAt = "2026-06-01T00:00:00Z", completionPercent = 50, isBehind = true),
                roadmap(id = "completed-1", startedAt = "2026-06-01T00:00:00Z", completionPercent = 100, isBehind = false),
                roadmap(id = "not-started", startedAt = null, completionPercent = 0, isBehind = false)
            )
        )

        assertEquals(MyRoadmapFilter.All, state.selectedFilter)

        val expectedFilters = listOf(
            MyRoadmapFilterUiModel(MyRoadmapFilter.All, 4),
            MyRoadmapFilterUiModel(MyRoadmapFilter.Active, 2),
            MyRoadmapFilterUiModel(MyRoadmapFilter.Completed, 1),
            MyRoadmapFilterUiModel(MyRoadmapFilter.Behind, 1)
        )

        assertEquals(expectedFilters, state.filters)
    }

    private fun state(
        selectedFilter: MyRoadmapFilter = MyRoadmapFilter.Active,
        searchQuery: String = "",
        roadmaps: List<MyRoadmapCardUiModel> = emptyList()
    ): MyRoadmapUiState {
        return MyRoadmapUiState(
            selectedFilter = selectedFilter,
            searchQuery = searchQuery,
            roadmaps = roadmaps
        )
    }

    private fun roadmap(
        id: String,
        startedAt: String? = "2026-06-01T00:00:00Z",
        completionPercent: Int = 10,
        isBehind: Boolean = false,
        categoryLabel: String = "Web Development",
        deadlineDate: String? = null
    ): MyRoadmapCardUiModel {
        return MyRoadmapCardUiModel(
            id = id,
            title = "Roadmap $id",
            categoryKey = "WEB_DEVELOPMENT",
            categoryLabel = categoryLabel,
            isTemplate = false,
            completionPercent = completionPercent,
            nodesCompleted = 1,
            nodesTotal = 10,
            deadlineDate = deadlineDate,
            estimatedWeeks = null,
            startedAt = startedAt,
            isBehind = isBehind
        )
    }
}
