---
description: Configure PostHog and Firebase analytics + crash reporting
---

You are running **`/setup-analytics`** for the ShipKaro Android Kit. Goal: wire
product analytics and crash reporting.

Audience: first-time mobile developers. Be brief; you make the edits.

## Step 1 — Pick what to enable

Ask (AskUserQuestion, multi-select) which the developer wants:
- **PostHog** — product analytics (events, funnels).
- **Firebase Analytics** — Google's analytics.
- **Firebase Crashlytics** — crash reporting.

If they want none, set `ANALYTICS_ENABLED = false` in `KitConfig.kt` and stop.
Note: Crashlytics crash logging is intentionally NOT gated by that flag — crash
reports still go out even with analytics off.

## Step 2 — PostHog

Guide them: posthog.com → create a project → Project Settings → copy the
**Project API Key** (starts with `phc_`) and note the **host** — US cloud is
`https://us.i.posthog.com`, EU cloud is `https://eu.i.posthog.com`.

Write to `local.properties` (git-ignored — never commit it):

    posthog.api.key=phc_XXXXXXXXXXXX
    posthog.host=https://us.i.posthog.com

## Step 3 — Firebase Analytics / Crashlytics

These need the Firebase plugins. Read `.claude/commands/setup-firebase.md` and
follow it (include the Crashlytics plugin if they chose crash reporting), then
return here. No extra keys are needed beyond `google-services.json`.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was enabled. Mention the in-app analytics toggle: end users can opt
out via Settings → Privacy, and that preference is respected for analytics events
(crash reporting still runs).
