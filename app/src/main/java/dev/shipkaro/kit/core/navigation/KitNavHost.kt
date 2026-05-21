package dev.shipkaro.kit.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shipkaro.kit.feature.demo.DemoNavHost
import dev.shipkaro.kit.feature.welcome.WelcomeScreen

/**
 * Top-level navigation. Out of the box the kit shows only [WelcomeScreen]; its
 * "Launch Demo" button enters the bundled demo ([DemoNavHost]).
 *
 * There is intentionally no real app flow here — when you build your app you replace
 * `WelcomeScreen` with your start screen and wire your own graph using the reusable
 * screens under `feature/`. See the "Make it yours" guide.
 */
@Composable
fun KitNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Welcome) {
        composable<Route.Welcome> {
            WelcomeScreen(onLaunchDemo = { navController.navigate(Route.Demo) })
        }
        composable<Route.Demo> {
            DemoNavHost()
        }
    }
}
