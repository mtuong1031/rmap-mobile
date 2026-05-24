package com.rmap.mobile.features.roadmap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.RMapCardDefaults
import com.rmap.mobile.core.ui.theme.AppShapes
import com.rmap.mobile.core.ui.theme.AppTextStyles
import com.rmap.mobile.core.ui.theme.BackgroundLight
import com.rmap.mobile.core.ui.theme.Dimens
import com.rmap.mobile.core.ui.theme.PrimaryLight
import com.rmap.mobile.core.ui.theme.RMapTheme
import com.rmap.mobile.core.ui.theme.cardShadow

private val AiScholarTipCardShape = AppShapes.card

@Composable
fun AiScholarTipCard(
    currentModule: String,
    recommendedTopic: String,
    nextModule: String,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        PrimaryLight,
        Color(0xFF6366F1)
    )

    val bgGradientColors = listOf(
        BackgroundLight,
        Color(0xFFEAF3FF)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(shape = AiScholarTipCardShape)
            .background(
                brush = Brush.linearGradient(colors = bgGradientColors),
                shape = AiScholarTipCardShape
            )
            .border(
                width = RMapCardDefaults.borderWidth,
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
                        .clip(AppShapes.iconContainer)
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
                    style = AppTextStyles.titleMediumStrong.copy(
                        color = MaterialTheme.colorScheme.onSurface
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
                    color = Color(0xFF4A5565)
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

