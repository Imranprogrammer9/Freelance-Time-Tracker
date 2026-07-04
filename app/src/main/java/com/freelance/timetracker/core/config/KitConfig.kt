package com.freelance.timetracker.core.config

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

    /**
     * Wire auth screens + auth-gated navigation.
     *
     * Ships **false** by default so a fresh clone runs without showing a sign-in
     * screen. `/kit-setup-auth` flips this to `true` and sets [AUTH_PROVIDER] to a
     * real backend (Supabase or Firebase) at the same time. The [STUB] provider is
     * an internal fallback used when [AUTH_ENABLED] is true but no real provider
     * has been configured yet — it should NOT be picked deliberately for a
     * production app.
     */
    const val AUTH_ENABLED: Boolean = true
    val AUTH_PROVIDER: AuthProvider = AuthProvider.SUPABASE

    /** Show the email + password form on the auth screen. */
    const val EMAIL_SIGN_IN_ENABLED: Boolean = false

    /**
     * Show the "Continue with Google" button on the auth screen.
     * Native Google sign-in also needs [GOOGLE_WEB_CLIENT_ID]; without it the
     * button falls back to the deeplink OAuth flow.
     */
    const val GOOGLE_SIGN_IN_ENABLED: Boolean = true

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
    const val GOOGLE_WEB_CLIENT_ID: String = "827036908541-649mqtjj5adru236aua06226nir8g8pe.apps.googleusercontent.com"

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

    /**
     * Pre-registration reward — a **time-limited** premium unlock (set up by
     * `/kit-pre-register-setup`). Leave [PRE_REGISTER_REWARD_PRODUCT_ID] blank to disable
     * (the default — no behaviour change).
     *
     * Why this exists: a Play pre-registration reward must be a one-time product, and
     * RevenueCat grants a one-time product's entitlement **for life** — so a *timed* reward
     * (e.g. "30 days free") can't come from RevenueCat config. Instead, attach **no**
     * entitlement to the reward product in RevenueCat, set its product ID here, and
     * [PurchaseManager] grants premium for [PRE_REGISTER_REWARD_DURATION_DAYS] days measured
     * from the **Play purchase date** (read from `CustomerInfo`, server-side — survives
     * reinstalls, doesn't reset or stack).
     */
    const val PRE_REGISTER_REWARD_PRODUCT_ID: String = ""
    const val PRE_REGISTER_REWARD_DURATION_DAYS: Int = 30

    /** Wire PostHog / Firebase analytics + Crashlytics. */
    const val ANALYTICS_ENABLED: Boolean = true

    /**
     * Wire Sentry error + breadcrumb reporting alongside (or instead of) Firebase Crashlytics.
     *
     * Both crash reporters can run in parallel — each gets its own `Timber.Tree`. Use Sentry
     * when:
     *  - you need richer breadcrumb / release-health features than Crashlytics offers,
     *  - you want crash reporting without Firebase / `google-services.json`,
     *  - your team already has a Sentry org and dashboard set up.
     *
     * Inert until [SENTRY_ENABLED] is true AND `local.properties` has `sentry.dsn` set
     * (sentry.io → Project → Settings → Client Keys → DSN).
     */
    const val SENTRY_ENABLED: Boolean = false

    /**
     * Backend for [RemoteAppConfig] (runtime feature flags + the update gate).
     *  - [LOCAL]    : no-op, returns defaults. Default — builds & runs offline.
     *  - [FIREBASE] : Firebase Remote Config. Requires `google-services.json` + plugin.
     *  - [SUPABASE] : reads a public `app_config` Postgres table on Supabase. Reuses
     *                 `supabase.url` / `supabase.key` from `local.properties`, so no extra
     *                 credentials are needed if the app already uses Supabase auth.
     */
    enum class RemoteConfigProvider { LOCAL, FIREBASE, SUPABASE }
    val REMOTE_CONFIG_PROVIDER: RemoteConfigProvider = RemoteConfigProvider.LOCAL

    /**
     * Base URL for the Retrofit API client. Placeholder by default — set this to your real
     * API host before shipping. Must end with `/`. If your app has no REST backend, you can
     * leave it as-is; the Retrofit instance is only created when something injects it.
     */
    const val API_BASE_URL: String = "https://example.com/"

    /**
     * Wire the OpenRouter AI client + Koin bindings for [OpenRouterAiRepository].
     *
     * OpenRouter (https://openrouter.ai) proxies 100+ models from Anthropic, OpenAI,
     * Google, Meta, Mistral, etc. behind one API. Set the key in `local.properties`
     * (`openrouter.api.key`) → it lands in BuildConfig.OPENROUTER_API_KEY.
     *
     * The OpenRouter client is provider-specific by design: its Retrofit instance,
     * base URL, and auth header are isolated from the app's own [API_BASE_URL]
     * Retrofit. Adding your own backend never collides with this client.
     */
    const val OPENROUTER_ENABLED: Boolean = false

    /**
     * Default OpenRouter model used by [OpenRouterAiRepository] when callers don't
     * pass a specific model. Pick from https://openrouter.ai/models. Cheap fast
     * default: `meta-llama/llama-3.2-3b-instruct:free`. Heavy reasoning: try
     * `anthropic/claude-sonnet-4-6` or `google/gemini-2.5-flash`.
     */
    const val OPENROUTER_DEFAULT_MODEL: String = "meta-llama/llama-3.2-3b-instruct:free"

    /**
     * Privacy policy URL. Opened from Settings → Privacy via Chrome Custom Tabs.
     * Replace with your real published policy before shipping. Empty = link disabled.
     */
    const val PRIVACY_URL: String = "https://example.com/privacy"

    /**
     * Terms of Service URL. Opened from Settings → Terms via Chrome Custom Tabs.
     * Replace with your real published terms before shipping. Empty = link disabled.
     */
    const val TERMS_URL: String = "https://example.com/terms"
}
