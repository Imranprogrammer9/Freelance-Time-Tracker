---
description: Plan your app's monetization — pick a pricing model, decide what to lock, set price anchors, then wire the premium gates into your app
---

You are running **`/kit-plan-monetization`** for NowKit. Goal: help the developer
decide **how the app makes money** — which model, what's free vs premium, what to
charge — then **wire the premium gates into their real screens** using the kit's
entitlement system. Nothing is hardcoded: the app only ever checks the `premium`
entitlement, so products, prices, trials, and paywall design stay editable in the
RevenueCat dashboard forever.

Audience: first-time mobile developers / vibe coders. Assume **zero** pricing or
subscription knowledge. Talk in plain language — "lock this feature", "charge
monthly", not "SKU", "base plan", "LTV". **You make the edits and give the
recommendations — the developer just decides and approves.**

This command is the **strategy + gating** step. `/kit-setup-paywall` is the
**plumbing** step (RevenueCat key, the `premium` entitlement, products, paywall
design). They are deliberately separate — this one needs the app's real screens to
exist, so run it **after `/kit-design-app`**.

**Docs:** https://kit.shipkaro.dev/docs/pricing-strategy

When a section shows a block quoted with `>`, present that block to the developer
**verbatim** — do not paraphrase. Prose outside those blocks is instructions for
you. **Console steps for RevenueCat / Google Play are transcribed from the real UI
— show them exactly; never improvise a menu path from memory.**

## How the kit's paywall works (so you wire it right)

- The app checks **one thing**: the `premium` entitlement, via
  `PurchaseManager.isPremium: StateFlow<Boolean>` (`KitConfig.ENTITLEMENT_ID`).
  It **never** checks product IDs, prices, or offering names — so the developer
  can change any of those in the dashboard with **no code change**.
- A screen reads premium status with the two-liner (canonical — `HomeScreen`):
  ```kotlin
  val purchases = koinInject<PurchaseManager>()
  val isPremium by purchases.isPremium.collectAsState()
  ```
- The paywall is **RevenueCat's prebuilt Paywall** hosted by `PaywallScreen`; its
  design + prices live in the RC dashboard. You **never build a paywall or
  hardcode products** in the app.
- After a purchase, `PurchaseManager.refresh()` flips `isPremium` → every
  `collectAsState()` recomposes → locked UI unlocks **automatically**. You wire the
  lock; the unlock is reactive and free.
- **Scope guard:** only offer models that gate **individual features** (freemium /
  trial / one-time). Do **not** offer a whole-app "hard paywall" — the kit's
  `PAYWALL_MODE = HARD` is declared but not yet enforced. Leave `PAYWALL_MODE`
  untouched.

## Progress tracking

Before Step 1, call **TaskCreate** with:

- Step 1 — Check paywall is set up
- Step 2 — Understand the app
- Step 3 — Pick a pricing model
- Step 4 — Decide what's premium + price anchors
- Step 5 — Wire the gates into the app
- Step 6 — Create products + entitlement + paywall in RevenueCat
- Step 7 — Verify

Mark each `in_progress` on entry, `completed` when done. `[skipped] ` prefix via
**TaskUpdate** if the developer opts out of a step.

## Step 1 — Check the paywall is set up (re-route if not)

The gates you wire depend on the `premium` entitlement existing and RevenueCat
being wired. Read `app/src/main/java/<base>/core/config/KitConfig.kt` (find `<base>`
from `app/build.gradle.kts` `namespace` — never hardcode it) and `local.properties`:

- `PAYWALL_ENABLED` — must be `true`.
- `ENTITLEMENT_ID` — must be set (default `premium`).
- `revenuecat.android.api.key` in `local.properties` — should start with `goog_`.

Branch (check in this order):

- **`PAYWALL_ENABLED = false` — app is currently free.** Don't assume it stays
  free; monetization is often added at a later stage. AskUserQuestion:

  > Your app is set up as **free** right now. Do you want to add monetization —
  > lock some features behind a paywall and charge for them?

  - **Yes, add monetization** → run **`/kit-setup-paywall`** inline to do the full
    paywall setup (RevenueCat project, Play app, `premium` entitlement, API key,
    flip `PAYWALL_ENABLED = true`), then return here and continue to Step 2.
  - **No, keep it free** → leave `PAYWALL_ENABLED = false` and stop. They can
    re-run this anytime they decide to charge.

