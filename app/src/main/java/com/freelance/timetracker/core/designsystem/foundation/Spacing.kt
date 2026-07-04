package com.freelance.timetracker.core.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale. 4dp base unit. Use [KitSpacing] tokens instead of literals
 * so layouts stay consistent across the kit.
 *
 * Read inside composables via `MaterialTheme` extension or `LocalKitSpacing.current`.
 */
@Immutable
data class KitSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val screenHorizontal: Dp = 16.dp,
    val screenVertical: Dp = 16.dp,
    val sectionGap: Dp = 24.dp,
)

val LocalKitSpacing = staticCompositionLocalOf { KitSpacing() }
