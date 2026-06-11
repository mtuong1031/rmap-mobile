package com.rmap.mobile.features.auth.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens

enum class AuthPromptReason(
    @param:StringRes val titleResId: Int,
    @param:StringRes val descriptionResId: Int
) {
    AiRoadmap(
        titleResId = R.string.auth_prompt_ai_title,
        descriptionResId = R.string.auth_prompt_ai_description
    ),
    MyRoadmaps(
        titleResId = R.string.auth_prompt_my_roadmaps_title,
        descriptionResId = R.string.auth_prompt_my_roadmaps_description
    ),
    StartRoadmap(
        titleResId = R.string.auth_prompt_start_roadmap_title,
        descriptionResId = R.string.auth_prompt_start_roadmap_description
    ),
    Milestone(
        titleResId = R.string.auth_prompt_milestone_title,
        descriptionResId = R.string.auth_prompt_milestone_description
    ),
    NotificationSettings(
        titleResId = R.string.auth_prompt_notifications_title,
        descriptionResId = R.string.auth_prompt_notifications_description
    ),
    SkillDetails(
        titleResId = R.string.auth_prompt_skill_details_title,
        descriptionResId = R.string.auth_prompt_skill_details_description
    )
}

@Composable
fun AuthRequiredDialog(
    reason: AuthPromptReason,
    onContinueClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingXxl)
                .widthIn(max = AuthDialogMaxWidth),
            shape = AppShapes.heroCard,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                width = Dimens.borderThin,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            shadowElevation = Dimens.cardElevationLg
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spacingXxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                Surface(
                    modifier = Modifier.size(AuthDialogIconContainerSize),
                    shape = AppShapes.iconContainerLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.iconXl),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                ) {
                    Text(
                        text = stringResource(R.string.auth_prompt_eyebrow),
                        style = AppTextStyles.eyebrow,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(reason.titleResId),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(reason.descriptionResId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.iconContainerLarge,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = Dimens.spacingLg,
                            vertical = Dimens.spacingMd
                        ),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Replay,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.iconMd),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.auth_prompt_return_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
                ) {
                    RMapButton(
                        text = stringResource(R.string.auth_prompt_continue),
                        onClick = onContinueClick,
                        modifier = Modifier.fillMaxWidth(),
                        size = RMapButtonSize.Medium,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Login,
                                contentDescription = null
                            )
                        }
                    )
                    RMapButton(
                        text = stringResource(R.string.auth_prompt_not_now),
                        onClick = onDismissRequest,
                        modifier = Modifier.fillMaxWidth(),
                        variant = RMapButtonVariant.Outline,
                        size = RMapButtonSize.Medium
                    )
                }
            }
        }
    }
}

private val AuthDialogMaxWidth = 400.dp
private val AuthDialogIconContainerSize = 58.dp
