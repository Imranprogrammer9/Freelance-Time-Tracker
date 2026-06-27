---
description: Put your app on Google Play pre-registration — reuse your signed AAB, optionally wire a pre-registration reward, and walk the Play Console + RevenueCat setup
---

You are running **`/kit-pre-register-setup`** for NowKit.

Goal: get the app live on **Google Play pre-registration** — its store listing
visible to users *before* launch, collecting pre-registrations — with the least
friction. Optionally wire a **pre-registration reward** (a free perk users get on
launch day for pre-registering).

Audience: first-time mobile developers / vibe coders. Most of this is manual Play
Console + RevenueCat dashboard work that **can't** be automated — your job is to
do the local/code checks **for** them, then present each web step verbatim and
wait. When a block is quoted with `>`, show it to the developer **verbatim**.
Prose outside those blocks is instructions for you.

**Docs:** https://kit.shipkaro.dev/docs/release

## What pre-registration is (one-paragraph primer for the developer)

Pre-registration makes your store listing public *before* the app launches. Users
tap **Pre-register**; on launch day Play push-notifies them and (if eligible)
auto-installs the app. It builds day-one installs and an early ranking signal.
The build you upload to the pre-registration track is used **only to determine
which devices can pre-register** — it is **never shipped** to users.

## Why your existing build already qualifies

If the developer has run **`/kit-sign-release`**, they already have a **signed
AAB on a testing track with the `BILLING` permission**. That is exactly the
artifact pre-registration needs:
- it specifies supported devices for the pre-registration track, and
- if they offer a reward (a one-time product), Play **requires** the bundle to
  include the Play Billing Library — which RevenueCat pulls in transitively, so
  it's already there.

So this command **reuses** that build. It does not rebuild or re-sign anything.

## Progress tracking

Call **TaskCreate** with these tasks; mark each `in_progress` on entry and
`completed` when done (prefix `[skipped] ` via **TaskUpdate** if skipped):

- 1 — Check preconditions (signed AAB, RC↔Play, listing readiness)
- 2 — New-account testing-gate eligibility
- 3 — Decide on a pre-registration reward
- 4 — (If reward) create the one-time product + map it in RevenueCat
- 5 — Turn on pre-registration in Play Console
- 6 — (If reward) configure the reward in Play Console
- 7 — Launch reminders (90-day clock + auto-install)

## Step 1 — Check preconditions

Do these checks locally and report a short readiness summary. Don't block on every
item — surface gaps and let the developer decide.

**1.1 — Paywall + entitlement.** Read `KitConfig.kt` (`core/config/`). Confirm
`PAYWALL_ENABLED = true` and note the **actual** `ENTITLEMENT_ID` value (default
`"premium"`, but the developer may have changed it — **never hardcode it**; read
it and use the real value everywhere below).

**1.2 — Signed build exists.** Check for a release AAB / prior signing:

```bash
ls -1 app/build/outputs/bundle/release/app-release.aab 2>/dev/null || echo "no local AAB"
grep -E '^release\.(store|key)\.' local.properties 2>/dev/null || echo "no release.* keys"
```

- AAB present **or** signing configured → good; the developer has (or can build) a
  signed bundle. If they haven't uploaded one to a track yet, point them to
  **`/kit-sign-release`** first and stop.
- Neither → they haven't signed yet. Run **`/kit-sign-release`** first, then come
  back. Stop here.

**1.3 — RevenueCat ↔ Play connection.** This can't be checked from code. Just
remind: pre-registration and rewards assume the RC↔Play connection from
`/kit-setup-paywall` is done. If they haven't, the reward path (Step 4) won't work
— the non-reward path still does.

