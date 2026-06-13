package com.rmap.mobile.features.widget.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.rmap.mobile.MainActivity
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState

class ContinueLearningWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        RMapAppGraph.initialize(context.applicationContext)
        val snapshot = RMapAppGraph.continueLearningWidgetRepository.getSnapshot()
        val strings = ContinueLearningWidgetStrings.from(
            context = context,
            streakDays = snapshot.streakDays
        )

        provideContent {
            ContinueLearningWidgetContent(
                snapshot = snapshot,
                strings = strings
            )
        }
    }
}

@Composable
private fun ContinueLearningWidgetContent(
    snapshot: ContinueLearningWidgetSnapshot,
    strings: ContinueLearningWidgetStrings
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val layout = continueLearningWidgetLayout(
        width = size.width,
        height = size.height
    )
    val openAction = actionStartActivity(
        continueLearningIntent(
            context = context,
            snapshot = snapshot
        )
    )

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(WidgetBackground)
            .appWidgetBackground()
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .clickable(openAction)
            .padding(layout.contentPadding)
    ) {
        WidgetHeader(
            showFullTitle = layout.showFullTitle,
            strings = strings
        )
        Spacer(modifier = GlanceModifier.height(layout.headerContentSpacing))

        when (snapshot.state) {
            ContinueLearningWidgetState.Active -> ActiveRoadmapContent(
                snapshot = snapshot,
                strings = strings,
                showSupportingText = layout.showSupportingText,
                showAction = layout.showActiveAction,
                openAction = openAction
            )
            ContinueLearningWidgetState.Empty -> EmptyContent(
                title = strings.emptyTitle,
                message = strings.emptyMessage,
                actionLabel = strings.exploreAction,
                showMessage = layout.showSupportingText,
                actionSpacing = layout.emptyActionSpacing,
                openAction = openAction
            )
            ContinueLearningWidgetState.SignedOut -> EmptyContent(
                title = strings.signedOutTitle,
                message = strings.signedOutMessage,
                actionLabel = strings.signInAction,
                showMessage = layout.showSupportingText,
                actionSpacing = layout.emptyActionSpacing,
                openAction = openAction
            )
        }
    }
}

