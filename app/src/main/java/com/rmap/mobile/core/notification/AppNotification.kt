package com.rmap.mobile.core.notification

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

data class AppNotification(
    @StringRes val titleResId: Int,
    @StringRes val messageResId: Int? = null,
    val message: String? = null,
    val variant: AppNotificationVariant,
    val duration: SnackbarDuration = SnackbarDuration.Long,
    @StringRes val actionLabelResId: Int? = null,
    val action: AppNotificationAction? = null
)

enum class AppNotificationVariant {
    Success,
    Error,
    Warning,
    Info
}

enum class AppNotificationAction {
    Login
}
