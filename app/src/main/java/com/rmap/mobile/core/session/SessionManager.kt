package com.rmap.mobile.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager(
    private val clearSessionStorage: () -> Unit
) {
    private val lock = Any()
    private var hasEmittedSessionExpired = false

    private val _events = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    suspend fun handleUnauthorized() {
        clearSessionStorage()

        val shouldEmit = synchronized(lock) {
            if (hasEmittedSessionExpired) {
                false
            } else {
                hasEmittedSessionExpired = true
                true
            }
        }
        if (shouldEmit) {
            _events.emit(SessionEvent.SessionExpired)
        }
    }

    fun clearSession() {
        clearSessionStorage()
        markSessionActive()
    }

    fun markSessionActive() {
        synchronized(lock) {
            hasEmittedSessionExpired = false
        }
    }
}

sealed interface SessionEvent {
    data object SessionExpired : SessionEvent
}
