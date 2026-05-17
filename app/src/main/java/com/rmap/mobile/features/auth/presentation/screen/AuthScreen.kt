package com.rmap.mobile.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.FilledButton
import com.rmap.mobile.core.ui.components.FilledTonalButton
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AuthHeroPlaceholderColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun AuthScreen(
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    modifier: Modifier = Modifier,
    heroPainter: Painter? = null
) {
    val isPreview = LocalInspectionMode.current
    val resolvedHeroPainter = heroPainter ?: if (isPreview) {
        ColorPainter(AuthHeroPlaceholderColor)
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
                .height(Dimens.authHeroHeight)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.58f)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            shape = AppShapes.bottomSheet,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = Dimens.cardElevationMdPlus
        ) {
            AuthFormSection(
                onContinueWithGoogle = onContinueWithGoogle,
                onContinueWithFacebook = onContinueWithFacebook,
                showSocialIcons = !isPreview,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.spacingHuge, vertical = Dimens.spacingMassive)
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
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    showSocialIcons: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXxxl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            Text(
                text = stringResource(id = R.string.auth_heading_continue_account),
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

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)) {
            FilledTonalButton(
                text = stringResource(id = R.string.button_continue_with_google),
                onClick = onContinueWithGoogle,
                modifier = Modifier.fillMaxWidth(),
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

            FilledButton(
                text = stringResource(id = R.string.button_continue_with_facebook),
                onClick = onContinueWithFacebook,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.button),
                leadingIcon = if (showSocialIcons) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logo_facebook),
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

        Spacer(modifier = Modifier.height(Dimens.borderThin))
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
            onContinueWithGoogle = {},
            onContinueWithFacebook = {},
            heroPainter = ColorPainter(AuthHeroPlaceholderColor)
        )
    }
}