**1.4 — Store listing readiness.** Pre-registration shows your **public** store
listing, so it should look finished. Check whether they've generated copy +
assets; if not, recommend (don't force) running these first:
- **`/kit-generate-aso`** — title, short + long description.
- **`/kit-generate-screenshots`** — Play screenshots.
- **`/kit-generate-legal`** — privacy policy (required for any public listing).

## Step 2 — New-account testing-gate eligibility

Google requires **personal developer accounts created on or after 13 Nov 2023** to
complete closed testing (**≥12 testers, opted-in for ≥14 days**) before any
production-facing release — and **pre-registration counts as production-facing**.
Org accounts and older personal accounts are exempt. This can't be detected from
code, so **ask** (AskUserQuestion):

> **Is your Google Play developer account ready for a public release?**
> - **Organisation account, or personal account created before 13 Nov 2023** —
>   exempt, you can pre-register now.
> - **Personal account (created 13 Nov 2023 or later) — testing already done** —
>   you've run ≥12 testers for ≥14 days on a closed track.
> - **Personal account (created 13 Nov 2023 or later) — testing NOT done yet** —
>   you still need to complete the closed-testing requirement.

- First two answers → continue to Step 3.
- Third answer → **stop the pre-registration flow** and explain: they must finish
  the 12-tester / 14-day closed test before Play will let them pre-register. Point
  them to **`/kit-sign-release`** (to get a build on a closed track) and Play
  Console's testing requirements. Mark remaining tasks `[skipped]`.

## Step 3 — Decide on a pre-registration reward

Explain the trade-off, then ask (AskUserQuestion):

> **Offer a pre-registration reward?** A reward is a free perk users get on launch
> day for pre-registering — it lifts conversion, but has strict rules.
> - **Yes — give a free premium pass** *(recommended for subscription apps)* — a
>   one-time product that unlocks premium for a fixed window (e.g. 30 days).
> - **Yes — give a cosmetic / content unlock** — a theme, icon pack, or bonus
>   content, not the subscription itself.
> - **No reward** — just collect pre-registrations.

Make these constraints explicit before they commit:

> ⚠️ **Reward rules — read before choosing:**
> - You get **one** reward per app, **for the entire lifetime** of the app's
>   pre-registration. Once created it **cannot be edited or deleted**.
> - Rewards must be **active one-time products** — **subscriptions are not
>   allowed**. That's why a subscription app gives a *one-time premium pass*, not
>   "a free month of the subscription".
> - If you promise a reward and fail to deliver it, Play can **suspend** the app.

- **No reward** → skip to Step 5.
- **Reward** → go to Step 4. For a subscription app, the "premium pass" option is
  the right fit and slots straight into the kit's entitlement gate.

## Step 4 — Create the one-time product + map it in RevenueCat

Two dashboards. The kit's paywall gate (`PurchaseManager`) checks **one
entitlement** (`<ENTITLEMENT_ID>`) and is **product-type agnostic** — it doesn't
care whether the entitlement was granted by a subscription or a one-time product.
So the whole job is: make a one-time product, and attach it to that same
entitlement. **No app code changes are needed.**

**Pace this — one sub-step at a time. Present 4.1 and wait for the developer to say
"done" before showing 4.2; then wait again before 4.3. Do NOT dump all three blocks
at once** (these are two different dashboards — they'll lose their place).

**4.1 — Create the one-time product in Play Console** (present verbatim, then wait):

> **Create the reward product in Play Console:**
> 1. Play Console → your app → **Monetize → Products → In-app products →
>    Create product**.
> 2. **Product ID** — e.g. `premium_pass_30d` (you can't change this later).
> 3. **Name / description** — what the user sees, e.g. "30 days of Premium, free".
> 4. **Price** — set any price and **activate** it (it must be *active* to be
>    selectable as a reward; users won't pay — they receive it free).
> 5. **Save** and make sure its status is **Active**.

Wait for "done".

**4.2 — Map it to your entitlement in RevenueCat** (present verbatim, substituting
the real `<ENTITLEMENT_ID>`, then wait):

> **Make the pass unlock premium in RevenueCat:**
> 1. RevenueCat dashboard → your project → **Products → + New** → import/select the
>    Play one-time product `premium_pass_30d`.
> 2. Open the product → **Entitlements → attach `<ENTITLEMENT_ID>`**.
> 3. Because it's a one-time (non-subscription) product, RevenueCat asks for a
>    **duration** — set it to match the perk (e.g. **1 month** for a 30-day pass).
>    After that window the entitlement expires and your paywall returns.

Wait for "done".

**4.3 — Confirm the unlock is automatic (no code).** Reassure the developer:

> ✅ Nothing to code. Your paywall already unlocks whenever `<ENTITLEMENT_ID>` is
> active, and the kit re-checks entitlements every time the app comes to the
> foreground — so when a user claims the reward in the Play Store and returns to
> your app, premium unlocks the same session.

(That foreground re-check is the `ProcessLifecycleOwner` observer in
`KitApplication`. If `PAYWALL_ENABLED` is somehow off, turn it on — otherwise the
reward won't gate anything.)

## Step 5 — Turn on pre-registration in Play Console

Manual web step. **First read the `applicationId`** from `app/build.gradle.kts`
and substitute it where the block references your package. Then present verbatim
and wait:

> **Turn on pre-registration:**
> 1. Play Console → your app → **Test and release → Pre-registration** (in the
>    left nav, directly under **Testing**).
> 2. **Select / upload a build for the pre-registration track.** Choose the **same
>    signed AAB** you already put on your internal/closed track — Play uses it only
>    to work out supported devices. (Path:
>    `app/build/outputs/bundle/release/app-release.aab`.)
> 3. **Choose countries/regions** for availability.
> 4. **Auto-install** — leave **on** so eligible users get the app installed
>    automatically on launch day (recommended).
> 5. Make sure your **store listing** (descriptions, screenshots, privacy policy)
>    is complete — this is what pre-registering users see.
> 6. **Save** and submit the pre-registration for review.

Wait for "done".

## Step 6 — Configure the reward in Play Console (reward path only)

Present verbatim:

> **Attach the reward to your pre-registration:**
> 1. In the **Pre-registration** section, find **Pre-registration rewards** →
>    **Set up reward**.
> 2. Select the one-time product you created (`premium_pass_30d`).
> 3. Add the optional **reward badge** and a **link to your Terms & Conditions**
>    (required — users must accept before claiming).
> 4. Review the reward summary carefully — **remember it can't be edited or deleted
>    after this** — then confirm.

Wait for "done".

## Step 7 — Launch reminders

Summarise in a few lines and leave them with the clock + next action:

- ⏰ **90-day rule** — once pre-registration is live in a country, you must launch
  to **production within 90 days** or the campaign is cancelled there. Turn
  pre-registration on **3–6 weeks** before the planned launch, not months ahead.
- 📣 Share the store listing link (with Google's pre-registration badge) on socials
  / landing page — see **`/kit-generate-landing`**.
- 🚀 When ready to launch, finish the full production release with
  **`/kit-upload-on-google-play`**.
- 🎁 If you set a reward: it unlocks automatically via the `<ENTITLEMENT_ID>`
  entitlement — no code change, and the kit re-checks on app foreground so it
  applies the same session the user claims it.

## Wrap up

State plainly what's done (pre-registration on/off, reward configured or skipped,
the entitlement that gates the reward) and the single next action — usually "share
the pre-registration link" or, if the testing gate blocked them, "finish closed
testing first".
