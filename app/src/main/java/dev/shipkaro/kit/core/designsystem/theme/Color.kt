package dev.shipkaro.kit.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Brand seed. Replace these with the app's palette (or Figma/Stitch export).
val BrandPrimary = Color(0xFF2962FF)
val BrandSecondary = Color(0xFF00B8D4)
val BrandTertiary = Color(0xFFFF6D00)

// Light scheme
val LightPrimary = BrandPrimary
val LightOnPrimary = Color(0xFFFFFFFF)
val LightBackground = Color(0xFFFDFDFF)
val LightOnBackground = Color(0xFF1A1C1E)
val LightSurface = Color(0xFFFDFDFF)
val LightOnSurface = Color(0xFF1A1C1E)
val LightSurfaceVariant = Color(0xFFE1E2EC)
val LightOnSurfaceVariant = Color(0xFF44474F)
val LightOutline = Color(0xFF74777F)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)

// Dark scheme
val DarkPrimary = Color(0xFFADC6FF)
val DarkOnPrimary = Color(0xFF002E69)
val DarkBackground = Color(0xFF1A1C1E)
val DarkOnBackground = Color(0xFFE3E2E6)
val DarkSurface = Color(0xFF1A1C1E)
val DarkOnSurface = Color(0xFFE3E2E6)
val DarkSurfaceVariant = Color(0xFF44474F)
val DarkOnSurfaceVariant = Color(0xFFC4C6CF)
val DarkOutline = Color(0xFF8E9099)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)

// Semantic state colors (used by Banner / State components). Not in MaterialTheme — exposed via KitColors.
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFED6C02)
val InfoBlue = Color(0xFF0288D1)
