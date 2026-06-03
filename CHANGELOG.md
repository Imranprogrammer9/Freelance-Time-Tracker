# Changelog

Release notes for the app. Keep an `## [Unreleased]` section at the top and move
entries under a version heading on each release.

For Play Store release notes per version, write to
`playstore/changelogs/<versionCode>.txt` (the `/kit-upload-on-google-play`
command handles this).

## [Unreleased]

### Added (2026-06-04 — Phase 7 site bootstrap + screenshots command)
- New `/kit-generate-screenshots` standalone command. Promoted from
  inline-in-/kit-upload-on-google-play to first-class so devs can refresh
  shots without re-walking the release flow. Step 0 detects existing PNGs
  (keep/regenerate/add-more); Step 1 generate-via-skill (uses
  `aso-appstore-screenshots`, Play Store dimensions 1080×1920) OR
  provide-own PNGs.
- `/kit-upload-on-google-play` Step E (first-version + update) now invokes
  `/kit-generate-screenshots` inline.
- `/kit-start-setup` outro optional-commands list gains
  `/kit-generate-screenshots`.
- **Phase 7 sales+docs site bootstrap** in separate repo
  https://github.com/wajahatkarim3/shipkit-site (private):
  - Next.js 14 + Tailwind 3 + TypeScript landing page scaffold
  - 12 hand-rolled components covering Header / Hero / PlatformStrip /
    TrustStrip / FeatureCards / ShowDontTell / TechStack / Comparison /
    Pricing / FAQ / FinalCta / Footer
  - Brand decisions locked: ShipKit name, kit.shipkaro.dev subdomain,
    LemonSqueezy MoR, $29 early-bird (first 30) → $49 regular, cohort
    100%-off coupon, 14-day refund, WhatsApp community, autoplay video
    hero, generic "other kits" comparison, multi-platform Coming Soon
    teaser for iOS/Flutter/React Native/Web

### Added (2026-06-02 — 8 full-screen permission flows)
- `core/designsystem/permission/KitPermissionScreen.kt` — parameterised
  full-screen permission flow (icon chip + title + rationale + Allow + Skip).
  Auto-swaps to "Open Settings" + denied-state rationale on permanent
  denial.
- `feature/permissions/PermissionScreens.kt` — 8 pre-instantiated screens:
  `NotificationsPermissionScreen`, `CameraPermissionScreen`,
  `MicrophonePermissionScreen`, `PhotoPermissionScreen`,
  `ContactsPermissionScreen`, `CalendarPermissionScreen`,
  `LocationPermissionScreen`, `MotionPermissionScreen`.
- `KitPermission` enum gains MICROPHONE, PHOTO, CONTACTS, CALENDAR, MOTION.
- `KitPermissionState` handles SDK gates: PHOTO falls back to
  `READ_EXTERNAL_STORAGE` on API < 33; MOTION auto-grants on API < 29.
- 5 new `KitIcons` slots (`microphone`, `photo`, `contacts`, `calendar`,
  `motion`) wired across Material / Feather / Tabler packs.
- 32 permission strings (`permission_<name>_{title,rationale,denied}`) + 4
  shared button labels (`permission_action_{allow,enable,skip,open_settings}`).
- 8 commented-out `<uses-permission>` opt-in lines in AndroidManifest.

### Changed (2026-06-02 — 8 full-screen permission flows)
- Removed legacy `permission_allow` / `permission_not_now` /
  `permission_open_settings` + duplicate `permission_*_title` / `_rationale`
  strings from the old primer section. `KitPermissionState` KDoc updated
  to reference the new `permission_action_*` keys.

### Added (2026-06-02 — runtime feedback pass)
- `/kit-env-check` command — cross-platform per-machine env probe (JDK 17,
  `ANDROID_HOME`, adb, android CLI, Android Skills, optional scrcpy + Holo).
  `uname`-branched install commands for macOS / Windows / Linux. References
  `shipkaro.dev/mobile-docs/getting-started/setup-macos` + `.../setup-windows`.
- `/kit-upload-on-google-play` Step D — release SHA-1 register for Google
  sign-in. Detects `KitConfig.GOOGLE_SIGN_IN_ENABLED` + `AUTH_PROVIDER`;
  3 sub-steps (Play App Signing vs self-managed → keytool / Play Console SHA-1
  read → register in Google Cloud Console or Firebase). First-version path
  only.
