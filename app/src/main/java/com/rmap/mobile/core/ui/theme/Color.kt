package com.rmap.mobile.core.ui.theme

import androidx.compose.ui.graphics.Color

private val RMapBlue = Color(0xFF2B7FFF)
private val RMapBlueStrong = Color(0xFF155DFC)
private val RMapBlueLegacy = Color(0xFF298CF7)
private val RMapBlueSoft = Color(0xFFEFF6FF)
private val RMapBlueSubtle = Color(0xFFF4F8FF)
private val RMapBlueContainer = Color(0xFFDBEAFE)

private val RMapSurface = Color(0xFFFFFFFF)
private val RMapSurfaceSoft = Color(0xFFF9FAFB)
private val RMapSurfaceMuted = Color(0xFFF8FAFC)
private val RMapBackground = Color(0xFFF4F8FF)
private val RMapBorder = Color(0xFFE5E7EB)
private val RMapBorderSoft = Color(0xFFF3F4F6)
private val RMapDivider = Color(0xFFF1F5F9)

private val RMapInk = Color(0xFF000000)
private val RMapInkSoft = Color(0xFF111827)
private val RMapInkMuted = Color(0xFF4A5565)
private val RMapTextSecondary = Color(0xFF6A7282)
private val RMapTextPlaceholder = Color(0xFF99A1AF)
private val RMapTextDisabled = Color(0xFF94A3B8)
private val RMapSlate = Color(0xFF314158)
private val RMapSlateSoft = Color(0xFF364153)

private val RMapGreen = Color(0xFF10B981)
private val RMapGreenStrong = Color(0xFF009966)
private val RMapGreenBright = Color(0xFF00C950)
private val RMapGreenSoft = Color(0xFFECFDF5)
private val RMapGreenSubtle = Color(0xFFF0FDF4)

private val RMapAmber = Color(0xFFF59E0B)
private val RMapAmberStrong = Color(0xFFD97706)
private val RMapOrange = Color(0xFFFF6900)
private val RMapOrangeStrong = Color(0xFFF54900)
private val RMapOrangeSoft = Color(0xFFFFF7ED)
private val RMapWarningSoft = Color(0xFFFFFBEB)

private val RMapRed = Color(0xFFFB2C36)
private val RMapRedSoft = Color(0xFFFEF2F2)
private val RMapRedSubtle = Color(0xFFFEE2E2)

private val RMapPurple = Color(0xFF9810FA)
private val RMapPurpleSoft = Color(0xFFFAF5FF)
private val RMapPurpleSubtle = Color(0xFFF3F0FF)
private val RMapIndigo = Color(0xFF6366F1)
private val RMapIndigoSoft = Color(0xFFEEF2FF)
private val RMapPinkSoft = Color(0xFFFDF2F8)

val PrimaryLight = RMapBlue
val OnPrimaryLight = RMapSurface
val PrimaryContainerLight = RMapBlueSoft
val OnPrimaryContainerLight = RMapBlueStrong
val SecondaryLight = RMapTextSecondary
val OnSecondaryLight = RMapSurface
val SecondaryContainerLight = RMapBorderSoft
val OnSecondaryContainerLight = RMapInkMuted
val TertiaryLight = RMapPurple
val OnTertiaryLight = RMapSurface
val TertiaryContainerLight = RMapPurpleSoft
val OnTertiaryContainerLight = RMapPurple
val ErrorLight = RMapRed
val OnErrorLight = RMapSurface
val ErrorContainerLight = RMapRedSoft
val OnErrorContainerLight = RMapRed
val BackgroundLight = RMapBackground
val OnBackgroundLight = RMapInkSoft
val SurfaceLight = RMapSurface
val OnSurfaceLight = RMapInkSoft
val SurfaceVariantLight = RMapSurfaceSoft
val OnSurfaceVariantLight = RMapTextSecondary
val SurfaceContainerLight = RMapSurfaceSoft
val SurfaceContainerLowLight = RMapSurfaceMuted
val SurfaceContainerHighLight = RMapBorderSoft
val OutlineLight = RMapBorder
val OutlineVariantLight = RMapDivider
val InverseSurfaceLight = RMapInkSoft
val InverseOnSurfaceLight = RMapSurface
val InversePrimaryLight = RMapBlueContainer
val ScrimLight = Color(0x99000000)

