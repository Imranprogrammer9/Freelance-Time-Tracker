# Project: ShipKaro Android Kit
> Last updated: 2026-05-18 (Phase 0 unblocked — architecture locked)

## What This Is
A production-ready **pure Android native** (Kotlin + Jetpack Compose) starter kit that lets indie developers skip auth, paywall, analytics, and launch plumbing and ship a Play Store app fast. Primary use: handed to **ShipKaro Weekend** cohort attendees on Day 1 so live sessions focus on app logic, not boilerplate. Also sold standalone (ShipFast model: one-time price, lifetime updates) via a marketing landing page. iOS native variant is a future, separate effort — not in scope now.

## Context & References
- shipkaro.dev — community (300+), Wajahat Karim. Lists "The Kit" as launching soon.
- 1dayapp.shipkaro.dev — The Weekend cohort. Stack taught: Supabase, RevenueCat, PostHog, Firebase, Fastlane, Stitch/Figma.
- shipkaro.dev/mobile-docs — OLD KMM kit (reference ONLY, rebuild from scratch, pure Android). Feature parity target + docs IA reference.
- Comparable products studied: shipfa.st, swiftstarterkits.com, flutterfasttemplate.com (positioning, pricing, page structure).

## Tech Stack
- Language/UI: Kotlin, Jetpack Compose, Material 3, Navigation 3 (type-safe routes)
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

### Phase 1: Auth + Account (Play compliance core)
- [ ] Supabase auth: email/password + Google sign-in
- [ ] Firebase Auth alternative (toggle-able via app config)
- [ ] Session handling + auth-gated navigation
- [ ] Settings screen (theme, account, manage subscription, legal links)
- [ ] Account deletion + in-app data deletion flow (Play mandatory)

### Phase 2: Monetization
- [ ] RevenueCat SDK wiring + entitlement checks + restore
- [ ] Paywall UI (hard + soft variants)
- [ ] Onboarding → paywall combo flow (conversion pattern)

### Phase 3: Onboarding + Design System
- [ ] Onboarding flow (customizable steps)
- [ ] Reusable Compose component library
- [ ] Light/dark theming + dynamic app icon
- [ ] Empty / loading / error state components

### Phase 4: Analytics + Ops
- [ ] PostHog events + Firebase Analytics
- [ ] Crashlytics crash reporting
- [ ] Remote config + feature flags
- [ ] Push notifications (FCM) + deep links / app links
- [ ] Force / soft update gate (kill switch)
- [ ] In-app review prompt (Google Play In-App Review API)

### Phase 5: Launch Tooling
- [ ] Fastlane: Play Store submission lane (internal track)
- [ ] Fastlane: screenshot generation + metadata
- [ ] GitHub Actions CI (build, test, lint, detekt/ktlint)
- [ ] Package-name refactor script
- [ ] Changelog generator tool
- [ ] Privacy policy + terms templates
- [ ] Play Data Safety mapping doc
- [ ] ASO assets (icon, screenshots, listing copy template)

### Phase 6: Sample App
- [ ] End-to-end sample feature demonstrating the full pattern (auth → paywall → feature → analytics)

### Phase 7: Landing / Sales Page
- [ ] ShipFast-style landing page (hero, problem/solution, features, pricing, FAQ, CTA)
- [ ] Standalone purchase flow / pricing tier(s)
- [ ] Cohort-attendee free-access path vs paid standalone

### Phase 8: Documentation
- [ ] Getting Started (requirements, macOS/Windows setup, first run)
- [ ] Architecture overview
- [ ] Guides (adding a screen, toggling modules, rename package)
- [ ] Deployment + Troubleshooting

## Session Log
<!-- Update at END of each session -->
- **2026-05-18**: Discovery + planning. Studied shipfa.st, swiftstarterkits, flutterfasttemplate, shipkaro.dev, 1dayapp, mobile-docs. Locked scope: pure Android native, core 4 modules + Play compliance + Conversion + Ops packs, kit + landing page. Wrote this CLAUDE.md. Evaluated cortinico/kotlin-android-template vs Drjacky/MVVMTemplate → rejected multi-module for beginner/AI audience. Architecture locked: single `app` module package-by-feature, Koin DI, Retrofit/Room/DataStore.
- **2026-05-18 (cont.)**: Built Phase 0. Hand-scaffolded full source/config (build files, Koin graph, Material3 theme, type-safe nav, KitConfig/RemoteAppConfig, Room, DataStore, locale en+ur, 6 feature screens). Hit wrapper wall (can't author binary gradle-wrapper.jar; system Gradle 8.5 < AGP-required 8.7). Resolved by cloning cortinico template and overlaying its working wrapper (Gradle 8.14.5). `:app:assembleDebug` → **BUILD SUCCESSFUL**. **Next: Phase 1 (Supabase auth + account/Play-compliance).**

## Important Decisions Made
- **Pure Android native, not KMP** — Cohort 1 ran KMP; user found it a bad decision. iOS native deferred to a future separate effort.
- **Auth is dual**: Supabase primary, Firebase Auth toggle-able — broadens standalone-buyer appeal.
- **Single-module, package-by-feature** — multi-module rejected: target audience = Android beginners + AI vibe-coding + weekend ship; build-logic/convention-plugin indirection hurts them, multi-module payoff irrelevant at indie scale.
- **Base = cortinico template, app module only** — its library-android/kotlin/compose modules are library-publishing demos, not needed; keep tooling (version catalog, detekt/ktlint, CI).
- **DI = Koin** — no Hilt/KSP; less magic, faster builds, simpler for beginners + AI.
- **Patterns from MVVMTemplate, flattened** — MVVM + NetworkResponse + Material3/edge-to-edge; drop build-logic, RxJava, KSP.
- **DEVIATION: Navigation Compose 2.8 type-safe instead of androidx Navigation 3** — Nav3 still alpha, unsafe for a stability-critical beginner kit. Same type-safe-route goal delivered via kotlinx.serialization routes. Revisit when Nav3 stable.
- **Wrapper bootstrapped from cortinico template** — gradle-wrapper.jar is binary, not authorable. Cloned template, copied its Gradle 8.14.5 wrapper. Toolchain: AGP 8.5.2, Kotlin 2.0.21, JDK 17, compile/targetSdk 34, minSdk 26.
- **Two-layer config** — `KitConfig` (compile-time, kit author edits) vs `RemoteAppConfig` (runtime, end-app wires to Supabase/Firebase). Kept strictly separate per user.
- **Net/persistence = Retrofit + Room + DataStore** — max tutorial/AI coverage (chose over old kit's Ktor for learnability).
- **Sold standalone + free for cohort** — ShipFast pricing model (one-time, lifetime updates).
- **Checklist lives here in CLAUDE.md** (survives /compact), not in task tool — per user's stated workflow.

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