- `Step 0 — Detect existing state` preamble added to every `/kit-setup-*`
  command (8 files). Re-runs now resume from where they left off instead of
  re-asking every question.
- `feature/licenses/OssLicensesScreen.kt` — Compose-native OSS licenses
  screen using `LibrariesContainer` from `aboutlibraries-compose-m3:11.6.2`.
  `Route.OssLicenses` wired in `KitNavHost`. `SettingsScreen` takes new
  `onOssLicenses` callback.
- Holo (`measure-sh/holo`) added as recommended optional terminal-first
  Android profiler in `/kit-env-check` and `/kit-start-setup` outro.

### Changed (2026-06-02 — runtime feedback pass)
- `/kit-setup-auth` Step 4a (Supabase + Google) rewritten with the correct
  flow: two Google Cloud OAuth clients (Web for Supabase + Android for
  Credential Manager). Previous copy wrongly implied Supabase generates the
  Web Client ID. Now 6 paced sub-steps: OAuth consent screen → Create Web
  client (with Supabase callback as Authorized redirect URI) → Connect
  Supabase to that Web client → Add in-app redirect URL → Grab debug SHA-1
  (wait for "yes") → Create Android client.
- `/kit-setup-auth` Steps 4a + 4b: paced sub-steps with stop-and-wait gates;
  no shell command runs before explicit "yes".
- `/kit-setup-paywall` Step 2 rewritten for current RevenueCat dashboard
  (Apps + API keys are top-level sidebar items, not under Project Settings).
  6 paced sub-steps including release-time info block covering Play Console
  products + service account JSON + offerings + paywall design.
- `/kit-setup-analytics` Steps 2/3/4 paced with per-provider waits;
  PostHog / Firebase / Sentry never chained in one message.
- `/kit-start-setup` task list 8 → 7 steps. Step 7 Ops removed; Step 8 Build
  promoted to Step 7. Step 0 feature-list Ops bullet removed. Wrap-up gained
  optional-commands outro listing 8 commands (kit-env-check, kit-setup-updates,
  kit-setup-review-dialog, kit-setup-ai, kit-translate, kit-design-onboarding,
  kit-design-app, kit-upload-on-google-play). Step 0 prereq block top points
  at `/kit-env-check` for env-level failures.
- `/kit-start-setup` task list `Brand & theme` → `Brand and theme` and
  `Build & run` → `Build and run` (TaskList HTML-encodes `&` in subjects).
- `/kit-upload-on-google-play` task list renumbered D–K (Step D inserted).
- OSS Licenses swapped from `play-services-oss-licenses` (Google Play
  Services only — blank for kit deps) to `mikepenz/aboutlibraries-compose-m3`
  (scans full Gradle dep tree).
- Removed `play-services-oss-licenses` lib + `oss-licenses` plugin +
  `settings.gradle.kts` plugin-resolution mapping for the gms plugin.

### Fixed (2026-06-02 — runtime feedback pass)
- App crash at launch on any clone without `sentry.dsn` configured: added
  `<meta-data android:name="io.sentry.auto-init" android:value="false" />`
  to `<application>` so Sentry's `SentryInitProvider` ContentProvider stops
  trying to init before `KitApplication.onCreate` runs.
- `OssLicensesMenuActivity` blank screen — root cause was the gms plugin
  not generating entries for non-Google deps. Replaced w/ AboutLibraries.

### Added (2026-06-02 — 19-component SwiftStarterKits-parity pass)
- **KitSnackbar (Light-style)** — 4-variant severity-tagged snackbars (INFO /
  SUCCESS / WARNING / ERROR). `KitSnackbarController` w/ `.info()/.success()
  /.warning()/.error()` helpers + `rememberKitSnackbarController()` + drop-in
  `KitSnackbarHost`. Falls back to Material default for plain-string callers.
- **KitPullToRefresh** — wrapper over Material 3 `PullToRefreshBox`.
- **KitFab** — single-mode + extended FAB.
- **KitBottomNavScaffold + KitBottomNavBar + KitBottomNavItem** — Scaffold w/
  `NavigationBar` slot, badged items supported.
