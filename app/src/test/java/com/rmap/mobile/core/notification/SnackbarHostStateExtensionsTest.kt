package com.rmap.mobile.core.notification

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SnackbarHostStateExtensionsTest {
    @Test
    fun `showRMapSnackbar preserves typed visuals`() = runTest {
        val hostState = SnackbarHostState()
        val showJob = backgroundScope.launch {
            hostState.showRMapSnackbar(
                title = "Roadmap saved",
                message = "Your progress is up to date.",
                variant = AppNotificationVariant.Success,
                actionLabel = "View"
            )
        }

        runCurrent()

        val visuals = hostState.currentSnackbarData?.visuals as AppSnackbarVisuals
        assertEquals("Roadmap saved", visuals.title)
        assertEquals("Your progress is up to date.", visuals.message)
        assertEquals(AppNotificationVariant.Success, visuals.variant)
        assertEquals("View", visuals.actionLabel)

        hostState.currentSnackbarData?.dismiss()
        showJob.join()
    }
}
