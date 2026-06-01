---
description: Configure PostHog, Firebase Analytics, Crashlytics and Sentry
---

You are running **`/kit-setup-analytics`** for ShipKit. Goal: wire product analytics
and crash reporting.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/analytics

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

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

## Step 2 — PostHog

Show the developer exactly this:

> **Get your PostHog key:**
> 1. Sign up at https://posthog.com and create a project.
> 2. Open **Settings → General**. Under **Project token & ID**, copy the
>    **Project token** (it starts with `phc_`).
> 3. In the same section, check the **Region** — US Cloud uses
>    `https://us.i.posthog.com`, EU Cloud uses `https://eu.i.posthog.com`.

Write to `local.properties` (git-ignored — never committed):

    posthog.api.key=phc_YOUR_POSTHOG_KEY
    posthog.host=https://us.i.posthog.com

## Step 3 — Firebase Analytics / Crashlytics

These need the Firebase plugins. Read `.claude/commands/kit-setup-firebase.md` and
follow it (include the Crashlytics plugin if they chose crash reporting), then
return here. No extra keys are needed beyond `google-services.json`.

## Step 4 — Sentry

Only run this step if the developer picked Sentry in Step 1.

Show the developer exactly this:

> **Get your Sentry DSN:**
> 1. Sign up at https://sentry.io and create an **Android** project.
> 2. Open **Settings → Projects → <your project> → Client Keys (DSN)**.
> 3. Copy the **DSN** value (looks like
>    `https://abc123@o456.ingest.sentry.io/7890`).

Write the DSN into `local.properties`:

    sentry.dsn=https://abc123@o456.ingest.sentry.io/7890

In `KitConfig.kt`, flip `SENTRY_ENABLED = true`.

If the developer picked **both** Crashlytics and Sentry, mention they will run
in parallel — each event / non-fatal will land in both dashboards. This is fine
and sometimes desired (Sentry's release health vs Crashlytics' Play integration);
remove one later by flipping its `KitConfig` flag.

## Step 5 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was enabled. Mention the in-app analytics toggle: end users can opt
out via Settings → Privacy, and that preference is respected for analytics events
(crash reporting still runs).
