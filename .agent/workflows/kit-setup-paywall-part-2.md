---
description: Continuation of kit-setup-paywall
---

This continues `/kit-setup-paywall` from **sub-step 2.6** — products + Play billing
(the release-time work). You should have done sub-steps 2.1–2.5 in the main command
first.

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

That covers the Play-side pieces (account, signed build, products). Next, connect
Play to RevenueCat and build the offering + paywall — call
/kit-setup-paywall-part-3 to continue.
