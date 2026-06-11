package com.rmap.mobile.features.auth.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rmap.mobile.R

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
    )
}

@Composable
fun AuthRequiredDialog(
    reason: AuthPromptReason,
    onContinueClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(reason.titleResId))
        },
        text = {
            Text(text = stringResource(reason.descriptionResId))
        },
        confirmButton = {
            TextButton(onClick = onContinueClick) {
                Text(text = stringResource(R.string.auth_prompt_continue))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.auth_prompt_not_now))
            }
        }
    )
}
