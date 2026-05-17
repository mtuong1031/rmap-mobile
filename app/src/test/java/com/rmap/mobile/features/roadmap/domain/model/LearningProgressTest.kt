package com.rmap.mobile.features.roadmap.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LearningProgressTest {
    @Test
    fun progressFraction_returnsZero_whenTotalLessonsIsZero() {
        val progress = LearningProgress(
            completedLessons = 10,
            totalLessons = 0,
            streakDays = 0,
            todayGoalCompleted = 0,
            todayGoalTotal = 0,
            completedRoadmaps = 0
        )

        assertEquals(0f, progress.progressFraction, 0f)
    }

    @Test
    fun progressFraction_clampsPartialCompleteAndOverflowValues() {
        val partial = progress(completedLessons = 25, totalLessons = 100)
        val complete = progress(completedLessons = 100, totalLessons = 100)
        val overflow = progress(completedLessons = 150, totalLessons = 100)

        assertEquals(0.25f, partial.progressFraction, 0f)
        assertEquals(1f, complete.progressFraction, 0f)
        assertEquals(1f, overflow.progressFraction, 0f)
    }

    @Test
    fun remainingLessons_neverReturnsNegativeValue() {
        val progress = progress(completedLessons = 150, totalLessons = 100)

        assertEquals(0, progress.remainingLessons)
    }

    private fun progress(completedLessons: Int, totalLessons: Int): LearningProgress {
        return LearningProgress(
            completedLessons = completedLessons,
            totalLessons = totalLessons,
            streakDays = 0,
            todayGoalCompleted = 0,
            todayGoalTotal = 0,
            completedRoadmaps = 0
        )
    }
}
