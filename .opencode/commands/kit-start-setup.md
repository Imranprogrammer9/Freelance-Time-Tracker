---
description: Guided end-to-end setup of NowKit — run this first
---

You are running **`/kit-start-setup`**, the master setup command for NowKit.

This is the FIRST command a developer runs after cloning the kit. Your job: walk
them through turning the starter kit into their own app — renaming, branding, and
configuring only the features they need — then build and run it.

The audience is first-time mobile developers. Be warm, brief, and concrete. One
line of "why" per step, never paragraphs. Never dump code at them — you make the
edits, they answer questions.

## How this command works

Each setup step has its own command file. For each step below, **Read that file
and follow its instructions fully**, then return here and continue to the next
step. The sub-command files contain a literal `$ARGUMENTS` token — ignore it
(treat it as empty) when running them as part of this flow.

Every step is independent. If the developer wants to stop after any step, that is
fine — they can resume later by running that single command on its own.

**Speed — important:** do NOT run `./gradlew` compile or build commands between
steps. They are slow and the wait adds up across the flow. Each sub-command has a
"Verify" step that compiles — SKIP that step while running inside `/kit-start-setup`. A
single build runs once at the very end, in Step 7 (`/kit-run-app`), and it catches
any compile error introduced by any earlier step.

## Resume check — do this FIRST (before greeting)

