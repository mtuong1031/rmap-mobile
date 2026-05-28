package com.rmap.mobile.core.session

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {
    @Test
    fun `handleUnauthorized clears session signs out and emits one session expired event`() = runTest {
        var clearCount = 0
        val events = mutableListOf<SessionEvent>()
        val manager = SessionManager(
            clearSessionStorage = { clearCount++ }
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.events.collect { event -> events.add(event) }
        }

        manager.handleUnauthorized()
        manager.handleUnauthorized()
        runCurrent()

        assertEquals(2, clearCount)
        assertEquals(listOf(SessionEvent.SessionExpired), events)
    }

    @Test
    fun `markSessionActive allows future session expired event`() = runTest {
        val events = mutableListOf<SessionEvent>()
        val manager = SessionManager(
            clearSessionStorage = {}
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            manager.events.collect { event -> events.add(event) }
        }

        manager.handleUnauthorized()
        manager.markSessionActive()
        manager.handleUnauthorized()
        runCurrent()

        assertEquals(
            listOf(SessionEvent.SessionExpired, SessionEvent.SessionExpired),
            events
        )
    }
}