- **kitShimmer modifier + KitSkeleton + KitSkeletonListItem** — looping linear
  gradient sweep; canonical loading-placeholder pattern.
- **KitSlider** — Material 3 Slider w/ label + value read-out.
- **KitStepper** — integer +/- stepper, clamped to a range.
- **KitSegmentedButton** — single-choice segmented row (SwiftUI Picker
  `.segmented` equivalent).
- **KitDatePicker + KitInlineDatePicker** — Material 3 DatePickerDialog
  wrapper + inline variant.
- **KitDropdownMenu** — exposed-dropdown picker (SwiftUI Picker `.menu`
  equivalent).
- **KitSearchField** — single-line search w/ leading magnifier + clear button
  + IME Search action.
- **KitSwipeableRow** — SwipeToDismissBox wrapper, left/right callbacks,
  state auto-resets per swipe.
- **KitLinearProgress + KitIndeterminateLinearProgress**.
- **KitDisclosureGroup** — expandable / collapsible group (SwiftUI
  DisclosureGroup equivalent), stateless + stateful overloads.
- **KitLazyGrid + KitAdaptiveLazyGrid** — fixed-column + adaptive-column
  vertical grids w/ kit defaults.
- **KitBadge** — number / text / dot badge overlaid on a target via
  `BadgedBox`.
- **KitRatingBar** — 5-star tappable rating, tap same star to clear.
- **KitOtpField** — N-cell OTP code entry, transparent BasicTextField over
  rendered cells.
- **KitWebView** — embedded WebView; JS off by default. Use
  `CustomTabs.openUrl` for external links.
- **EmailLauncher** — `mailto:` intent w/ subject + body + chooser-title.
- **KitHaptics + rememberKitHaptics** — semantic haptic helpers
  (`lightTap()` / `mediumTap()` / `longPress()` / `success()`).
- 7 new `Components catalog` sections live-preview every addition above.
- `kit_datepicker_*` and 40+ catalog strings.

### Added (2026-06-01 — Components catalog screen)
- New `feature/catalog/ComponentsCatalogScreen.kt` — live preview of every kit
  component grouped by section (buttons, inputs, chips, cards/lists, banners,
  avatars, paywall pieces, state views, settings rows, dialogs/sheets, ops
  sheets). Pure dummy data; every onClick is a no-op or local state.
- `Route.ComponentsCatalog` + wiring in `KitNavHost` (treated as post-auth for
  the sign-out redirect).
- `HomeScreen` adds a primary "Browse kit components" button above the
  existing "Open settings" button.
- 80+ new `catalog_*` strings.

### Added (2026-06-01 — Sentry pass)
- Sentry crash + breadcrumb reporting (`io.sentry:sentry-android:7.20.0`) +
  new `core/log/SentryTree.kt`. Plants alongside `CrashlyticsTree` in release
  when `KitConfig.SENTRY_ENABLED` is true AND `BuildConfig.SENTRY_DSN` is
  non-blank. INFO/WARN logs become Sentry breadcrumbs; ERROR/ASSERT becomes
  `captureException()` (with throwable) or `captureMessage(SentryLevel.ERROR)`
  (without).
- `KitConfig.SENTRY_ENABLED` (default false).
- `BuildConfig.SENTRY_DSN` from `local.properties` (`sentry.dsn`). Template
  updated.
- `/kit-setup-analytics` Step 1 multi-select now includes **Sentry** alongside
  PostHog / Firebase Analytics / Crashlytics. New Step 4 walks the DSN-paste
  path; both Crashlytics + Sentry can run in parallel.

### Added (2026-06-01)
- Timber logging (`com.jakewharton.timber:timber:5.0.1`) + new
  `core/log/CrashlyticsTree.kt`. `KitApplication.onCreate` plants `DebugTree`
  in debug builds and `CrashlyticsTree` in release (gated on
  `KitConfig.ANALYTICS_ENABLED`). `CrashlyticsTree` mirrors INFO+ to
  `Crashlytics.log()` and routes ERROR/ASSERT to `recordException()`.
- `core/util/PlayStoreLauncher.kt` — opens the app's Play Store listing
  (`market://` with `https://play.google.com/store/apps/details?id=…`
  fallback). Settings → Rate now uses this; `InAppReviewManager` is reserved
  for `/kit-setup-review-dialog`.
