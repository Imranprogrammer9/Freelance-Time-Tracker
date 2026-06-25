---
description: Configure the RevenueCat paywall and subscriptions
---

You are running **`/kit-setup-paywall`** for NowKit. Goal: wire RevenueCat so the
app can sell subscriptions.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/paywall

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 0 — Detect existing state

Before walking the full setup, check what's already configured:

1. Read `KitConfig.kt` — note `PAYWALL_ENABLED`, `ENTITLEMENT_ID`, `PAYWALL_MODE`.
2. Read `local.properties` (if it exists) — note whether
   `revenuecat.android.api.key` is set + starts with `goog_`.

Branch on the result:

- **Fully configured** (`PAYWALL_ENABLED = true` AND key set AND entitlement
  non-default) — Ask the user (wait for their answer):
  - **Keep as-is** (recommended) — exit without changes.
  - **Reconfigure** — walk the full flow.
  - **Change paywall mode only** — jump to sub-step 2.5.
  - **Change API key only** — jump to sub-step 2.4.
  - **Change entitlement only** — jump to sub-step 2.3 (re-prompt for ID + edit
    `KitConfig.ENTITLEMENT_ID`).
  - **Set up products + Play billing** — jump to sub-step 2.6 (the release-time
    products / service-account JSON / offerings + paywall work). This is the
    dashboard-only part that the kit can't detect from your code, so offer it
    here for anyone coming back to finish it before release.
- **Partially configured** — tell the developer which sub-steps you're
  skipping (e.g. "API key already in local.properties — skipping 2.4") and
  walk only the missing pieces.
- **Nothing configured** (default state — `PAYWALL_ENABLED = true` but no key)
  — walk the full flow below.

## Step 1 — Does the app need a paywall?

Ask the user (wait for their answer): does the app need a paywall, or is it
free? If the app is free, set `PAYWALL_ENABLED = false` in
`KitConfig.kt`, make sure the `com.android.vending.BILLING` permission in
`app/src/main/AndroidManifest.xml` stays **commented out** (a free app shouldn't
declare billing), and stop. The kit still builds; the paywall screen just is not
wired into navigation.

## Step 2 — Set up RevenueCat

**Pacing rule for this step:** the developer is a non-coder switching between
this terminal and the RevenueCat dashboard. Show **one sub-step at a time**,
then **STOP and wait** for "done" / "next" before printing the next sub-step.
Do NOT dump the full RevenueCat set-up in one message.

### Sub-step 2.1 — Create the RevenueCat project

Show this verbatim. Then **STOP and wait** for "done":

> **Create your RevenueCat project**
> 1. Sign up at https://app.revenuecat.com.
> 2. **Create a New project** (top-left project picker) → name it after your app.
> 3. Choose **Native Android** in the the **Platform(s)** dropdown.
> 4. Click **Create project** button.
> 5. Click the **Go to dashboard** and DO NOT click the **Continue** button.
> 3. Wait for the project dashboard to open.
>
> Say "done" when the dashboard is open.

### Sub-step 2.2 — Add a Play Store app

Show this verbatim. Then **STOP and wait** for "done":

> **Connect your Google Play app**
> 1. In the left sidebar, click **Apps** (near the bottom, between *Lifecycle*
>    and *Web*).
> 2. Click the **New app configuration** card → pick **Google Play Store**.
> 3. Fill the form:
>    - **App name** — anything, e.g. `<your app name> (Play Store)`.
>    - **Google Play package name** — your `applicationId` from
>      `app/build.gradle.kts` (e.g. `dev.shipkaro.kit` if not renamed).
> 4. **Leave the *Service Account Credentials JSON* upload empty for now** —
>    we'll handle that in sub-step 2.6 (release-time only; not needed to ship
>    the paywall UI).
> 5. Click **Save changes**.
>
> Say "done" when the Play Store app appears under **Apps**.

### Sub-step 2.3 — Create the entitlement

Show this verbatim. Then **STOP and wait** for "done":

> **Create the `premium` entitlement**
> 1. Left sidebar → **Product catalog → Entitlements**.
> 2. **+ New entitlement**.
> 3. Identifier: `premium` (lowercase). Display name: anything.
> 4. Save.
>
> Say "done" when the entitlement is saved.

You'll write this identifier into `KitConfig.ENTITLEMENT_ID` in Step 3.
Remember the exact value the developer typed (use `premium` unless they
deliberately picked something else).

### Sub-step 2.4 — Copy your Android API key

Show this verbatim. Then **STOP and wait** for the value:

> **Copy your Android API key**
> 1. Left sidebar → **API keys**.
> 2. Find the **Google / Android** key (starts with `goog_`).
> 3. Click the copy icon → paste the value back here.

When the developer pastes the key, write it into `local.properties`
(git-ignored — never committed):

    revenuecat.android.api.key=goog_YOUR_ANDROID_KEY

Confirm what you wrote.

### Sub-step 2.5 — Pick paywall enforcement

Ask the user (wait for their answer):
- **Soft (recommended)** — paywall is dismissible; the user can skip it and
  use the free version of the app.
- **Hard** — paywall blocks the app until the user purchases or restores.

Set `KitConfig.PAYWALL_MODE` + `KitConfig.ENTITLEMENT_ID` (from 2.3) +
`KitConfig.PAYWALL_ENABLED = true`.

Also **uncomment the billing permission** in `app/src/main/AndroidManifest.xml`:

    <uses-permission android:name="com.android.vending.BILLING" />

RevenueCat / Google Play Billing needs it for purchases, and Google Play Console
won't let you create in-app products until an uploaded build declares it. (It's a
normal permission — no runtime prompt.)

Confirm what you wrote (config + the permission).


---

The steps above (2.1–2.5) are all you need at setup time. The products + Play
billing work (sub-step 2.6) is release-time only — when you are ready for it, call
/kit-setup-paywall-part-2 to continue.
