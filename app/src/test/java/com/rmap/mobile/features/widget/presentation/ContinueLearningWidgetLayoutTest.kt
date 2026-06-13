package com.rmap.mobile.features.widget.presentation

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContinueLearningWidgetLayoutTest {
    @Test
    fun tinyWidget_onlyShowsEssentialHeroContent() {
        val layout = continueLearningWidgetLayout(180.dp, 110.dp)

        assertEquals(WidgetLayoutTier.Tiny, layout.tier)
        assertFalse(layout.showStats)
        assertFalse(layout.showAction)
        assertFalse(layout.showHeroDetails)
    }

    @Test
    fun compactWidget_showsHeroAndSegmentedStats() {
        val layout = continueLearningWidgetLayout(360.dp, 210.dp)

        assertEquals(WidgetLayoutTier.Compact, layout.tier)
        assertTrue(layout.showStats)
        assertFalse(layout.showStatCards)
        assertFalse(layout.showAction)
    }

    @Test
    fun standardWidget_showsCtaDotsAndStatCards() {
        val layout = continueLearningWidgetLayout(360.dp, 280.dp)

        assertEquals(WidgetLayoutTier.Standard, layout.tier)
        assertTrue(layout.showAction)
        assertTrue(layout.showDots)
        assertTrue(layout.showStatCards)
        assertFalse(layout.showNextUnlock)
    }

    @Test
    fun expandedWidget_showsNextUnlock() {
        val layout = continueLearningWidgetLayout(360.dp, 400.dp)

        assertEquals(WidgetLayoutTier.Expanded, layout.tier)
        assertTrue(layout.showNextUnlock)
    }
}
