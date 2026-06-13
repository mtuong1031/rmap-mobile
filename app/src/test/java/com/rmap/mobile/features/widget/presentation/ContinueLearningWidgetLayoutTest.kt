package com.rmap.mobile.features.widget.presentation

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContinueLearningWidgetLayoutTest {
    @Test
    fun compactWidget_usesShortHeaderAndHidesOptionalContent() {
        val layout = continueLearningWidgetLayout(
            width = 180.dp,
            height = 110.dp
        )

        assertFalse(layout.showFullTitle)
        assertFalse(layout.showSupportingText)
        assertFalse(layout.showActiveAction)
        assertEquals(10.dp, layout.contentPadding)
        assertEquals(8.dp, layout.emptyActionSpacing)
    }

    @Test
    fun standardWidget_showsFullContentAndAction() {
        val layout = continueLearningWidgetLayout(
            width = 373.dp,
            height = 210.dp
        )

        assertTrue(layout.showFullTitle)
        assertTrue(layout.showSupportingText)
        assertTrue(layout.showActiveAction)
        assertEquals(16.dp, layout.contentPadding)
        assertEquals(40.dp, layout.emptyActionSpacing)
    }

    @Test
    fun shortWideWidget_doesNotShowActionThatWouldBeClipped() {
        val layout = continueLearningWidgetLayout(
            width = 373.dp,
            height = 150.dp
        )

        assertTrue(layout.showFullTitle)
        assertFalse(layout.showSupportingText)
        assertFalse(layout.showActiveAction)
    }
}
