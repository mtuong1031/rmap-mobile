package com.rmap.mobile.features.widget.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
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
import androidx.glance.unit.ColorProvider as GlanceColorProvider
import com.rmap.mobile.MainActivity
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetState
import com.rmap.mobile.features.widget.domain.model.WidgetLearningPlan
import kotlin.math.roundToInt

class ContinueLearningWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        RMapAppGraph.initialize(context.applicationContext)
        val snapshot = RMapAppGraph.continueLearningWidgetRepository.getSnapshot()
        val strings = ContinueLearningWidgetStrings.from(context)

        provideContent {
            val preferences = currentState<Preferences>()
            ContinueLearningWidgetContent(
                snapshot = snapshot,
                selectedIndex = preferences[WidgetCarouselIndexKey] ?: 0,
                strings = strings
            )
        }
    }
}

@Composable
private fun ContinueLearningWidgetContent(
    snapshot: ContinueLearningWidgetSnapshot,
    selectedIndex: Int,
    strings: ContinueLearningWidgetStrings
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val layout = continueLearningWidgetLayout(size.width, size.height)
    val planIndex = selectedIndex.coerceIn(0, (snapshot.learningPlans.size - 1).coerceAtLeast(0))
    val selectedPlan = snapshot.learningPlans.getOrNull(planIndex)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(WidgetBackground)
            .appWidgetBackground()
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .padding(layout.contentPadding)
    ) {
        WidgetHeader(
            streakDays = snapshot.streakDays,
            strings = strings,
            compact = layout.tier == WidgetLayoutTier.Tiny
        )
        Spacer(modifier = GlanceModifier.height(layout.sectionSpacing))

        when (snapshot.state) {
            ContinueLearningWidgetState.Active -> {
                if (selectedPlan != null) {
                    ActiveHero(
                        plan = selectedPlan,
                        planIndex = planIndex,
                        planCount = snapshot.learningPlans.size,
                        hiddenPlanCount = (snapshot.totalActiveRoadmaps - snapshot.learningPlans.size)
                            .coerceAtLeast(0),
                        layout = layout,
                        strings = strings,
                        detailAction = actionStartActivity(
                            continueLearningIntent(context, snapshot.state, selectedPlan.roadmapId)
                        )
                    )
                    if (layout.showStats) {
                        Spacer(modifier = GlanceModifier.height(layout.sectionSpacing))
                        LearningPulse(
                            snapshot = snapshot,
                            asCards = layout.showStatCards,
                            expanded = layout.tier == WidgetLayoutTier.Expanded,
                            strings = strings,
                            roadmapAction = actionStartActivity(widgetMainTabIntent(context, true)),
                            homeAction = actionStartActivity(widgetMainTabIntent(context, false))
                        )
                    }
                }
            }
            ContinueLearningWidgetState.Empty -> EmptyHero(
                title = strings.emptyTitle,
                message = strings.emptyMessage,
                actionLabel = strings.exploreAction,
                layout = layout,
                action = actionStartActivity(
                    continueLearningIntent(context, snapshot.state, null)
                )
            )
            ContinueLearningWidgetState.SignedOut -> EmptyHero(
                title = strings.signedOutTitle,
                message = strings.signedOutMessage,
                actionLabel = strings.signInAction,
                layout = layout,
                action = actionStartActivity(
                    continueLearningIntent(context, snapshot.state, null)
                )
            )
        }

        if (snapshot.state == ContinueLearningWidgetState.Empty && layout.showStats) {
            Spacer(modifier = GlanceModifier.height(layout.sectionSpacing))
            LearningPulse(
                snapshot = snapshot,
                asCards = layout.showStatCards,
                expanded = layout.tier == WidgetLayoutTier.Expanded,
                strings = strings,
                roadmapAction = actionStartActivity(widgetMainTabIntent(context, true)),
                homeAction = actionStartActivity(widgetMainTabIntent(context, false))
            )
        }
    }
}

