package com.rmap.mobile.core.notification

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class AppSnackbarVisuals(
    val title: String,
    override val message: String,
    val variant: AppNotificationVariant,
    val action: AppNotificationAction? = null,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Long,
    override val withDismissAction: Boolean = true
) : SnackbarVisuals
