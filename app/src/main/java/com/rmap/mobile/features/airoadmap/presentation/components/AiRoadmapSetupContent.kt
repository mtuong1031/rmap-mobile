package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapCardDefaults
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapViewModel

@Composable
fun AiRoadmapSetupContent(
    topic: String,
    deadlineText: String,
    dailyStudyHours: Float,
    isSubmitEnabled: Boolean,
    isLoading: Boolean,
    errorText: String?,
    guideTitle: String,
    guideBody: String,
    topicLabel: String,
    topicPlaceholder: String,
    suggestionsLabel: String,
    suggestedTopics: List<String>,
    deadlineLabel: String,
    deadlineSupportingText: String,
    dailyHoursLabel: String,
    dailyHoursSupportingText: String,
    dailyHoursValueText: String,
    submitText: String,
    onTopicChange: (String) -> Unit,
    onDeadlineClick: () -> Unit,
    onDailyStudyHoursChange: (Float) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFormEnabled = !isLoading

    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.largeCard,
        border = RMapCardDefaults.themedBorder(
            color = MaterialTheme.colorScheme.primary.copy(alpha = SetupCardBorderAlpha)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
        ) {
            SetupFormIntro(
                title = guideTitle,
                body = guideBody,
                enabled = isFormEnabled
            )

            RoadmapGoalField(
                label = topicLabel,
                value = topic,
                placeholder = topicPlaceholder,
                enabled = isFormEnabled,
                onValueChange = onTopicChange
            )

            TopicSuggestions(
                label = suggestionsLabel,
                topics = suggestedTopics,
                enabled = isFormEnabled,
                onTopicClick = onTopicChange
            )

            DailyStudyTimeField(
                label = dailyHoursLabel,
                supportingText = dailyHoursSupportingText,
                valueText = dailyHoursValueText,
                dailyStudyHours = dailyStudyHours,
                enabled = isFormEnabled,
                onDailyStudyHoursChange = onDailyStudyHoursChange,
                modifier = Modifier.fillMaxWidth()
            )

            DeadlineField(
                label = deadlineLabel,
                supportingText = deadlineSupportingText,
                valueText = deadlineText,
                onClick = onDeadlineClick,
                enabled = isFormEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            FormActionArea(
                errorText = errorText,
                submitText = submitText,
                isEnabled = isSubmitEnabled && !isLoading,
                isLoading = isLoading,
                onSubmitClick = onSubmitClick
            )
        }
    }
}

@Composable
private fun SetupFormIntro(
    title: String,
    body: String,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RoadmapGoalField(
    label: String,
    value: String,
    placeholder: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    FieldBlock(
        label = label,
        supportingText = placeholder,
        enabled = true
    ) {
        RMapTextInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(R.string.ai_roadmap_topic_placeholder),
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge,
            height = FormInputHeight,
            colors = RMapTextInputDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = FormFieldBorderAlpha)
            ),
            shape = AppShapes.card,
            border = RMapTextInputDefaults.border(
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = FormFieldBorderAlpha)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.TrackChanges,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
    }
}

@Composable
private fun TopicSuggestions(
    label: String,
    topics: List<String>,
    enabled: Boolean,
    onTopicClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
        ) {
            items(
                items = topics,
                key = { it }
            ) { topic ->
                SuggestionBadge(
                    text = topic,
                    enabled = enabled,
                    onClick = { onTopicClick(topic) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionBadge(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = SuggestionBadgeContainerAlpha))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXsPlus),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Book,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconXs)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ValueBadge(
    text: String,
    accentColor: Color
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(accentColor.copy(alpha = ValueBadgeContainerAlpha))
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXs),
        style = MaterialTheme.typography.labelLarge.copy(
            color = accentColor,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
private fun FieldHeader(
    label: String,
    supportingText: String? = null,
    enabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        if (trailingContent != null) {
            Box(
                modifier = Modifier.padding(top = Dimens.spacingXs),
                contentAlignment = Alignment.TopEnd
            ) {
                trailingContent()
            }
        }
    }
}

@Composable
private fun FieldBlock(
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        content()
    }
}

@Composable
private fun DeadlineField(
    label: String,
    supportingText: String,
    valueText: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    FieldBlock(
        label = label,
        modifier = modifier,
        supportingText = supportingText,
        enabled = true
    ) {
        SelectorField(
            text = valueText,
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
        )
    }
}