@Composable
private fun WidgetHeader(
    streakDays: Int,
    strings: ContinueLearningWidgetStrings,
    compact: Boolean
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_rmap),
            contentDescription = strings.logoDescription,
            modifier = GlanceModifier.size(if (compact) 18.dp else 22.dp)
        )
        Spacer(modifier = GlanceModifier.size(8.dp))
        Text(
            text = if (compact) strings.brandTitle else strings.learningPulse,
            maxLines = 1,
            style = TextStyle(
                color = WidgetText,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 13.sp else 15.sp
            )
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        if (streakDays > 0) {
            Row(
                modifier = GlanceModifier
                    .background(WidgetWarningContainer)
                    .cornerRadius(50.dp)
                    .padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_fire),
                    contentDescription = null,
                    modifier = GlanceModifier.size(14.dp)
                )
                Spacer(modifier = GlanceModifier.size(4.dp))
                Text(
                    text = strings.streakShort(streakDays),
                    style = TextStyle(
                        color = WidgetWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ActiveHero(
    plan: WidgetLearningPlan,
    planIndex: Int,
    planCount: Int,
    hiddenPlanCount: Int,
    layout: ContinueLearningWidgetLayout,
    strings: ContinueLearningWidgetStrings,
    detailAction: Action
) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(WidgetHeroBackground)
            .cornerRadius(layout.heroCornerRadius)
            .clickable(detailAction)
    ) {
        HomeHeroSectionBackground(layout.tier)
        Column(modifier = GlanceModifier.padding(layout.heroPadding)) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.title,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = WidgetPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (layout.tier == WidgetLayoutTier.Tiny) 13.sp else 14.sp
                    )
                )
                if (hiddenPlanCount > 0) {
                    Text(
                        text = "+$hiddenPlanCount",
                        style = TextStyle(
                            color = WidgetTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.size(6.dp))
                }
                if (planCount > 1) {
                    CarouselArrow(
                        icon = R.drawable.ic_widget_previous,
                        description = strings.previousDescription,
                        action = actionRunCallback<PreviousLearningPlanAction>()
                    )
                    CarouselArrow(
                        icon = R.drawable.ic_widget_next,
                        description = strings.nextDescription,
                        action = actionRunCallback<NextLearningPlanAction>()
                    )
                }
            }

            val skillTitle = plan.currentSkillTitle
                ?.takeIf(String::isNotBlank)
                ?: strings.continueMessage
            Spacer(modifier = GlanceModifier.height(3.dp))
            Text(
                text = skillTitle,
                maxLines = 1,
                style = TextStyle(
                    color = WidgetText,
                    fontWeight = FontWeight.Bold,
                    fontSize = layout.heroTitleSize
                )
            )

            if (layout.showHeroDetails) {
                Spacer(modifier = GlanceModifier.height(7.dp))
                HeroDetailRow(plan, strings)
            } else if (!plan.chapterLabel.isNullOrBlank()) {
                Text(
                    text = plan.chapterLabel,
                    maxLines = 1,
                    style = TextStyle(color = WidgetTextSecondary, fontSize = 11.sp)
                )
            }

            Spacer(modifier = GlanceModifier.height(layout.progressSpacing))
            ProgressRow(plan, strings)

            if (layout.showAction) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                WidgetAction(
                    label = strings.continueAction,
                    icon = R.drawable.ic_widget_play,
                    action = detailAction
                )
            }

            if (layout.showNextUnlock && !plan.nextUnlockTitle.isNullOrBlank()) {
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = strings.nextUnlock(plan.nextUnlockTitle),
                    maxLines = 1,
                    style = TextStyle(color = WidgetTextSecondary, fontSize = 12.sp)
                )
            }

            if (layout.showDots && planCount > 1) {
                Spacer(modifier = GlanceModifier.height(7.dp))
                CarouselDots(planIndex, planCount)
            }
        }
    }
}

@Composable
private fun HomeHeroSectionBackground(tier: WidgetLayoutTier) {
    if (tier < WidgetLayoutTier.Standard) return
    Box(modifier = GlanceModifier.fillMaxSize()) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = GlanceModifier
                    .size(if (tier == WidgetLayoutTier.Expanded) 112.dp else 88.dp)
                    .background(WidgetHeroDecoration)
                    .cornerRadius(if (tier == WidgetLayoutTier.Expanded) 56.dp else 44.dp)
            ) {}
        }

        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = GlanceModifier
                    .size(if (tier == WidgetLayoutTier.Expanded) 80.dp else 64.dp)
                    .background(WidgetHeroDecorationSecondary)
                    .cornerRadius(if (tier == WidgetLayoutTier.Expanded) 40.dp else 32.dp)
            ) {}
        }
    }
}

