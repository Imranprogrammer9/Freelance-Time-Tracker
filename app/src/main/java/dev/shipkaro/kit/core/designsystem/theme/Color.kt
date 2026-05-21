package dev.shipkaro.kit.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ── Brand ────────────────────────────────────────────────────────────────────
// ShipKaro brand purple. This is the single knob — change BrandPrimary and the
// whole app reskins. Light/dark schemes below derive from it.
val BrandPrimary = Color(0xFF7C3AED) // violet 600 — white text is AA-contrast safe
val BrandPrimaryBright = Color(0xFFA974F5) // keycap purple — accents, logo, highlights
val BrandSecondary = Color(0xFF00B8D4)
val BrandTertiary = Color(0xFFFF6D00)

// ── Light scheme ─────────────────────────────────────────────────────────────
val LightPrimary = BrandPrimary
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEDE4FF)
val LightOnPrimaryContainer = Color(0xFF24005A)
val LightBackground = Color(0xFFFCFBFF)
val LightOnBackground = Color(0xFF1B1B1F)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1B1B1F)
val LightSurfaceVariant = Color(0xFFF2EEF8)
val LightOnSurfaceVariant = Color(0xFF49454E)
val LightOutline = Color(0xFF7A757F)
val LightOutlineVariant = Color(0xFFE5E0EC)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)

// ── Dark scheme ──────────────────────────────────────────────────────────────
val DarkPrimary = Color(0xFFCBB5FF)
val DarkOnPrimary = Color(0xFF3A1D80)
val DarkPrimaryContainer = Color(0xFF5B2BB5)
val DarkOnPrimaryContainer = Color(0xFFEDE4FF)
val DarkBackground = Color(0xFF131316)
val DarkOnBackground = Color(0xFFE5E1E9)
val DarkSurface = Color(0xFF1B1B1F)
val DarkOnSurface = Color(0xFFE5E1E9)
val DarkSurfaceVariant = Color(0xFF49454E)
val DarkOnSurfaceVariant = Color(0xFFCAC4CF)
val DarkOutline = Color(0xFF948F99)
val DarkOutlineVariant = Color(0xFF49454E)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)

// ── Semantic state colors (Banner / state views) — not in Material's scheme ──
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFED6C02)
val InfoBlue = Color(0xFF0288D1)
