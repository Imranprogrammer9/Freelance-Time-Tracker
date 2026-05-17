package dev.shipkaro.kit.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe routes (kotlinx.serialization + Navigation Compose 2.8).
 *
 * NOTE: CLAUDE.md originally specified "Navigation 3". androidx.navigation3 is
 * still alpha — substituted stable Navigation Compose type-safe routes, which
 * delivers the same compile-time-safe destination contract. Recorded as a
 * deviation in CLAUDE.md; swap later if Nav3 stabilises.
 */
sealed interface Route {
    @Serializable data object Onboarding : Route
    @Serializable data object Auth : Route
    @Serializable data object Paywall : Route
    @Serializable data object Home : Route
    @Serializable data object Settings : Route
    @Serializable data object Sample : Route
}