- `core/security/SecureDataStore.kt` — AES-256/GCM encrypted key-value store
  for dev-held secrets. Master key in Android Keystore. Ciphertext + IV
  packed + Base64'd into a dedicated DataStore Preferences file
  `kit_secure_prefs`. Single Koin singleton.
- `core/ai/` package — `OpenRouterAiClient`, `OpenRouterAiRepository`,
  `OpenRouterAuthInterceptor`, `AiModels`. Provider-generic methods
  (`generateText`, `generateTextWithMessages`, `streamText`, `generateJson`)
  with model-first signature. Separate named Retrofit `retrofit_openrouter`
  (base URL `https://openrouter.ai/api/v1/`) — never collides with
  `KitConfig.API_BASE_URL`.
- `KitConfig.OPENROUTER_ENABLED` (default false) + `OPENROUTER_DEFAULT_MODEL`
  (default `meta-llama/llama-3.2-3b-instruct:free`).
- `BuildConfig.OPENROUTER_API_KEY` from `local.properties`
  (`openrouter.api.key`). Template updated.
- `/kit-setup-ai` command — collects OpenRouter key, picks a default model
  bucket (Fast & free / Cheap workhorse / Premium reasoning / pick own),
  flips `OPENROUTER_ENABLED`.
- `/kit-setup-review-dialog` command — wires
  `InAppReviewManager.requestReview()` at one of four trigger styles (Nth
  launch / key action / time delay / manual) with a `reviewPrompted`
  DataStore flag.
- `/kit-plan-release-analytics` command — pre-release funnel planner. Scans
  changelog + existing events + screens, derives a release-goal question,
  suggests 3–5 events, and auto-inserts `analytics.logEvent(...)` at Grep'd
  call-sites. Wired into `/kit-upload-on-google-play` (first-version Step G,
  update Step F).
- `/kit-generate-legal` command — scans `libs.versions.toml` + `KitConfig` +
  Retrofit annotations + Supabase `.from()` calls, asks 8 legal questions,
  writes `playstore/privacy_policy.md` + `.html` + filled
  `playstore/play_data_safety.md`. Wired into `/kit-upload-on-google-play`
  (first-version Step F, update Step A regenerates on SDK change).
- OSS licenses screen — Google's `oss-licenses-plugin:0.10.6` +
  `play-services-oss-licenses:17.1.0`. Settings → About → "Open source
  licenses" launches `OssLicensesMenuActivity` with a localized title.
  `settings.gradle.kts` carries a `resolutionStrategy.eachPlugin` mapping
  because the plugin has no plugin-marker artifact.

### Changed (2026-06-01)
- Settings → Rate row swapped from `InAppReviewManager.requestReview()` to
  `PlayStoreLauncher.openListing()`. Play's in-app review API is
  quota-throttled and silently no-ops most of the time — sending users to
  the listing always works and lets them see other reviews. The Play in-app
  review prompt now lives behind `/kit-setup-review-dialog` (wire it at the
  trigger that fits the app, not the Settings row).
- `CustomTabs.kt` replaced `android.util.Log.w` with `Timber.tag(...).w(...)`
  (the only `Log.x` call in the kit).
- `local.properties.template` — new `openrouter.api.key` entry.
- `/kit-upload-on-google-play` — first-version Step F now runs
  `/kit-generate-legal` instead of pointing at `legal/play-data-safety.md`
  manually. New Step G "Plan release analytics" runs
  `/kit-plan-release-analytics`. Steps renumbered (H = Create Console app,
  I = Build AAB, J = Manual upload). Update path Step A now asks whether any
  SDKs changed and runs `/kit-generate-legal` on a yes. Update path Step F
  also runs `/kit-plan-release-analytics`.

### Added (2026-05-31)
- `aso-googleplay-listing` skill at `.claude/skills/` — Play Store listing-copy
  generator (replaces the placeholder iOS clone with a Play-specific three-field
  flow: app name + short_description + full_description, keyword strategy baked
  into prose).
- `onboarding-questionnaire` skill at `.claude/skills/` — Calm / Headspace-style
  10–14 screen interactive onboarding generator (Compose native; scoped to
  `feature/onboarding/`, `Route.kt`, `KitNavHost.kt`, `SettingsRepository.kt`,
  `values/strings.xml`).
