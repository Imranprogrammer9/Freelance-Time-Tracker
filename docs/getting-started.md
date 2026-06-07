# Getting Started

This page gets your machine ready and the kit running on a phone — **before** you start
configuring features. Budget 20–40 minutes the first time (most of it is downloading the
Android SDK once).

> The fastest path: clone the kit, open it in your AI agent, and run **`/kit-env-check`**.
> It checks everything below for you and prints the exact install command for whatever's
> missing — on macOS, Windows, or Linux. The rest of this page is what that command checks,
> explained.

---

## 1. Requirements

You need four things on your machine:

| Tool | Why | How to check |
|------|-----|--------------|
| **JDK 17** | Android builds run on Java 17 | `java -version` → should say `17.x` |
| **Android SDK** | Compiles + installs the app | Installed via Android Studio (once) |
| **`adb`** | Talks to your phone/emulator | `adb devices` → lists your device |
| **An AI coding agent** | Runs the `/kit-*` commands | Claude Code, Cursor, or Windsurf |

Plus a **device to run on**: either a physical Android phone with USB debugging on, or an
emulator. The kit targets **Android 8.0 (API 26) and up**.

**Don't have these yet?** Run `/kit-env-check`. It detects your OS and prints copy-paste
install commands:

- **macOS** — Homebrew + `curl | sh` installers
- **Windows** — Scoop + PowerShell
- **Linux** — apt + `curl | sh`

Android Studio is the one heavy install, and it's only needed **once** to download the SDK.
After that, everything runs from the terminal.

### Optional but recommended

- **scrcpy** — mirror your phone screen on your computer (great for demos + screenshots)
- **Holo** — a terminal-first Android profiler, for the no-Android-Studio workflow

`/kit-env-check` checks for these too and tells you they're optional.

---

## 2. Get the kit

After purchase you're added as a collaborator on the private kit repo. Clone it:

```bash
git clone https://github.com/<you>/nowkit-yourapp.git
cd nowkit-yourapp
```

Open the folder in your AI agent (Claude Code: `claude` in that directory).

---

## 3. First run — let the kit set itself up

Don't poke around the code yet. Run the master command:

```
/kit-start-setup
```

It walks you through, in order:

0. **Orientation + prereqs** — confirms your SDK path, writes `local.properties`
1. **Rename** — your package name, applicationId, and app display name
2. **Onboarding content** — one sentence about your app → 3 intro screens
3. **Brand & theme** — your color + icon pack
4. **Authentication** — Supabase / Firebase / none
5. **Paywall** — RevenueCat (skipped automatically for free apps)
6. **Analytics** — PostHog / Firebase / Crashlytics / Sentry
7. **Build & run** — compiles, installs, launches on your device

You answer questions; the kit makes every edit. At the end you have **your** app — renamed,
branded, configured — running on your phone.

> Full walkthrough of every step: **[The Flow — 0 to Google Play](the-flow.md)**.

### `local.properties` — your secrets file

Step 0 creates `local.properties` from the template. This file holds your SDK path and all
your API keys (Supabase, RevenueCat, PostHog, …). It is **git-ignored** — never commit it.
The setup commands write keys into it for you.

---

## 4. Project structure

NowKit is a **single `app` module, package-by-feature**. No multi-module maze, no
`buildSrc` indirection — just one place for everything, which is what you want when an AI
agent (or a beginner) is navigating the code.

```
app/src/main/java/dev/shipkaro/kit/
├── core/                  ← shared building blocks (you rarely edit these)
│   ├── ai/                ← OpenRouter client (AI features)
│   ├── analytics/         ← PostHog + Firebase + Crashlytics + Sentry
│   ├── auth/              ← AuthRepository + Supabase/Firebase/Stub impls
│   ├── billing/           ← PurchaseManager (RevenueCat)
│   ├── config/            ← KitConfig (your compile-time switches)
│   ├── data/              ← Room database + DataStore settings
│   ├── designsystem/      ← the 40+ components, theme, icons, foundation tokens
│   ├── log/               ← Timber + Crashlytics/Sentry trees
│   ├── navigation/        ← type-safe Route graph + KitNavHost
│   ├── ops/               ← UpdateManager, ChangelogManager, RemoteAppConfig
│   ├── security/          ← SecureDataStore (AES-GCM + Android Keystore)
│   └── util/              ← CustomTabs, PlayStoreLauncher, EmailLauncher, …
└── feature/               ← screens (you build your app here)
    ├── auth/              ← sign-in screen
    ├── catalog/           ← live component catalog (delete when done exploring)
    ├── home/              ← Home + system-status panel
    ├── onboarding/        ← intro pager
    ├── paywall/           ← RevenueCat paywall wrapper
    ├── permissions/       ← 8 full-screen permission flows
    ├── profile/           ← read-only profile + sign-out
    └── settings/          ← settings + theme/account/legal rows
```

The golden rule for updates: **don't edit `core/` — extend it.** That keeps future kit
versions easy to merge in. Build your app under `feature/`.

### The default app flow

Out of the box the kit runs a real, end-to-end flow (not a demo):

```
Splash (OS) → Onboarding (first launch) → Auth (if enabled, signed out)
            → Paywall (if enabled, not subscribed) → Home → Settings / Profile
```

Each gate is skipped automatically based on your config. A free, no-login app goes
**straight to Home**. You replace `HomeScreen` with your app's main screen.

---

## Troubleshooting first run

| Problem | Fix |
|---------|-----|
| `java -version` shows wrong version | Run `/kit-env-check` → installs JDK 17 |
| Build fails: `SDK location not found` | Wrong `sdk.dir` in `local.properties`. Re-run `/kit-start-setup` Step 0 — it verifies the path. On Windows use forward slashes. |
| `adb devices` shows nothing | Enable USB debugging on the phone, or start an emulator |
| Gradle errors on first build | First build downloads dependencies — let it finish. If it still fails, `/kit-env-check`. |

---

**Next:** [The Flow — 0 to Google Play](the-flow.md) — the full journey from here to a
live listing.
