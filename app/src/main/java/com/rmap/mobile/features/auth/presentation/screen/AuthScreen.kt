package com.rmap.mobile.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import com.rmap.mobile.BuildConfig
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.features.auth.presentation.viewmodel.AuthUiState

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onGoogleIdTokenReceived: (String) -> Unit,
    onLoginError: (String) -> Unit,
    onGithubLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    heroPainter: Painter? = null
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val resolvedHeroPainter = heroPainter ?: if (isPreview) {
        ColorPainter(Color(0xFFE8DDFF))
    } else {
        painterResource(id = R.drawable.auth_hero)
    }

    val handleGoogleLogin = {
        if (!isPreview) {
            coroutineScope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val rawNonce = UUID.randomUUID().toString()
                    val bytes = rawNonce.toByteArray()
                    val md = MessageDigest.getInstance("SHA-256")
                    val digest = md.digest(bytes)
                    val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        .setNonce(hashedNonce)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        request = request,
                        context = context,
                    )

                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        onGoogleIdTokenReceived(googleIdTokenCredential.idToken)
                    } else {
                        onLoginError(context.getString(R.string.auth_sign_in_failed))
                    }
                } catch (e: GetCredentialException) {
                    val message = when (e) {
                        is NoCredentialException -> context.getString(R.string.auth_error_no_accounts)
                        is GetCredentialCancellationException -> context.getString(R.string.auth_error_cancelled)
                        else -> context.getString(R.string.auth_sign_in_failed)
                    }
                    onLoginError(message)
                } catch (e: Exception) {
                    onLoginError(context.getString(R.string.auth_sign_in_failed))
                }
            }
        }
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
                .fillMaxHeight(0.6f)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f)
                .align(Alignment.BottomCenter),
            shape = AppShapes.bottomSheet,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = Dimens.cardElevationMdPlus
        ) {
            AuthFormSection(
                uiState = uiState,
                onGoogleLoginClick = handleGoogleLogin,
                onGithubLoginClick = onGithubLoginClick,
                onBackClick = onBackClick,
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
    onGoogleLoginClick: () -> Unit,
    onGithubLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXl)
    ) {
        AuthHeader()

        val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()

        val googleButtonColors = if (isDarkTheme) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(com.rmap.mobile.core.ui.theme.OnSurfaceDisabledLight.value)
            )
        } else {
            com.rmap.mobile.core.ui.components.RMapButtonDefaults.colors(variant = RMapButtonVariant.Primary)
        }

        val githubButtonColors = if (isDarkTheme) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color(com.rmap.mobile.core.ui.theme.OnSurfaceDisabledLight.value)
            )
        } else {
            com.rmap.mobile.core.ui.components.RMapButtonDefaults.colors(variant = RMapButtonVariant.Secondary)
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
            RMapButton(
                text = stringResource(id = R.string.auth_continue_with_google),
                onClick = onGoogleLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                colors = googleButtonColors,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_google),
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconLg),
                        tint = if (isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            RMapButton(
                text = stringResource(id = R.string.auth_continue_with_github),
                onClick = onGithubLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                colors = githubButtonColors,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_github),
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconLg),
                        tint = if (isDarkTheme) Color.White else Color.Unspecified
                    )
                }
            )

            RMapButton(
                text = stringResource(R.string.auth_explore_as_guest),
                onClick = onBackClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Neutral,
                size = RMapButtonSize.Large
            )
        }

    }
}

@Composable
private fun AuthHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
        Text(
            text = stringResource(id = R.string.auth_sign_in_or_sign_up),
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
            uiState = AuthUiState(),
            onGoogleIdTokenReceived = {},
            onLoginError = {},
            onGithubLoginClick = {},
            onBackClick = {},
            heroPainter = ColorPainter(Color(0xFFE8DDFF))
        )
    }
}
