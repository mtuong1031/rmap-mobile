package com.rmap.mobile.core.notification

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppNotificationManager {
    private val _notifications = MutableSharedFlow<AppNotification>(
        extraBufferCapacity = NOTIFICATION_BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val notifications: SharedFlow<AppNotification> = _notifications.asSharedFlow()

    fun enqueue(notification: AppNotification) {
        _notifications.tryEmit(notification)
    }

    private companion object {
        const val NOTIFICATION_BUFFER_CAPACITY = 8
    }
}
