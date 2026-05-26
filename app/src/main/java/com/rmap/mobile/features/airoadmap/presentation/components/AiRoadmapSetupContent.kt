package com.rmap.mobile.features.airoadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.features.airoadmap.presentation.viewmodel.AiRoadmapViewModel

@Composable
fun AiRoadmapSetupContent(
    topic: String,
    deadlineText: String,
    dailyStudyHours: Float,
    isSubmitEnabled: Boolean,
    isLoading: Boolean,
    errorText: String?,
    topicLabel: String,
    topicPlaceholder: String,
    deadlineLabel: String,
    dailyHoursLabel: String,
    dailyHoursValueText: String,
    submitText: String,
    onTopicChange: (String) -> Unit,
    onDeadlineClick: () -> Unit,
    onDailyStudyHoursChange: (Float) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.largeCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingXl),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            FieldBlock(label = topicLabel) {
                RMapTextInput(
                    value = topic,
                    onValueChange = onTopicChange,
                    placeholder = topicPlaceholder,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    height = Dimens.controlXl,
                    border = RMapTextInputDefaults.border(MaterialTheme.colorScheme.secondaryContainer),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(Dimens.iconMd)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)

            FieldBlock(label = deadlineLabel) {
                SelectorField(
                    text = deadlineText,
                    onClick = onDeadlineClick,
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

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)

            DailyStudyTimeField(
                label = dailyHoursLabel,
                valueText = dailyHoursValueText,
                dailyStudyHours = dailyStudyHours,
                onDailyStudyHoursChange = onDailyStudyHoursChange
            )

            if (errorText != null) {
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            RMapButton(
                text = submitText,
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isSubmitEnabled && !isLoading
            )
        }
    }
}

@Composable
private fun FieldBlock(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        content()
    }
}

@Composable
private fun SelectorField(
    text: String,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.card)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = AppShapes.card
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .height(Dimens.controlXl)
            .padding(horizontal = Dimens.spacingLg),
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

@Composable
private fun DailyStudyTimeField(
    label: String,
    valueText: String,
    dailyStudyHours: Float,
    onDailyStudyHoursChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.card)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = AppShapes.card
            )
            .padding(Dimens.spacingLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconMd)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Slider(
            value = dailyStudyHours,
            onValueChange = onDailyStudyHoursChange,
            valueRange = AiRoadmapViewModel.MIN_DAILY_STUDY_HOURS..AiRoadmapViewModel.MAX_DAILY_STUDY_HOURS,
            steps = DAILY_HOURS_SLIDER_STEPS
        )
    }
}

private const val DAILY_HOURS_SLIDER_STEPS = 22