- **`PAYWALL_ENABLED = true` but the entitlement or API key is missing** → the
  paywall is switched on but the plumbing is half-done. Tell the developer:

  > Before we plan what's premium, your app needs the paywall wiring finished — the
  > RevenueCat connection and the `premium` switch your locked features check. I'll
  > run **`/kit-setup-paywall`** now to finish it, then come straight back here to
  > plan your pricing.

  Then **run `/kit-setup-paywall` inline** — walk its plumbing (its Steps 0–2.5:
  RevenueCat project, Play app, `premium` entitlement, API key). You do **not** need
  its product/offering/paywall-design work (2.6 / 5A–5D) yet — Step 6 of *this*
  command handles that, tailored to the plan. When the key is set, return and
  continue.

- **All set** (`PAYWALL_ENABLED = true`, entitlement set, key present) → say so in
  one line and continue to Step 2.

## Step 2 — Understand the app (silently)

Read these and form a mental model — do **not** dump their contents at the
developer:

- `app/src/main/java/<base>/core/navigation/Route.kt` — the screen list.
- `feature/` subdirectories — the developer's real screens (ignore kit defaults:
  onboarding, auth, paywall, home, settings, profile, changelog, catalog,
  licenses). What's left is the app's own surface — the gating candidates.
- `app/src/main/res/values/strings.xml` — skim to infer the app's domain.
- Check for a prior run: `docs/monetization.md` (this command writes it) and any
  existing `isPremium` gates in `feature/` — if found, summarise the current plan
  and ask whether to **adjust**, **gate more features**, or **finish** (idempotent
  — never wire the same feature twice).

If the app has **no custom feature screens yet** (only kit defaults), tell the
developer there's nothing real to lock — suggest building the app with
`/kit-design-app` first. You can still discuss a model + prices, but gating waits
for real features.

Confirm your read in one sentence: *"Looks like a <domain> app with these
screens: … — right?"* and let them correct it.

## Step 3 — Pick a pricing model (plain language)

