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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapCard
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.profile.presentation.components.ProfileSettingsTopBar
import com.rmap.mobile.features.profile.presentation.viewmodel.PrivacySecurityFieldError
import com.rmap.mobile.features.profile.presentation.viewmodel.PrivacySecurityUiState

@Composable
fun PrivacySecurityScreen(
    uiState: PrivacySecurityUiState,
    onBackClick: () -> Unit,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onChangePasswordClick: () -> Unit,
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
                titleResId = R.string.privacy_security_title,
                onBackClick = onBackClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.spacingXl,
                    top = Dimens.spacingXxl,
                    end = Dimens.spacingXl,
                    bottom = Dimens.spacingXxxl
                )
            ) {
                item {
                    SecurityCard(
                        uiState = uiState,
                        onCurrentPasswordChanged = onCurrentPasswordChanged,
                        onNewPasswordChanged = onNewPasswordChanged,
                        onConfirmNewPasswordChanged = onConfirmNewPasswordChanged,
                        onToggleCurrentPasswordVisibility = onToggleCurrentPasswordVisibility,
                        onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
                        onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                        onChangePasswordClick = onChangePasswordClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityCard(
    uiState: PrivacySecurityUiState,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onChangePasswordClick: () -> Unit,
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
                    text = stringResource(id = R.string.privacy_security_card_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(id = R.string.privacy_security_card_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            SectionDivider()

            Column(
                modifier = Modifier.padding(Dimens.spacingXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
            ) {
                SectionIntro(
                    title = stringResource(id = R.string.privacy_security_password_title),
                    subtitle = stringResource(id = R.string.privacy_security_password_subtitle)
                )

                PasswordForm(
                    uiState = uiState,
                    onCurrentPasswordChanged = onCurrentPasswordChanged,
                    onNewPasswordChanged = onNewPasswordChanged,
                    onConfirmNewPasswordChanged = onConfirmNewPasswordChanged,
                    onToggleCurrentPasswordVisibility = onToggleCurrentPasswordVisibility,
                    onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
                    onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
                    onChangePasswordClick = onChangePasswordClick
                )

                uiState.errorMessage?.let { message ->
                    ErrorMessage(message = message)
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
                    text = stringResource(
                        id = if (uiState.isSaving) {
                            R.string.privacy_security_changing_password
                        } else {
                            R.string.privacy_security_change_password
                        }
                    ),
                    onClick = onChangePasswordClick,
                    variant = RMapButtonVariant.Primary,
                    size = RMapButtonSize.Medium,
                    enabled = !uiState.isSaving,
                    isLoading = uiState.isSaving
                )
            }
        }
    }
}

@Composable
private fun PasswordForm(
    uiState: PrivacySecurityUiState,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        PasswordField(
            value = uiState.currentPassword,
            onValueChange = onCurrentPasswordChanged,
            label = stringResource(id = R.string.privacy_security_current_password_label),
            placeholder = stringResource(id = R.string.privacy_security_current_password_placeholder),
            isPasswordVisible = uiState.isCurrentPasswordVisible,
            onTogglePasswordVisibility = onToggleCurrentPasswordVisibility,
            error = uiState.currentPasswordError,
            imeAction = ImeAction.Next
        )

        PasswordField(
            value = uiState.newPassword,
            onValueChange = onNewPasswordChanged,
            label = stringResource(id = R.string.privacy_security_new_password_label),
            placeholder = stringResource(id = R.string.privacy_security_new_password_placeholder),
            helperText = stringResource(id = R.string.privacy_security_minimum_password_helper),
            isPasswordVisible = uiState.isNewPasswordVisible,
            onTogglePasswordVisibility = onToggleNewPasswordVisibility,
            error = uiState.newPasswordError,
            imeAction = ImeAction.Next
        )

        PasswordField(
            value = uiState.confirmNewPassword,
            onValueChange = onConfirmNewPasswordChanged,
            label = stringResource(id = R.string.privacy_security_confirm_password_label),
            placeholder = stringResource(id = R.string.privacy_security_confirm_password_placeholder),
            helperText = stringResource(id = R.string.privacy_security_password_match_helper),
            isPasswordVisible = uiState.isConfirmPasswordVisible,
            onTogglePasswordVisibility = onToggleConfirmPasswordVisibility,
            error = uiState.confirmPasswordError,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onChangePasswordClick()
                }
            )
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    error: PrivacySecurityFieldError?,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        RMapTextInput(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            showClearButton = false,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            trailingIcon = {
                PasswordVisibilityIcon(
                    isPasswordVisible = isPasswordVisible,
                    onTogglePasswordVisibility = onTogglePasswordVisibility
                )
            }
        )
        helperText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        error?.let {
            Text(
                text = it.message(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun PasswordVisibilityIcon(
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    val contentDescription = stringResource(
        id = if (isPasswordVisible) {
            R.string.privacy_security_hide_password
        } else {
            R.string.privacy_security_show_password
        }
    )

    Box(
        modifier = Modifier
            .size(Dimens.iconLg)
            .clip(CircleShape)
            .clickable(onClick = onTogglePasswordVisibility),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPasswordVisible) {
                Icons.Outlined.VisibilityOff
            } else {
                Icons.Outlined.Visibility
            },
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.iconMd)
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
private fun PrivacySecurityFieldError.message(): String {
    return when (this) {
        PrivacySecurityFieldError.CurrentPasswordRequired -> {
            stringResource(id = R.string.privacy_security_current_password_required)
        }
        PrivacySecurityFieldError.NewPasswordTooShort -> {
            stringResource(id = R.string.privacy_security_new_password_too_short)
        }
        PrivacySecurityFieldError.ConfirmPasswordRequired -> {
            stringResource(id = R.string.privacy_security_confirm_password_required)
        }
        PrivacySecurityFieldError.PasswordsDoNotMatch -> {
            stringResource(id = R.string.privacy_security_passwords_do_not_match)
        }
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
private fun PrivacySecurityScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        PrivacySecurityScreen(
            uiState = PrivacySecurityUiState(),
            onBackClick = {},
            onCurrentPasswordChanged = {},
            onNewPasswordChanged = {},
            onConfirmNewPasswordChanged = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onChangePasswordClick = {}
        )
    }
}
