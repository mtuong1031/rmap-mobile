package com.rmap.mobile.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val RMapShapes = Shapes(
    extraSmall = RoundedCornerShape(Dimens.spacingXs),
    small = RoundedCornerShape(Dimens.spacingSm),
    medium = RoundedCornerShape(Dimens.cardRadiusSm),
    large = RoundedCornerShape(Dimens.cardRadiusMd),
    extraLarge = RoundedCornerShape(Dimens.cardRadiusXl)
)
