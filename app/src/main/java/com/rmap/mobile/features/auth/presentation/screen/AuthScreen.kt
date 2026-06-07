package com.rmap.mobile.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthMode
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthUiState

private val AuthHeroHeight = 320.dp

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    heroPainter: Painter? = null
) {
    val isPreview = LocalInspectionMode.current
    val resolvedHeroPainter = heroPainter ?: if (isPreview) {
        ColorPainter(Color(0xFFE8DDFF))
    } else {
        painterResource(id = R.drawable.auth_hero)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeroSection(
            painter = resolvedHeroPainter,
            modifier = Modifier
                .fillMaxWidth()
                .height(AuthHeroHeight)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.58f)
                .align(Alignment.BottomCenter),
            shape = AppShapes.bottomSheet,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = Dimens.cardElevationMdPlus
        ) {
            AuthFormSection(
                uiState = uiState,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onFullNameChange = onFullNameChange,
                onToggleMode = onToggleMode,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                onSubmit = onSubmit,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.spacingHuge, vertical = Dimens.spacingXxxl)
            )
        }
    }
}

@Composable
private fun HeroSection(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.30f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 100f
                    )
                )
        )
    }
}

@Composable
private fun AuthFormSection(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        AuthHeader(mode = uiState.mode)

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
            if (uiState.isRegisterMode) {
                AuthInput(
                    value = uiState.fullName,
                    onValueChange = onFullNameChange,
                    placeholder = stringResource(R.string.auth_full_name_placeholder),
                    enabled = !uiState.isLoading,
                    leadingIcon = {
                        AuthInputIcon(imageVector = Icons.Outlined.Person)
                    }
                )
            }

            AuthInput(
                value = uiState.email,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.auth_email_placeholder),
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    AuthInputIcon(imageVector = Icons.Outlined.Email)
                }
            )

            AuthInput(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholder = stringResource(R.string.auth_password_placeholder),
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    AuthInputIcon(imageVector = Icons.Outlined.Lock)
                },
                trailingIcon = if (uiState.password.isNotEmpty()) {
                    {
                        Box(
                            modifier = Modifier
                                .size(RMapTextInputDefaults.ClearButtonSize)
                                .clickable(
                                    enabled = !uiState.isLoading,
                                    onClick = onTogglePasswordVisibility
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = stringResource(R.string.auth_toggle_password_visibility),
                                tint = RMapTextInputDefaults.colors().placeholderColor,
                                modifier = Modifier.size(Dimens.iconLg)
                            )
                        }
                    }
                } else null
            )
        }

        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        RMapButton(
            text = if (uiState.isRegisterMode) {
                stringResource(R.string.auth_create_account)
            } else {
                stringResource(R.string.auth_sign_in)
            },
            onClick = onSubmit,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            variant = RMapButtonVariant.Primary,
            size = RMapButtonSize.Large,
            leadingIcon = if (uiState.isLoading) {
                {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                null
            }
        )

        ModeToggleRow(
            mode = uiState.mode,
            enabled = !uiState.isLoading,
            onToggleMode = onToggleMode
        )
    }
}

@Composable
private fun AuthInputIcon(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = RMapTextInputDefaults.colors().placeholderColor,
        modifier = Modifier.size(Dimens.iconLg)
    )
}

@Composable
private fun AuthHeader(mode: AuthMode) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
        Text(
            text = if (mode == AuthMode.Register) {
                stringResource(R.string.auth_heading_create_account)
            } else {
                stringResource(R.string.auth_heading_continue_account)
            },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Text(
            text = welcomeAnnotatedText(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
            )
        )
    }
}

@Composable
private fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    RMapTextInput(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}

@Composable
private fun ModeToggleRow(
    mode: AuthMode,
    enabled: Boolean,
    onToggleMode: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (mode == AuthMode.Register) {
                stringResource(R.string.auth_have_account)
            } else {
                stringResource(R.string.auth_need_account)
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
            )
        )

        TextButton(
            onClick = onToggleMode,
            enabled = enabled
        ) {
            Text(
                text = if (mode == AuthMode.Register) {
                    stringResource(R.string.auth_sign_in)
                } else {
                    stringResource(R.string.auth_create_account)
                }
            )
        }
    }
}

@Composable
private fun welcomeAnnotatedText(): AnnotatedString {
    val welcomePrefix = stringResource(id = R.string.auth_welcome_prefix)
    val brand = stringResource(id = R.string.auth_brand_name)
    val welcomeSuffix = stringResource(id = R.string.auth_welcome_suffix)

    return buildAnnotatedString {
        append(welcomePrefix)
        append(" ")
        pushStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        )
        append(brand)
        pop()
        append(" ")
        append(welcomeSuffix)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AuthScreen(
            uiState = AuthUiState(
                email = "learner@example.com",
                password = "password123"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onFullNameChange = {},
            onToggleMode = {},
            onTogglePasswordVisibility = {},
            onSubmit = {},
            heroPainter = ColorPainter(Color(0xFFE8DDFF))
        )
    }
}