- `/kit-design-onboarding` command — standalone wrapper around the
  `onboarding-questionnaire` skill.
- `/kit-design-app` Phase 1B.5 — AskUserQuestion "Simple 3-page intro" vs
  "Personalised questionnaire"; the questionnaire pick invokes the
  `onboarding-questionnaire` skill before Phase 1C runs.
- `/kit-setup-theme` Step 2 "Icon pack" — Material (default) / Feather / Tabler /
  Pick-another (Simple Icons, Font Awesome, Eva, Octicons, Line Awesome, Linea,
  Weather, CSS GG). Each option carries the pack's preview URL. Picking a non-
  default pack rewires `LocalKitIcons` and reversibly disables the unused packs
  by commenting catalog + build lines and wrapping impl-file content in `/* */`.
- `TaskCreate` baked into `/kit-start-setup`, `/kit-design-app`, and
  `/kit-upload-on-google-play` for deterministic step tracking; skipped steps
  marked `completed` with `[skipped] ` title prefix.
- Real ShipKit logo PNG ships as the default brand mark (replaces the
  hand-drawn placeholder vector). Adaptive-icon foreground insets it 19 % so
  the logo sits inside Android's 66 dp safe zone.

### Changed (2026-05-31)
- Paywall now uses **RevenueCat's prebuilt `Paywall` composable** from
  `com.revenuecat.purchases:purchases-ui:9.23.1` (standard Jetpack Compose for
  Android — the earlier "Compose Multiplatform conflict" note was wrong).
  Paywall design + copy live in the RC dashboard.
- `KitConfig.AUTH_ENABLED` default flipped to `false`. A fresh clone runs
  Onboarding → Home with no auth screen. `/kit-setup-auth` flips it back to
  `true` and sets a real provider in the same step.
- `/kit-setup-auth` provider question now offers **Supabase (recommended) +
  Firebase only**. Stub dropped — it had no production purpose. Stub stays as
  an internal fallback when `AUTH_ENABLED` is true but no real provider is
  picked.
- `/kit-setup-theme` Step 1 derives `LightPrimaryContainer`,
  `LightOnPrimaryContainer`, and all four Dark variants from `BrandPrimary`
  via HSL targets (hue preserved). Previously only `LightPrimary` was derived;
  dark stayed hardcoded purple.
- `ic_launcher_background` switched from `#2962FF` to `BrandPrimary #7C3AED`.
- `/kit-change-app-id` runs the rename immediately after validating the package
  format. The "Confirm? Run rename?" prompt and the `git status` pre-warning
  are gone.
- `/kit-start-setup` Step 0 feature list dropped the demo bullet, added
  Localization + Release bullets.

### Removed (2026-05-31)
- In-app `SplashScreen` + `Route.Splash` + `feature/splash/` — the Android OS
  cold-start splash already covers the gap. `KitNavHost` resolves the start
  route on initial composition under a blank `Box` with the theme background
  so the OS splash stays visible.
- Custom paywall UI in `PaywallScreen.kt` + `PurchaseViewModel.kt` + all
  `paywall_*` strings. Billing error strings (`billing_error_*`) kept — still
  used by `PurchaseManager` for programmatic errors.

### Fixed (2026-05-31)
- Free-app paywall leak: `/kit-start-setup` Step 5 free-app branch now sets
  `PAYWALL_ENABLED = false` before skipping; previously the orchestrator only
  skipped the question.
