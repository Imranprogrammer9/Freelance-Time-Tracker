---
name: kit-pre-register-setup
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
> - **Yes — give a free 30-day Premium pass** *(recommended for subscription apps)* —
>   a one-time product the kit grants as premium for **30 days from claim**, then it
>   expires and your paywall returns.
> - **Yes — give a cosmetic / content unlock** — a theme, icon pack, or bonus
>   content, not the subscription itself.
> - **No reward** — just collect pre-registrations.

Make these constraints explicit before they commit:

> ⚠️ **Reward rules — read before choosing:**
> - You get **one** reward per app, **for the entire lifetime** of the app's
>   pre-registration. Once created it **cannot be edited or deleted**.
> - Rewards must be **active one-time products** — **subscriptions are not
>   allowed**. So a subscription app gives a *one-time pass*, not "a free month".
> - **Important — how the time limit works.** RevenueCat grants a one-time product's
>   entitlement **for life**, so the 30-day window does **not** come from RevenueCat.
>   Instead you attach **no** entitlement to the reward product, and the kit grants
>   premium for a fixed number of days measured from the **Play purchase date**
>   (`KitConfig.PRE_REGISTER_REWARD_PRODUCT_ID` / `PRE_REGISTER_REWARD_DURATION_DAYS`,
>   wired by this command). If you instead want the reward to be **permanent**, you'd
>   attach the entitlement in RevenueCat and skip the KitConfig wiring — but for a
>   subscription app a timed pass is almost always what you want.
> - If you promise a reward and fail to deliver it, Play can **suspend** the app.

- **No reward** → skip to Step 5.
- **Reward** → go to Step 4. For a subscription app, the **30-day Premium pass** is
  the right fit — the kit already has the code to time-box it.

## Step 4 — Create the reward product + wire the timed pass

Three dashboards/files: create a one-time product in **Play**, import it into
**RevenueCat** *without* an entitlement, then set its product ID in **`KitConfig`**.
The kit's `PurchaseManager` already grants premium for
`PRE_REGISTER_REWARD_DURATION_DAYS` days from the Play purchase date — so you do
**not** attach a RevenueCat entitlement (that would make it permanent).

**Pace this — ONE sub-step at a time. Present 4.1, wait for "done", then 4.2, wait,
then 4.3, wait, then do 4.4 + 4.5. Do NOT dump multiple blocks at once** — the Play
product form is a two-page wizard with confusingly-named fields, and 4.3 is a
different dashboard (RevenueCat). They WILL lose their place if rushed.

The Play "Create one-time product" form has **two pages** (Product details →
Availability and pricing) and **two different IDs** — flag that explicitly so they
don't confuse them.

**4.1 — Play Console, page 1 (Product details)** (present verbatim, then wait):

> **Create the reward product — page 1 of 2:**
> 1. Play Console → your app → **Monetize with Play → Products → One-time products →
>    Create product**.
> 2. **Product ID** — the product's permanent identifier, e.g. `premium_pass_30d`.
>    You **can't change it later**. (This is the ID RevenueCat imports + the kit checks.)
> 3. **Name** — what the user sees, e.g. "30 days of Premium, free".
> 4. **Description** — one line about the perk.
> 5. Click **Next** to go to *Availability and pricing*.

Wait for "done".

**4.2 — Play Console, page 2 (Availability and pricing)** (present verbatim, then
wait):

> **Set pricing + tax — page 2 of 2:**
> 1. Under **Purchase option**:
>    - **Purchase option ID** — a **second, separate** ID (not the Product ID above).
>      Rules: start with a lowercase letter; lowercase letters, numbers, hyphens
>      only; keep it short (~20 chars), e.g. `pass-30d`.
>    - **Purchase type** — leave as **Buy** (a one-time purchase).
>    - **Tags** — optional, leave blank.
> 2. **Price** — set any price (it must have one to activate; users get it free as
>    the reward, they never pay).
> 3. Under **Tax, compliance, and programs** → **Product tax category** — choose
>    **Digital app sales**. This sets the correct sales tax / VAT and avoids tax
>    penalties. Leave **Age rating** and **Payment location restriction** at their
>    defaults.
> 4. **Save**, then **activate** the product — confirm its status shows **Active**
>    (it must be Active to be selectable as a reward).

Wait for "done".

**4.3 — Import into RevenueCat — NO entitlement** (present verbatim, then wait):

> **Import the product into RevenueCat (do NOT attach an entitlement):**
> 1. RevenueCat dashboard → your project → **Products → Import** (on your Play Store
>    app row) → select the Play one-time product `premium_pass_30d`.
> 2. RevenueCat asks for a **type** — choose **Non-consumable** (granted **once per
>    customer**; *Consumable* is for things bought repeatedly, like coins — not this).
> 3. **Do NOT attach any entitlement to this product.** RevenueCat would grant it
>    **for life**, which would make the reward permanent. Leave its **Entitlements
>    empty** — the kit applies the 30-day limit itself. (If you previously attached
>    one, open the product and **detach** it.)
> 4. Done when the product shows **Published** with **0 entitlements**.

Wait for "done".