val PrimaryDark = Color(0xFF90A1B9)
val OnPrimaryDark = Color(0xFF0B1220)
val PrimaryContainerDark = Color(0xFF1E3A8A)
val OnPrimaryContainerDark = RMapBlueContainer
val SecondaryDark = Color(0xFFCBD5E1)
val OnSecondaryDark = Color(0xFF0B1220)
val SecondaryContainerDark = RMapSlate
val OnSecondaryContainerDark = Color(0xFFE2E8F0)
val TertiaryDark = Color(0xFFC27AFF)
val OnTertiaryDark = Color(0xFF270044)
val TertiaryContainerDark = Color(0xFF4F39F6)
val OnTertiaryContainerDark = RMapPurpleSoft
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)
val BackgroundDark = Color(0xFF0B1220)
val OnBackgroundDark = Color(0xFFE5E7EB)
val SurfaceDark = RMapInkSoft
val OnSurfaceDark = Color(0xFFE5E7EB)
val SurfaceVariantDark = Color(0xFF1D293D)
val OnSurfaceVariantDark = Color(0xFFCBD5E1)
val SurfaceContainerDark = Color(0xFF1E2939)
val SurfaceContainerLowDark = Color(0xFF0F172B)
val SurfaceContainerHighDark = Color(0xFF1D293D)
val OutlineDark = Color(0xFF45556C)
val OutlineVariantDark = RMapSlate
val InverseSurfaceDark = Color(0xFFE5E7EB)
val InverseOnSurfaceDark = Color(0xFF111827)
val InversePrimaryDark = RMapBlueStrong
val ScrimDark = Color(0xCC000000)

val CardBorderColor = Color(0x80F9FAFB)
val CardShadowColor = Color(0x0F000000)
val CardDividerColor = RMapBorderSoft
val CardDividerStrongColor = RMapBorderSoft.copy(alpha = 0.8f)
val CardShadowSubtleColor = Color(0x0A000000)
val CardShadowSoftColor = Color(0x08000000)
val CardShadowOverlayColor = Color(0x1A000000)
val CardPrimaryBorderColor = RMapBlue.copy(alpha = 0.1f)
val CardPrimaryGlowColor = RMapBlueSubtle.copy(alpha = 0.8f)
val CardPrimaryGlowStrongColor = RMapBlue.copy(alpha = 0.3f)
val CardPrimaryGlowMediumColor = RMapBlue.copy(alpha = 0.15f)
val CardPrimaryGlowSoftColor = RMapBlue.copy(alpha = 0.08f)
val CardPrimaryGlowSubtleColor = RMapBlue.copy(alpha = 0.06f)
val CardPrimaryGlowFaintColor = RMapBlue.copy(alpha = 0.04f)
val CardPrimaryGlowBareColor = RMapBlue.copy(alpha = 0.05f)
val PrimaryBlueOverlaySoftColor = RMapBlue.copy(alpha = 0.15f)
val PrimaryBlueOverlayFaintColor = RMapBlue.copy(alpha = 0.05f)
val PrimaryBlueShadowColor = RMapBlue.copy(alpha = 0.25f)
val PrimaryHeroShadowColor = RMapBlueStrong.copy(alpha = 0.4f)
val PrimaryAvatarShadowColor = RMapBlueLegacy.copy(alpha = 0.2f)

val NeutralBorderColor = RMapBorder
val NeutralDividerDotColor = Color(0xFFD1D5DC)
val NeutralTextMutedColor = RMapTextSecondary
val NeutralTextBodyColor = RMapInkMuted
val NeutralDisabledColor = RMapTextPlaceholder
val NeutralDisabledTextColor = RMapTextSecondary
val NeutralSoftSurfaceColor = RMapBorderSoft
val Neutral900Color = RMapInkSoft
val ProfileIconContainerColor = Color(0xFFF3F0FF)
val HeadingTextColor = RMapInk

val DifficultyExpertContainerColor = RMapPurpleSoft
val DifficultyExpertContentColor = RMapPurple
val DifficultyBeginnerContainerColor = RMapGreenSubtle
val DifficultyIntermediateContainerColor = RMapBlueContainer
val DifficultyHardContainerColor = Color(0xFFFFEDD4)
val StatusSuccessContainerColor = RMapGreenSoft
val StatusSuccessContentColor = RMapGreen
val StatusCompletedContentColor = RMapGreenStrong
val StatusWarningContainerColor = RMapWarningSoft
val StatusWarningContentColor = RMapAmber
val StatusDangerContainerColor = RMapRedSoft
val StatusDangerContentColor = RMapRed
val StatusHardContentColor = RMapOrangeStrong
val InfoContainerColor = RMapBlueSoft
val ExploreBlueContainerColor = RMapBlueSoft
val ExploreRoseContainerColor = RMapPinkSoft
val ExploreGreenContainerColor = RMapGreenSubtle
val ExplorePurpleContainerColor = RMapIndigoSoft

val AccentPurpleColor = RMapIndigo
val AccentCyanColor = Color(0xFF00D3F3)
val AccentBlueColor = RMapBlue
val AccentPurpleGlowColor = Color(0xFFC27AFF)
val AiTipContainerEndColor = Color(0xFFEAF3FF)
val AuthHeroPlaceholderColor = Color(0xFFE8DDFF)
val CoverSurfaceColor = Color(0xFF0F172B)
val CoverPreviewSurfaceColor = Color(0xFF1E3A8A)
val CoverScrimColor = ScrimLight