@Composable
private fun WidgetHeader(
    showFullTitle: Boolean,
    strings: ContinueLearningWidgetStrings
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_rmap),
            contentDescription = strings.logoDescription,
            modifier = GlanceModifier.size(if (showFullTitle) 24.dp else 20.dp)
        )
        Spacer(modifier = GlanceModifier.size(8.dp))
        Text(
            text = if (showFullTitle) strings.widgetTitle else strings.brandTitle,
            style = TextStyle(
                color = WidgetText,
                fontWeight = FontWeight.Bold
            )
        )
        if (strings.streakLabel != null) {
            Spacer(modifier = GlanceModifier.size(10.dp))
            Text(
                text = strings.streakLabel,
                style = TextStyle(
                    color = WidgetWarning,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun ActiveRoadmapContent(
    snapshot: ContinueLearningWidgetSnapshot,
    strings: ContinueLearningWidgetStrings,
    showSupportingText: Boolean,
    showAction: Boolean,
    openAction: androidx.glance.action.Action
) {
    Text(
        text = snapshot.roadmapTitle.orEmpty(),
        maxLines = 1,
        style = TextStyle(
            color = WidgetText,
            fontWeight = FontWeight.Bold
        )
    )

    if (showSupportingText) {
        val supportingText = snapshot.currentSkillTitle
            ?.takeIf { it.isNotBlank() }
            ?: snapshot.chapterLabel?.takeIf { it.isNotBlank() }
            ?: strings.continueMessage
        Text(
            text = supportingText,
            maxLines = 1,
            style = TextStyle(color = WidgetTextSecondary)
        )
    }

    Spacer(modifier = GlanceModifier.height(if (showSupportingText) 10.dp else 6.dp))
    LinearProgressIndicator(
        progress = snapshot.progressPercent / 100f,
        modifier = GlanceModifier.fillMaxWidth(),
        color = WidgetPrimary,
        backgroundColor = WidgetPrimaryContainer
    )
    Spacer(modifier = GlanceModifier.height(6.dp))

    Row(modifier = GlanceModifier.fillMaxWidth()) {
        val progressLabel = if (snapshot.totalNodes > 0) {
            strings.progressFormat.format(
                snapshot.progressPercent,
                snapshot.completedNodes,
                snapshot.totalNodes
            )
        } else {
            strings.percentFormat.format(snapshot.progressPercent)
        }
        Text(
            text = progressLabel,
            style = TextStyle(color = WidgetTextSecondary)
        )
        Spacer(modifier = GlanceModifier.size(10.dp))
        if (snapshot.isBehind) {
            Text(
                text = strings.behindLabel,
                style = TextStyle(
                    color = WidgetError,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    if (showAction) {
        Spacer(modifier = GlanceModifier.height(10.dp))
        WidgetAction(
            label = strings.continueAction,
            action = openAction
        )
    }
}

@Composable
private fun EmptyContent(
    title: String,
    message: String,
    actionLabel: String,
    showMessage: Boolean,
    actionSpacing: Dp,
    openAction: androidx.glance.action.Action
) {
    Text(
        text = title,
        maxLines = 1,
        style = TextStyle(
            color = WidgetText,
            fontWeight = FontWeight.Bold
        )
    )
    if (showMessage) {
        Text(
            text = message,
            maxLines = 2,
            style = TextStyle(color = WidgetTextSecondary)
        )
    }
    Spacer(modifier = GlanceModifier.height(actionSpacing))
    WidgetAction(
        label = actionLabel,
        action = openAction
    )
}

@Composable
private fun WidgetAction(
    label: String,
    action: androidx.glance.action.Action
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(WidgetPrimary)
            .cornerRadius(12.dp)
            .clickable(action)
            .padding(vertical = 9.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = WidgetOnPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

private fun continueLearningIntent(
    context: Context,
    snapshot: ContinueLearningWidgetSnapshot
): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        when (snapshot.state) {
            ContinueLearningWidgetState.Active -> {
                putExtra(
                    MainActivity.EXTRA_NOTIFICATION_DESTINATION,
                    MainActivity.DESTINATION_ROADMAP_DETAIL
                )
                snapshot.roadmapId?.let { roadmapId ->
                    putExtra(MainActivity.EXTRA_ROADMAP_ID, roadmapId)
                }
            }
            ContinueLearningWidgetState.SignedOut -> {
                putExtra(
                    MainActivity.EXTRA_NOTIFICATION_DESTINATION,
                    MainActivity.DESTINATION_AUTH
                )
            }
            ContinueLearningWidgetState.Empty -> {
                putExtra(
                    MainActivity.EXTRA_NOTIFICATION_DESTINATION,
                    MainActivity.DESTINATION_EXPLORE
                )
            }
        }
    }
}

private data class ContinueLearningWidgetStrings(
    val widgetTitle: String,
    val brandTitle: String,
    val logoDescription: String,
    val signedOutTitle: String,
    val signedOutMessage: String,
    val signInAction: String,
    val emptyTitle: String,
    val emptyMessage: String,
    val exploreAction: String,
    val continueMessage: String,
    val continueAction: String,
    val behindLabel: String,
    val streakLabel: String?,
    val progressFormat: String,
    val percentFormat: String
) {
    companion object {
        fun from(
            context: Context,
            streakDays: Int
        ): ContinueLearningWidgetStrings {
            return ContinueLearningWidgetStrings(
                widgetTitle = context.getString(R.string.widget_continue_learning_title),
                brandTitle = context.getString(R.string.widget_brand_title),
                logoDescription = context.getString(R.string.widget_logo_content_description),
                signedOutTitle = context.getString(R.string.widget_signed_out_title),
                signedOutMessage = context.getString(R.string.widget_signed_out_message),
                signInAction = context.getString(R.string.widget_sign_in_action),
                emptyTitle = context.getString(R.string.widget_empty_title),
                emptyMessage = context.getString(R.string.widget_empty_message),
                exploreAction = context.getString(R.string.widget_explore_action),
                continueMessage = context.getString(R.string.widget_continue_message),
                continueAction = context.getString(R.string.widget_continue_action),
                behindLabel = context.getString(R.string.widget_behind_label),
                streakLabel = streakDays.takeIf { it > 0 }?.let {
                    context.resources.getQuantityString(
                        R.plurals.widget_streak_days,
                        it,
                        it
                    )
                },
                progressFormat = context.getString(R.string.widget_progress_format),
                percentFormat = context.getString(R.string.widget_percent_format)
            )
        }
    }
}

private val WidgetBackground = ColorProvider(
    day = Color(0xFFF9FAFB),
    night = Color(0xFF202029)
)
private val WidgetText = ColorProvider(
    day = Color(0xFF000000),
    night = Color(0xFFF8FAFC)
)
private val WidgetTextSecondary = ColorProvider(
    day = Color(0xFF4A5565),
    night = Color(0xFFCBD5E1)
)
private val WidgetPrimary = ColorProvider(
    day = Color(0xFF2B7FFF),
    night = Color(0xFF60A5FA)
)
private val WidgetPrimaryContainer = ColorProvider(
    day = Color(0xFFDBEAFE),
    night = Color(0xFF1D3A5F)
)
private val WidgetOnPrimary = ColorProvider(
    day = Color.White,
    night = Color.Black
)
private val WidgetWarning = ColorProvider(
    day = Color(0xFFD97706),
    night = Color(0xFFFBBF24)
)
private val WidgetError = ColorProvider(
    day = Color(0xFFFB2C36),
    night = Color(0xFFFF8A8A)
)

internal data class ContinueLearningWidgetLayout(
    val showFullTitle: Boolean,
    val showSupportingText: Boolean,
    val showActiveAction: Boolean,
    val contentPadding: Dp,
    val headerContentSpacing: Dp,
    val emptyActionSpacing: Dp
)

internal fun continueLearningWidgetLayout(
    width: Dp,
    height: Dp
): ContinueLearningWidgetLayout {
    val isCompactHeight = height < COMFORTABLE_HEIGHT
    return ContinueLearningWidgetLayout(
        showFullTitle = width >= FULL_HEADER_WIDTH,
        showSupportingText = !isCompactHeight,
        showActiveAction = width >= ACTION_WIDTH && height >= ACTION_HEIGHT,
        contentPadding = if (isCompactHeight) 10.dp else 16.dp,
        headerContentSpacing = if (isCompactHeight) 6.dp else 10.dp,
        emptyActionSpacing = if (isCompactHeight) {
            8.dp
        } else {
            (height - 170.dp).coerceIn(12.dp, 40.dp)
        }
    )
}

private val FULL_HEADER_WIDTH = 300.dp
private val ACTION_WIDTH = 240.dp
private val COMFORTABLE_HEIGHT = 160.dp
private val ACTION_HEIGHT = 200.dp
