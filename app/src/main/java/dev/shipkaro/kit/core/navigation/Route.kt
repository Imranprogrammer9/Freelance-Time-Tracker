package dev.shipkaro.kit.core.navigation

import kotlinx.serialization.Serializable

/**
 * Top-level routes (kotlinx.serialization + Navigation Compose 2.8 type-safe routes).
 *
 * Default flow:
 *   Splash → Onboarding (first launch only) → Auth (if `AUTH_ENABLED` + signed out)
 *   → Paywall (if `PAYWALL_ENABLED` + not premium + first time) → Home → Settings/Profile.
 *
 * Each gate skips automatically when its toggle is off; gates the user already passed
 * are skipped on subsequent launches. Replace [Home] with your own start screen.
 *
 * NOTE: CLAUDE.md originally specified "Navigation 3" (still alpha) — substituted
 * stable type-safe Navigation Compose. Recorded as a deviation; revisit when Nav3 ships.
 */
sealed interface Route {
    @Serializable data object Onboarding : Route
    @Serializable data object Auth : Route
    @Serializable data object Paywall : Route
    @Serializable data object Home : Route
    @Serializable data object Settings : Route
    @Serializable data object Profile : Route
    @Serializable data object Changelog : Route
    @Serializable data object ComponentsCatalog : Route
}
