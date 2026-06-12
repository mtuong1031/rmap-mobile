package com.rmap.mobile.features.home.presentation.components.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapButton
import com.rmap.mobile.core.ui.components.RMapButtonSize
import com.rmap.mobile.core.ui.components.RMapButtonVariant
import com.rmap.mobile.core.ui.components.RMapHeroSectionBackground
import com.rmap.mobile.core.ui.icons.RMapIcons
import com.rmap.mobile.core.ui.theme.Dimens

private val HomeHeroEmptyCardHorizontalPadding = 32.dp
private val HomeHeroEmptyIconTileSize = 72.dp

@Composable
internal fun    HomeHeroEmptyRoadmapCard(
    onCreateRoadmapWithAiClick: () -> Unit,
    onExploreReadyMadeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        RMapHeroSectionBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HomeHeroEmptyCardHorizontalPadding,
                    vertical = Dimens.spacingHuge
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(HomeHeroEmptyIconTileSize)
                    .clip(RoundedCornerShape(Dimens.cardRadiusXl))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = Dimens.borderThin,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(Dimens.cardRadiusXl)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RMapIcons.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.iconXxl)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spacingXl))

            Text(
                text = stringResource(R.string.home_empty_roadmap_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSmPlus))

            Text(
                text = stringResource(R.string.home_empty_roadmap_description),
                modifier = Modifier.widthIn(max = 278.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spacingHuge))

            RMapButton(
                text = stringResource(R.string.home_empty_roadmap_create_ai),
                onClick = onCreateRoadmapWithAiClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Primary,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            )

            Spacer(modifier = Modifier.height(Dimens.spacingMd))

            RMapButton(
                text = stringResource(R.string.home_empty_roadmap_explore),
                onClick = onExploreReadyMadeClick,
                modifier = Modifier.fillMaxWidth(),
                variant = RMapButtonVariant.Secondary,
                size = RMapButtonSize.Large,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }
            )
        }
    }
}
