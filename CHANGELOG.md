# Changelog

Release notes for the app. Keep an `## [Unreleased]` section at the top and move
entries under a version heading on each release.

For Play Store release notes per version, write to
`playstore/changelogs/<versionCode>.txt` (the `/kit-upload-on-google-play`
command handles this).

## [Unreleased]

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
