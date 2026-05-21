package dev.shipkaro.kit.feature.demo

import kotlinx.serialization.Serializable

/**
 * Routes INTERNAL to the demo subtree. Self-contained — the kit's top-level [Route]
 * graph only knows `Route.Demo`, which hosts [DemoNavHost]. Deleting `feature/demo/`
 * removes all of this with it.
 */
sealed interface DemoRoute {
    @Serializable data object Splash : DemoRoute

    @Serializable data object Onboarding : DemoRoute

    @Serializable data object Paywall : DemoRoute

    @Serializable data object Auth : DemoRoute

    @Serializable data object Habits : DemoRoute

    @Serializable data object AddHabit : DemoRoute

    @Serializable data object Settings : DemoRoute
}
