# NowKit — agent guide

This file orients any AI coding agent (Claude Code, Cursor, Google Antigravity, OpenCode) working
in a NowKit project. Read it first.

**Full docs:** https://kit.shipkaro.dev/docs

## What this is

NowKit is a **production-ready, pure Android-native starter kit** (Kotlin + Jetpack Compose +
Material 3). It's not a framework you import — it's a **real app the developer owns**. They clone
it, rename it, turn on the features they need with `/kit-*` commands, build their screens, and
ship to Google Play.

> **This is Android-only — NOT Kotlin Multiplatform / Compose Multiplatform.** It's a single
> Android (`app`) module wired to Android-only SDKs (RevenueCat, Credential Manager, non-KMP
> Room). It is **not** a drop-in base for a KMP project. If the developer's existing project is
> KMP (or they assume it is), say so up front: the kit is Android-native; iOS/other targets would
> be a separate future effort, not something this kit composes into. Don't try to make it
> multiplatform.

The developer is often a **non-coder / vibe-coder**: they describe what they want and approve;
**you** write the code. Be concrete, edit files for them, and prefer the kit's commands over
hand-wiring.

## Tech stack

- **Language / UI:** Kotlin, Jetpack Compose, Material 3, Navigation Compose (type-safe routes)
- **DI:** Koin (no Hilt/KSP)
- **Networking / persistence:** Retrofit + Room + DataStore
- **Auth:** Supabase (default) or Firebase — toggled by `KitConfig.AUTH_PROVIDER`
- **Paywall:** RevenueCat (its prebuilt Compose `Paywall` — design/prices live in the RC dashboard)
- **Analytics:** PostHog, Firebase Analytics, Crashlytics, Sentry (via `AnalyticsManager`)
- **AI:** OpenRouter (`core/ai`, one key, 100+ models)
- **Ops:** `RemoteAppConfig` (LOCAL / Supabase / Firebase) — update gate, maintenance mode, changelog
- **Build:** Gradle Kotlin DSL + version catalog, detekt; Gradle wrapper 8.14.5, JDK 17, minSdk 26

## Architecture

**Single `app` module, package-by-feature.** No multi-module, no `buildSrc`. Base package is read
from `app/build.gradle.kts` `namespace` (default `dev.shipkaro.kit`, but the developer may have run
`/kit-change-app-id` — never hardcode it).

```
app/src/main/java/<base>/
├── core/            ← shared building blocks. Rarely edit; extend instead.
│   ├── ai/          ← OpenRouter client
│   ├── analytics/   ← AnalyticsManager
│   ├── auth/        ← AuthRepository + Supabase/Firebase/Stub impls
│   ├── billing/     ← PurchaseManager (RevenueCat)
│   ├── config/      ← KitConfig (compile-time switches)
│   ├── data/        ← Room + DataStore
│   ├── designsystem/← 40+ Kit* components, KitTheme tokens, icons
│   ├── navigation/  ← type-safe Route + KitNavHost
│   ├── ops/         ← UpdateManager, ChangelogManager, RemoteAppConfig
│   ├── security/    ← SecureDataStore (AES-GCM + Keystore)
│   └── util/        ← CustomTabs, PlayStoreLauncher, EmailLauncher, …
└── feature/         ← screens. The developer's app lives HERE.
```

**Two-layer config (keep them separate):**
- `KitConfig` (`core/config/`) — compile-time switches the developer sets once (auth provider,
  paywall mode, which analytics). The `/kit-setup-*` commands edit this.
- `RemoteAppConfig` (`core/ops/`) — runtime values changed later *without shipping an update*
  (force-update version, maintenance mode, changelog).

**Default app flow** (gates auto-skip per config + DataStore first-launch flags):
`Splash (OS) → Onboarding → Auth (if enabled, signed out) → Paywall (if enabled, not subscribed)
→ Home → Settings / Profile`. Replace `HomeScreen` with the app's main screen.

## How to work in this kit

- **Build under `feature/`. Leave `core/` mostly alone** — it's the shared base. If you must touch
  `core/`, keep it small and isolated.
- **Localize every user-facing string.** All UI text, content descriptions, dialog/label strings
  go in `app/src/main/res/values/strings.xml` (feature-prefixed). Never hardcode literals in
  `Text(...)`, `contentDescription`, `label`, etc. English ships by default; `/kit-translate` adds
  locales.
