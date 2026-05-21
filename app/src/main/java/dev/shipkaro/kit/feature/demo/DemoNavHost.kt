package dev.shipkaro.kit.feature.demo

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shipkaro.kit.feature.auth.AuthScreen
import dev.shipkaro.kit.feature.changelog.ChangelogScreen
import dev.shipkaro.kit.feature.demo.habits.AddHabitScreen
import dev.shipkaro.kit.feature.demo.habits.HabitListScreen
import dev.shipkaro.kit.feature.demo.splash.DemoSplashScreen
import dev.shipkaro.kit.feature.onboarding.OnboardingScreen
import dev.shipkaro.kit.feature.paywall.PaywallScreen
import dev.shipkaro.kit.feature.settings.SettingsScreen

/**
 * Self-contained nav graph for the bundled demo app. Hosted by `Route.Demo` in the
 * kit's top-level [dev.shipkaro.kit.core.navigation.KitNavHost].
 *
 * Flow: splash -> onboarding -> paywall -> auth -> habit list (-> add / settings).
 * The onboarding / paywall / auth / settings screens are the REAL kit screens,
 * reused here with demo navigation callbacks — a living integration example.
 */
@Composable
fun DemoNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = DemoRoute.Splash) {
        composable<DemoRoute.Splash> {
            DemoSplashScreen(
                onTimeout = {
                    nav.navigate(DemoRoute.Onboarding) {
                        popUpTo(DemoRoute.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable<DemoRoute.Onboarding> {
            OnboardingScreen(
                onFinished = {
                    nav.navigate(DemoRoute.Paywall) {
                        popUpTo(DemoRoute.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable<DemoRoute.Paywall> {
            val toAuth: () -> Unit = {
                nav.navigate(DemoRoute.Auth) {
                    popUpTo(DemoRoute.Paywall) { inclusive = true }
                }
            }
            PaywallScreen(onPurchased = toAuth, onDismiss = toAuth)
        }
        composable<DemoRoute.Auth> {
            AuthScreen(
                onAuthenticated = {
                    nav.navigate(DemoRoute.Habits) {
                        popUpTo(DemoRoute.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable<DemoRoute.Habits> {
            HabitListScreen(
                onAddHabit = { nav.navigate(DemoRoute.AddHabit) },
                onUpgrade = { nav.navigate(DemoRoute.Paywall) },
                onOpenSettings = { nav.navigate(DemoRoute.Settings) },
            )
        }
        composable<DemoRoute.AddHabit> {
            AddHabitScreen(onDone = { nav.popBackStack() })
        }
        composable<DemoRoute.Settings> {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onWhatsNew = { nav.navigate(DemoRoute.Changelog) },
            )
        }
        composable<DemoRoute.Changelog> {
            ChangelogScreen(onBack = { nav.popBackStack() })
        }
    }
}