**4.4 — Wire the product ID into the app (you do this for them).** Edit
`KitConfig.kt` (`core/config/`):
- Set `PRE_REGISTER_REWARD_PRODUCT_ID` to the **Product ID** from 4.1 (e.g.
  `"premium_pass_30d"`).
- Set `PRE_REGISTER_REWARD_DURATION_DAYS` to the window (default `30`).

Then run **`/kit-compile-app`** to confirm it builds.

**4.5 — Confirm the unlock works (no further code).** Reassure the developer:

> ✅ Wired. When a user claims the reward in the Play Store and opens your app, the
> kit reads the **Play purchase date** and grants Premium for the next
> **`PRE_REGISTER_REWARD_DURATION_DAYS` days**, then the paywall returns. It re-checks
> every time the app comes to the foreground, so it unlocks the same session — and
> because it uses the store purchase date (not the device), reinstalling won't reset
> or extend it.

(That foreground re-check is the `ProcessLifecycleOwner` observer in
`KitApplication`. If `PAYWALL_ENABLED` is somehow off, turn it on — otherwise the
reward won't gate anything.)

## Step 5 — Turn on pre-registration in Play Console

Manual web step. The Pre-registration page is **tabbed** (not a single form) and
shows **Track summary: Inactive** with a **Start pre-registration** button (top
right) until you activate it. Present verbatim and wait:

> **Turn on pre-registration:**
> 1. Play Console → your app → **Test and release → Pre-registration** (left nav,
>    under **Testing**). Track summary will say **Inactive**.
> 2. **Supported devices** tab — this is filled in automatically from a build on a
>    testing track. If it's **empty** (or Play shows *"We recommend testing your app
>    before starting pre-registration → Go to Closed testing"*), that's the
>    **closed-testing requirement**, not just a build upload:
>    - **Exempt account** (org, or personal account created *before* 13 Nov 2023) —
>      a build on **any** track (even your billing-permission placeholder AAB) is
>      enough; pick it / **Go to Closed testing** to upload one, then come back.
>    - **New personal account** (created on/after 13 Nov 2023) — you must complete
>      **closed testing: 12 testers opted in for 14 continuous days** before Play
>      lets you start pre-registration. Test your **real app**, not a placeholder: an
>      empty / billing-only shell can be **rejected** in review (minimum-functionality
>      policy) and makes a meaningless test. Build your app's core first
>      (**`/kit-design-app`**) — the kit already ships a complete, functional app, so
>      it passes review — then **`/kit-sign-release`** to put a signed build on the
>      **closed** track and recruit 12 testers (see the launch roadmap). Until that's
>      done, **Start pre-registration** stays blocked. Stop here and come back after.
> 3. **Countries / regions** tab — choose where users can pre-register.
> 4. If you're giving a reward, set it on the **Rewards** tab next (Step 6) **before**
>    you start — then come back here.
> 5. Make sure your **store listing** (descriptions, screenshots, privacy policy) is
>    complete — it's what pre-registering users see. Play delivers the app via
>    **auto-install** to eligible opted-in users on launch day; no separate toggle.
> 6. Click **Start pre-registration** (top right) to go live.

Wait for "done".

## Step 6 — Configure the reward (Rewards tab — reward path only)

Do this on the **Rewards** tab of the same Pre-registration page, **before** you
click *Start pre-registration*.

**6.1 — Make sure you have a public Terms & Conditions URL first.** The reward form
has a **required** "Terms and Conditions URLs" field (red-starred) — Play won't save
the reward without a real, reachable URL. Most developers don't have one yet, so
sort it before opening the form:
- If the developer already ran **`/kit-generate-landing`**, their legal pages are
  hosted — use the **terms** URL (e.g. `https://<their-pages-site>/terms.html`).
- If not, run **`/kit-generate-legal`** (writes privacy + terms from the codebase),
  then **`/kit-generate-landing`** (hosts them for free on GitHub Pages and prints
  the public URLs). Use the resulting **terms** URL.
- Don't paste a placeholder or a localhost URL — Play requires a live page, and a
  broken terms link for a reward risks app removal.

**6.2 — Create the reward** (present verbatim, then wait):

> **Set up the reward (Rewards tab):**
> 1. On the **Pre-registration** page → **Rewards** tab → **Set up reward**. (The
>    reward reuses the **name + description + translations** of the one-time product
>    you selected — you don't re-enter them.)
> 2. **Product** — select the one-time product you created (`premium_pass_30d`).
> 3. **Reward badge** (optional) — a 512×512 transparent PNG/JPEG (≤1 MB) shown in
>    the Play store next to your reward. Skip if you don't have one.
> 4. **Terms and Conditions URLs** (required) — paste the **terms URL** from 6.1.
> 5. Review the reward summary carefully — **remember it can't be edited or deleted
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
- 🎁 If you set the 30-day pass reward: the kit grants Premium for
  `PRE_REGISTER_REWARD_DURATION_DAYS` days from the Play purchase date and re-checks
  on app foreground, so it applies the same session the user claims it — then expires.

## Wrap up

State plainly what's done (pre-registration on/off, reward configured or skipped, and
if a reward — the product ID + day window wired into `KitConfig`) and the single next
action — usually "share the pre-registration link" or, if the testing gate blocked
them, "finish closed testing first".
