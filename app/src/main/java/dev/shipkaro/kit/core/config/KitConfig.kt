package dev.shipkaro.kit.core.config

/**
 * CODE-CONFIG (compile-time template switches).
 *
 * This is the kit-level "what features exist in this template" config.
 * Flip these to strip whole features out of the build before you start.
 * It is NOT a runtime flag system.
 *
 * Difference from runtime app-config:
 *  - [KitConfig]            -> compile-time, edited in code, ships fixed in the APK.
 *                              Decides which modules/screens are wired at all.
 *  - [RemoteAppConfig]      -> runtime, fetched from Supabase / Firebase by the
 *                              END app the developer builds with this kit.
 *                              Decides behaviour per release without recompiling.
 *
 * Rule of thumb: kit author edits [KitConfig]; app users wire [RemoteAppConfig].
 */
object KitConfig {

    /**
     * Auth provider baked into the build.
     *
     *  - [STUB]     : in-memory dev provider, no credentials needed. Default — lets the kit build & run
     *                 immediately. Sign-in always succeeds with a fake user.
     *  - [SUPABASE] : real Supabase auth. Requires `supabase.url` + `supabase.key` in `local.properties`.
     *  - [FIREBASE] : Firebase Auth. Requires `google-services.json` at `app/` + applying the
     *                 `com.google.gms.google-services` plugin in `app/build.gradle.kts`.
     */
    enum class AuthProvider { STUB, SUPABASE, FIREBASE }

    /** Show onboarding flow on first launch. */
    const val ONBOARDING_ENABLED: Boolean = true

    /** Wire auth screens + auth-gated navigation. Set false to ship a no-auth app. */
    const val AUTH_ENABLED: Boolean = true
    val AUTH_PROVIDER: AuthProvider = AuthProvider.STUB

    /**
     * Web OAuth client ID used by Credential Manager for native Google sign-in.
     *
     * Where to get it:
     *  - Supabase backend: Supabase Auth → Providers → Google → "Authorized client IDs"
     *  - Firebase backend: Firebase Console → Project settings → "Web client ID"
     *  - Or Google Cloud Console → Credentials → OAuth 2.0 client (type "Web application")
     *
     * Leave empty to disable native Google sign-in (the AuthScreen falls back to deeplink
     * OAuth, which works without a web client ID).
     */
    const val GOOGLE_WEB_CLIENT_ID: String = ""

    /** Wire RevenueCat paywall + entitlement gating. */
    const val PAYWALL_ENABLED: Boolean = true

    /**
     * RevenueCat entitlement identifier checked to decide premium status. Must match the
     * entitlement configured in the RevenueCat dashboard. The RevenueCat API key itself is
     * NOT here — it lives in `local.properties` (`revenuecat.android.api.key`) → BuildConfig,
     * since it's environment config, not a template switch. Empty key = PurchaseManager no-ops.
     */
    const val ENTITLEMENT_ID: String = "premium"

    /**
     * Paywall enforcement style.
     *  - [SOFT] : paywall is dismissible — user can skip and continue free.
     *  - [HARD] : paywall blocks access until purchase or restore (no skip).
     */
    enum class PaywallMode { SOFT, HARD }
    val PAYWALL_MODE: PaywallMode = PaywallMode.SOFT

    /** Wire PostHog / Firebase analytics + Crashlytics. */
    const val ANALYTICS_ENABLED: Boolean = true

    /**
     * Backend for [RemoteAppConfig] (runtime feature flags + the update gate).
     *  - [LOCAL]    : no-op, returns defaults. Default — builds & runs offline.
     *  - [FIREBASE] : Firebase Remote Config. Requires `google-services.json` + plugin.
     */
    enum class RemoteConfigProvider { LOCAL, FIREBASE }
    val REMOTE_CONFIG_PROVIDER: RemoteConfigProvider = RemoteConfigProvider.LOCAL

    /** Ship the demo sample feature (turn off before release). */
    const val SAMPLE_FEATURE_ENABLED: Boolean = true
}
