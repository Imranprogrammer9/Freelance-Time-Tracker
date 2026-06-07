# Commands Reference

NowKit is driven by **slash commands**. You type `/kit-` in your AI agent (Claude Code,
Cursor, Windsurf) and pick one; it asks plain questions and edits the code for you. This is
the whole product — you almost never edit files by hand.

Type `/kit-` any time to see the full list. Commands are grouped below by when you use them.

> **Two rules every command follows:**
> 1. **Paced.** Provider setups (Supabase, Firebase, RevenueCat, …) are broken into small
>    steps with "stop and wait" gates. No wall of instructions. Nothing runs until you say go.
> 2. **Resumable + idempotent.** Re-running a command is safe. It detects what's already
>    configured and offers to keep it, change one thing, or redo that piece.

---

## 🚀 Start here

### `/kit-start-setup`
**The first command you run.** Guided end-to-end setup: rename, onboarding copy, brand &
theme, auth, paywall, analytics, then build & run. Asks what you're building (free / paid /
exploring) and skips what you don't need. Runs the setup commands below inline.
→ See **[The Flow](the-flow.md)**.

### `/kit-env-check`
Verify your machine has every tool the kit needs — JDK 17, Android SDK, `adb`, the android
CLI, Android Skills, and optional tools (scrcpy, Holo). Detects your OS and prints the exact
install command for anything missing. Run this first if any build fails.

---

## ⚙️ Setup commands

These configure one feature each. `/kit-start-setup` runs the core ones for you; run any
standalone later to add or change a feature.

### `/kit-change-app-id`
Rename the kit — package name, applicationId, and app display name — in one guided step.
Do this **first**, before adding any code, so the rename stays clean. Also rewrites
`/kit-run-app` so it always launches your renamed app.

### `/kit-setup-theme`
Set your **brand color** (one hex — the kit derives the full light + dark palette and all
container shades from it) and pick your **icon pack** (Material / Feather / Tabler / many
more). Switching packs later is reversible.

### `/kit-setup-auth`
Choose and configure authentication: **Supabase** (recommended) or **Firebase**, with
email + Google sign-in toggles. Walks the full provider setup — including the two Google
Cloud OAuth clients Google sign-in needs — paced, with stop-and-wait gates. Flips
`AUTH_ENABLED` on.

### `/kit-setup-firebase`
Shared helper used by auth, analytics, and updates when you pick Firebase. You point it at
your downloaded `google-services.json`; it copies it into `app/` and applies the Firebase
Gradle plugins.

### `/kit-setup-paywall`
Configure the **RevenueCat** paywall and subscriptions. Sets up your RevenueCat app +
entitlement + API key, and picks the paywall mode: **SOFT** (skippable, "Maybe later") or
**HARD** (blocking, must subscribe). The paywall UI is RevenueCat's — you design it in their
dashboard.

### `/kit-setup-analytics`
Configure analytics + crash reporting. Multi-select: **PostHog**, **Firebase Analytics**,
**Crashlytics**, **Sentry**. Crash reporting always runs; product analytics respects the
user's privacy toggle.

### `/kit-setup-ai`
Wire **OpenRouter** so your app can call any AI model with one key — Claude, GPT, Gemini,
Llama, 100+ models. Picks a default model (free / cheap / premium). Flips `OPENROUTER_ENABLED`.

### `/kit-setup-updates`
Configure **remote config**, the **force/soft update gate**, **maintenance mode**, and push.
Pick a provider — **LOCAL** (default, works offline), **Supabase** (prints the SQL for the
config table), or **Firebase**. Lets you push an update prompt or maintenance notice later
**without** shipping a new version.

### `/kit-setup-review-dialog`
Wire the Google Play **in-app review** prompt at a trigger you choose — Nth launch, after a
key action, time delay, or a manual call-site. Fires once (tracked in DataStore).

---

## 🎨 Build commands

### `/kit-design-app`
Design and build **your app's screens** — the main event of Phase 2. Two phases:
**(1)** generate the UI with your components + dummy data, you approve the look on-device;
**(2)** wire each screen to data — Supabase, Room, Retrofit, or static — without touching
the approved layout. Source the design from Stitch, screenshots, plain text, or scratch.

### `/kit-design-onboarding`
Build a personalised, multi-screen **Calm / Headspace-style questionnaire** onboarding —
welcome → value-prop → 3–5 question screens → permission priming → social proof →
personalised plan → ready. Replaces the simple 3-page intro.

### `/kit-translate`
Translate every app string into one or more languages and wire the locales in. Multi-select
buckets: **RTL** (Arabic, Hebrew, Urdu), **Asia** (Chinese, Japanese, Korean, Hindi, …),
**Europe** (20 languages). Updates the locale manager + config; RTL layouts flip automatically.

---

## 📦 Run + release commands

### `/kit-compile-app`
Compile-check only (Kotlin compile, no install or launch). Fast sanity check.

### `/kit-run-app`
Compile, install, and launch the app on your connected device or emulator. This is the
build that runs at the end of `/kit-start-setup`.

### `/kit-generate-legal`
Generate your **privacy policy** + **Play Data Safety** answers from the actual codebase —
it reads which SDKs, network calls, and Supabase tables you use, asks 8 legal questions, and
writes `privacy_policy.md` + `.html` + `play_data_safety.md` into `playstore/`.

### `/kit-generate-screenshots`
Generate Play Store phone screenshots (1080×1920) from your app — codebase-driven and
ASO-optimised via the `aso-appstore-screenshots` skill — or drop your own PNGs.

### `/kit-plan-release-analytics`
Plan and **auto-wire** release-specific analytics events + a conversion funnel before a Play
upload. Inserts the tracking calls at the right call-sites; confirms ambiguous ones with you.

### `/kit-upload-on-google-play`
The release finale. Detects first-version vs update and walks the right path: app icon,
release keystore, SHA-1 registration, screenshots, listing copy, Data Safety + privacy,
create the Play Console app, build the signed AAB, and the manual Play Console upload.

---

## Skills the commands use

Some commands invoke **skills** — focused generators — under the hood. You don't call these
directly; the command offers them:

- **`aso-googleplay-listing`** — writes your Play Store app name, short + long description,
  with a keyword strategy. Offered by `/kit-upload-on-google-play`.
- **`aso-appstore-screenshots`** — generates ASO-optimised screenshots. Offered by
  `/kit-generate-screenshots`.
- **`onboarding-questionnaire`** — builds the questionnaire onboarding flow. Behind
  `/kit-design-onboarding`.

---

## Command cheat-sheet

| Command | Use when |
|---------|----------|
| `/kit-start-setup` | First thing, right after cloning |
| `/kit-env-check` | A build fails, or new machine |
| `/kit-change-app-id` | Rename your app |
| `/kit-setup-theme` | Change brand color or icons |
| `/kit-setup-auth` | Add or change login |
| `/kit-setup-paywall` | Add subscriptions |
| `/kit-setup-analytics` | Add tracking / crash reports |
| `/kit-setup-ai` | Add AI features |
| `/kit-setup-updates` | Add remote config / update gate |
| `/kit-setup-review-dialog` | Ask users for a review |
| `/kit-design-app` | Build your own screens |
| `/kit-design-onboarding` | Personalised onboarding quiz |
| `/kit-translate` | Add languages |
| `/kit-run-app` | Build + run on device |
| `/kit-generate-legal` | Privacy policy + Data Safety |
| `/kit-generate-screenshots` | Play screenshots |
| `/kit-upload-on-google-play` | Ship to Play |

**Next:** [Example Recipes](example-recipes.md) — see these commands strung together for
five real apps.
