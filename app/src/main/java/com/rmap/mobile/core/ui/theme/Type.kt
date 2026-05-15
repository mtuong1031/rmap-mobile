package com.rmap.mobile.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.rmap.mobile.R

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private val InterFontFamily = FontFamily(
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    )
)

private fun TextStyle.withInter(): TextStyle = copy(fontFamily = InterFontFamily)

private val BaseTypography = Typography()

val Typography = Typography(
    displayLarge = BaseTypography.displayLarge.withInter(), // 57sp, 64sp, Regular
    displayMedium = BaseTypography.displayMedium.withInter(), // 45sp, 52sp, Regular
    displaySmall = BaseTypography.displaySmall.withInter(), // 36sp, 44sp, Regular

    headlineLarge = BaseTypography.headlineLarge.withInter(), // 32sp, 40sp, Regular
    headlineMedium = BaseTypography.headlineMedium.withInter(), // 28sp, 36sp, Regular
    headlineSmall = BaseTypography.headlineSmall.withInter(), // 24sp, 32sp, Regular

    titleLarge = BaseTypography.titleLarge.withInter(), // 22sp, 28sp, Regular
    titleMedium = BaseTypography.titleMedium.withInter(), // 16sp, 24sp, Medium
    titleSmall = BaseTypography.titleSmall.withInter(), // 14sp, 20sp, Medium

    bodyLarge = BaseTypography.bodyLarge.withInter(), // 16sp, 24sp, Regular
    bodyMedium = BaseTypography.bodyMedium.withInter(), // 14sp, 20sp, Regular
    bodySmall = BaseTypography.bodySmall.withInter(), // 12sp, 16sp, Regular

    labelLarge = BaseTypography.labelLarge.withInter(), // 14sp, 20sp, Medium
    labelMedium = BaseTypography.labelMedium.withInter(), // 12sp, 16sp, Medium
    labelSmall = BaseTypography.labelSmall.withInter() // 11sp, 16sp, Medium
)
