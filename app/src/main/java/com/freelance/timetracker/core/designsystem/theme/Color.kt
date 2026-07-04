package com.freelance.timetracker.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ── Brand ────────────────────────────────────────────────────────────────────
// ShipKaro brand purple. This is the single knob for the BRAND color — change
// BrandPrimary (or run /kit-setup-theme) and the primary + its containers reskin.
// The neutral surfaces / outlines below are deliberately brand-INDEPENDENT gray
// (Material-classic: gray window background + white cards), so a non-purple brand
// never ends up sitting on lavender-tinted neutrals.
val BrandPrimary = Color(0xFF7C3AED) // violet 600 — white text is AA-contrast safe
val BrandPrimaryBright = Color(0xFFA974F5) // keycap purple — accents, logo, highlights
val BrandSecondary = Color(0xFF00B8D4)
val BrandTertiary = Color(0xFFFF6D00)

// ── Light scheme ─────────────────────────────────────────────────────────────
val LightPrimary = BrandPrimary
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEDE4FF)
val LightOnPrimaryContainer = Color(0xFF24005A)
val LightBackground = Color(0xFFEEEEEE) // neutral gray window background (Material-classic)
val LightOnBackground = Color(0xFF1B1B1B)
val LightSurface = Color(0xFFFFFFFF) // white cards
val LightOnSurface = Color(0xFF1B1B1B)
val LightSurfaceVariant = Color(0xFFE0E0E0) // neutral fill, not lavender
val LightOnSurfaceVariant = Color(0xFF44474A)
val LightOutline = Color(0xFF757575)
val LightOutlineVariant = Color(0xFFE0E0E0)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)

// ── Dark scheme ──────────────────────────────────────────────────────────────
val DarkPrimary = Color(0xFFCBB5FF)
val DarkOnPrimary = Color(0xFF3A1D80)
val DarkPrimaryContainer = Color(0xFF5B2BB5)
val DarkOnPrimaryContainer = Color(0xFFEDE4FF)
val DarkBackground = Color(0xFF121212) // neutral dark background (Material-classic)
val DarkOnBackground = Color(0xFFE3E3E3)
val DarkSurface = Color(0xFF1E1E1E) // neutral dark cards
val DarkOnSurface = Color(0xFFE3E3E3)
val DarkSurfaceVariant = Color(0xFF44474A) // neutral fill, not purple
val DarkOnSurfaceVariant = Color(0xFFC6C6C6)
val DarkOutline = Color(0xFF919191)
val DarkOutlineVariant = Color(0xFF44474A)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)

// ── Semantic state colors (Banner / state views) — not in Material's scheme ──
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFED6C02)
val InfoBlue = Color(0xFF0288D1)
