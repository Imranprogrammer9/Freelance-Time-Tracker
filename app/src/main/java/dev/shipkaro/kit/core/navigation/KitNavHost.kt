package dev.shipkaro.kit.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.feature.auth.AuthScreen
import dev.shipkaro.kit.feature.home.HomeScreen
import dev.shipkaro.kit.feature.onboarding.OnboardingScreen
import dev.shipkaro.kit.feature.paywall.PaywallScreen
import dev.shipkaro.kit.feature.sample.SampleScreen
import dev.shipkaro.kit.feature.settings.SettingsScreen
import org.koin.compose.koinInject

/**
 * Single source of truth for navigation. Start destination is decided by
 * KitConfig (code-config) + onboarding-seen state, not hardcoded.
 */
@Composable
fun KitNavHost() {
    val navController = rememberNavController()
    val settings = koinInject<SettingsRepository>()
    val onboardingDone by settings.onboardingDone.collectAsState(initial = true)

    val start: Route = when {
        KitConfig.ONBOARDING_ENABLED && !onboardingDone -> Route.Onboarding
        KitConfig.AUTH_ENABLED -> Route.Auth
        else -> Route.Home
    }

    NavHost(navController = navController, startDestination = start) {
        composable<Route.Onboarding> {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(
                        if (KitConfig.AUTH_ENABLED) Route.Auth else Route.Home,
                    ) { popUpTo(Route.Onboarding) { inclusive = true } }
                },
            )
        }
        composable<Route.Auth> {
            AuthScreen(
                onAuthenticated = {
                    val next = if (KitConfig.PAYWALL_ENABLED) Route.Paywall else Route.Home
                    navController.navigate(next) {
                        popUpTo(Route.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Paywall> {
            PaywallScreen(onContinue = { navController.navigate(Route.Home) })
        }
        composable<Route.Home> {
            HomeScreen(
                onOpenSettings = { navController.navigate(Route.Settings) },
                onOpenSample = { navController.navigate(Route.Sample) },
            )
        }
        composable<Route.Settings> { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable<Route.Sample> { SampleScreen(onBack = { navController.popBackStack() }) }
    }
}
