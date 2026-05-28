package com.rmap.mobile.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.FilledButton
import com.rmap.mobile.core.ui.components.FilledTonalButton
import com.rmap.mobile.core.ui.components.RMapTextInput
import com.rmap.mobile.core.ui.components.RMapTextInputDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val AuthHeroHeight = 400.dp
private const val SheetHeightFraction = 0.58f

private enum class AuthMode {
    SignIn,
    SignUp
}

@Composable
fun AuthScreen(
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    onSignInWithPassword: (email: String, password: String) -> Unit = { _, _ -> },
    onCreateAccountWithPassword: (
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) -> Unit = { _, _, _, _ -> },
    onForgotPassword: () -> Unit = {},
    modifier: Modifier = Modifier,
    heroPainter: Painter? = null
) {
    val isPreview = LocalInspectionMode.current
    var authMode by rememberSaveable { mutableStateOf(AuthMode.SignIn) }
    val resolvedHeroPainter = heroPainter ?: if (isPreview) {
        ColorPainter(Color(0xFFE8DDFF))
    } else {
        painterResource(id = R.drawable.auth_hero)
    }
    val sheetHeightFraction = SheetHeightFraction

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
                .fillMaxHeight(sheetHeightFraction)
                .align(Alignment.BottomCenter),
            shape = AppShapes.bottomSheet,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = Dimens.cardElevationMdPlus
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(
                        start = Dimens.spacingHuge,
                        top = Dimens.spacingXxl,
                        end = Dimens.spacingHuge,
                        bottom = Dimens.spacingXl
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                AuthFormSection(
                    authMode = authMode,
                    onAuthModeChange = { authMode = it },
                    onSignInWithPassword = onSignInWithPassword,
                    onCreateAccountWithPassword = onCreateAccountWithPassword,
                    onForgotPassword = onForgotPassword,
                    onContinueWithGoogle = onContinueWithGoogle,
                    onContinueWithFacebook = onContinueWithFacebook,
                    showSocialIcons = !isPreview,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }
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
    authMode: AuthMode,
    onAuthModeChange: (AuthMode) -> Unit,
    onSignInWithPassword: (email: String, password: String) -> Unit,
    onCreateAccountWithPassword: (
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) -> Unit,
    onForgotPassword: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    showSocialIcons: Boolean,
    modifier: Modifier = Modifier
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
    ) {
        AuthHeader(authMode = authMode)

        if (authMode == AuthMode.SignIn) {
            SignInFields(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                onForgotPassword = onForgotPassword
            )
        } else {
            SignUpFields(
                fullName = fullName,
                onFullNameChange = { fullName = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it }
            )
        }

        FilledButton(
            text = stringResource(
                id = if (authMode == AuthMode.SignIn) {
                    R.string.auth_sign_in
                } else {
                    R.string.auth_create_account
                }
            ),
            onClick = {
                if (authMode == AuthMode.SignIn) {
                    onSignInWithPassword(email, password)
                } else {
                    onCreateAccountWithPassword(fullName, email, password, confirmPassword)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .cardShadow(shape = AppShapes.button)
        )

        if (authMode == AuthMode.SignIn) {
            AuthDivider()

            SocialButtons(
                onContinueWithGoogle = onContinueWithGoogle,
                onContinueWithFacebook = onContinueWithFacebook,
                showSocialIcons = showSocialIcons
            )
        }

        AuthModeSwitch(
            authMode = authMode,
            onAuthModeChange = onAuthModeChange,
            modifier = Modifier.fillMaxWidth()
        )

        if (authMode == AuthMode.SignUp) {
            AgreementText(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AuthHeader(authMode: AuthMode) {
    val titleRes = if (authMode == AuthMode.SignIn) {
        R.string.auth_sign_in_title
    } else {
        R.string.auth_sign_up_title
    }
    val descriptionRes = if (authMode == AuthMode.SignIn) {
        R.string.auth_sign_in_description
    } else {
        R.string.auth_sign_up_description
    }

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)) {
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 0.sp
            )
        )

        Text(
            text = stringResource(id = descriptionRes),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun SignInFields(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(id = R.string.auth_email_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXsPlus)) {
            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = stringResource(id = R.string.auth_password_placeholder),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Text(
                text = stringResource(id = R.string.auth_forgot_password),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onForgotPassword),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun SignUpFields(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
        AuthTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            placeholder = stringResource(id = R.string.auth_full_name_placeholder)
        )

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(id = R.string.auth_email_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(id = R.string.auth_password_placeholder),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        AuthTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(id = R.string.auth_confirm_password_placeholder),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    RMapTextInput(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier.fillMaxWidth(),
        height = Dimens.controlLg,
        shape = AppShapes.iconContainerLarge,
        contentPadding = PaddingValues(horizontal = Dimens.spacingLg),
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
        colors = RMapTextInputDefaults.colors(
            borderColor = MaterialTheme.colorScheme.outline,
            shadowColor = Color.Transparent
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        showClearButton = false
    )
}

@Composable
private fun AuthDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = stringResource(id = R.string.auth_or_divider),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
            )
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SocialButtons(
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    showSocialIcons: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
        FilledTonalButton(
            text = stringResource(id = R.string.button_continue_with_google),
            onClick = onContinueWithGoogle,
            modifier = Modifier
                .fillMaxWidth()
                .cardShadow(shape = AppShapes.button),
            leadingIcon = if (showSocialIcons) {
                {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun AuthModeSwitch(
    authMode: AuthMode,
    onAuthModeChange: (AuthMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefix = stringResource(
        id = if (authMode == AuthMode.SignIn) {
            R.string.auth_new_to_rmap
        } else {
            R.string.auth_already_have_account
        }
    )
    val action = stringResource(
        id = if (authMode == AuthMode.SignIn) {
            R.string.auth_create_an_account
        } else {
            R.string.auth_sign_in
        }
    )
    val targetMode = if (authMode == AuthMode.SignIn) AuthMode.SignUp else AuthMode.SignIn

    Text(
        text = buildAnnotatedString {
            append(prefix)
            append(" ")
            pushStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            append(action)
            pop()
        },
        modifier = modifier.clickable { onAuthModeChange(targetMode) },
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun AgreementText(modifier: Modifier = Modifier) {
    Text(
        text = agreementAnnotatedText(),
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun agreementAnnotatedText(): AnnotatedString {
    val prefix = stringResource(id = R.string.auth_terms_prefix)
    val terms = stringResource(id = R.string.auth_terms_service)
    val and = stringResource(id = R.string.auth_terms_and)
    val privacy = stringResource(id = R.string.auth_privacy_policy)
    val linkStyle = SpanStyle(textDecoration = TextDecoration.Underline)

    return buildAnnotatedString {
        append(prefix)
        append(" ")
        pushStyle(linkStyle)
        append(terms)
        pop()
        append(" ")
        append(and)
        append(" ")
        pushStyle(linkStyle)
        append(privacy)
        pop()
        append(".")
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthScreenPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        AuthScreen(
            onContinueWithGoogle = {},
            onContinueWithFacebook = {},
            onSignInWithPassword = { _, _ -> },
            onCreateAccountWithPassword = { _, _, _, _ -> },
            heroPainter = ColorPainter(Color(0xFFE8DDFF))
        )
    }
}
