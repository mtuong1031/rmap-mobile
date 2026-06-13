package com.rmap.mobile.features.profile.presentation.components.header

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.RMapTheme

@Composable
fun ProfileCard(
    avatarUrl: String,
    name: String,
    role: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val avatarRequest = remember(avatarUrl, context) {
            val builder = ImageRequest.Builder(context)
                .data(avatarUrl)
                .crossfade(true)

            if (avatarUrl.needsSvgDecoder()) {
                builder.decoderFactory(SvgDecoder.Factory())
            }

            builder.build()
        }
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = spring(stiffness = 620f),
            label = "profile_avatar_scale"
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = Dimens.cardElevationPressed,
                    shape = AppShapes.largeCard,
                    ambientColor = Color(0x1A000000),
                    spotColor = Color(0x1A000000)
                )
                .clip(AppShapes.largeCard)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onEditClick
                )
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = Dimens.borderThin,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = AppShapes.largeCard
                )
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .clip(AppShapes.heroCard)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AvatarPlaceholder()
                AsyncImage(
                    model = avatarRequest,
                    contentDescription = stringResource(id = R.string.profile_avatar_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacingLg))

        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun String.needsSvgDecoder(): Boolean {
    return contains("/svg", ignoreCase = true) || substringBefore("?").endsWith(".svg", ignoreCase = true)
}

@Composable
private fun AvatarPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.controlLg)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun ProfileCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        ProfileCard(
            avatarUrl = "",
            name = "Thinh Duy",
            role = "Aspiring Frontend Developer",
            onEditClick = {}
        )
    }
}
