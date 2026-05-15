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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R
import com.rmap.mobile.core.ui.components.AppCardDefaults
import com.rmap.mobile.core.ui.components.appCardShadow
import com.rmap.mobile.core.ui.theme.RMapTheme

private val AiScholarTipCardShape = RoundedCornerShape(24.dp)

@Composable
fun AiScholarTipCard(
    currentModule: String,
    recommendedTopic: String,
    nextModule: String,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        Color(0xFF298CF7),
        Color(0xFF7B61FF)
    )

    val bgGradientColors = listOf(
        Color(0xFFF4F8FF),
        Color(0xFFEAF2FF)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appCardShadow(
                shape = AiScholarTipCardShape,
                ambientColor = Color(0x08000000),
                spotColor = Color(0x08000000)
            )
            .background(
                brush = Brush.linearGradient(colors = bgGradientColors),
                shape = AiScholarTipCardShape
            )
            .border(
                width = AppCardDefaults.borderWidth,
                color = Color.White.copy(alpha = 0.8f),
                shape = AiScholarTipCardShape
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(colors = gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
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
                    color = Color(0xFF4B5563)
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FF, widthDp = 390)
@Composable
private fun AiScholarTipCardPreview() {
    RMapTheme(darkTheme = false, dynamicColor = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            AiScholarTipCard(
                currentModule = "Asynchronous JS",
                recommendedTopic = "Promises",
                nextModule = "DOM Manipulation"
            )
        }
    }
}
