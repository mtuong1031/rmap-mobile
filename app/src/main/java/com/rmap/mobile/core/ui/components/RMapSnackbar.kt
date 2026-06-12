package com.rmap.mobile.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.notification.AppNotificationAction
import com.rmap.mobile.core.notification.AppNotificationVariant
import com.rmap.mobile.core.notification.AppSnackbarVisuals
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppShadows
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.LocalRMapSemanticColors
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.SemanticColorSet
import com.rmap.mobile.core.ui.theme.cardShadow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class RMapSnackbarColors(
    val accent: Color,
    val container: Color,
    val content: Color,
    val border: Color
)

object RMapSnackbarDefaults {
    val shape = AppShapes.card
    val borderWidth = Dimens.borderThin
    val iconSize = Dimens.iconMdPlus
    val dismissIconSize = Dimens.iconSmPlus
    val swipeHandleWidth = 32.dp
    val swipeHandleHeight = 4.dp
    const val DISMISS_THRESSHOLD = 100f

    @Composable
    fun colors(variant: AppNotificationVariant): RMapSnackbarColors {
        val semanticColors = LocalRMapSemanticColors.current
        val colorSet = when (variant) {
            AppNotificationVariant.Success -> semanticColors.success
            AppNotificationVariant.Error -> semanticColors.error
            AppNotificationVariant.Warning -> semanticColors.warning
            AppNotificationVariant.Info -> semanticColors.info
        }
        return colorSet.toSnackbarColors()
    }

    fun icon(variant: AppNotificationVariant): ImageVector {
        return when (variant) {
            AppNotificationVariant.Success -> Icons.Outlined.CheckCircle
            AppNotificationVariant.Error -> Icons.Outlined.ErrorOutline
            AppNotificationVariant.Warning -> Icons.Outlined.WarningAmber
            AppNotificationVariant.Info -> Icons.Outlined.Info
        }
    }

    fun actionIcon(action: AppNotificationAction?, label: String?): ImageVector? {
        if (label == null) return null
        return when (action) {
            AppNotificationAction.Login -> Icons.AutoMirrored.Outlined.Login
            else -> {
                val normalizedLabel = label.lowercase()
                when {
                    "retry" in normalizedLabel -> Icons.Outlined.Refresh
                    "view" in normalizedLabel || "open" in normalizedLabel -> Icons.AutoMirrored.Outlined.OpenInNew
                    else -> null
                }
            }
        }
    }
}

@Composable
fun RMapSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val sourceVisuals = snackbarData.visuals
    val visuals = sourceVisuals as? AppSnackbarVisuals ?: AppSnackbarVisuals(
        title = "",
        message = sourceVisuals.message,
        variant = AppNotificationVariant.Info,
        actionLabel = sourceVisuals.actionLabel,
        duration = sourceVisuals.duration,
        withDismissAction = sourceVisuals.withDismissAction
    )

    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    val draggableState = rememberDraggableState { delta ->
        coroutineScope.launch {
            // Only allow dragging down
            if (offsetY.value + delta >= 0) {
                offsetY.snapTo(offsetY.value + delta)
            }
        }
    }

    Box(
        modifier = modifier
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    if (offsetY.value > RMapSnackbarDefaults.DISMISS_THRESSHOLD || velocity > 500f) {
                        coroutineScope.launch {
                            offsetY.animateTo(targetValue = 500f)
                            snackbarData.dismiss()
                        }
                    } else {
                        coroutineScope.launch {
                            offsetY.animateTo(targetValue = 0f)
                        }
                    }
                }
            )
            .alpha((1f - (offsetY.value / 500f)).coerceIn(0f, 1f))
    ) {
        RMapSnackbarContent(
            visuals = visuals,
            onAction = snackbarData::performAction,
            onDismiss = snackbarData::dismiss
        )
    }
}