A developer can leave mid-setup and come back later — next day, a new chat
session, even another machine. The kit has no in-memory checklist that survives
that, so **derive progress from the project files themselves** (they persist; a
conversation doesn't). Read these signals and classify each step **done** or
**to do**:

| Step | Read this | Already DONE when | Still TO DO (kit default) |
|------|-----------|-------------------|----------------------------|
| 1 Rename | `app/build.gradle.kts` → `namespace` | ≠ `dev.shipkaro.kit` | `= dev.shipkaro.kit` |
| 2 Onboarding | `res/values/strings.xml` → `onboarding_page1_title` | ≠ `Welcome aboard` | `= Welcome aboard` |
| 3 Brand & theme | `core/designsystem/theme/Color.kt` → `BrandPrimary` | ≠ `Color(0xFF7C3AED)` | `= Color(0xFF7C3AED)` |
| 4 Auth | `KitConfig.kt` → `AUTH_ENABLED` | `= true` | `= false` |
| 5 Paywall | `KitConfig.kt` `PAYWALL_ENABLED` + `local.properties` `revenuecat.android.api.key` | key set (`goog_…`) **or** `PAYWALL_ENABLED = false` (free app, deliberately off) | `PAYWALL_ENABLED = true` **and** no key |
| 6 Analytics | `local.properties` (`posthog.api.key`, `sentry.dsn`) + `app/google-services.json` | any provider key/file present | none present |

> Note: `PAYWALL_ENABLED` and `ANALYTICS_ENABLED` both **default to `true`**, so the
> flag alone doesn't prove a step ran — that's why Steps 5/6 also need a key/file
> signal (mirrors each sub-command's own Step 0).

**Then branch:**

- **Fresh clone** — Steps 1, 2 AND 3 are ALL still at defaults (namespace =
  `dev.shipkaro.kit` AND title = `Welcome aboard` AND BrandPrimary =
  `0xFF7C3AED`). Treat as a first run: greet fully (Step 0 — Orientation below),
  walk the whole flow as normal.

- **Resumed setup** — at least one step is already done:
  1. Greet **briefly** — "Welcome back 👋 — picking up your NowKit setup where you
     left off." Skip the long orientation blurb (they've seen it).
  2. Print a short **resume status** list — one line per step, ✅ done (with the
     detected value, e.g. "Auth — Supabase") or ⬜ to do.
  3. Ask the developer (wait for their answer) — "How do you want to continue?":
     - **Resume from <first ⬜ step>** (recommended) — continue from the first
       unfinished step.
     - **Review a finished step** — pick any ✅ step to revisit (its own command
       offers keep-as-is / change).
     - **Start over** — re-run every step from Step 1 (nothing is deleted; each
       step just re-confirms).
  4. Run from the chosen point. **Do NOT re-enter steps already done.**
  5. Still confirm prerequisites if `local.properties` is **missing** (e.g. a new
     machine — it's git-ignored, so it doesn't travel with the repo): run Step 0
     parts **a–f** only, then continue. If `local.properties` exists, skip straight
     to the resume point.

**Safety net:** every `/kit-setup-*` sub-command has its own "Step 0 — Detect
existing state" that re-checks and offers keep-as-is, so even if this
classification is slightly off, finished work is never clobbered.

## Step 0 — Orientation

> **Resumed setup?** Skip this orientation blurb and the "what are you building?"
> question (infer free-vs-paid from `PAYWALL_ENABLED`). Run only the prerequisite
> checks **a–f** below, and only if `local.properties` is missing. Then jump to the
> resume point chosen in the Resume check.

Open with one line: **NowKit** is a starter kit from **ShipKaro**, a community
for indie mobile developers who ship fast — ShipKaro's motto is "Stop
perfecting. Start shipping."

Then point the developer at the two places they will need:
- **Docs:** https://kit.shipkaro.dev/docs — setup guides, feature reference,
  troubleshooting.
- **Kit home & updates:** https://kit.shipkaro.dev — NowKit's page, changelog,
  and new releases.

Then show the developer what NowKit gives them — present this list as-is:

- 🔑 **Authentication** — email + Google sign-in
- 💳 **Paywall + subscriptions** — RevenueCat
- 📊 **Analytics + crash reporting**
- 🌐 **Localization** — English by default; add more languages anytime with `/kit-translate`
- 🚀 **Release** — Play Store assets + listing + upload via `/kit-publish-to-play`

Then say you will now configure it together.

Then ask the developer (wait for their answer) **what they are building**:
- A free app
- A paid / subscription app
- Just exploring the kit

Use the answer to decide which steps to run: a **free app** skips the paywall
step (Step 4) entirely; a **paid / subscription app** includes it; **just
exploring** walks through everything.

Confirm prerequisites. This is where first-time developers — **especially on
Windows** — get stuck, so do it carefully and verify, never guess.

**If anything below fails (or the dev says `java -version` errors, `adb`
isn't found, etc.), stop here and tell the developer to run `/kit-env-check`
first.** That command checks JDK 17 / Android CLI / Android Skills / optional
terminal tools and prints OS-specific install commands for whatever is
missing. Come back to `/kit-start-setup` once it reports clean.

**a. Create `local.properties`.** If it does not exist, copy it from the
template: `cp local.properties.template local.properties` on macOS/Linux, or
`copy local.properties.template local.properties` on Windows.

**b. Detect the OS.** Run `uname`. macOS reports `Darwin`, Linux reports `Linux`,
and Windows under Git Bash reports something starting with `MINGW` or `MSYS`. If
the result is unclear, ask the developer whether they are on macOS or Windows.

**c. Find the Android SDK path** for that OS:
- **macOS:** `/Users/<username>/Library/Android/sdk` — get `<username>` from
  `echo $HOME`.
- **Windows:** usually `C:\Users\<username>\AppData\Local\Android\Sdk`. An older
  alternative is `C:\Program Files (x86)\Android\android-sdk`.
- **Linux:** usually `/home/<username>/Android/Sdk`.

If you cannot work it out, tell the developer to open Android Studio → Settings
→ Appearance & Behavior → System Settings → Android SDK and read the path at the
top, labelled **"Android SDK Location"** — then paste it back to you.

**d. Verify the path exists — do NOT skip this.** A wrong `sdk.dir` is the single
most common reason the build fails for new developers. Check the directory
actually exists: `test -d "<path>" && echo "SDK OK" || echo "NOT FOUND"`. If it
is not found, ask the developer for the correct path (or to open it in their
file explorer to confirm) and re-check — do not continue on a guessed path.

**e. Write `sdk.dir` — ONE line, the real resolved path (no placeholder left behind).** First
**remove any existing `sdk.dir` line** in `local.properties` — a fresh clone ships a placeholder
(e.g. `sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk`), and on Windows the agent often *adds* a
line without deleting that macOS default, leaving two wrong lines. There must be exactly one
`sdk.dir`, pointing at the verified path:
- **macOS / Linux:** use the path as-is — `sdk.dir=/Users/<name>/Library/Android/sdk`.
- **Windows: escape every backslash (double them)** —
  `sdk.dir=C:\\Users\\<name>\\AppData\\Local\\Android\\Sdk`. A Java `.properties` file reads a
  single `\` as an escape character, so a real Windows path only parses correctly with each `\`
  **doubled**. This is the reliable format — **do not** use forward slashes (`C:/Users/…`); they
  work on some setups but silently fail on others.

**f. Gradle check.** Run `./gradlew --version` to confirm the Gradle wrapper
works.

If anything fails, help fix it before continuing. Tell the developer that
`local.properties` holds their SDK path and all secret keys, is git-ignored, and
must never be committed — later steps write keys into it.

## Step 1 — Rename the kit  →  /kit-change-app-id

Make the kit theirs: new package name, applicationId, app display name. Do this
first — before any other code is added — so the rename stays clean.

## Step 2 — Onboarding content

The kit ships a 3-page onboarding (`OnboardingScreen`) with placeholder copy.
Tailor it to the developer's app now — they only have to answer one question.

Ask in plain conversational text (free-form, no multi-choice):
- **What's your app about?** A single sentence — e.g. "An offline habit tracker
  with streaks", "Voice-first journaling with AI cleanup".

If they skip / decline, leave the defaults and move on.

Otherwise, write **3 page titles + 3 page descriptions** that introduce the app's
core value (problem solved → key feature → invitation to start). Each title
should be 1–4 words; each description should be one short sentence.

Edit only `app/src/main/res/values/strings.xml`. Overwrite the existing string
IDs in place:

    onboarding_page1_title       onboarding_page1_desc
    onboarding_page2_title       onboarding_page2_desc
    onboarding_page3_title       onboarding_page3_desc

Do NOT add new string IDs, and do NOT bring up other locales — the kit ships
English-only and `/kit-translate` handles every other language later. Keep
entity escapes (`'` → `\'`, `&` → `&amp;`) correct.

## Step 3 — Brand & theme  →  /kit-setup-theme

Set their brand color and app icon.

## Step 4 — Authentication  →  /kit-setup-auth

Pick and configure the auth provider (Stub / Supabase / Firebase).

## Step 5 — Paywall & subscriptions  →  /kit-setup-paywall

If the developer chose **"free app"** in Step 0, SKIP the full setup but DO
flip the kit's paywall switch off — otherwise the kit's nav still shows the
default Paywall screen after Auth on first launch. Find `KitConfig.kt`
(under `app/src/main/java`, package `...core.config`) and set:

    const val PAYWALL_ENABLED: Boolean = false

Then tell the developer the paywall is disabled and they can run
`/kit-setup-paywall` later if they ever add subscriptions. Move to Step 6.

Otherwise (paid app, or just exploring), configure RevenueCat via the command
file.

## Step 6 — Analytics & crash reporting  →  /kit-setup-analytics

Configure PostHog and/or Firebase Analytics + Crashlytics.

## Step 7 — Build & run  →  /kit-run-app

Final verification. Run **`/kit-run-app`** — it compiles the app, installs it on
the connected device, and launches it. This is the single build that runs
during `/kit-start-setup` (every earlier step skipped its own verify).

If the developer just wants a compile check without installing, they can use
`/kit-compile-app` instead.

## Step 8 — Kick off the slow billing setup early (only if the app sells)

Read `KitConfig.kt`. **If `PAYWALL_ENABLED = false`, skip this step entirely** —
nothing to sell, no billing clocks to start.

If `PAYWALL_ENABLED = true`, there are **two things that take real time** and can be
started **now**, while the developer keeps building — so the clocks aren't waiting at
the end:

1. **RevenueCat ↔ Google Play connection** (the service-account JSON) — Google takes
   **~36 hours** to propagate the permission before purchases validate.
2. **A build on an internal testing track** — Google Play only serves your products to
   a build that's live on a track, and the **first review** of a new app can take hours
   to a few days. Until a build is on a track (+ tester opt-in), the paywall shows
   "offerings empty" — so you can't test real purchases.

Both can run **in the background** while the developer builds their app's features.
Show this verbatim, then ask the developer (wait for their answer) ("Start these now" / "Later"):

> **Heads up — billing has two slow steps.** Connecting Google Play to RevenueCat
> takes **~36 hours** to go live, and your first build needs to sit on a Play test
> track (first review can take a few days) before purchases work. Good news: you can
> **start both now** and keep building while the clocks tick — by the time your app's
> done, billing is ready.
>
> **Start them now, or later?**

- **Start now** — run **`/kit-setup-paywall`** and take its sub-step 2.6 **"Now"**
  path. That walks the whole chain **in dependency order** (it's the one place that
  gets the ordering right, so don't reorder it here):
  1. Play Console account + **app entry** (the first blocker — needs the $25 account).
  2. **Signed build on a testing track** via **`/kit-sign-release`** — this is what
     *unblocks product creation* and starts the first-review clock.
  3. **One-time products** (now unblocked) + activate.
  4. **Service-account JSON → RevenueCat** — starts the ~36 h propagation clock.
  5. **Offerings + paywall** in RevenueCat.

  Then tell them to **keep building** their app's features while the clocks tick;
  check back in ~36 h and test a real purchase. Full reference:
  https://kit.shipkaro.dev/docs/paywall
- **Later** — fine. Note the kit will walk all of this in
  `/kit-publish-to-play` before they ship, and they can start it anytime with
  `/kit-setup-paywall` → "Set up products + Play billing". Move on.

## Step 9 — Back up to GitHub?

Ask the developer (wait for their answer) — don't write a paragraph; the options carry the why:

- **Yes, create a private repo** (recommended) — free, private (only you see it),
  gives you a backup + full history. I'll create the repo and push everything via
  the GitHub CLI.
- **Not now** — your app stays only on this laptop; you can run
  `/kit-save-to-github` anytime later.

If **Yes** → Call `/kit-save-to-github` (it handles GitHub login, creates the
repo, pushes, and shows its own short done summary). If **Not now** → one line, move
on. Don't dump command explanations.

## Wrap up

Keep the wrap-up **short** — a vibe coder shouldn't have to scroll. Three parts:

1. A **"Setup complete"** table: one row per configured step (Rename / Onboarding /
   Brand & theme / Auth / Paywall / Analytics / Build), Result column with the
   concrete value. End with a one-line **Skipped:** note.
2. **Next moves** — at most 3 short lines (e.g. build your first screen with
   `/kit-design-app`; before release run `/kit-publish-to-play`). Include
   one line: "Run the app yourself anytime — no AI agent needed — with the
   commands saved in **`RUN.md`**" (Step 7's run wrote it).
3. The compact optional-commands list below, verbatim.

> **Run these later when you need them** (type `/kit-` to see all):
>
> - `/kit-design-app` — build your app's own screens
> - `/kit-design-onboarding` — personalised onboarding flow
> - `/kit-setup-ai` — AI features (OpenRouter, one key)
> - `/kit-translate` — add more languages
> - `/kit-setup-updates` — update gate + remote config
> - `/kit-setup-review-dialog` — in-app review prompt
> - `/kit-save-to-github` — back up to GitHub
> - `/kit-generate-screenshots` — Play Store screenshots
> - `/kit-generate-aso` — Play listing copy (title + descriptions)
> - `/kit-generate-landing` — static landing + privacy + terms page
> - `/kit-publish-to-play` — ship to Google Play
> - `/kit-env-check` — check your machine's tools
