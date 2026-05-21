# Project: ShipKaro Android Kit
> Last updated: 2026-05-21 (Phase 3 monetization done)

## What This Is
A production-ready **pure Android native** (Kotlin + Jetpack Compose) starter kit that lets indie developers skip auth, paywall, analytics, and launch plumbing and ship a Play Store app fast. Primary use: handed to **ShipKaro Weekend** cohort attendees on Day 1 so live sessions focus on app logic, not boilerplate. Also sold standalone (ShipFast model: one-time price, lifetime updates) via a marketing landing page. iOS native variant is a future, separate effort — not in scope now.

## Context & References
- shipkaro.dev — community (300+), Wajahat Karim. Lists "The Kit" as launching soon.
- 1dayapp.shipkaro.dev — The Weekend cohort. Stack taught: Supabase, RevenueCat, PostHog, Firebase, Fastlane, Stitch/Figma.
- shipkaro.dev/mobile-docs — OLD KMM kit (reference ONLY, rebuild from scratch, pure Android). Feature parity target + docs IA reference.
- Comparable products studied: shipfa.st, swiftstarterkits.com, flutterfasttemplate.com (positioning, pricing, page structure).

## Tech Stack
- Language/UI: Kotlin, Jetpack Compose, Material 3, Navigation Compose 2.8 (type-safe routes; Nav3 deferred — see Decisions)
- Auth: Supabase (email + Google), Firebase Auth (toggle-able alternative)
- Paywall: RevenueCat
- Analytics: PostHog + Firebase Analytics + Crashlytics
- Backend: Supabase (Firebase optional)
- Launch/CI: Fastlane, GitHub Actions
- App config: runtime feature flags to toggle auth/paywall/modules
- Architecture: **single `app` module, package-by-feature** (no multi-module, no build-logic/buildSrc)
- Base template: cortinico/kotlin-android-template — keep ONLY `app` module; delete library-android / library-kotlin / library-compose
- Tooling (from cortinico): Gradle Kotlin DSL, version catalog, detekt + ktlint, GitHub Actions CI
- DI: **Koin** (no Hilt/KSP — simpler for beginner + AI vibe-coding audience)
- Patterns (from Drjacky/MVVMTemplate, flattened): MVVM, `NetworkResponse` sealed result, Navigation 3 type-safe, Material 3 + edge-to-edge. Drop build-logic, RxJava, KSP.
- Networking + persistence: **Retrofit + Room + DataStore** (max tutorial/AI coverage for beginners; veto window open)

## Project To-Do List
Check off as completed. Update the date when marking done.

