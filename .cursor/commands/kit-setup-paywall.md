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

### Sub-step 2.6 — Products + billing: now or later?

A few pieces are needed before you can charge **real** users: a Play app + a
billing-enabled build on a testing track, then in-app products, then the
RevenueCat ↔ Play connection (service-account JSON), then offerings + paywall
design. None of this is needed to keep building — the paywall screen renders
today (it just shows an "offerings empty" state on device, which is fine in
development).

**Order matters:** Google Play **blocks product creation until a build that
declares the BILLING permission is live on a track**. So the build comes
*before* products — not after. (This is the #1 thing developers trip on.)

**If running as part of `/kit-start-setup`: skip ALL of the now/later content
below** — don't ask the question, and don't show the "before your first release"
block. It only confuses a vibe coder this early and tempts them to run
`/kit-upload-on-google-play` prematurely. Instead, just confirm the paywall config
is set and add **one neutral line** — e.g. "Real Play billing gets set up later;
the kit asks you about it after your first build runs." — then continue to the
next sub-step. Don't name release/upload commands here. The full now/later decision
+ instructions live in `/kit-start-setup` **Step 8**, after the first build is on
the device. Only run the question + blocks below when `/kit-setup-paywall` is
invoked **standalone**.

Ask the user (wait for their answer):

- **Later (recommended)** — you only need this right before your first Play
  release. Show the short summary block below, then continue.
- **Now** — walk through all three pieces (only pick this if you already have a
  Play Console developer account and want to set up billing today).

**If "Later"** — show this verbatim, then continue (don't wait long):

> **IMPORTANT before your first release.** 
>
> To charge real users in production, you have to connect the Google Play Console
> with the RevenueCat. That needs to be done in both websites and can't be done
> here by the AI agent.
>
> It's not urgent right now and can be skipped. It needs to be done before the 
> app's first public release on Google Play.
>
> Follow this full step-by-step to connect Google Play with RevenueCat
> **https://kit.shipkaro.dev/docs/paywall**
>
> Note that the integration between Google Play and RevenueCat takes around
> **36 hours** to propagte.
>
> The kit reminds you again in `/kit-upload-on-google-play` before you ship. Say
> "got it" to continue.

**If "Now"** — walk these pieces **in this exact order** (each unblocks the next),
one at a time, **STOP and wait** after each (same pacing rule as above). Full
content on the docs page (https://kit.shipkaro.dev/docs/paywall, "Before you
charge real users"); present it paced, not all at once:

1. **Play Console account + app entry** — you need a Google Play developer account
   (one-time $25). No account at all? Stop here — say so and we'll skip the rest
   until you have one; the paywall still renders in dev meanwhile. If you have an
   account but no app yet, create the app entry now. **First read the
   `applicationId`** from `app/build.gradle.kts` and substitute it into the
   **Package name** line, then show this verbatim:

   > **Create your app in Play Console:**
   > 1. Open https://play.google.com/console → **Create app**.
   > 2. **App name** — your app's public name (max 30 chars; changeable later).
   > 3. **Package name** — paste exactly: `<APPLICATION_ID>` (your package from
   >    `app/build.gradle.kts`; it must match your build, and it's **permanent**
   >    once created — don't typo it). Click **Check availability**.
   > 4. **Default language** — your main locale (e.g. English (US)).
   > 5. **App or game** — **App**.
   > 6. **Free or paid** — pick **Free** (you sell subscriptions / in-app purchases,
   >    so the app download is free and you charge via billing; you **can't switch
   >    Free → Paid after publishing**).
   > 7. Tick the declarations → **Create app**.
2. **Signed build on a testing track** — run **`/kit-sign-release`**: it builds a
   signed AAB (the BILLING permission is already in the manifest) and walks the
   **internal testing** upload. *This is what unblocks product creation* and starts
   the first-review clock. Open the **tester opt-in URL** on your test device after.
3. **In-app products** *(now unblocked by step 2)* — the fiddly part: Google Play
   nests subscriptions as **Subscription → Base plan**, and you must activate
   *both*. **STOP and wait** while the developer creates + activates each one.

   **For subscriptions** (recurring — most apps), show this verbatim:
   > **Create a subscription in Play Console:**
   > 1. Play Console → your app → **Monetize → Products → Subscriptions → Create
   >    subscription**.
   > 2. **Product ID** — the permanent identifier you'll paste into RevenueCat,
   >    e.g. `premium_monthly` or `pro_yearly` (lowercase, digits, underscores).
   >    **Can't be changed or reused once created** — pick carefully.
   > 3. **Name** — internal only; users never see it (your paywall text comes from
   >    RevenueCat, not from here).
   > 4. Create it, then **add a base plan** (a subscription needs at least one):
   >    - **Base plan ID** — e.g. `monthly` / `yearly` (permanent; lowercase +
   >      hyphens).
   >    - **Type** — pick **Auto-renewing** (the normal subscription; *Prepaid* and
   >      *Installments* are special cases — skip them).
   >    - **Billing period** — e.g. Monthly or Yearly.
   >    - **Price** — set it for your markets.
   > 5. **Activate BOTH the base plan AND the subscription.** An inactive
   >    subscription, or one with no active base plan, silently won't load — a very
   >    common "offerings empty" cause.
   > 6. Repeat for each tier. **Note every Product ID** — you'll import them into
   >    RevenueCat in piece 5.

   **For one-time unlocks** (non-recurring) use **In-app products** instead —
   simpler: an ID + price, then **Activate**. Note each Product ID.
4. **Service-account JSON → RevenueCat** (the actual Play ↔ RevenueCat connection)
   — Google Cloud Console → IAM & Admin → Service Accounts → create → Keys → JSON;
   Play Console → Users and permissions → invite that service-account email with
   *View financial data* + *Manage orders and subscriptions*; RevenueCat → Apps →
   your Play Store app → upload the JSON. **~36 h** to propagate. (Official guide:
   https://www.revenuecat.com/docs/platform-resources/google-platform-resources)
5. **RevenueCat: products → entitlement → offering → paywall** — the mapping that
   actually makes a purchase unlock the app. The **entitlement attach** (step 2) is
   the most-missed step. Show this verbatim:
   > 1. **Import products** — RevenueCat → **Product catalog → Products → + New** →
   >    pick your **Google Play** app → paste each **Product ID** from Play. For a
   >    subscription, RevenueCat pulls its base plans in automatically.
   > 2. **Attach to the `premium` entitlement** — Product catalog → **Entitlements →
   >    premium → Attach products** → add each product. *This is what flips the user
   >    to premium* — the kit unlocks on the `premium` entitlement
   >    (`KitConfig.ENTITLEMENT_ID`). Skip this and purchases succeed but **nothing
   >    unlocks**.
   > 3. **Offering** — Product catalog → **Offerings** → use `default` (or create
   >    one) → add your products as packages.
   > 4. **Paywall** — **Paywalls → + New** → pick the offering → design → **Save +
   >    Publish**. The kit's `PaywallScreen` renders this automatically.

   Until a build is on a track *and* all of the above is done, the device paywall
   shows "offerings empty".

The kit's `PaywallScreen` loads the published paywall automatically — no code
changes when you tweak the design.

## Step 3 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it
builds once at the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Tell the developer real purchases can only be tested with a signed build on a
Play Console test track (and after sub-step 2.6 is complete) — the paywall
screen renders now, but real billing won't credit entitlements until the
Service Account JSON is uploaded. Report what was set.
