package com.rmap.mobile.features.profile.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.ProfileSettingsTopBar
import com.rmap.mobile.features.profile.presentation.viewmodel.PersonalInformationFieldError
import com.rmap.mobile.features.profile.presentation.viewmodel.PersonalInformationUiState
import com.rmap.mobile.features.profile.presentation.viewmodel.buildPersonalInformationAvatarUrl

@Composable
fun PersonalInformationScreen(
    uiState: PersonalInformationUiState,
    onBackClick: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onStartEditingDetails: () -> Unit,
    onCancelEditDetails: () -> Unit,
    onOpenAvatarPicker: () -> Unit,
    onCancelAvatarPicker: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    onResetSelectedAvatar: () -> Unit,
    onRegenerateAvatars: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            ProfileSettingsTopBar(
                titleResId = R.string.personal_information_title,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingXl,
                    top = Dimens.spacingXxl,
                    end = Dimens.spacingXl,
                    bottom = Dimens.spacingXxxl
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
            ) {
                item {
                    PublicProfileCard(
                        uiState = uiState,
                        onFullNameChanged = onFullNameChanged,
                        onStartEditingDetails = onStartEditingDetails,
                        onCancelEditDetails = onCancelEditDetails,
                        onOpenAvatarPicker = onOpenAvatarPicker,
                        onCancelAvatarPicker = onCancelAvatarPicker,
                        onAvatarSelected = onAvatarSelected,
                        onResetSelectedAvatar = onResetSelectedAvatar,
                        onRegenerateAvatars = onRegenerateAvatars,
                        onSaveClick = onSaveClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PublicProfileCard(
    uiState: PersonalInformationUiState,
    onFullNameChanged: (String) -> Unit,
    onStartEditingDetails: () -> Unit,
    onCancelEditDetails: () -> Unit,
    onOpenAvatarPicker: () -> Unit,
    onCancelAvatarPicker: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    onResetSelectedAvatar: () -> Unit,
    onRegenerateAvatars: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RMapCard(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        border = BorderStroke(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                Text(
                    text = stringResource(id = R.string.personal_information_public_profile_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.personal_information_public_profile_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            SectionDivider()

            if (uiState.isLoading) {
                LoadingContent()
            } else {
                Column(
                    modifier = Modifier.padding(Dimens.spacingXl),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxl)
                ) {
                    AvatarSection(
                        uiState = uiState,
                        onOpenAvatarPicker = onOpenAvatarPicker,
                        onCancelAvatarPicker = onCancelAvatarPicker,
                        onAvatarSelected = onAvatarSelected,
                        onResetSelectedAvatar = onResetSelectedAvatar,
                        onRegenerateAvatars = onRegenerateAvatars
                    )

                    SectionDivider()

                    PersonalDetailsSection(
                        uiState = uiState,
                        onFullNameChanged = onFullNameChanged,
                        onStartEditingDetails = onStartEditingDetails,
                        onCancelEditDetails = onCancelEditDetails
                    )

                    uiState.errorMessage?.let { message ->
                        ErrorMessage(message = message)
                    }
                }
            }

            SectionDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spacingXl),
                horizontalArrangement = Arrangement.End
            ) {
                RMapButton(
                    text = stringResource(id = R.string.personal_information_save_changes),
                    onClick = onSaveClick,
                    variant = RMapButtonVariant.Primary,
                    size = RMapButtonSize.Medium,
                    enabled = uiState.isSaveEnabled,
                    isLoading = uiState.isSaving
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun AvatarSection(
    uiState: PersonalInformationUiState,
    onOpenAvatarPicker: () -> Unit,
    onCancelAvatarPicker: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    onResetSelectedAvatar: () -> Unit,
    onRegenerateAvatars: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        SectionIntro(
            title = stringResource(id = R.string.personal_information_avatar_title),
            subtitle = stringResource(id = R.string.personal_information_avatar_subtitle)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
        ) {
            ProfileAvatar(
                avatarUrl = uiState.avatarUrl,
                contentDescription = stringResource(id = R.string.profile_avatar_content_description)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
            ) {
                Text(
                    text = stringResource(id = R.string.personal_information_current_avatar),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                RMapButton(
                    text = stringResource(
                        id = if (uiState.isAvatarPickerOpen) {
                            R.string.action_cancel
                        } else {
                            R.string.personal_information_change_avatar
                        }
                    ),
                    onClick = if (uiState.isAvatarPickerOpen) onCancelAvatarPicker else onOpenAvatarPicker,
                    variant = RMapButtonVariant.Secondary,
                    size = RMapButtonSize.Small,
                    leadingIcon = {
                        Icon(
                            imageVector = if (uiState.isAvatarPickerOpen) Icons.Outlined.Close else Icons.Outlined.Edit,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        if (uiState.isAvatarPickerOpen) {
            AvatarPicker(
                avatarSeeds = uiState.avatarSeeds,
                selectedUrl = uiState.avatarUrl,
                onAvatarSelected = onAvatarSelected,
                onResetSelectedAvatar = onResetSelectedAvatar,
                onRegenerateAvatars = onRegenerateAvatars
            )
        }
    }
}

@Composable
private fun AvatarPicker(
    avatarSeeds: List<String>,
    selectedUrl: String,
    onAvatarSelected: (String) -> Unit,
    onResetSelectedAvatar: () -> Unit,
    onRegenerateAvatars: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.button)
            .padding(Dimens.spacingMd),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.personal_information_pick_avatar),
                style = AppTextStyles.metadata.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            )

            RMapButton(
                text = stringResource(id = R.string.personal_information_generate_avatars),
                onClick = onRegenerateAvatars,
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.XSmall,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null
                    )
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(648.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
            userScrollEnabled = false
        ) {
            items(avatarSeeds) { seed ->
                val avatarUrl = remember(seed) { buildPersonalInformationAvatarUrl(seed) }
                AvatarOption(
                    avatarUrl = avatarUrl,
                    isSelected = selectedUrl == avatarUrl,
                    onClick = { onAvatarSelected(seed) },
                    onResetSelectedAvatar = onResetSelectedAvatar
                )
            }
        }
    }
}

@Composable
private fun AvatarOption(
    avatarUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onResetSelectedAvatar: () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .clip(AppShapes.iconContainerLarge)
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .border(
                    width = if (isSelected) Dimens.borderMedium else Dimens.borderThin,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                    shape = AppShapes.iconContainerLarge
                )
                .clickable(onClick = onClick)
                .padding(Dimens.spacingXs),
            contentAlignment = Alignment.Center
        ) {
            ProfileAvatarImage(
                avatarUrl = avatarUrl,
                contentDescription = stringResource(id = R.string.personal_information_avatar_option_content_description),
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(Dimens.iconLg)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .clickable(onClick = onResetSelectedAvatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(id = R.string.personal_information_deselect_avatar),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.iconXs)
                )
            }
        }
    }
}

@Composable
private fun PersonalDetailsSection(
    uiState: PersonalInformationUiState,
    onFullNameChanged: (String) -> Unit,
    onStartEditingDetails: () -> Unit,
    onCancelEditDetails: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
        SectionIntro(
            title = stringResource(id = R.string.personal_information_details_title),
            subtitle = stringResource(id = R.string.personal_information_details_subtitle)
        )

        if (uiState.isEditingDetails) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
                Text(
                    text = stringResource(id = R.string.personal_information_full_name_label),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                RMapTextInput(
                    value = uiState.fullName,
                    onValueChange = onFullNameChanged,
                    placeholder = stringResource(id = R.string.personal_information_full_name_placeholder),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    showClearButton = false,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )
                uiState.fieldError?.let { error ->
                    Text(
                        text = error.message(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                RMapButton(
                    text = stringResource(id = R.string.action_cancel),
                    onClick = onCancelEditDetails,
                    variant = RMapButtonVariant.Outline,
                    size = RMapButtonSize.Small
                )
            }
        } else {
            ReadOnlyDetailsCard(
                fullName = uiState.fullName,
                onStartEditingDetails = onStartEditingDetails
            )
        }
    }
}

@Composable
private fun ReadOnlyDetailsCard(
    fullName: String,
    onStartEditingDetails: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.button)
            .padding(Dimens.spacingLg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)
        ) {
            Text(
                text = stringResource(id = R.string.personal_information_full_name_label),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        RMapButton(
            text = stringResource(id = R.string.personal_information_edit),
            onClick = onStartEditingDetails,
            variant = RMapButtonVariant.Secondary,
            size = RMapButtonSize.Small,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun SectionIntro(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ProfileAvatar(
    avatarUrl: String,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(AppShapes.heroCard)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(Dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, AppShapes.heroCard)
            .padding(Dimens.spacingXs),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isBlank()) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.controlLg)
            )
        } else {
            ProfileAvatarImage(
                avatarUrl = avatarUrl,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ProfileAvatarImage(
    avatarUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val request = remember(avatarUrl, context) {
        val builder = ImageRequest.Builder(context)
            .data(avatarUrl)
            .crossfade(true)

        if (avatarUrl.needsSvgDecoder()) {
            builder.decoderFactory(SvgDecoder.Factory())
        }

        builder.build()
    }

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

private fun String.needsSvgDecoder(): Boolean {
    return contains("/svg", ignoreCase = true) || substringBefore("?").endsWith(".svg", ignoreCase = true)
}

@Composable
private fun PersonalInformationFieldError.message(): String {
    return when (this) {
        PersonalInformationFieldError.NameTooShort -> stringResource(id = R.string.personal_information_name_too_short)
        PersonalInformationFieldError.NameTooLong -> stringResource(id = R.string.personal_information_name_too_long)
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.button)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(Dimens.spacingMd),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun SectionDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.borderThin)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390, heightDp = 844)
@Composable
private fun PersonalInformationScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        PersonalInformationScreen(
            uiState = PersonalInformationUiState(
                originalFullName = "Thinh Hoang Duy",
                originalAvatarUrl = buildPersonalInformationAvatarUrl("thinh"),
                fullName = "Thinh Hoang Duy",
                avatarUrl = buildPersonalInformationAvatarUrl("thinh"),
                avatarSeeds = List(32) { "avatar-$it" },
                isLoading = false
            ),
            onBackClick = {},
            onFullNameChanged = {},
            onStartEditingDetails = {},
            onCancelEditDetails = {},
            onOpenAvatarPicker = {},
            onCancelAvatarPicker = {},
            onAvatarSelected = {},
            onResetSelectedAvatar = {},
            onRegenerateAvatars = {},
            onSaveClick = {}
        )
    }
}
