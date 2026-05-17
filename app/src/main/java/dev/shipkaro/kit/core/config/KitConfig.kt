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

    /** Auth provider baked into the build. */
    enum class AuthProvider { SUPABASE, FIREBASE, NONE }

    /** Show onboarding flow on first launch. */
    const val ONBOARDING_ENABLED: Boolean = true

    /** Wire auth screens + auth-gated navigation. */
    const val AUTH_ENABLED: Boolean = true
    val AUTH_PROVIDER: AuthProvider = AuthProvider.SUPABASE

    /** Wire RevenueCat paywall + entitlement gating. */
    const val PAYWALL_ENABLED: Boolean = true

    /** Wire PostHog / Firebase analytics + Crashlytics. */
    const val ANALYTICS_ENABLED: Boolean = true

    /** Ship the demo sample feature (turn off before release). */
    const val SAMPLE_FEATURE_ENABLED: Boolean = true
}
