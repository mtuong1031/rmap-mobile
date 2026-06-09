package com.rmap.mobile.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rmap.mobile.R

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private val FigtreeFontFamily = FontFamily(
    Font(
        resId = R.font.figtree_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.figtree_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.figtree_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.figtree_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    )
)

private fun TextStyle.withFigtree(): TextStyle = copy(fontFamily = FigtreeFontFamily)

private val BaseTypography = Typography()

val Typography = Typography(
    displayLarge = BaseTypography.displayLarge.withFigtree(), // 57sp, 64sp, Regular
    displayMedium = BaseTypography.displayMedium.withFigtree(), // 45sp, 52sp, Regular
    displaySmall = BaseTypography.displaySmall.withFigtree(), // 36sp, 44sp, Regular

    headlineLarge = BaseTypography.headlineLarge.withFigtree(), // 32sp, 40sp, Regular
    headlineMedium = BaseTypography.headlineMedium.withFigtree(), // 28sp, 36sp, Regular
    headlineSmall = BaseTypography.headlineSmall.withFigtree(), // 24sp, 32sp, Regular

    titleLarge = BaseTypography.titleLarge.withFigtree(), // 22sp, 28sp, Regular
    titleMedium = BaseTypography.titleMedium.withFigtree(), // 16sp, 24sp, Medium
    titleSmall = BaseTypography.titleSmall.withFigtree(), // 14sp, 20sp, Medium

    bodyLarge = BaseTypography.bodyLarge.withFigtree(), // 16sp, 24sp, Regular
    bodyMedium = BaseTypography.bodyMedium.withFigtree(), // 14sp, 20sp, Regular
    bodySmall = BaseTypography.bodySmall.withFigtree(), // 12sp, 16sp, Regular

    labelLarge = BaseTypography.labelLarge.withFigtree(), // 14sp, 20sp, Medium
    labelMedium = BaseTypography.labelMedium.withFigtree(), // 12sp, 16sp, Medium
    labelSmall = BaseTypography.labelSmall.withFigtree() // 11sp, 16sp, Medium
)

object AppTextStyles {
    val sectionTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )

    val compactCardTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold
        )

    val titleMediumStrong: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        )

    val heroTitle: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall.copy(
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Bold
        )

    val progressTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        )

    val progressValue: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall.copy(
            fontSize = 28.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

    val headerTitle: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall.copy(
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )

    val headerGreeting: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontSize = 13.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        )

    val statValue: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )

    val eyebrow: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )

    val sectionEyebrow: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

    val tag: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

    val metadata: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        )

    val recommendedCardTitle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold
        )

    val badge: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )

    val badgeSmall: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium.copy(
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.325.sp
        )

    val navigationLabel: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            lineHeight = 15.sp,
            letterSpacing = 0.sp
        )
}
