# Changelog

Release notes for the app. Keep an `## [Unreleased]` section at the top and move
entries under a version heading on each release.

For Play Store release notes per version, write to
`playstore/changelogs/<versionCode>.txt` (the `/kit-upload-on-google-play`
command handles this).

## [Unreleased]

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
