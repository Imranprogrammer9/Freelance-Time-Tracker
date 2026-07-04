package com.freelance.timetracker.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic state colors (success/warning/info) — not part of Material 3's color scheme.
 * Material 3 already supplies `error`; the rest live here.
 *
 * Override per-app by passing a custom [KitColors] to [ShipKaroTheme].
 */
@Immutable
data class KitColors(
    val success: Color = SuccessGreen,
    val onSuccess: Color = Color.White,
    val warning: Color = WarningAmber,
    val onWarning: Color = Color.White,
    val info: Color = InfoBlue,
    val onInfo: Color = Color.White,
)

val LocalKitColors = staticCompositionLocalOf { KitColors() }
