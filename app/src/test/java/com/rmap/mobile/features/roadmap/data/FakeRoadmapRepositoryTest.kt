package com.rmap.mobile.features.roadmap.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeRoadmapRepositoryTest {
    private val repository = FakeRoadmapRepository()

    @Test
    fun getRoadmapDetail_returnsMatchingRoadmapById() = runTest {
        val result = repository.getRoadmapDetail("frontend-pro")

        assertTrue(result.isSuccess)
        assertEquals("frontend-pro", result.getOrThrow().id)
    }

    @Test
    fun getRoadmapDetail_returnsFailureForUnknownId() = runTest {
        val result = repository.getRoadmapDetail("unknown-roadmap")

        assertTrue(result.isFailure)
    }
}