@Composable
private fun SelectorField(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .height(FormInputHeight)
            .clip(AppShapes.card)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            ),
        shape = AppShapes.card,
        color = MaterialTheme.colorScheme.surface,
        border = RMapCardDefaults.border(
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = FormFieldBorderAlpha)
        ),
        tonalElevation = Dimens.cardElevationNone
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLg),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.controlSm)
                    .clip(AppShapes.iconContainer)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun DailyStudyTimeField(
    label: String,
    supportingText: String,
    valueText: String,
    dailyStudyHours: Float,
    enabled: Boolean,
    onDailyStudyHoursChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = dailyStudyHours.toStudyHoursAccentColor()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        FieldHeader(
            label = label,
            supportingText = supportingText,
            enabled = enabled,
            trailingContent = {
                ValueBadge(
                    text = valueText,
                    accentColor = accentColor
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingXs),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            Slider(
                value = dailyStudyHours,
                onValueChange = onDailyStudyHoursChange,
                enabled = enabled,
                valueRange = AiRoadmapViewModel.MIN_DAILY_STUDY_HOURS..AiRoadmapViewModel.MAX_DAILY_STUDY_HOURS,
                steps = DAILY_HOURS_SLIDER_STEPS,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = SliderActiveTickAlpha),
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    inactiveTickColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledThumbColor = accentColor,
                    disabledActiveTrackColor = accentColor,
                    disabledActiveTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = SliderActiveTickAlpha),
                    disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    disabledInactiveTickColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}

@Composable
private fun FormActionArea(
    errorText: String?,
    submitText: String,
    isEnabled: Boolean,
    isLoading: Boolean,
    onSubmitClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        if (errorText != null) {
            Text(
                text = errorText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.chip)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(Dimens.spacingMd),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        RMapButton(
            text = submitText,
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            leadingIcon = if (isLoading) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.iconSm),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeWidth = LoadingSpinnerStrokeWidth
                    )
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun Float.toStudyHoursAccentColor(): Color {
    return when {
        this < 2f -> Color(0xFF10B981)
        this < 5f -> MaterialTheme.colorScheme.primary
        this < 8f -> Color(0xFF7C3AED)
        else -> Color(0xFFF97316)
    }
}

private fun Boolean.formContentAlpha(): Float {
    return if (this) FormEnabledAlpha else FormDisabledAlpha
}

private const val DAILY_HOURS_SLIDER_STEPS = 22
private const val SetupCardBorderAlpha = 0.16f
private const val FormFieldBorderAlpha = 0.72f
private const val SuggestionBadgeContainerAlpha = 0.72f
private const val ValueBadgeContainerAlpha = 0.12f
private const val SliderActiveTickAlpha = 0.72f
private const val FormEnabledAlpha = 1f
private const val FormDisabledAlpha = 0.48f

private val FormInputHeight = 58.dp
private val LoadingSpinnerStrokeWidth = 2.dp

@Preview(showBackground = true, name = "AI Setup Form - Ready", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupContentPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupContent(
            topic = "Android Developer",
            deadlineText = "Jul 21, 2026",
            dailyStudyHours = 3.5f,
            isSubmitEnabled = true,
            isLoading = false,
            errorText = null,
            guideTitle = "What can I help you learn?",
            guideBody = "Enter a topic below to generate a personalized roadmap for it.",
            topicLabel = "What do you want to learn?",
            topicPlaceholder = "Enter any topic that you want to learn",
            suggestionsLabel = "Suggestions",
            suggestedTopics = listOf(
                "Backend Intern",
                "Frontend Developer",
                "iOS Developer",
                "DevOps Engineer",
                "Data Analyst"
            ),
            deadlineLabel = "Target deadline",
            deadlineSupportingText = "Pick when this goal should feel achievable.",
            dailyHoursLabel = "Daily study time",
            dailyHoursSupportingText = "Choose a pace you can repeat on most days.",
            dailyHoursValueText = "3.5 hours/day",
            submitText = "Get personalized questions",
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Setup Form - Preparing", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupContentLoadingPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupContent(
            topic = "Frontend Developer",
            deadlineText = "Aug 10, 2026",
            dailyStudyHours = 7f,
            isSubmitEnabled = true,
            isLoading = true,
            errorText = null,
            guideTitle = "What can I help you learn?",
            guideBody = "Enter a topic below to generate a personalized roadmap for it.",
            topicLabel = "What do you want to learn?",
            topicPlaceholder = "Enter any topic that you want to learn",
            suggestionsLabel = "Suggestions",
            suggestedTopics = listOf("Backend Intern", "Frontend Developer", "iOS Developer"),
            deadlineLabel = "Target deadline",
            deadlineSupportingText = "Pick when this goal should feel achievable.",
            dailyHoursLabel = "Daily study time",
            dailyHoursSupportingText = "Choose a pace you can repeat on most days.",
            dailyHoursValueText = "7.0 hours/day",
            submitText = "Preparing questions...",
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "AI Setup Form - Error", backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiRoadmapSetupContentErrorPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AiRoadmapSetupContent(
            topic = "React",
            deadlineText = "Choose a deadline",
            dailyStudyHours = 1f,
            isSubmitEnabled = false,
            isLoading = false,
            errorText = "Enter a roadmap goal with at least 10 characters.",
            guideTitle = "What can I help you learn?",
            guideBody = "Enter a topic below to generate a personalized roadmap for it.",
            topicLabel = "What do you want to learn?",
            topicPlaceholder = "Enter any topic that you want to learn",
            suggestionsLabel = "Suggestions",
            suggestedTopics = listOf("Backend Intern", "Frontend Developer", "Data Analyst"),
            deadlineLabel = "Target deadline",
            deadlineSupportingText = "Pick when this goal should feel achievable.",
            dailyHoursLabel = "Daily study time",
            dailyHoursSupportingText = "Choose a pace you can repeat on most days.",
            dailyHoursValueText = "1.0 hours/day",
            submitText = "Get personalized questions",
            onTopicChange = {},
            onDeadlineClick = {},
            onDailyStudyHoursChange = {},
            onSubmitClick = {}
        )
    }
}
