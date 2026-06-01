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

**Pacing rule for this step:** the developer is a non-coder switching between
this terminal and the RevenueCat dashboard. Show **one sub-step at a time**,
then **STOP and wait** for "done" / "next" before printing the next sub-step.
Do NOT dump the full RevenueCat set-up in one message.

### Sub-step 2.1 — Create the RevenueCat project

Show this verbatim. Then **STOP and wait** for "done":

> **Create your RevenueCat project**
> 1. Sign up at https://app.revenuecat.com.
> 2. **+ New project** (top-left project picker) → name it after your app →
>    **Create**.
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

Ask (AskUserQuestion):
- **Soft (recommended)** — paywall is dismissible; the user can skip it and
  use the free version of the app.
- **Hard** — paywall blocks the app until the user purchases or restores.

Set `KitConfig.PAYWALL_MODE` + `KitConfig.ENTITLEMENT_ID` (from 2.3) +
`KitConfig.PAYWALL_ENABLED = true`. Confirm what you wrote.

### Sub-step 2.6 — Release-time setup (products, offerings, paywall design)

Show this verbatim. This block is **informational** — the developer reads it
once now, then comes back to do it before their first Play release. Tell them
up-front this is "save-for-later, not for now".

> **Before you can charge real users, three pieces have to be wired up.**
> The paywall screen renders today without them (you'll see an
> "offerings empty" state on device — fine for development). Come back to
> this block right before your first Play release.
>
> **1. Create your in-app products in Play Console**
>
> - Play Console → your app → **Monetize → Products → Subscriptions** (for
>   recurring) or **In-app products** (for one-time purchases).
> - Create each tier you want to sell (e.g. monthly / yearly / lifetime).
>   Each gets a Product ID — write them down, you'll paste them into
>   RevenueCat.
> - **Activate** each product. Inactive products won't appear in offerings.
>
> **2. Connect Play to RevenueCat (service account JSON)**
>
> RevenueCat validates purchases by talking to Play on your behalf. It
> needs a Google service account with Play Console permissions.
>
> Follow https://www.revenuecat.com/docs/platform-resources/google-platform-resources
> end-to-end. The short version:
>
> - Google Cloud Console → **IAM & Admin → Service Accounts → Create
>   service account** (in the same Google account that owns your Play
>   Console).
> - Give it a name like `revenuecat`. Skip the role steps.
> - Open the new service account → **Keys → Add key → Create new key →
>   JSON** → download the file.
> - Play Console → **Users and permissions → Invite users** → invite the
>   service account email → grant **View financial data** + **Manage
>   orders and subscriptions** access for your app.
> - In RevenueCat → **Apps → your Play Store app** (the one from sub-step
>   2.2) → upload the JSON into **Service Account Credentials**.
> - **Wait ~36 hours** for Google to propagate the permission before you
>   test real purchases.
>
> **3. Create offerings + design your paywall in RevenueCat**
>
> - RevenueCat → **Product catalog → Products → + New product** → pick
>   *Google Play* → paste the Product IDs from step 1. Repeat per product.
> - RevenueCat → **Product catalog → Offerings → + New offering** (call
>   it `default` unless you want multiple offerings). **Attach** each
>   product you imported.
> - RevenueCat → **Paywalls → + New paywall** → pick the offering →
>   use the visual editor to set the layout, colors, copy, and call to
>   action. **Save + Publish**.
>
> The kit's `PaywallScreen` uses RevenueCat's prebuilt `Paywall`
> composable, which automatically loads the published paywall on first
> launch — no app code changes needed when you tweak the paywall design.
>
> More step-by-step (with screenshots from the old KMM kit, but the
> RevenueCat side is identical):
>
>     https://www.shipkaro.dev/mobile-docs/features/in-app-purchases/
>
> Say "got it" — you don't have to do any of step 2.6 right now to continue
> with the rest of the setup.

## Step 3 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it
builds once at the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Tell the developer real purchases can only be tested with a signed build on a
Play Console test track (and after sub-step 2.6 is complete) — the paywall
screen renders now, but real billing won't credit entitlements until the
Service Account JSON is uploaded. Report what was set.
