---
description: Continuation of kit-start-setup
---

This continues **`/kit-start-setup`** from Step 5.

## Step 5 — Paywall & subscriptions  →  `.claude/commands/kit-kit-setup-paywall.md`

If the developer chose **"free app"** in Step 0, SKIP the full setup but DO
flip the kit's paywall switch off — otherwise the kit's nav still shows the
default Paywall screen after Auth on first launch. Find `KitConfig.kt`
(under `app/src/main/java`, package `...core.config`) and set:

    const val PAYWALL_ENABLED: Boolean = false

Then tell the developer the paywall is disabled and they can run
`/kit-setup-paywall` later if they ever add subscriptions. Move to Step 6.

Otherwise (paid app, or just exploring), configure RevenueCat via the command
file.

## Step 6 — Analytics & crash reporting  →  `.claude/commands/kit-kit-setup-analytics.md`

Configure PostHog and/or Firebase Analytics + Crashlytics.

## Step 7 — Build & run  →  `.claude/commands/kit-run-app.md`

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
Show this verbatim, then ask the user (wait for their answer) — "Start these now" / "Later":

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

Ask the user (wait for their answer) — don't write a paragraph; the options carry the why:

- **Yes, create a private repo** (recommended) — free, private (only you see it),
  gives you a backup + full history. I'll create the repo and push everything via
  the GitHub CLI.
- **Not now** — your app stays only on this laptop; you can run
  `/kit-save-to-github` anytime later.

If **Yes** → Call /kit-save-to-github (it handles GitHub login, creates the
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