- **Use the design system.** Compose with `KitTheme.spacing/colors/icons`, `KitButton`, `KitCard`,
  `KitTextField`, `KitListItem`, `KitBanner`, `KitDialog`, `KitBottomSheet`, etc. — not raw
  Material widgets. Brand color flows from `core/designsystem/theme/Color.kt`.
- **Add a screen** = Compose file in `feature/<name>/`, a `@Serializable data object <Name> : Route`
  in `Route.kt`, and a `composable<Route.<Name>>` entry in `KitNavHost`. Don't touch the
  Splash/Onboarding/Auth/Paywall gating.
- **Wire data** with Koin (`AppModules.kt`) — Repository → ViewModel (`StateFlow<UiState>`) →
  screen reads `vm.state`.
- **Prefer the `/kit-*` commands** over hand-wiring providers (they're paced, idempotent, and edit
  the right files).

## The `/kit-*` commands

The same `/kit-*` commands (and the kit's two skills, `aso-googleplay-listing` +
`onboarding-questionnaire`) ship for every supported agent — type `/kit-` to discover them:
- **Claude Code** → `.claude/commands/` + `.claude/skills/`
- **Antigravity** → `.agent/workflows/`
- **Cursor** → `.cursor/commands/`
- **OpenCode** → `.opencode/commands/`
- **Codex** → `.codex/skills/` (each command/skill is a `SKILL.md`)

Key ones:

- **`/kit-start-setup`** — the first command. Rename → onboarding → brand/theme → auth → paywall →
  analytics → build. Runs the setup commands inline.
- **`/kit-design-app`** — build the app's own screens (UI first with dummy data, then wire data).
- **`/kit-plan-monetization`** — after the app's screens exist, decide the pricing model, which
  features to lock behind the paywall, and the prices — then wire the `premium` gates into those
  screens (entitlement-only; products + prices stay in the RevenueCat / Play dashboards).
- **Setup:** `/kit-change-app-id`, `/kit-setup-theme`, `/kit-setup-auth`, `/kit-setup-paywall`,
  `/kit-setup-analytics`, `/kit-setup-ai`, `/kit-setup-updates`, `/kit-setup-review-dialog`
- **Run:** `/kit-compile-app`, `/kit-run-app`, `/kit-env-check`
- **Maintain:** `/kit-update` — pull the latest kit commands + skills (your code is untouched)
- **Release:** `/kit-generate-legal`, `/kit-generate-aso`, `/kit-generate-screenshots`,
  `/kit-generate-changelog`, `/kit-generate-landing`, `/kit-plan-release-analytics`,
  `/kit-sign-release`, `/kit-publish-to-play`, `/kit-save-to-github`,
  `/kit-translate`, `/kit-translate-listing`

Full reference: https://kit.shipkaro.dev/docs/commands

## Build & run

```bash
./gradlew :app:assembleDebug --no-daemon   # debug APK
./gradlew :app:compileDebugKotlin          # compile-check only
./gradlew detekt                           # lint
```

Use the Gradle **wrapper** (`./gradlew`, 8.14.5) — not a system `gradle`.

**Run shell steps yourself.** Gradle builds, `npm install`, signing, `adb`, file ops and any
other command a `/kit-*` step lists are **non-interactive — run them directly with your shell
tool.** Don't hand them to the developer or tell them to type `! <command>`. Only ask the
developer to run something themselves when it's genuinely interactive (e.g. an OAuth/`gcloud`
login, a browser step) or when your environment truly can't run it (no JDK / Android SDK / Node
on PATH) — then present the exact command and wait.

**Present third-party UI steps verbatim — don't improvise from memory.** When a `/kit-*` command
gives exact steps for an external console (Google Play Console, RevenueCat, Supabase, Firebase),
those screen titles / button labels / menu paths are **transcribed from the real, current UI**.
Show them **exactly as written**; do **not** paraphrase or substitute a path/label from your own
training data — these UIs change and your memory is stale (e.g. Play's old "App content" menu is
gone; tasks are rows in the Dashboard checklist). If the live screen differs from the command
text, **trust the screen, do the step, and tell the developer the wording looks outdated** — never
silently invent a different path.
