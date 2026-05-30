package dev.shipkaro.kit.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.SessionState
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.feature.auth.AuthScreen
import dev.shipkaro.kit.feature.changelog.ChangelogScreen
import dev.shipkaro.kit.feature.home.HomeScreen
import dev.shipkaro.kit.feature.onboarding.OnboardingScreen
import dev.shipkaro.kit.feature.paywall.PaywallScreen
import dev.shipkaro.kit.feature.profile.ProfileScreen
import dev.shipkaro.kit.feature.settings.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Top-level navigation.
 *
 * Default flow: Onboarding (first launch only) → Auth (if `AUTH_ENABLED` + signed
 * out) → Paywall (if `PAYWALL_ENABLED` + not premium + first time) → Home →
 * Settings / Profile / Changelog.
 *
 * No in-app splash composable — the Android OS cold-start splash covers the
 * brief window while [resolveStartRoute] reads DataStore on first composition.
 * If the user signs out from a post-auth screen (Home / Settings / Profile),
 * the kit automatically routes back to Auth.
 */
@Composable
fun KitNavHost() {
    val navController = rememberNavController()
    val settings = koinInject<SettingsRepository>()
    val auth = koinInject<AuthRepository>()
    val purchases = koinInject<PurchaseManager>()
    val scope = rememberCoroutineScope()

    var startRoute by remember { mutableStateOf<Route?>(null) }
    LaunchedEffect(Unit) {
        startRoute = resolveStartRoute(settings, auth, purchases)
    }
    val start = startRoute
    if (start == null) {
        // System splash is still visible while DataStore resolves the first route.
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    // Bounce back to Auth on sign-out from a post-auth screen.
    val session by auth.sessionState.collectAsState(initial = SessionState.Loading)
    val currentEntry = navController.currentBackStackEntryAsState().value
    LaunchedEffect(session, currentEntry) {
        if (!KitConfig.AUTH_ENABLED || session !is SessionState.SignedOut) return@LaunchedEffect
        val dest = currentEntry?.destination ?: return@LaunchedEffect
        val isPostAuth = dest.hasRoute<Route.Home>() ||
            dest.hasRoute<Route.Settings>() ||
            dest.hasRoute<Route.Profile>() ||
            dest.hasRoute<Route.Changelog>()
        if (isPostAuth) {
            navController.navigate(Route.Auth) {
                popUpTo<Route.Home> { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = start) {
        composable<Route.Onboarding> {
            OnboardingScreen(onFinished = {
                scope.launch {
                    val next = afterOnboarding(settings, auth, purchases)
                    navController.navigate(next) {
                        popUpTo<Route.Onboarding> { inclusive = true }
                    }
                }
            })
        }
        composable<Route.Auth> {
            AuthScreen(onAuthenticated = {
                scope.launch {
                    val next = afterAuth(settings, purchases)
                    navController.navigate(next) {
                        popUpTo<Route.Auth> { inclusive = true }
                    }
                }
            })
        }
        composable<Route.Paywall> {
            val finish: () -> Unit = {
                scope.launch {
                    settings.setPaywallSeen(true)
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Paywall> { inclusive = true }
                    }
                }
            }
            PaywallScreen(onPurchased = finish, onDismiss = finish)
        }
        composable<Route.Home> {
            HomeScreen(
                onOpenSettings = { navController.navigate(Route.Settings) },
            )
        }
        composable<Route.Settings> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onWhatsNew = { navController.navigate(Route.Changelog) },
                onProfile = { navController.navigate(Route.Profile) },
            )
        }
        composable<Route.Profile> {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        composable<Route.Changelog> {
            ChangelogScreen(onBack = { navController.popBackStack() })
        }
    }
}

/** First-launch route resolution. Runs once on app start before NavHost composes. */
private suspend fun resolveStartRoute(
    settings: SettingsRepository,
    auth: AuthRepository,
    purchases: PurchaseManager,
): Route {
    if (KitConfig.ONBOARDING_ENABLED && !settings.onboardingDone.first()) return Route.Onboarding
    if (KitConfig.AUTH_ENABLED && auth.sessionState.first() is SessionState.SignedOut) {
        return Route.Auth
    }
    return afterAuth(settings, purchases)
}

/** After onboarding completes — skip auth/paywall as appropriate. */
private suspend fun afterOnboarding(
    settings: SettingsRepository,
    auth: AuthRepository,
    purchases: PurchaseManager,
): Route {
    if (KitConfig.AUTH_ENABLED && auth.sessionState.first() is SessionState.SignedOut) {
        return Route.Auth
    }
    return afterAuth(settings, purchases)
}

/** After auth completes — show the paywall once, then Home. */
private suspend fun afterAuth(
    settings: SettingsRepository,
    purchases: PurchaseManager,
): Route {
    val paywallSeen = settings.paywallSeen.first()
    val premium = purchases.isPremium.value
    return if (KitConfig.PAYWALL_ENABLED && !paywallSeen && !premium) {
        Route.Paywall
    } else {
        Route.Home
    }
}