### Phase 0: Foundation & Scaffolding  ✅ DONE 2026-05-18 (assembleDebug green)
- [x] Single `app` module scaffold (wrapper bootstrapped from cortinico template, no library modules)
- [x] Gradle Kotlin DSL + version catalog (AGP 8.5.2, Kotlin 2.0.21, Gradle 8.14.5)
- [x] Package-by-feature source layout (core/ + feature/*)
- [x] Koin DI setup (coreModule + dataModule + featureModule)
- [x] Networking layer: Retrofit + `NetworkResponse` sealed result + `safeApiCall`
- [x] Persistence: Room (`KitDatabase`) + DataStore (`SettingsRepository`)
- [x] Base theme + Material 3 + dynamic color + light/dark/system + edge-to-edge
- [x] Type-safe route graph (Navigation Compose 2.8 — see deviation note)
- [x] Code-config (`KitConfig`, compile-time) + runtime app-config contract (`RemoteAppConfig`)
- [x] Localization-ready: externalized strings, en + ur, per-app `LocaleManager`, locales_config
- [ ] Wire detekt to `:app` (currently root-only → detekt NO-SOURCE) — moved to Phase 5 CI

### Phase 1: Component Library + Design System (Swift-catalog parity)  ✅ DONE 2026-05-20 (assembleDebug green)
REORDERED to first — components are building blocks; auth/paywall/settings screens consume them. Building screens before components = double work.
- [x] `designsystem/foundation/` — Spacing, Shape, Elevation tokens + `KitTheme` ergonomic accessor
- [x] Icons via compose-icons lib `br.com.devsrsouza.compose.icons:{pack}:1.1.1` + **material-icons-extended retained as default**. KitIcons interface + 3 packs (Material default, Feather, Tabler). LocalKitIcons CompositionLocal. Per-callsite override supported. Other packs commented opt-in in version catalog.
- [x] `designsystem/components/` — KitButton (primary/secondary/text/loading), KitTextField, KitPasswordField, KitCard, KitListItem, KitBottomSheet, KitDialog, KitChip + KitFilterChip, KitAvatar, KitBanner
- [x] `designsystem/state/` — KitLoadingState / KitInlineLoading / KitEmptyState / KitErrorState / KitSuccessState (full-screen + inline via `fullScreen` param)
- [x] `designsystem/onboarding/` — KitOnboardingPager + KitPageIndicator
- [x] `designsystem/settings/` — SettingsSection, ToggleRow, NavRow, AccountRow, DangerRow, LegalLinks, SettingsDivider
- [ ] ~~Dynamic app icon~~ — DEFERRED (user request, revisit later)
- [ ] No in-app showcase screen — components documented in /docs (Phase 8)

### Phase 2: Auth + Account (Play compliance core) — built ON Phase 1 components  ✅ DONE 2026-05-21 (assembleDebug green)
- [x] Supabase auth: email/password + Google sign-in (Credential Manager primary + OAuth deeplink fallback)
- [x] Firebase Auth alternative (KitConfig.AUTH_PROVIDER toggle; Stub default, no creds needed to build)
- [x] Session handling + auth-gated navigation (SessionState Flow → nav guard redirects SignedOut → SignIn)
- [x] Settings screen (theme picker, account row, sign-out, delete account, legal links)
- [x] Account deletion + in-app data deletion flow (Play mandatory) — repo.deleteAccount + DataStore.clearAll + db.clearAllTables
- [x] Toolchain upgraded for supabase-kt 3.6.0: AGP 8.5.2→8.9.2, Kotlin 2.0.21→2.2.21, KSP 2.2.21-2.0.4, Room 2.6.1→2.7.2, compileSdk 34→36, Compose BOM 2024.09.02→2025.06.00. Targetsdk stays 35.

### Phase 3: Monetization — built ON Phase 1 components  ✅ DONE 2026-05-21 (assembleDebug green)
- [x] RevenueCat SDK wiring (`com.revenuecat.purchases:purchases:9.23.1`, native — no KMP) + entitlement checks + restore
- [x] `core/billing/PurchaseManager` — ported from KMM starter's manager pattern; extended w/ offerings + `purchase()`. isPremium/customerInfo/currentOffering StateFlows. Builds w/o API key (no-ops).
- [x] Paywall UI (hard + soft variants) — custom `PaywallScreen` on Phase 1 components (NOT RC's prebuilt `purchases-ui`). `KitConfig.PAYWALL_MODE` SOFT(skip)/HARD(blocking).
- [x] Onboarding → paywall combo flow — nav chains Onboarding → Auth → Paywall → Home; premium users auto-skip paywall.
- [x] BillingErrorCode enum + localized error mapping (en + ur); paywall strings externalized.

### Phase 4: Analytics + Ops  ✅ DONE 2026-05-21 (assembleDebug green)
- [x] PostHog events + Firebase Analytics — `AnalyticsManager` (ported from KMM starter, single native class)
- [x] Crashlytics crash reporting — `AnalyticsManager.logError` (always on, not gated by analytics toggle)
- [x] Remote config + feature flags — `FirebaseRemoteAppConfig` impl of `RemoteAppConfig`; `KitConfig.REMOTE_CONFIG_PROVIDER` (LOCAL default / FIREBASE)
- [x] Push notifications (FCM) + deep links / app links — `KitMessagingService` + `KitNotifications` channel; POST_NOTIFICATIONS perm; manifest service + https App Links intent-filter
- [x] Force / soft update gate (kill switch) — `UpdateManager` (reads min_supported_version / latest_version from RemoteAppConfig) + `UpdateGate` composable wrapping nav host
- [x] In-app review prompt — `InAppReviewManager` (ported from KMM, Play In-App Review API; FakeReviewManager in debug); Settings → About → Rate row

### Phase 5: Launch Tooling
- [ ] Fastlane: Play Store submission lane (internal track)
- [ ] Fastlane: screenshot generation + metadata
- [ ] GitHub Actions CI (build, test, lint, detekt/ktlint)
- [ ] Package-name refactor script
- [ ] Changelog generator tool
- [ ] Privacy policy + terms templates
- [ ] Play Data Safety mapping doc
- [ ] ASO assets (icon, screenshots, listing copy template)

### Phase 6: In-App Demo Showcase (`feature/demo/`)
Single-module. Clean placeholder Home is attendee's real start screen.
- [ ] Clean professional placeholder Home (replaces Phase 0 dummy screens)
- [ ] Dev-only "View sample app" button on placeholder, gated `BuildConfig.DEBUG && KitConfig.SAMPLE_FEATURE_ENABLED` (default true, never in release)
- [ ] Self-contained demo nav subtree under `feature/demo/` (nav wiring + fake data ONLY)
- [ ] Demo flow REUSES real designsystem components + real infra screens (auth/onboarding/paywall/settings) with fake data — living integration example, not fake copies
- [ ] Delete placeholder → demo entry auto-severed; delete `feature/demo/` → demo fully gone (2-step, both trivial)

### Phase 7: Landing / Sales Page
- [ ] ShipFast-style landing page (hero, problem/solution, features, pricing, FAQ, CTA)
- [ ] Standalone purchase flow / pricing tier(s)
- [ ] Cohort-attendee free-access path vs paid standalone

### Phase 8: Documentation
- [ ] Getting Started (requirements, macOS/Windows setup, first run)
- [ ] Architecture overview
- [ ] Component catalog docs (Swift-catalog parity: name + screenshot + usage; user provides screenshots)
- [ ] "Make it yours" 2-step demo-removal guide
- [ ] Guides (adding a screen, toggling modules, rename package)
- [ ] Permissions priming components (deferred from Phase 3; user provides specs here)
- [ ] Deployment + Troubleshooting

## Session Log
<!-- Update at END of each session -->
- **2026-05-18**: Discovery + planning. Studied shipfa.st, swiftstarterkits, flutterfasttemplate, shipkaro.dev, 1dayapp, mobile-docs. Locked scope: pure Android native, core 4 modules + Play compliance + Conversion + Ops packs, kit + landing page. Wrote this CLAUDE.md. Evaluated cortinico/kotlin-android-template vs Drjacky/MVVMTemplate → rejected multi-module for beginner/AI audience. Architecture locked: single `app` module package-by-feature, Koin DI, Retrofit/Room/DataStore.
- **2026-05-18 (cont.)**: Built Phase 0. Hand-scaffolded full source/config (build files, Koin graph, Material3 theme, type-safe nav, KitConfig/RemoteAppConfig, Room, DataStore, locale en+ur, 6 feature screens). Hit wrapper wall (can't author binary gradle-wrapper.jar; system Gradle 8.5 < AGP-required 8.7). Resolved by cloning cortinico template and overlaying its working wrapper (Gradle 8.14.5). `:app:assembleDebug` → **BUILD SUCCESSFUL**. Private repo created + pushed (github.com/wajahatkarim3/shipkaro-android-kit).
- **2026-05-18 (discussion)**: Locked demo/component model. Rejected `:demo` module (breaks single-module). Chosen: single-module, clean placeholder Home + dev-only debug-gated "View sample app" button → self-contained `feature/demo/` subtree reusing real components/infra with fake data. Reshaped Phase 3 (real component library, Swift-catalog parity) + Phase 6 (in-app demo showcase) + Phase 8 (catalog docs, removal guide). Permissions + screenshots deferred to Phase 8.
- **2026-05-18 (reorder)**: User flagged building auth before components = double work. Reordered: Phase 1 = Component Library + Design System, Phase 2 = Auth, Phase 3 = Monetization. Icons decision changed to compose-icons Gradle lib (feather+tabler default; composables.com in docs for Heroicons). **Next: Phase 1 (components, design system, icons).**
- **2026-05-20**: Built Phase 1. KEPT `material-icons-extended` (user reversed earlier drop decision). Designed pluggable icon system: `KitIcons` interface + 3 packs (Material default / Feather / Tabler) + `LocalKitIcons` CompositionLocal + per-callsite `ImageVector?` override. User extends by subclassing one of the `*Impl` open classes. Built foundation tokens (Spacing/Shape/Elevation), expanded color scheme (surfaceVariant/outline/error), full M3 typography. Wrote 10 core components, 5 state views, onboarding pager, 7 settings rows. Skipped dynamic app icon (deferred). `:app:assembleDebug` BUILD SUCCESSFUL. **Next: Phase 2 (Auth + Account on Phase 1 components).**
- **2026-05-21 (Phase 4 complete)**: Finished Ops. `FirebaseRemoteAppConfig` impl of `RemoteAppConfig` (generation-counter flow so collectors re-read after fetchAndActivate); `KitConfig.REMOTE_CONFIG_PROVIDER` LOCAL default. FCM: `KitMessagingService` + `KitNotifications` channel + POST_NOTIFICATIONS perm + manifest service & https App Links intent-filter (`shipkaro.dev` placeholder, autoVerify). Update gate: `UpdateManager` compares `BuildConfig.VERSION_CODE` against `min_supported_version`/`latest_version` remote keys → `UpdateGate` composable wraps nav host, REQUIRED = non-dismissible KitDialog, OPTIONAL = dismissible. In-app review: ported KMM `InAppReviewManager` (Play API, `FakeReviewManager` in debug), wired to Settings → About → Rate row. `logScreen` threaded into Auth/Paywall/Settings (Phase 0 placeholders skipped — they get replaced). All Firebase ops pieces inert until google-services.json added. `play-review` dep added (no KMP). `:app:assembleDebug` green. **Next: Phase 5 (Launch Tooling).**
- **2026-05-21 (Phase 4, partial)**: Started Analytics + Ops. Ported KMM starter's analytics layer — KMM had `Analytics` (expect/actual bridge) + `AnalyticsManager` (gated facade); native Android collapses to one `AnalyticsManager` class fanning out to PostHog (`com.posthog:posthog-android:3.44.2`) + Firebase Analytics + Crashlytics. Analytics user-toggle persisted in DataStore (`SettingsRepository.analyticsEnabled`), surfaced as a ToggleRow in Settings → Privacy section; crash reporting (`logError`) intentionally NOT gated by the toggle. PostHog key from `local.properties` (`posthog.api.key`/`posthog.host`) → BuildConfig. Firebase Analytics/Crashlytics inert until google-services.json + plugins added. `AnalyticsEvents`/`AnalyticsParams`/`ScreenNames` constants. `:app:assembleDebug` green. **Phase 4 remaining: remote config/feature flags, FCM push + deep links, update gate, in-app review.**
- **2026-05-21 (Phase 3)**: Built Monetization. Studied the ShipKaro KMM starter (`../shipkaro-kmm-starter`) per user — ported its `PurchaseManager` manager-pattern to native (`com.revenuecat.purchases:purchases:9.23.1`, no KMP). KMM used RC's prebuilt `Paywall` composable; kit diverges — custom `PaywallScreen` on Phase 1 components (hard + soft variants per CLAUDE.md), `purchases-ui` excluded to avoid Compose-Multiplatform dep conflict (same class as Phase 2's compose-auth-ui). PurchaseManager extended beyond KMM's (which only did refresh+restore) with `offerings` + `purchase()` since custom paywall owns purchasing. KitConfig: `ENTITLEMENT_ID` + `PAYWALL_MODE`; RevenueCat key in `local.properties` (`revenuecat.android.api.key`) → BuildConfig. `BillingErrorCode` enum + localized mapping. RevenueCat configured in KitApplication.onCreate. KMM starter also has an AnalyticsManager — noted for Phase 4. `:app:assembleDebug` green. **Next: Phase 4 (Analytics + Ops).**
- **2026-05-21**: Built Phase 2. Verified supabase-kt is the only first-party Android Supabase client (KMP under hood; quickstart user provided uses it); user picked supabase-kt with `ktor-client-okhttp` engine to share OkHttp with Retrofit. Hit toolchain wall — supabase-kt 3.6.0 transitives require AGP 8.9+ / Kotlin 2.2+ / compileSdk 36. Upgraded toolchain comprehensively (see Phase 2 checklist). Dropped `compose-auth` + `compose-auth-ui` artifacts — they drag Compose Multiplatform material3 (1.9.0-beta) + activity 1.12.x which conflict with androidx Material3. Designed provider-agnostic AuthRepository + 3 impls (Stub default, Supabase, Firebase). KitConfig.AUTH_PROVIDER picks via Koin. Google sign-in: Credential Manager primary (native bottomsheet) → falls back to OAuth deeplink (Custom Tabs + supabase.handleDeeplinks). KitConfig.GOOGLE_WEB_CLIENT_ID + local.properties SUPABASE_URL/KEY → BuildConfig. Account deletion + DataStore + Room wipe. User flagged hardcoded "Continue with Google" → externalised ALL Phase 2 strings to res/values/strings.xml + values-ur/strings.xml (en+ur); KitDialog dismissLabel made required (no English default); KitBanner contentDescription + KitPasswordField show/hide a11y strings localized. Added i18n rule to Decisions + saved memory. `:app:assembleDebug` BUILD SUCCESSFUL. **Next: Phase 3 (Monetization — RevenueCat).**

## Important Decisions Made
- **Pure Android native, not KMP** — Cohort 1 ran KMP; user found it a bad decision. iOS native deferred to a future separate effort.
- **Auth is dual**: Supabase primary, Firebase Auth toggle-able — broadens standalone-buyer appeal.
- **Single-module, package-by-feature** — multi-module rejected: target audience = Android beginners + AI vibe-coding + weekend ship; build-logic/convention-plugin indirection hurts them, multi-module payoff irrelevant at indie scale.
- **Base = cortinico template, app module only** — its library-android/kotlin/compose modules are library-publishing demos, not needed; keep tooling (version catalog, detekt/ktlint, CI).
- **DI = Koin** — no Hilt/KSP; less magic, faster builds, simpler for beginners + AI.
- **Patterns from MVVMTemplate, flattened** — MVVM + NetworkResponse + Material3/edge-to-edge; drop build-logic, RxJava, KSP.
- **DEVIATION: Navigation Compose 2.8 type-safe instead of androidx Navigation 3** — Nav3 still alpha, unsafe for a stability-critical beginner kit. Same type-safe-route goal delivered via kotlinx.serialization routes. Revisit when Nav3 stable.
- **Wrapper bootstrapped from cortinico template** — gradle-wrapper.jar is binary, not authorable. Cloned template, copied its Gradle 8.14.5 wrapper. Toolchain: **AGP 8.9.2, Kotlin 2.2.21, KSP 2.2.21-2.0.4, Room 2.7.2, JDK 17, compileSdk 36, targetSdk 35, minSdk 26, Compose BOM 2025.06.00.** Bumped from Phase 0's (8.5.2 / 2.0.21 / 34 / 2024.09.02) in Phase 2 to satisfy supabase-kt 3.6.0 transitive deps (androidx.browser 1.10.0 + ktor 3.4.x metadata).
- **Two-layer config** — `KitConfig` (compile-time, kit author edits) vs `RemoteAppConfig` (runtime, end-app wires to Supabase/Firebase). Kept strictly separate per user.
- **Net/persistence = Retrofit + Room + DataStore** — max tutorial/AI coverage (chose over old kit's Ktor for learnability).
- **Monetization = RevenueCat native SDK** (`com.revenuecat.purchases:purchases`) — `PurchaseManager` singleton manager-pattern ported from the ShipKaro KMM starter. Custom paywall on kit components (NOT RC's prebuilt `purchases-ui` — drags Compose-Multiplatform deps + needs hard/soft variants). API key in `local.properties`, entitlement id in `KitConfig`.
- **Sold standalone + free for cohort** — ShipFast pricing model (one-time, lifetime updates).
- **Checklist lives here in CLAUDE.md** (survives /compact), not in task tool — per user's stated workflow.
- **Demo = in-app, single-module, NOT a module/flavor/branch** — clean placeholder Home is attendee's real start; dev-only debug-gated button enters self-contained `feature/demo/` subtree. Demo reuses real components/infra with fake data (living example). Deletion: drop placeholder button (auto-severs) then `feature/demo/` folder. `:demo` Gradle module explicitly rejected — would force multi-module refactor we ruled out.
- **Component library, no in-app showcase** — Swift-catalog parity via well-organized `designsystem/` packages; documented in /docs, not a gallery screen. Infra screens built AS reusable components.
- **Phase 0 screens are throwaway tech debt** — replaced by real components across Phase 1–3/6.
- **Phases reordered (components first)** — Phase 1 = component library, then Phase 2 auth, Phase 3 monetization. Screens consume components; building screens first = double work (rejected, per user).
- **Icons = pluggable pack system, Material default** — `KitIcons` semantic interface + 3 bundled impls: `MaterialKitIcons` (default, material-icons-extended), `FeatherKitIcons`, `TablerKitIcons`. `LocalKitIcons` CompositionLocal lets `ShipKaroTheme(icons = …)` swap kit-wide in one line. Components consume via `KitTheme.icons.back` etc. User extends by subclassing `MaterialKitIconsImpl()` to override individual icons, or writes a custom `KitIcons` impl. Per-callsite override = any component taking an icon accepts an `ImageVector?`. `material-icons-extended` KEPT (reversed earlier drop). compose-icons feather+tabler shipped; others commented opt-in in catalog. R8 strips unused.
- **ALL user-facing strings MUST be localized** — never hardcode UI text. Every screen / VM-status / component contentDescription / dialog label / accessibility hint goes into `app/src/main/res/values/strings.xml` AND every `values-XX/strings.xml` shipped (currently en + ur). Components should accept String params (not hardcode English defaults) so callers stay localizable; where a sensible English fallback is unavoidable, source it via `stringResource()` so values-XX/ overrides work. Audit before commit: grep for hardcoded Western-script literals inside `Text(...)`, `contentDescription = "..."`, `label = "..."`, `confirmLabel = "..."`. Reason: kit ships en + ur, audience extends; missing string IDs silently leave English for RTL/non-English users. Tests + reviewers should reject hardcoded strings.

## Known Issues / Blockers
- [ ] detekt plugin applied at root only → `./gradlew detekt` = NO-SOURCE. Wire to `:app` in Phase 5 (CI relies on it).
- [ ] Retrofit baseUrl is a placeholder (`https://example.com/`) — real host set per app in Phase 1+.

## Commands to Remember
- Build debug APK: `./gradlew :app:assembleDebug --no-daemon`
- Compile only: `./gradlew :app:compileDebugKotlin`
- Lint (after Phase 5 wiring): `./gradlew detekt`
- Gradle wrapper = 8.14.5; do NOT run system `gradle` (8.5, too old for AGP)
- Add a language: values-XX/strings.xml + resourceConfigurations (app/build.gradle.kts) + res/xml/locales_config.xml

## Daily Workflow
- Session start: "Read CLAUDE.md and tell me where we left off."
- Session end: "Update CLAUDE.md: mark completed, add decisions, update session log."
