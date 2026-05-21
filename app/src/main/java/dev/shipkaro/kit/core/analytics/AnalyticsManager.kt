package dev.shipkaro.kit.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Single analytics facade — ported from the ShipKaro KMM starter (KMM split it into an
 * expect/actual `Analytics` bridge + a gated `AnalyticsManager`; native Android needs
 * only one class).
 *
 * Fans events out to PostHog + Firebase Analytics. Each backend degrades gracefully:
 *  - PostHog : active only when `posthog.api.key` is set in `local.properties`.
 *  - Firebase: active only when `google-services.json` + the google-services plugin are added.
 *
 * The user-facing analytics toggle ([SettingsRepository.analyticsEnabled]) gates
 * [logEvent] / [logScreen] / [setUser]. Crash reporting ([logError]) is intentionally
 * NOT gated — crashes are always reported.
 *
 * The whole manager no-ops when [KitConfig.ANALYTICS_ENABLED] (compile-time) is false.
 */
class AnalyticsManager(
    private val appContext: Context,
    settings: SettingsRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val posthogConfigured = BuildConfig.POSTHOG_API_KEY.isNotEmpty()
    private var firebaseAnalytics: FirebaseAnalytics? = null

    // Synchronous snapshot of the user opt-in, kept fresh from the DataStore flow so
    // logEvent() can gate without suspending.
    @Volatile
    private var userEnabled: Boolean = true

    init {
        if (KitConfig.ANALYTICS_ENABLED) {
            scope.launch {
                settings.analyticsEnabled.collect { enabled ->
                    userEnabled = enabled
                    applyEnabledToProviders(enabled)
                }
            }
        }
    }

    /** Initialise SDKs. Call once from KitApplication.onCreate. */
    fun init() {
        if (!KitConfig.ANALYTICS_ENABLED) return

        if (posthogConfigured) {
            PostHogAndroid.setup(
                appContext,
                PostHogAndroidConfig(
                    apiKey = BuildConfig.POSTHOG_API_KEY,
                    host = BuildConfig.POSTHOG_HOST,
                ).apply { debug = BuildConfig.DEBUG },
            )
        }
        // Firebase Analytics — getInstance throws if Firebase isn't configured; null = inactive.
        firebaseAnalytics = runCatching { FirebaseAnalytics.getInstance(appContext) }.getOrNull()
    }

    fun logEvent(name: String, params: Map<String, Any>? = null) {
        if (!enabled()) return
        if (posthogConfigured) PostHog.capture(event = name, properties = params)
        firebaseAnalytics?.logEvent(name, params?.toBundle())
    }

    fun logScreen(name: String) {
        if (!enabled()) return
        if (posthogConfigured) PostHog.screen(screenTitle = name)
        firebaseAnalytics?.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply { putString(FirebaseAnalytics.Param.SCREEN_NAME, name) },
        )
    }

    fun setUser(userId: String, email: String? = null) {
        if (!enabled()) return
        if (posthogConfigured) {
            PostHog.identify(
                distinctId = userId,
                userProperties = email?.let { mapOf("email" to it) },
            )
        }
        firebaseAnalytics?.setUserId(userId)
        runCatching {
            FirebaseCrashlytics.getInstance().setUserId(userId)
            email?.let { FirebaseCrashlytics.getInstance().setCustomKey("email", it) }
        }
    }

    /** Crash / non-fatal reporting. NOT gated by the analytics toggle — always reported. */
    fun logError(throwable: Throwable, message: String? = null) {
        if (!KitConfig.ANALYTICS_ENABLED) return
        runCatching {
            message?.let { FirebaseCrashlytics.getInstance().log(it) }
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    /** Clear the identified user (call on sign-out). */
    fun reset() {
        if (posthogConfigured) PostHog.reset()
        firebaseAnalytics?.setUserId(null)
    }

    fun flush() {
        if (posthogConfigured) PostHog.flush()
    }

    private fun enabled(): Boolean = KitConfig.ANALYTICS_ENABLED && userEnabled

    private fun applyEnabledToProviders(enabled: Boolean) {
        if (posthogConfigured) {
            if (enabled) PostHog.optIn() else PostHog.optOut()
        }
        firebaseAnalytics?.setAnalyticsCollectionEnabled(enabled)
    }
}

private fun Map<String, Any>.toBundle(): Bundle = Bundle().also { bundle ->
    forEach { (key, value) ->
        when (value) {
            is String -> bundle.putString(key, value)
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is Double -> bundle.putDouble(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            else -> bundle.putString(key, value.toString())
        }
    }
}
