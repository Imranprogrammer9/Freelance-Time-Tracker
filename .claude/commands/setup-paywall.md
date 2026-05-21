---
description: Configure the RevenueCat paywall and subscriptions
---

You are running **`/setup-paywall`** for the ShipKaro Android Kit. Goal: wire
RevenueCat so the app can sell subscriptions.

Audience: first-time mobile developers. Be brief; you make the edits.

## Step 1 — Does the app need a paywall?

Ask (AskUserQuestion). If the app is free, set `PAYWALL_ENABLED = false` in
`KitConfig.kt` and stop. The kit still builds; the paywall screen just is not
wired into navigation.

## Step 2 — RevenueCat account + product

Guide the developer:
1. Create an account at revenuecat.com and a new project.
2. Connect their Google Play app. This needs a Play Console app with a
   subscription product — tell them this can be done later; the kit runs fine
   without it, the paywall just shows no offerings until products exist.
3. In RevenueCat, create an **Entitlement** and note its identifier (the kit
   default is `premium`).
4. Get the **Android API key**: RevenueCat dashboard → Project Settings → API
   keys → the Google / Android key (it starts with `goog_`).

## Step 3 — Configure the kit

Write the API key to `local.properties` (git-ignored — never commit it):

    revenuecat.android.api.key=goog_XXXXXXXXXXXX

In `KitConfig.kt` set:
- `ENTITLEMENT_ID` — must match the entitlement identifier from RevenueCat
  (default `premium`).
- `PAYWALL_MODE` — ask the developer which they want:
  - `SOFT` — the paywall is dismissible; the user can skip it and use the app
    for free.
  - `HARD` — the paywall blocks the app until the user purchases or restores.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Tell the developer real purchases can only be tested with a signed build on a
Play Console test track — the paywall screen renders now, but shows offerings
only once Play products are live. Report what was set.
