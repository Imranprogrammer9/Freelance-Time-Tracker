package dev.shipkaro.kit.core.navigation

import kotlinx.serialization.Serializable

/**
 * Top-level routes (kotlinx.serialization + Navigation Compose 2.8 type-safe routes).
 *
 * The kit ships with NO real app flow — out of the box it shows [Welcome] and, behind
 * the Demo button, the bundled demo. When you build your real app you replace
 * `WelcomeScreen`, delete `feature/demo/`, and wire your own destinations here using
 * the reusable screens in `feature/onboarding`, `feature/auth`, `feature/paywall`,
 * `feature/settings`.
 *
 * NOTE: CLAUDE.md originally specified "Navigation 3" (still alpha) — substituted
 * stable type-safe Navigation Compose. Recorded as a deviation; revisit when Nav3 ships.
 */
sealed interface Route {
    @Serializable data object Welcome : Route

    /** Hosts the self-contained demo nav graph ([dev.shipkaro.kit.feature.demo.DemoNavHost]). */
    @Serializable data object Demo : Route
}
