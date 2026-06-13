package com.rmap.mobile.features.widget.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class ContinueLearningWidgetCarouselTest {
    @Test
    fun next_wrapsFromLastPlanToFirst() {
        assertEquals(0, updatedCarouselIndex(4, 5, WidgetCarouselDirection.Next))
    }

    @Test
    fun previous_wrapsFromFirstPlanToLast() {
        assertEquals(4, updatedCarouselIndex(0, 5, WidgetCarouselDirection.Previous))
    }

    @Test
    fun indexIsClampedWhenPlanListShrinks() {
        assertEquals(0, updatedCarouselIndex(4, 2, WidgetCarouselDirection.Next))
        assertEquals(0, updatedCarouselIndex(4, 1, WidgetCarouselDirection.Previous))
    }
}
