package com.rmap.mobile.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PendingProtectedActionStore {
    private val _pendingAction = MutableStateFlow<PendingProtectedAction?>(null)
    val pendingAction: StateFlow<PendingProtectedAction?> = _pendingAction.asStateFlow()

    fun store(action: PendingProtectedAction) {
        _pendingAction.value = action
    }

    fun consume(action: PendingProtectedAction): Boolean {
        return if (_pendingAction.value == action) {
            _pendingAction.value = null
            true
        } else {
            false
        }
    }

    fun clear() {
        _pendingAction.value = null
    }

    fun clear(action: PendingProtectedAction) {
        if (_pendingAction.value == action) {
            _pendingAction.value = null
        }
    }
}
