---
description: Guided end-to-end setup of ShipKit — run this first
---

You are running **`/start-kit`**, the master setup command for ShipKit.

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
"Verify" step that compiles — SKIP that step while running inside `/start-kit`. A
single build runs once at the very end, in Step 8 (`/run-kit`), and it catches
any compile error introduced by any earlier step.

## Step 0 — Orientation

Open with one line: **ShipKit** is a starter kit from **ShipKaro**, a community
for indie mobile developers who ship fast — ShipKaro's motto is "Stop
perfecting. Start shipping."

Then point the developer at the two places they will need:
- **Docs:** https://kit.shipkaro.dev/docs — setup guides, feature reference,
  troubleshooting.
- **Kit home & updates:** https://kit.shipkaro.dev — ShipKit's page, changelog,
  and new releases.

(Both URLs are placeholders for now — the kit author should confirm the final
links before release.)

Briefly tell the developer what the kit gives them: authentication, a paywall,
analytics, ops (updates / push), and a bundled demo Habit Tracker app. Say you
will now configure it together.

Then ask, with the **AskUserQuestion** tool, **what they are building**:
- A free app
- A paid / subscription app
- Just exploring the kit

Use the answer to recommend which steps matter (e.g. skip the paywall for a free
app) — but let them override any recommendation.

Confirm prerequisites quickly:
- If `local.properties` does not exist, copy it from the template:
  `cp local.properties.template local.properties`. Then make sure the `sdk.dir`
  line points at the developer's real Android SDK path.
- Run `./gradlew --version` to check the Gradle wrapper works.

If either fails, help fix it before continuing. Tell the developer that
`local.properties` holds their SDK path and all secret keys, is git-ignored, and
must never be committed — the later steps will write keys into it.

## Step 1 — Rename the kit  →  `.claude/commands/refactor.md`

Make the kit theirs: new package name, applicationId, app display name. Do this
first — before any other code is added — so the rename stays clean.

## Step 2 — Brand & theme  →  `.claude/commands/setup-theme.md`

Set their brand color, theme options, and app icon.

## Step 3 — Authentication  →  `.claude/commands/setup-auth.md`

Pick and configure the auth provider (Stub / Supabase / Firebase).

## Step 4 — Paywall & subscriptions  →  `.claude/commands/setup-paywall.md`

Configure RevenueCat. If they said "free app", ask whether to skip this.

## Step 5 — Analytics & crash reporting  →  `.claude/commands/setup-analytics.md`

Configure PostHog and/or Firebase Analytics + Crashlytics.

## Step 6 — Ops: remote config, updates, push  →  `.claude/commands/setup-ops.md`

Configure the remote-config provider, the force/soft update gate, and FCM push.

## Step 7 — Make it yours: remove the demo  →  `.claude/commands/make-it-yours.md`

OPTIONAL, and usually LAST. The kit ships with a demo Habit Tracker app. Ask
whether they want to keep it for now (useful while learning) or strip it. Most
people keep it until they start building their own screens.

## Step 8 — Build & run  →  `.claude/commands/run-kit.md`

Build the app and run it on a device or emulator to confirm everything works.

## Wrap up

Summarise what was configured, what was skipped, and the obvious next move (e.g.
"run `/setup-paywall` later when you add subscriptions", or "start building your
first screen"). Keep it to a few lines.
