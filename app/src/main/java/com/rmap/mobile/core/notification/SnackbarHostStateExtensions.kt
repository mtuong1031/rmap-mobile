package com.rmap.mobile.core.notification

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.showRMapSnackbar(
    title: String,
    message: String,
    variant: AppNotificationVariant,
    actionLabel: String? = null,
    action: AppNotificationAction? = null,
    duration: SnackbarDuration = SnackbarDuration.Long,
    withDismissAction: Boolean = true
): SnackbarResult {
    return showSnackbar(
        AppSnackbarVisuals(
            title = title,
            message = message,
            variant = variant,
            action = action,
            actionLabel = actionLabel,
            duration = duration,
            withDismissAction = withDismissAction
        )
    )
}
