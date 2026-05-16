package com.rmap.mobile.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

object AppShapes {
    val extraSmall = RoundedCornerShape(Dimens.spacingXs)
    val small = RoundedCornerShape(Dimens.spacingSm)
    val chip = RoundedCornerShape(Dimens.radiusSm)
    val iconContainer = RoundedCornerShape(Dimens.cardRadiusSm)
    val iconContainerLarge = RoundedCornerShape(Dimens.cardRadiusSmPlus)
    val button = RoundedCornerShape(Dimens.cardRadiusMd)
    val iconFrameInner = RoundedCornerShape(Dimens.cardRadiusMdPlus)
    val searchBar = RoundedCornerShape(Dimens.cardRadiusLg)
    val statCard = RoundedCornerShape(Dimens.cardRadiusLgPlus)
    val card = RoundedCornerShape(Dimens.cardRadiusXl)
    val heroCard = RoundedCornerShape(Dimens.cardRadiusXxl)
    val largeCard = RoundedCornerShape(Dimens.cardRadiusHuge)
    val pill = RoundedCornerShape(percent = 50)
    val navigationBar = RoundedCornerShape(
        topStart = Dimens.cardRadiusHuge,
        topEnd = Dimens.cardRadiusHuge
    )
    val bottomSheet = RoundedCornerShape(
        topStart = Dimens.spacingMassive,
        topEnd = Dimens.spacingMassive
    )
}

val RMapShapes = Shapes(
    extraSmall = AppShapes.extraSmall,
    small = AppShapes.small,
    medium = AppShapes.iconContainer,
    large = AppShapes.button,
    extraLarge = AppShapes.card
)