- Delete-account blank screen: race condition between `KitNavHost`'s sign-out
  redirect and `SettingsScreen`'s `LaunchedEffect(deleteStatus == Done) {
  onBack() }`. The redundant `onBack()` was popping the just-pushed Auth and
  leaving an empty back stack. Removed; redirect alone handles navigation.
- `/kit-start-setup` Step 2 (Onboarding content) stopped mentioning Urdu so
  Claude no longer narrates "Urdu was removed" during setup.
- `/kit-design-app` pre-flight no longer references `/kit-remove-demo` (the
  command was removed when the demo was nuked).

### Added
- Default app flow shipped: `Splash` (1.5s) → `Onboarding` (first launch only,
  DataStore-gated) → `Auth` (if `AUTH_ENABLED` + signed out) → `Paywall` (if
  `PAYWALL_ENABLED` + not premium + first time) → `Home` → `Settings` /
  `Profile` / `Changelog`. Sign-out from any post-auth screen routes back to
  Auth automatically.
- `HomeScreen` shows a **System status** panel (Auth / Paywall / Analytics /
  Remote config) reading live state from `AuthRepository.sessionState` +
  `PurchaseManager.isPremium` + `KitConfig`, so a non-coder can confirm at a
  glance that their setup choices took effect.
- New screens: `SplashScreen`, `HomeScreen` (replaces the old Welcome
  placeholder), `ProfileScreen` (read-only + sign out).
- `KitConfig` additions: `PRIVACY_URL`, `TERMS_URL`, `EMAIL_SIGN_IN_ENABLED`,
  `GOOGLE_SIGN_IN_ENABLED`.
- `core/util/CustomTabs.kt` — opens privacy / terms in Chrome Custom Tabs.
- Supabase as a `RemoteAppConfig` provider (`SupabaseRemoteAppConfig` reads a
  public `app_config` Postgres table). Selected via
  `KitConfig.RemoteConfigProvider.SUPABASE`. Reuses `supabase.url` /
  `supabase.key` from `local.properties`.
- `local.properties.template` with OS-specific SDK path guidance and per-key
  blocks for Supabase / RevenueCat / PostHog.
- Interactive setup command suite under `.claude/commands/` (all kit-* prefixed):
  `kit-start-setup`, `kit-change-app-id`, `kit-setup-theme`, `kit-setup-auth`,
  `kit-setup-firebase`, `kit-setup-paywall`, `kit-setup-analytics`,
  `kit-setup-updates`, `kit-compile-app`, `kit-run-app`, `kit-translate`,
  `kit-upload-on-google-play`, `kit-design-app`.
- `aso-googleplay-listing` skill at `.claude/skills/` — Play-specific listing
  copy generator (no iOS subtitle / keywords fields).

### Changed
- Sign-in methods are now per-toggle on `AuthScreen`: `EMAIL_SIGN_IN_ENABLED`
  and `GOOGLE_SIGN_IN_ENABLED` render the email form and the Google button
  independently.
- Settings → Account row, sign-out, delete-account now gated by `AUTH_ENABLED`
  (the Stub provider was leaking the rows into a no-auth app).
- Settings → Language row auto-hides when `LocaleManager.supported.size <= 1`.
- Settings → Privacy / Terms opens `KitConfig.PRIVACY_URL` / `TERMS_URL` in
  Chrome Custom Tabs.
- Release assets path moved from `fastlane/metadata/android/en-US/...` to
  `playstore/` (title.txt, short_description.txt, full_description.txt,
  screenshots/, changelogs/<versionCode>.txt).

### Removed
- Bundled demo (`feature/demo/`, `Route.Demo`, `demoModule`,
  `SAMPLE_FEATURE_ENABLED`, habit_* strings, `/kit-remove-demo` command). The
  demo was a sealed second app whose hardcoded auth contradicted the
  developer's no-auth choice — the kit's real flow now showcases every feature
  end-to-end without a parallel demo.
- Fastlane entirely (`fastlane/` dir, root `Gemfile`, `.gitignore` rules).
  `/kit-upload-on-google-play` uses manual Play Console upload.
- Bundled Urdu translations (`values-ur/`). The kit ships English-only by
  default; `/kit-translate` adds locales on demand.
- Dynamic color (`dynamicColor` parameter on `ShipKaroTheme`). The app always
  uses the brand-derived light/dark scheme so branding stays consistent.
- `.debug` `applicationIdSuffix` on debug builds (was causing Firebase
  package-name mismatch).
- Deep links / App Links setup step in `/kit-setup-updates` — out of scope for
  micro-SaaS apps.

### Fixed
- `refactorPackage` Gradle task now also rewrites KDoc cross-reference links
  (`[dev.shipkaro.kit.core...]`) instead of leaving stale package names.
- `/kit-setup-auth` Supabase / Firebase steps match the current dashboard UI
  (Project ID under Settings → General; anon key under API Keys → Legacy
  anon, service_role tab).
- `/kit-setup-analytics` PostHog step references the renamed **Project token**
  under Settings → General → Project token & ID.
