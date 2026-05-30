---
description: Configure the RevenueCat paywall and subscriptions
---

You are running **`/kit-setup-paywall`** for ShipKit. Goal: wire RevenueCat so the
app can sell subscriptions.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/paywall

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Does the app need a paywall?

Ask (AskUserQuestion). If the app is free, set `PAYWALL_ENABLED = false` in
`KitConfig.kt` and stop. The kit still builds; the paywall screen just is not
wired into navigation.

## Step 2 — Set up RevenueCat

Show the developer exactly this:

> **Create your RevenueCat project:**
> 1. Sign up at https://app.revenuecat.com and create a new project.
> 2. Add an app: **Project Settings → Apps → New app** → choose **Google Play**.
> 3. Open **Entitlements** → create one (e.g. `premium`). Note its identifier.
> 4. Open **Project Settings → API keys** → copy the **Google / Android** key
>    (it starts with `goog_`).
>
> Connecting a real Play subscription product can be done later — the kit runs
> fine without it; the paywall just shows no offerings until products exist.

## Step 3 — Configure the kit

Write the API key into `local.properties` (git-ignored — never committed):

    revenuecat.android.api.key=goog_YOUR_ANDROID_KEY

In `KitConfig.kt`, set:
- `ENTITLEMENT_ID` — must match the entitlement identifier created in Step 2
  (default `premium`).
- `PAYWALL_MODE` — ask the developer which they want:
  - `SOFT` — the paywall is dismissible; the user can skip it and use the app
    for free.
  - `HARD` — the paywall blocks the app until the user purchases or restores.

## Step 4 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Tell the developer real purchases can only be tested with a signed build on a
Play Console test track — the paywall screen renders now, but shows offerings
only once Play products are live. Report what was set.
