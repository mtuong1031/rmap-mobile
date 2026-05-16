package com.rmap.mobile.features.roadmap.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.AccentPurpleColor
import com.rmap.mobile.core.ui.theme.AiTipContainerEndColor
import com.rmap.mobile.core.ui.theme.BackgroundLight
import com.rmap.mobile.core.ui.theme.CardShadowSoftColor
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.NeutralTextBodyColor
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme

private val AiScholarTipCardShape = RoundedCornerShape(Dimens.cardRadiusXl)

@Composable
fun AiScholarTipCard(
    currentModule: String,
    recommendedTopic: String,
    nextModule: String,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        PrimaryLight,
        AccentPurpleColor
    )

    val bgGradientColors = listOf(
        BackgroundLight,
        AiTipContainerEndColor
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(
                shape = AiScholarTipCardShape,
                ambientColor = CardShadowSoftColor,
                spotColor = CardShadowSoftColor
            )
            .background(
                brush = Brush.linearGradient(colors = bgGradientColors),
                shape = AiScholarTipCardShape
            )
            .border(
                width = AppCardDefaults.borderWidth,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = AiScholarTipCardShape
            )
            .padding(Dimens.spacingXl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.aiIconContainerSize)
                        .clip(RoundedCornerShape(Dimens.cardRadiusSm))
                        .background(Brush.linearGradient(colors = gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(Dimens.iconSm)
                    )
                }

                Text(
                    text = stringResource(R.string.roadmap_detail_ai_tip_heading),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )
            }

            val prefixStr = stringResource(R.string.roadmap_detail_ai_tip_prefix, currentModule)
            val suffixStr = stringResource(R.string.roadmap_detail_ai_tip_suffix, nextModule)
            
            Text(
                text = buildAnnotatedString {
                    append(prefixStr)
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(recommendedTopic)
                    }
                    append(suffixStr)
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 22.75.sp,
                    color = NeutralTextBodyColor
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiScholarTipCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(Dimens.spacingLg)) {
            AiScholarTipCard(
                currentModule = "Asynchronous JS",
                recommendedTopic = "Promises",
                nextModule = "DOM Manipulation"
            )
        }
    }
}
