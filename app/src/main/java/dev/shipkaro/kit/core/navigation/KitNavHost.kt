package dev.shipkaro.kit.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.SessionState
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.state.KitLoadingState
import dev.shipkaro.kit.feature.auth.AuthScreen
import dev.shipkaro.kit.feature.home.HomeScreen
import dev.shipkaro.kit.feature.onboarding.OnboardingScreen
import dev.shipkaro.kit.feature.paywall.PaywallScreen
import dev.shipkaro.kit.feature.sample.SampleScreen
import dev.shipkaro.kit.feature.settings.SettingsScreen
import org.koin.compose.koinInject

/**
 * Single source of truth for navigation.
 *
 * Start destination is decided by KitConfig + onboarding-seen state.
 * Auth gating is reactive: when [AuthRepository.sessionState] flips to SignedOut,
 * any non-auth destination redirects to [Route.Auth]. When it flips to SignedIn
 * from the auth screen, it advances to Paywall/Home.
 */
@Composable
fun KitNavHost() {
    val navController = rememberNavController()
    val settings = koinInject<SettingsRepository>()
    val onboardingDone by settings.onboardingDone.collectAsState(initial = true)

    val authRepo = koinInject<AuthRepository>()
    val session by authRepo.sessionState.collectAsState(initial = SessionState.Loading)

    // Wait for first session emit before deciding start.
    if (session is SessionState.Loading) {
        KitLoadingState()
        return
    }

    val start: Route = when {
        KitConfig.ONBOARDING_ENABLED && !onboardingDone -> Route.Onboarding
        KitConfig.AUTH_ENABLED && session is SessionState.SignedOut -> Route.Auth
        else -> Route.Home
    }

    // Reactive guard: if session expires mid-session, bounce to Auth.
    LaunchedEffect(session) {
        if (KitConfig.AUTH_ENABLED && session is SessionState.SignedOut) {
            val current = navController.currentBackStackEntry?.destination?.route
            val isAuthRelated = current?.contains("Auth") == true ||
                current?.contains("Onboarding") == true
            if (!isAuthRelated) {
                navController.navigate(Route.Auth) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
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
            val toHome: () -> Unit = {
                navController.navigate(Route.Home) {
                    popUpTo(Route.Paywall) { inclusive = true }
                }
            }
            PaywallScreen(onPurchased = toHome, onDismiss = toHome)
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
