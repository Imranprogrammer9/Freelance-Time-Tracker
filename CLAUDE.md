# Project: ShipKaro Android Kit
> Last updated: 2026-05-18 (phases reordered: components first)

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

### Phase 1: Component Library + Design System (Swift-catalog parity)
REORDERED to first — components are building blocks; auth/paywall/settings screens consume them. Building screens before components = double work.
- [ ] `designsystem/foundation/` — polish color, type, spacing, theme tokens
- [ ] Icons via **compose-icons lib** `br.com.devsrsouza.compose.icons:{pack}:1.1.1`. Default packs: feather + tabler-icons (Heroicons-like outline). Others (simple-icons, font-awesome, eva, octicons, line-awesome, linea, weather, css-gg) listed commented in version catalog as opt-in. R8 strips unused. Heroicons has NO module → docs point to composables.com/icons for Heroicons copy-paste + extras. Drop material-icons-extended dep.
- [ ] `designsystem/components/` — Button (primary/secondary/text/loading), TextField, PasswordField, Card, ListItem, BottomSheet, Dialog, Chip, Avatar, Banner
- [ ] `designsystem/state/` — Loading / Empty / Error / Success (full-screen + inline)
- [ ] `designsystem/onboarding/` — pager + page indicator components
- [ ] `designsystem/settings/` — section, toggle row, nav row, account row, danger/delete row, legal links
- [ ] Dynamic app icon
- [ ] No in-app showcase screen — components documented in /docs (Phase 8)

### Phase 2: Auth + Account (Play compliance core) — built ON Phase 1 components
- [ ] Supabase auth: email/password + Google sign-in
- [ ] Firebase Auth alternative (toggle-able via KitConfig)
- [ ] Session handling + auth-gated navigation
- [ ] Settings screen (theme, account, manage subscription, legal links)
- [ ] Account deletion + in-app data deletion flow (Play mandatory)

### Phase 3: Monetization — built ON Phase 1 components
- [ ] RevenueCat SDK wiring + entitlement checks + restore
- [ ] Paywall UI (hard + soft variants)
- [ ] Onboarding → paywall combo flow (conversion pattern; pager from Phase 1)

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
- **Demo = in-app, single-module, NOT a module/flavor/branch** — clean placeholder Home is attendee's real start; dev-only debug-gated button enters self-contained `feature/demo/` subtree. Demo reuses real components/infra with fake data (living example). Deletion: drop placeholder button (auto-severs) then `feature/demo/` folder. `:demo` Gradle module explicitly rejected — would force multi-module refactor we ruled out.
- **Component library, no in-app showcase** — Swift-catalog parity via well-organized `designsystem/` packages; documented in /docs, not a gallery screen. Infra screens built AS reusable components.
- **Phase 0 screens are throwaway tech debt** — replaced by real components across Phase 1–3/6.
- **Phases reordered (components first)** — Phase 1 = component library, then Phase 2 auth, Phase 3 monetization. Screens consume components; building screens first = double work (rejected, per user).
- **Icons = compose-icons Gradle lib** (`br.com.devsrsouza.compose.icons:{pack}:1.1.1`) — user chose lib over copy-paste so devs get multiple icon packs without pasting. Default feather + tabler-icons; rest opt-in (commented in catalog); R8 strips unused. No Heroicons module exists → docs reference composables.com/icons for Heroicons. Drop material-icons-extended.

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
