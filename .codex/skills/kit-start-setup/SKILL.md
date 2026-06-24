---
name: kit-start-setup
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

Each setup step has its own command file in `.claude/commands/`. For each step
below, **Read that file and follow its instructions fully**, then return here and
continue to the next step. The sub-command files contain a literal `$ARGUMENTS`
token — ignore it (treat it as empty) when running them as part of this flow.

Every step is independent. If the developer wants to stop after any step, that is
fine — they can resume later by running that single command on its own.

**Speed — important:** do NOT run `./gradlew` compile or build commands between
steps. They are slow and the wait adds up across the flow. Each sub-command has a
"Verify" step that compiles — SKIP that step while running inside `/kit-start-setup`. A
single build runs once at the very end, in Step 7 (`/kit-run-app`), and it catches
any compile error introduced by any earlier step.

## Resume check — do this FIRST

A developer can leave mid-setup and come back later — next day, a new session,
even another machine. Don't rely on memory of this chat; **read the project files
to see what's already done** (they persist, a conversation doesn't). Check:

- **Rename (Step 1):** `app/build.gradle.kts` `namespace` — done if ≠ `dev.shipkaro.kit`.
- **Onboarding (Step 2):** `res/values/strings.xml` `onboarding_page1_title` — done if ≠ `Welcome aboard`.
- **Brand & theme (Step 3):** `core/designsystem/theme/Color.kt` `BrandPrimary` — done if ≠ `Color(0xFF7C3AED)`.
- **Auth (Step 4):** `KitConfig.kt` `AUTH_ENABLED` — done if `= true`.
- **Paywall (Step 5):** `KitConfig.kt` `PAYWALL_ENABLED` + `local.properties` `revenuecat.android.api.key` — done if the key is set (`goog_…`) OR `PAYWALL_ENABLED = false` (free app, deliberately off).
- **Analytics (Step 6):** `local.properties` (`posthog.api.key`, `sentry.dsn`) or `app/google-services.json` — done if any is present.

(`PAYWALL_ENABLED` and `ANALYTICS_ENABLED` both default to `true`, so the flag alone
isn't proof — Steps 5/6 also need a key/file signal.)

Then branch:

- **Fresh clone** — Steps 1, 2 AND 3 are all still at defaults. Greet fully
  (Step 0 below) and walk the whole flow as normal.
- **Resumed setup** — at least one step is already done:
  1. Greet briefly — "Welcome back 👋 — picking up your NowKit setup where you left
     off." Skip the long orientation (they've seen it).
  2. Show a short status list — one line per step, ✅ done (with the detected value,
     e.g. "Auth — Supabase") or ⬜ to do.
  3. Ask the user (wait for their answer) how to continue: **Resume from the first
     ⬜ step** (recommended) / **Review a finished step** (pick any ✅ to revisit —
     its own command offers keep-as-is / change) / **Start over** (re-run from
     Step 1; nothing is deleted, each step just re-confirms).
  4. Run from the chosen point; do NOT re-enter finished steps.
  5. Only if `local.properties` is missing (e.g. a new machine — it's git-ignored,
     so it doesn't travel with the repo), run Step 0 parts a–f first.

Every `/kit-setup-*` sub-command also has its own "Step 0 — Detect existing state"
that re-checks and offers keep-as-is, so finished work is never clobbered.

## Step 0 — Orientation

> **Resumed setup?** Skip this orientation and the "what are you building?"
> question (infer free-vs-paid from `PAYWALL_ENABLED`). Run only prerequisite
> checks a–f below, and only if `local.properties` is missing. Then jump to the
> resume point.

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
- 🚀 **Release** — Play Store assets + listing + upload via `/kit-upload-on-google-play`

Then say you will now configure it together.

Then ask the user (wait for their answer): **what they are building**:
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

**e. Write `sdk.dir`.** Set the `sdk.dir` line in `local.properties` to the
verified path. On **Windows, write the path with forward slashes** —
`sdk.dir=C:/Users/Name/AppData/Local/Android/Sdk` — because single backslashes
need escaping in this file and that trips people up constantly.

**f. Gradle check.** Run `./gradlew --version` to confirm the Gradle wrapper
works.

If anything fails, help fix it before continuing. Tell the developer that
`local.properties` holds their SDK path and all secret keys, is git-ignored, and
must never be committed — later steps write keys into it.

## Step 1 — Rename the kit  →  `.claude/commands/kit-change-app-id.md`

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

## Step 3 — Brand & theme  →  `.claude/commands/kit-kit-setup-theme.md`

Set their brand color and app icon.

## Step 4 — Authentication  →  `.claude/commands/kit-kit-setup-auth.md`

Pick and configure the auth provider (Stub / Supabase / Firebase).

Then call /kit-start-setup-part-2 to continue.
