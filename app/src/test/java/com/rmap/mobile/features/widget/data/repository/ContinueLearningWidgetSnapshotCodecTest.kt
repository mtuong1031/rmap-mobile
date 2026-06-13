package com.rmap.mobile.features.widget.data.repository

import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.model.WidgetLearningPlan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContinueLearningWidgetSnapshotCodecTest {
    @Test
    fun versionTwoJson_roundTripsSnapshot() {
        val snapshot = ContinueLearningWidgetSnapshot(
            state = ContinueLearningWidgetState.Active,
            learningPlans = listOf(
                WidgetLearningPlan(
                    roadmapId = "roadmap-1",
                    title = "Frontend Developer",
                    progressPercent = 42,
                    completedNodes = 8,
                    totalNodes = 19
                )
            ),
            totalActiveRoadmaps = 3,
            roadmapCompletionPercent = 31,
            streakDays = 8,
            readinessPercent = 64
        )

        val decoded = ContinueLearningWidgetSnapshotCodec.decode(
            ContinueLearningWidgetSnapshotCodec.encode(snapshot)
        )

        assertEquals(snapshot, decoded)
    }

    @Test
    fun malformedJson_returnsNullForLegacyFallback() {
        assertNull(ContinueLearningWidgetSnapshotCodec.decode("{not-json"))
    }

    @Test
    fun unsupportedVersion_returnsNullForLegacyFallback() {
        assertNull(
            ContinueLearningWidgetSnapshotCodec.decode(
                """{"version":1,"snapshot":{"state":"SignedOut"}}"""
            )
        )
    }
}
