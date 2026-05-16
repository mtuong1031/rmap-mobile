package com.rmap.mobile.features.profile.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.Dimens
import coil.compose.AsyncImage
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.theme.Neutral900Color
import com.rmap.mobile.core.ui.theme.PrimaryAvatarShadowColor
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
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.profileAvatarSize)
                    .shadow(
                        elevation = Dimens.cardElevationLg,
                        shape = AppShapes.largeCard,
                        ambientColor = PrimaryAvatarShadowColor,
                        spotColor = PrimaryAvatarShadowColor
                    )
                    .border(
                        width = Dimens.borderThick,
                        color = MaterialTheme.colorScheme.surface,
                        shape = AppShapes.largeCard
                    )
                    .clip(AppShapes.largeCard)
                    .background(Neutral900Color)
            ) {
                // Replacing SubcomposeAsyncImage with AsyncImage to resolve NoClassDefFoundError in Preview.
                // SubcomposeAsyncImageScope can sometimes fail to load in the IDE's render environment.
                // We layer the placeholder behind the image to achieve the same loading/error behavior.
                AvatarPlaceholder()
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = stringResource(id = R.string.profile_avatar_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.94f else 1f,
                animationSpec = spring(stiffness = 620f),
                label = "edit_button_scale"
            )

            Box(
                modifier = Modifier
                    .size(Dimens.profileEditButtonSize)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(Dimens.borderMedium, MaterialTheme.colorScheme.surface, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onEditClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(id = R.string.profile_edit_avatar_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(Dimens.iconSmPlus)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacingMdPlus))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.spacingXs))

        Text(
            text = role,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AvatarPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
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