@Composable
internal fun RMapSnackbarContent(
    visuals: AppSnackbarVisuals,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RMapSnackbarDefaults.colors(visuals.variant)
    val hasAction = !visuals.actionLabel.isNullOrBlank()
    val actionIcon = RMapSnackbarDefaults.actionIcon(visuals.action, visuals.actionLabel)

    Surface(
        modifier = modifier
            .padding(horizontal = Dimens.spacingLg, vertical = Dimens.spacingMd)
            .fillMaxWidth()
            .cardShadow(
                shape = RMapSnackbarDefaults.shape,
                layers = AppShadows.card
            )
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RMapSnackbarDefaults.shape,
        color = colors.container,
        contentColor = colors.content,
        border = BorderStroke(RMapSnackbarDefaults.borderWidth, colors.border.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Nội dung chính - căn giữa theo chiều dọc trong Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(
                        start = Dimens.spacingMdPlus,
                        end = Dimens.spacingSm,
                        top = Dimens.spacingMd,
                        bottom = Dimens.spacingMd
                    ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RMapSnackbarDefaults.icon(visuals.variant),
                    contentDescription = null,
                    modifier = Modifier.size(RMapSnackbarDefaults.iconSize),
                    tint = colors.accent
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = visuals.message,
                        style = AppTextStyles.titleMediumStrong.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.content
                    )
                }

                if (!hasAction && visuals.withDismissAction) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(Dimens.controlSm)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.snackbar_dismiss_content_description),
                            modifier = Modifier.size(RMapSnackbarDefaults.dismissIconSize),
                            tint = colors.content.copy(alpha = 0.5f)
                        )
                    }
                } else if (hasAction) {
                    TextButton(
                        onClick = onAction,
                        contentPadding = PaddingValues(horizontal = Dimens.spacingSm),
                        modifier = Modifier.height(Dimens.controlSm)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                        ) {
                            actionIcon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimens.iconSm),
                                    tint = colors.accent
                                )
                            }
                            Text(
                                text = visuals.actionLabel.uppercase(),
                                style = AppTextStyles.snackbarAction.copy(
                                    color = colors.accent
                                )
                            )
                        }
                    }
                }
            }

            // Swipe handle - dính đáy, căn giữa theo chiều ngang
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.spacingXs)
                    .width(RMapSnackbarDefaults.swipeHandleWidth)
                    .height(RMapSnackbarDefaults.swipeHandleHeight)
                    .clip(AppShapes.pill)
                    .background(colors.content.copy(alpha = 0.1f))
            )
        }
    }
}

private fun SemanticColorSet.toSnackbarColors() = RMapSnackbarColors(
    accent = accent,
    container = container,
    content = content,
    border = border
)

@Preview(
    name = "Snackbar variants - Light",
    showBackground = true,
    backgroundColor = 0xFFF4F8FF,
    widthDp = 390
)
@Composable
private fun RMapSnackbarLightPreview() {
    RMapSnackbarPreviewMatrix(darkTheme = false)
}

@Preview(
    name = "Snackbar variants - Dark",
    showBackground = true,
    backgroundColor = 0xFF15151B,
    widthDp = 390
)
@Composable
private fun RMapSnackbarDarkPreview() {
    RMapSnackbarPreviewMatrix(darkTheme = true)
}

@Composable
private fun RMapSnackbarPreviewMatrix(darkTheme: Boolean) {
    RMapTheme(darkTheme = darkTheme, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(vertical = Dimens.spacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            AppNotificationVariant.entries.forEach { variant ->
                RMapSnackbarContent(
                    visuals = AppSnackbarVisuals(
                        title = "",
                        message = previewMessage(variant),
                        variant = variant,
                        actionLabel = if (variant == AppNotificationVariant.Warning) "Login" else null,
                        action = if (variant == AppNotificationVariant.Warning) AppNotificationAction.Login else null
                    ),
                    onAction = {},
                    onDismiss = {}
                )
            }
        }
    }
}

private fun previewMessage(variant: AppNotificationVariant): String {
    return when (variant) {
        AppNotificationVariant.Success -> "Your learning progress was saved."
        AppNotificationVariant.Error -> "We could not update."
        AppNotificationVariant.Warning -> "Sign in to generate your personalized roadmap."
        AppNotificationVariant.Info -> "This feature will be available in a future update."
    }
}