@Composable
private fun HeroDetailRow(
    plan: WidgetLearningPlan,
    strings: ContinueLearningWidgetStrings
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (plan.estimatedHours != null) {
            Row(
                modifier = GlanceModifier
                    .background(WidgetChipBackground)
                    .cornerRadius(50.dp)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_target),
                    contentDescription = null,
                    modifier = GlanceModifier.size(13.dp)
                )
                Spacer(modifier = GlanceModifier.size(4.dp))
                Text(
                    text = strings.requiredSkillTime(plan.estimatedHours),
                    maxLines = 1,
                    style = TextStyle(color = WidgetText, fontSize = 11.sp)
                )
            }
        }
        Spacer(modifier = GlanceModifier.defaultWeight())
        if (plan.isBehind) {
            Text(
                text = strings.behindLabel,
                modifier = GlanceModifier
                    .background(WidgetWarningContainer)
                    .cornerRadius(50.dp)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                maxLines = 1,
                style = TextStyle(
                    color = WidgetWarning,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun ProgressRow(
    plan: WidgetLearningPlan,
    strings: ContinueLearningWidgetStrings
) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = strings.nodesComplete(plan.completedNodes, plan.totalNodes),
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(color = WidgetTextSecondary, fontSize = 11.sp)
        )
        Text(
            text = strings.percent(plan.progressPercent),
            style = TextStyle(
                color = WidgetPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
    Spacer(modifier = GlanceModifier.height(4.dp))
    LinearProgressIndicator(
        progress = plan.progressPercent / 100f,
        modifier = GlanceModifier.fillMaxWidth(),
        color = WidgetPrimary,
        backgroundColor = WidgetProgressTrack
    )
}

@Composable
private fun CarouselArrow(
    icon: Int,
    description: String,
    action: Action
) {
    Image(
        provider = ImageProvider(icon),
        contentDescription = description,
        modifier = GlanceModifier
            .size(26.dp)
            .clickable(action)
            .padding(6.dp)
    )
}

@Composable
private fun CarouselDots(
    selectedIndex: Int,
    count: Int
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(count.coerceAtMost(5)) { index ->
            Box(
                modifier = GlanceModifier
                    .size(if (index == selectedIndex) 16.dp else 6.dp, 6.dp)
                    .background(if (index == selectedIndex) WidgetPrimary else WidgetDotInactive)
                    .cornerRadius(50.dp)
            ) {}
            if (index < count - 1) Spacer(modifier = GlanceModifier.size(4.dp))
        }
    }
}

@Composable
private fun LearningPulse(
    snapshot: ContinueLearningWidgetSnapshot,
    asCards: Boolean,
    expanded: Boolean,
    strings: ContinueLearningWidgetStrings,
    roadmapAction: Action,
    homeAction: Action
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .then(
                if (asCards) GlanceModifier else GlanceModifier
                    .background(WidgetSegmentBackground)
                    .cornerRadius(14.dp)
                    .padding(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PulseStat(
            icon = R.drawable.ic_widget_roadmap,
            value = strings.percent(snapshot.roadmapCompletionPercent),
            label = strings.roadmapStat,
            accent = WidgetPrimary,
            container = WidgetPrimaryContainer,
            asCard = asCards,
            expanded = expanded,
            action = roadmapAction,
            modifier = GlanceModifier.defaultWeight()
        )
        if (asCards) Spacer(modifier = GlanceModifier.size(7.dp))
        PulseStat(
            icon = R.drawable.ic_widget_fire,
            value = snapshot.streakDays.toString(),
            label = strings.streakStat,
            accent = WidgetWarning,
            container = WidgetWarningContainer,
            asCard = asCards,
            expanded = expanded,
            action = homeAction,
            modifier = GlanceModifier.defaultWeight()
        )
        if (asCards) Spacer(modifier = GlanceModifier.size(7.dp))
        PulseStat(
            icon = R.drawable.ic_widget_target,
            value = strings.percent(snapshot.readinessPercent),
            label = strings.readinessStat,
            accent = WidgetSuccess,
            container = WidgetSuccessContainer,
            asCard = asCards,
            expanded = expanded,
            action = homeAction,
            modifier = GlanceModifier.defaultWeight()
        )
    }
}

@Composable
private fun PulseStat(
    icon: Int,
    value: String,
    label: String,
    accent: GlanceColorProvider,
    container: GlanceColorProvider,
    asCard: Boolean,
    expanded: Boolean,
    action: Action,
    modifier: GlanceModifier
) {
    Column(
        modifier = modifier
            .background(
                if (asCard) {
                    WidgetStatCard
                } else {
                    ColorProvider(day = Color.Transparent, night = Color.Transparent)
                }
            )
            .cornerRadius(if (asCard) 16.dp else 10.dp)
            .clickable(action)
            .padding(
                horizontal = if (asCard) 8.dp else 5.dp,
                vertical = if (asCard) 9.dp else 6.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (asCard) {
            Box(
                modifier = GlanceModifier
                    .background(container)
                    .cornerRadius(10.dp)
                    .padding(6.dp)
            ) {
                Image(
                    provider = ImageProvider(icon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(if (expanded) 20.dp else 17.dp)
                )
            }
            Spacer(modifier = GlanceModifier.height(5.dp))
        }
        Text(
            text = value,
            maxLines = 1,
            style = TextStyle(
                color = if (asCard) WidgetText else accent,
                fontWeight = FontWeight.Bold,
                fontSize = if (expanded) 15.sp else 13.sp,
                textAlign = TextAlign.Center
            )
        )
        Text(
            text = label,
            maxLines = 1,
            style = TextStyle(
                color = WidgetTextSecondary,
                fontSize = if (expanded) 11.sp else 10.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun EmptyHero(
    title: String,
    message: String,
    actionLabel: String,
    layout: ContinueLearningWidgetLayout,
    action: Action
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(WidgetHeroBackground)
            .cornerRadius(layout.heroCornerRadius)
            .clickable(action)
            .padding(layout.heroPadding)
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = WidgetText,
                fontWeight = FontWeight.Bold,
                fontSize = layout.heroTitleSize
            )
        )
        if (layout.tier != WidgetLayoutTier.Tiny) {
            Spacer(modifier = GlanceModifier.height(5.dp))
            Text(
                text = message,
                maxLines = if (layout.tier >= WidgetLayoutTier.Standard) 2 else 1,
                style = TextStyle(color = WidgetTextSecondary, fontSize = 12.sp)
            )
        }
        if (layout.showAction) {
            Spacer(modifier = GlanceModifier.height(9.dp))
            WidgetAction(actionLabel, R.drawable.ic_widget_play, action)
        }
    }
}

@Composable
private fun WidgetAction(
    label: String,
    icon: Int,
    action: Action
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(WidgetPrimary)
            .cornerRadius(12.dp)
            .clickable(action)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(icon),
            contentDescription = null,
            modifier = GlanceModifier.size(14.dp)
        )
        Spacer(modifier = GlanceModifier.size(6.dp))
        Text(
            text = label,
            style = TextStyle(
                color = WidgetOnPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

internal fun continueLearningIntent(
    context: Context,
    state: ContinueLearningWidgetState,
    roadmapId: String?
): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        when (state) {
            ContinueLearningWidgetState.Active -> {
                putExtra(MainActivity.EXTRA_NOTIFICATION_DESTINATION, MainActivity.DESTINATION_ROADMAP_DETAIL)
                roadmapId?.let { putExtra(MainActivity.EXTRA_ROADMAP_ID, it) }
            }
            ContinueLearningWidgetState.SignedOut -> {
                putExtra(MainActivity.EXTRA_NOTIFICATION_DESTINATION, MainActivity.DESTINATION_AUTH)
            }
            ContinueLearningWidgetState.Empty -> {
                putExtra(MainActivity.EXTRA_NOTIFICATION_DESTINATION, MainActivity.DESTINATION_EXPLORE)
            }
        }
    }
}

internal fun widgetMainTabIntent(
    context: Context,
    myRoadmap: Boolean
): Intent {
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(
            MainActivity.EXTRA_NOTIFICATION_DESTINATION,
            if (myRoadmap) MainActivity.DESTINATION_MY_ROADMAP else MainActivity.DESTINATION_HOME
        )
    }
}

private data class ContinueLearningWidgetStrings(
    val learningPulse: String,
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
    val roadmapStat: String,
    val streakStat: String,
    val readinessStat: String,
    val previousDescription: String,
    val nextDescription: String,
    private val context: Context
) {
    fun streakShort(days: Int): String = context.resources.getQuantityString(
        R.plurals.widget_streak_days_short,
        days,
        days
    )

    fun percent(value: Int): String = context.getString(R.string.widget_percent_format, value)

    fun nodesComplete(completed: Int, total: Int): String = if (total > 0) {
        context.getString(R.string.widget_nodes_complete_format, completed, total)
    } else {
        context.getString(R.string.widget_progress_fallback)
    }

    fun requiredSkillTime(hours: Double): String {
        val roundedHours = hours.roundToInt().coerceAtLeast(1)
        return context.resources.getQuantityString(
            R.plurals.widget_required_skill_hours,
            roundedHours,
            roundedHours
        )
    }

    fun nextUnlock(title: String): String = context.getString(R.string.widget_next_unlock_format, title)

    companion object {
        fun from(context: Context): ContinueLearningWidgetStrings {
            return ContinueLearningWidgetStrings(
                learningPulse = context.getString(R.string.widget_learning_pulse),
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
                roadmapStat = context.getString(R.string.widget_stat_roadmap),
                streakStat = context.getString(R.string.widget_stat_streak),
                readinessStat = context.getString(R.string.widget_stat_readiness),
                previousDescription = context.getString(R.string.widget_previous_description),
                nextDescription = context.getString(R.string.widget_next_description),
                context = context
            )
        }
    }
}

internal enum class WidgetLayoutTier {
    Tiny,
    Compact,
    Standard,
    Expanded
}

internal data class ContinueLearningWidgetLayout(
    val tier: WidgetLayoutTier,
    val showHeroDetails: Boolean,
    val showAction: Boolean,
    val showNextUnlock: Boolean,
    val showDots: Boolean,
    val showStats: Boolean,
    val showStatCards: Boolean,
    val contentPadding: Dp,
    val heroPadding: Dp,
    val heroCornerRadius: Dp,
    val heroTitleSize: androidx.compose.ui.unit.TextUnit,
    val progressSpacing: Dp,
    val sectionSpacing: Dp
)

internal fun continueLearningWidgetLayout(
    width: Dp,
    height: Dp
): ContinueLearningWidgetLayout {
    val tier = when {
        width < 240.dp || height < 145.dp -> WidgetLayoutTier.Tiny
        height < 235.dp -> WidgetLayoutTier.Compact
        height < 335.dp -> WidgetLayoutTier.Standard
        else -> WidgetLayoutTier.Expanded
    }
    return ContinueLearningWidgetLayout(
        tier = tier,
        showHeroDetails = tier >= WidgetLayoutTier.Standard,
        showAction = tier >= WidgetLayoutTier.Standard,
        showNextUnlock = tier == WidgetLayoutTier.Expanded,
        showDots = tier >= WidgetLayoutTier.Standard,
        showStats = tier >= WidgetLayoutTier.Compact,
        showStatCards = tier >= WidgetLayoutTier.Standard,
        contentPadding = if (tier == WidgetLayoutTier.Tiny) 9.dp else 12.dp,
        heroPadding = if (tier <= WidgetLayoutTier.Compact) 10.dp else 13.dp,
        heroCornerRadius = if (tier == WidgetLayoutTier.Tiny) 12.dp else 18.dp,
        heroTitleSize = when (tier) {
            WidgetLayoutTier.Tiny -> 15.sp
            WidgetLayoutTier.Compact -> 17.sp
            WidgetLayoutTier.Standard -> 18.sp
            WidgetLayoutTier.Expanded -> 20.sp
        },
        progressSpacing = if (tier <= WidgetLayoutTier.Compact) 6.dp else 9.dp,
        sectionSpacing = if (tier == WidgetLayoutTier.Tiny) 6.dp else 9.dp
    )
}

private val WidgetBackground = ColorProvider(day = Color(0xFFF3F7FF), night = Color(0xFF14151C))
private val WidgetHeroBackground = ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF20212B))
private val WidgetHeroDecoration = ColorProvider(day = Color(0xFFEAF2FF), night = Color(0xFF243653))
private val WidgetHeroDecorationSecondary = ColorProvider(day = Color(0xFFF0FDF4), night = Color(0xFF142E28))
private val WidgetStatCard = ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF20212B))
private val WidgetSegmentBackground = ColorProvider(day = Color(0xFFE8EEF8), night = Color(0xFF1D1E27))
private val WidgetChipBackground = ColorProvider(day = Color(0xFFF7F9FC), night = Color(0xFF292A35))
private val WidgetText = ColorProvider(day = Color(0xFF101828), night = Color(0xFFF7F7FB))
private val WidgetTextSecondary = ColorProvider(day = Color(0xFF667085), night = Color(0xFFB7BDCC))
private val WidgetPrimary = ColorProvider(day = Color(0xFF2B7FFF), night = Color(0xFF68A8FF))
private val WidgetPrimaryContainer = ColorProvider(day = Color(0xFFEAF2FF), night = Color(0xFF213D63))
private val WidgetProgressTrack = ColorProvider(day = Color(0xFFE8EDF5), night = Color(0xFF30323F))
private val WidgetOnPrimary = ColorProvider(day = Color.White, night = Color(0xFF07111F))
private val WidgetWarning = ColorProvider(day = Color(0xFFC76A00), night = Color(0xFFFFB454))
private val WidgetWarningContainer = ColorProvider(day = Color(0xFFFFF4DE), night = Color(0xFF46351E))
private val WidgetSuccess = ColorProvider(day = Color(0xFF008A68), night = Color(0xFF46D8B1))
private val WidgetSuccessContainer = ColorProvider(day = Color(0xFFE5F8F1), night = Color(0xFF173D35))
private val WidgetDotInactive = ColorProvider(day = Color(0xFFD0D5DD), night = Color(0xFF4A4C58))