Ask the developer how they picture charging — **plainly**. AskUserQuestion, and
**recommend one** based on the app you just read (don't dump generic options):

- **Freemium (recommended for most)** — the app is free to use, and a few standout
  features cost money. Best when the app is useful on its own.
- **Free trial, then subscribe** — everyone gets the full app free for a while
  (e.g. 7 days), then premium features need a subscription. The trial is a setting
  on the subscription in Google Play — not code.
- **Pay once, unlock forever** — a single purchase unlocks the premium features for
  good. No recurring charge. Best for simple tools.

All three lock **individual features** behind the same `premium` switch — they
differ only in what you sell in the dashboard (a subscription, a subscription with
a trial, or a one-time product). Capture the choice. Do **not** touch
`PAYWALL_MODE`.

## Step 4 — Decide what's premium + price anchors

Two decisions, both yours to guide with a recommendation:

**4a — What to lock.** Show the developer their real feature list (from Step 2) and
AskUserQuestion (multi-select) which features are **premium**. Guide them:

> **Rule of thumb:** keep the "aha moment" free — whatever makes your app
> obviously useful should be free, because that's what earns the upgrade. Charge
> for *more* of it: unlimited use, export/share, advanced or AI features, extra
> content, remove-ads, extra slots.

Recommend a specific split for **their** app (name the actual features), then let
them adjust. Produce a clear list: each premium feature + how it's gated —
**whole-screen** (the screen needs premium) or **in-screen action** (a button /
section inside a free screen).

**4b — Price anchors.** A vibe coder rarely knows what to charge. Recommend
concrete starting numbers, scaled to the app type, then let them adjust. Offer a
**two-option anchor** (this is what the paywall shows side by side and what
converts best):

- **Monthly** — the low entry price.
- **Yearly** — cheaper per month, shown as best value; drives long-term revenue.
  Price it so a year ≈ 6–8 months of monthly.
- (One-time model → a single unlock price instead.)

Reasonable starting points to suggest (say these are starting points, easily
changed later — and **never underprice**; raising later is easy):

| App type | Monthly | Yearly | One-time |
| --- | --- | --- | --- |
| Simple utility / productivity | $2.99–4.99 | $19.99–29.99 | $9.99–29.99 |
| Content / health / learning | $6.99–9.99 | $39.99–59.99 | — |
| Pro / niche tool | $9.99–19.99 | $79.99–149.99 | $49.99+ |

These are **anchors, not code** — there is no price anywhere in the app. They get
typed into Google Play + RevenueCat in Step 6. Confirm the final list of premium
features + the model + the price anchors with the developer before wiring.

## Step 5 — Wire the gates into the app

Now lock each premium feature from Step 4a — **one feature at a time**, using the
kit's existing pattern. **No new paywall, no hardcoded products, no new file in
`core/`.** Reuse `PurchaseManager.isPremium` and route upsells to the existing
`Route.Paywall`.

For each premium feature, Grep to the real screen in `feature/`, then:

1. **Give the screen the premium flag** if it doesn't already read it — add the
   two-liner (`koinInject<PurchaseManager>()` + `isPremium by …collectAsState()`).
2. **Reach the paywall the kit's way.** Screens don't hold the `NavController` —
   they take lambda callbacks (see `HomeScreen(onOpenSettings = …)` wired in
   `KitNavHost`). So add an `onUpgrade: () -> Unit` parameter to the screen and, in
   that screen's `composable<Route.X>` entry in `KitNavHost.kt`, wire
   `onUpgrade = { navController.navigate(Route.Paywall) }`. Do **not** alter the
   Splash/Onboarding/Auth/Paywall **gating** logic — you're only adding a normal
   navigation call.
3. **Gate the UI** on `isPremium`, building the locked state from the **design
   system the app already uses** (`KitButton`, `KitCard`, `KitBanner`,
   `KitTheme.icons.lock`, `KitTheme.spacing/typography`) — match the look of the
   surrounding screens. **Do not invent components or paste a fixed layout** —
   check `core/designsystem/components` and mirror how the app's own screens are
   built.
   - **Whole-screen gate:** if `!isPremium`, show a short locked state (icon +
     one-line reason + a "Unlock premium" button calling `onUpgrade`) and `return`
     before the real content.
   - **In-screen action gate:** show the real control when `isPremium`; otherwise
     show a locked variant (e.g. a "🔒 Unlock" button) that calls `onUpgrade`.
4. **Localize every string** — lock titles, messages, CTA labels go in
   `res/values/strings.xml`, feature-prefixed (e.g. `export_locked_title`). Never
   hardcode literals in `Text` / `contentDescription`. English ships now;
   `/kit-translate` adds locales.

After each feature, confirm in one line what you locked and how. Because unlock is
reactive (`refresh()` flips `isPremium`), you never write unlock code — a purchase
opens every gate automatically.

Note the return behaviour once: after the paywall, the kit currently lands the user
on **Home** (the paywall's built-in finish). For a feature upsell they'll come back
to a premium-unlocked app, just via Home — acceptable for now; don't re-architect
navigation to change it.

## Step 6 — Create products + entitlement + paywall in RevenueCat

The gates are wired but sell nothing until the products exist in the dashboard and
are attached to `premium`. Walk the developer through it now, **tailored to the
plan** — inject the exact model + price anchors from Step 4. **Paced: show one
sub-step, then STOP and wait for "done"** before the next. These console steps are
transcribed from the real RevenueCat / Google Play UI — present verbatim.

First, tell them the shape of what's coming:

> Now we make your plan real in the dashboards. Three things: **create the
> products** you priced, **attach them to the `premium` switch** your app checks,
> and **put them on your paywall**. I'll walk each one — say "done" as you go.

**6a — Create the products in Google Play.** For a **subscription** (freemium /
trial models) show this — substitute the product IDs + prices from the plan:

> **Google Play Console → your app → Monetize → Products → Subscriptions → Create
> subscription:**
> - **Product ID** — e.g. `premium_monthly` (lowercase, permanent, can't be reused).
> - **Name** — internal only; users never see it.
> - Add a **base plan** → **Auto-renewing** → **Billing period** Monthly →
>   **Price** `<your monthly anchor>`.
> - **Activate BOTH the base plan AND the subscription** (an inactive one silently
>   won't load).
> - Repeat for the yearly plan (`premium_yearly`, Yearly, `<yearly anchor>`).

For a **pay-once** model, show the one-time-product variant instead (Play → Products
→ One-time products → an ID + price → Activate). **STOP and wait for "done".**

> **Note:** Google Play blocks product creation until a build that declares the
> billing permission is live on a testing track. If Play won't let you create
> these yet, that's why — run `/kit-sign-release` to get a build on a track first,
> then come back. (The kit already declares the permission.)

**6b — Import to RevenueCat + attach the entitlement.** Show verbatim, then STOP:

> **RevenueCat → Product catalog → Products → + New:** add each Google Play product
> ID you just created (pick your Google Play app as the store).
>
> **RevenueCat → Product catalog → Entitlements → `premium` → Attach products:**
> add every product you just imported. **This is the make-or-break step** — it's
> the switch your app checks. Skip it and purchases go through but **nothing
> unlocks**.

**6c — Put the products on the paywall.** Show verbatim, then STOP:

> **RevenueCat → Offerings → your `default` offering** (make it **current**):
> **+ Add package** for each tier (Monthly, Annual) and attach the matching
> product.
>
> **RevenueCat → Paywalls → your paywall:** make sure it uses the `default`
> offering, then **Save + Publish** (an unpublished paywall won't appear). Prices
> come from Google Play automatically — you don't type them here.

**6d — Offer a paywall review (optional).** After they publish, offer this:

> Want me to review your paywall? Take a **screenshot of your paywall in the
> RevenueCat dashboard** (or on your device) and share it here — I'll give you
> concrete tips to improve conversions.

If they share a screenshot, review it against conversion best practices and give
**specific, actionable** feedback (not generic): is the **yearly plan anchored as
best value** next to monthly, is any **free-trial framing** clear, are the **value
props benefit-led** (not feature-led), is the **primary CTA** the highest-contrast
element, are **prices legible**, is there a visible **restore-purchases** path.
Suggest changes they apply in the RevenueCat paywall editor. Don't touch code — the
paywall is dashboard-owned.

## Step 7 — Record the plan + verify

**Write `docs/monetization.md`** (create `docs/` if missing) so a re-run detects the
plan and `/kit-generate-aso` + `/kit-plan-release-analytics` can reuse it. Keep it
short:

```markdown
# Monetization plan

- **Model:** <freemium / trial / one-time>
- **Price anchors:** Monthly <$X> · Yearly <$Y> · One-time <$Z or —>
- **Premium features:**
  - <Feature> — <whole-screen / in-screen> gate
- **Free features:** <list>

Products + prices + paywall design live in RevenueCat + Google Play. This file is
the plan, not the source of prices.
```

**Verify the build:** run `./gradlew :app:compileDebugKotlin` (skip if you were
invoked as part of a larger orchestrator that builds at the end). Fix any gate
that broke the compile.

**Report:** the model, the premium features + how each is gated, the files touched,
and the price anchors. Then close:

> **Monetization planned and wired.** Your premium features are locked to the
> `premium` switch, and a purchase unlocks them automatically. Before real users
> can buy: finish any pending Play billing setup in **`/kit-setup-paywall`** (a
> signed build on a testing track + the Play↔RevenueCat connection, which takes
> ~36h to propagate). Want to measure the paywall funnel at launch?
> `/kit-plan-release-analytics`.

## Notes

- **Entitlement-only, always.** Never write a product ID, price, or offering name
  into app code. The only per-product check the kit makes is the pre-registration
  reward (a deliberate exception) — don't add more.
- **Never touch** the Splash / Onboarding / Auth / Paywall **gating** in
  `KitNavHost` — you only add normal `navigate(Route.Paywall)` calls for upsells.
- **Idempotent.** Re-running detects `docs/monetization.md` + existing gates and
  offers to adjust rather than duplicating. Never gate the same feature twice.
- **No prices in code.** If asked to "set the price", explain prices live in Google
  Play (RevenueCat reads them) and belong in Step 6 / `/kit-setup-paywall`.
