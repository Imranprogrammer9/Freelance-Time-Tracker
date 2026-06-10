---
description: Configure PostHog, Firebase Analytics, Crashlytics and Sentry
---

You are running **`/kit-setup-analytics`** for NowKit. Goal: wire product analytics
and crash reporting.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/analytics

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 0 — Detect existing state

Before walking the full setup, check what's already configured:

1. Read `KitConfig.kt` — note `ANALYTICS_ENABLED` and `SENTRY_ENABLED`.
2. Read `local.properties` (if it exists) — check `posthog.api.key` and
   `sentry.dsn` are set + non-blank.
3. Check whether `app/google-services.json` exists (Firebase wired) + whether
   the `google-services` + `firebase-crashlytics` plugins are applied in
   `app/build.gradle.kts`.

Build a state list ("PostHog: configured", "Firebase Analytics: not configured",
"Crashlytics: not configured", "Sentry: configured") and show it back to the
developer.

Branch:

- **All providers the dev cares about are already configured** — AskUserQuestion:
  - **Keep as-is** (recommended) — exit without changes.
  - **Add another provider** — skip Step 1, jump straight to whichever
    provider's sub-steps they pick.
  - **Reconfigure everything** — walk the full flow (Step 1 onwards).
  - **Change one provider** — jump to that provider's Step (2 / 3 / 4).
- **Partially configured** — tell the developer which providers are already
  wired ("PostHog already configured, skipping Step 2") and ask whether to add
  the missing ones now. Walk only the missing steps.
- **Nothing configured** — walk the full flow below.

## Step 1 — Pick what to enable

Ask (AskUserQuestion, multi-select) which the developer wants:
- **PostHog** — product analytics (events, funnels).
- **Firebase Analytics** — Google's analytics.
- **Firebase Crashlytics** — crash reporting.
- **Sentry** — alternative / additional crash reporter. Runs alongside
  Crashlytics if both are picked (each gets its own Timber.Tree).

If they pick none of analytics + neither crash reporter, set
`ANALYTICS_ENABLED = false` and `SENTRY_ENABLED = false` in `KitConfig.kt` and
stop. Note: when Crashlytics is on, its crash logging is intentionally NOT
gated by the in-app analytics user-toggle — crash reports still go out even
with the toggle off.

**Pacing rule for this command (important — read before you continue):** the
developer is a non-coder switching between this terminal, browser dashboards
(PostHog / Firebase / Sentry), and sometimes their email inbox. Show **one
sub-step at a time**, then **STOP and wait** for "done" / "next" / a value
they paste before printing the next sub-step. Do NOT chain providers (e.g.
dumping PostHog + Firebase + Sentry in one message) — they lose place.

## Step 2 — PostHog

**Skip this step if PostHog was not picked in Step 1.**

### Sub-step 2.1 — Create the PostHog project

Show this verbatim. Then **STOP and wait** for "done":

> **Create your PostHog project**
> 1. Sign up at https://posthog.com.
> 2. Pick a region during sign-up — **US Cloud** (`https://us.i.posthog.com`)
>    or **EU Cloud** (`https://eu.i.posthog.com`). Pick the one closest to
>    most of your users.
> 3. Create a new project — name it after your app.
>
> Say "done" when the PostHog dashboard is open.

### Sub-step 2.2 — Grab the Project token + region

Show this verbatim. Then **STOP and wait** for the values:

> **Get your PostHog Project token**
> 1. Open **Settings → General**.
> 2. Under **Project token & ID**, copy the **Project token** (starts with
>    `phc_`).
> 3. Note your region's host (top-right account menu or the URL you signed
>    up on — `us.i.posthog.com` or `eu.i.posthog.com`).
> 4. Paste **both** back here: the `phc_...` token first, then the host URL
>    on a new line.

When the developer pastes them, write to `local.properties` (git-ignored —
never committed):

    posthog.api.key=phc_YOUR_POSTHOG_KEY
    posthog.host=https://us.i.posthog.com

(Substitute the actual host they pasted.) Confirm what you wrote.

## Step 3 — Firebase Analytics / Crashlytics

**Skip this whole step if neither Firebase Analytics nor Firebase Crashlytics
was picked in Step 1.**

Tell the developer:

> Next we'll wire Firebase. This needs a `google-services.json` file from
> Firebase and the Crashlytics Gradle plugin if you picked Crashlytics. I'll
> walk you through it — say "next" when you're ready to switch from PostHog
> to Firebase.

**STOP and wait** for "next" / "ready" / "go".

When confirmed, read `.claude/commands/kit-setup-firebase.md` and follow it
inline. That helper already paces its own sub-steps. Include the Crashlytics
plugin if Crashlytics was picked.

When `kit-setup-firebase` reports done, tell the developer:

> Firebase wired. (Firebase Analytics works automatically with
> `google-services.json` — no extra config. Crashlytics needed the plugin,
> which is now applied.)

## Step 4 — Sentry

**Skip this whole step if Sentry was not picked in Step 1.**

Tell the developer:

> Last one — Sentry. Say "next" when you're ready to move on from Firebase.

**STOP and wait** for "next" / "ready" / "go".

### Sub-step 4.1 — Create the Sentry project

Show this verbatim. Then **STOP and wait** for "done":

> **Create your Sentry project**
> 1. Sign up at https://sentry.io.
> 2. Create a new project.
> 3. Pick **Android** as the platform when prompted.
> 4. Set any Alerts frequency you prefer.
> 5. Name the project after your app → **Create project**.
>
> Say "done" when the project dashboard is open.

### Sub-step 4.2 — Grab the DSN

Show this verbatim. Then **STOP and wait** for the value:

> **Get your Sentry DSN**
> 1. Click on Settings Gear icon on Top-Right to see Project Settings.
> 2. Scroll in the left sidebar to **SDK Setup → Client Keys (DSN)**.
> 3. Copy the **DSN** value (looks like
>    `https://abc123@o456.ingest.sentry.io/7890`).
> 3. Paste it back here.

When the developer pastes the DSN, write to `local.properties`:

    sentry.dsn=https://abc123@o456.ingest.sentry.io/7890

In `KitConfig.kt`, flip `SENTRY_ENABLED = true`. Confirm what you wrote.

If the developer picked **both** Crashlytics and Sentry, mention they will run
in parallel — each event / non-fatal will land in both dashboards. This is fine
and sometimes desired (Sentry's release health vs Crashlytics' Play
integration); remove one later by flipping its `KitConfig` flag.

## Step 5 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was enabled. Mention the in-app analytics toggle: end users can opt
out via Settings → Privacy, and that preference is respected for analytics events
(crash reporting still runs).
