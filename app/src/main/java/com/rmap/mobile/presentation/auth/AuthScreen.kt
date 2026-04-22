package com.rmap.mobile.presentation.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.rmap.mobile.R
import com.rmap.mobile.presentation.ui.components.FilledButton
import com.rmap.mobile.presentation.ui.components.FilledTonalButton
import com.rmap.mobile.presentation.ui.theme.RMapTheme

@Composable
fun AuthScreen(
    onContinueWithGoogle: () -> Unit,
    onContinueWithFacebook: () -> Unit,
    modifier: Modifier = Modifier,
    heroPainter: Painter? = null
) {
    val isPreview = LocalInspectionMode.current
    val resolvedHeroPainter = heroPainter ?: if (isPreview) {
        ColorPainter(Color(0xFFDCE7F7))
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
                .height(400.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.58f)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 12.dp
        ) {
            AuthFormSection(
                onContinueWithGoogle = onContinueWithGoogle,
                onContinueWithFacebook = onContinueWithFacebook,
                showSocialIcons = !isPreview,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp)
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
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    .clip(RoundedCornerShape(16.dp)),
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

        Spacer(modifier = Modifier.height(1.dp))
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
            heroPainter = ColorPainter(Color(0xFFDCE7F7))
        )
    }
}
