---
description: Guided end-to-end setup of ShipKit — run this first
---

You are running **`/kit-start-setup`**, the master setup command for ShipKit.

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
single build runs once at the very end, in Step 8 (`/kit-run-app`), and it catches
any compile error introduced by any earlier step.

## Progress tracking

Before you greet the developer, call **TaskCreate** with one task per Step
below, in order, all `pending`. As you enter each Step, mark its task
`in_progress`; when the Step finishes, mark it `completed`. If a Step is
**skipped** (e.g. "free app" hard-skips Step 5 Paywall; "no auth" stops Step 4
early; the developer skips a question), still mark the task `completed` but
prefix the task content with `[skipped] ` via **TaskUpdate** so the developer
can tell skips from real completions at a glance.

Use these task titles verbatim:

- Step 0 — Orientation + prereqs
- Step 1 — Rename kit
- Step 2 — Onboarding content
- Step 3 — Brand & theme
- Step 4 — Authentication
- Step 5 — Paywall
- Step 6 — Analytics
- Step 7 — Ops (remote config, updates, push)
- Step 8 — Build & run

## Step 0 — Orientation

Open with one line: **ShipKit** is a starter kit from **ShipKaro**, a community
for indie mobile developers who ship fast — ShipKaro's motto is "Stop
perfecting. Start shipping."

Then point the developer at the two places they will need:
- **Docs:** https://kit.shipkaro.dev/docs — setup guides, feature reference,
  troubleshooting.
- **Kit home & updates:** https://kit.shipkaro.dev — ShipKit's page, changelog,
  and new releases.

Then show the developer what ShipKit gives them — present this list as-is:

- 🔑 **Authentication** — email + Google sign-in
- 💳 **Paywall + subscriptions** — RevenueCat
- 📊 **Analytics + crash reporting**
- 🔧 **Ops** — remote config, force/soft updates, push notifications
- 🌐 **Localization** — English by default; add more languages anytime with `/kit-translate`
- 🚀 **Release** — Play Store assets + listing + upload via `/kit-upload-on-google-play`

Then say you will now configure it together.

Then ask, with the **AskUserQuestion** tool, **what they are building**:
- A free app
- A paid / subscription app
- Just exploring the kit

Use the answer to decide which steps to run: a **free app** skips the paywall
step (Step 4) entirely; a **paid / subscription app** includes it; **just
exploring** walks through everything.

Confirm prerequisites. This is where first-time developers — **especially on
Windows** — get stuck, so do it carefully and verify, never guess.

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
should be 1–4 words; each description should be one short sentence. Update
**both** locale files:

- `app/src/main/res/values/strings.xml` — English.
- `app/src/main/res/values-ur/strings.xml` — Urdu (translate the same content).

Replace these existing string IDs in both files:

    onboarding_page1_title       onboarding_page1_desc
    onboarding_page2_title       onboarding_page2_desc
    onboarding_page3_title       onboarding_page3_desc

Do NOT add new string IDs — overwrite the existing values. Keep entity escapes
(`'` → `\'`, `&` → `&amp;`) correct.

## Step 3 — Brand & theme  →  `.claude/commands/kit-kit-setup-theme.md`

Set their brand color and app icon.

## Step 4 — Authentication  →  `.claude/commands/kit-kit-setup-auth.md`

Pick and configure the auth provider (Stub / Supabase / Firebase).

## Step 5 — Paywall & subscriptions  →  `.claude/commands/kit-kit-setup-paywall.md`

If the developer chose **"free app"** in Step 0, SKIP this step entirely — do
NOT ask. Just tell them the paywall is skipped and they can run `/kit-setup-paywall`
later if they ever add subscriptions, then move to Step 6.

Otherwise (paid app, or just exploring), configure RevenueCat via the command
file.

## Step 6 — Analytics & crash reporting  →  `.claude/commands/kit-kit-setup-analytics.md`

Configure PostHog and/or Firebase Analytics + Crashlytics.

## Step 7 — Ops: remote config, updates, push  →  `.claude/commands/kit-setup-updates.md`

Configure the remote-config provider, the force/soft update gate, and FCM push.

## Step 8 — Build & run  →  `.claude/commands/kit-run-app.md`

Final verification. Run **`/kit-run-app`** — it compiles the app, installs it on
the connected device, and launches it. This is the single build that runs
during `/kit-start-setup` (every earlier step skipped its own verify).

If the developer just wants a compile check without installing, they can use
`/kit-compile-app` instead.

## Wrap up

Summarise what was configured, what was skipped, and the obvious next move (e.g.
"run `/kit-setup-paywall` later when you add subscriptions", or "start building your
first screen"). Keep it to a few lines.
