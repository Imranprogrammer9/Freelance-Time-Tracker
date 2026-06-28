---
description: Publish your app to Google Play the right way — signed build, app setup, closed testing (the 12-tester / 14-day gate), and production — in the correct order, no maze
---

You are running **`/kit-publish-to-play`** for NowKit.

Goal: take the developer's app from "built on my machine" to **live on Google Play**,
walking the Play Console **in the correct order** so they never hit the dependency maze
(privacy URL needs a hosted landing page, which needs screenshots; closed testing gates
production; etc.).

Audience: first-time mobile developers / vibe coders. Most of this is manual Play Console
work that **can't** be automated — your job is to do the **local/code work for them**
(builds, asset generation, surveys, the Data safety CSV) and then walk each web step,
**one at a time**.

## How to run this (read before starting)

- **Pace it — ONE sub-step at a time.** Present a step, then **wait for the developer to
  say "done"** before the next. **Never dump multiple Play Console screens at once** —
  they will lose their place. This is the #1 rule.
- When a block is quoted with `>`, show it to the developer **verbatim**. Prose outside
  `>` blocks is instructions for *you*.
- This is a **multi-day** flow — Phase 6 (closed testing) alone is **14 days**. Save
  progress by reading the project each run (see Resume check) and pick up where they left.
- **Docs:** https://kit.shipkaro.dev/docs/release

## Resume check — do this FIRST

Figure out where they are before greeting, so you resume instead of restarting:
- **Signed AAB?** `ls app/build/outputs/bundle/release/app-release.aab`; `release.*` keys in
  `local.properties`.
- **Legal generated?** `ls playstore/privacy_policy.md playstore/play_data_safety.md`.
- **Listing copy?** `ls playstore/title.txt playstore/full_description.txt`.
- **Screenshots?** `ls playstore/screenshots/`.
- Ask the developer (you can't detect Play Console state from code): *"Have you already
  created the app on Play Console / uploaded a build to a testing track / started closed
  testing?"* Resume at the right phase, mark earlier ones done.

## Survey the project (do this once, up front)

Read these so your guidance is accurate — never assume:
- **Base package + applicationId** → `app/build.gradle.kts` (`namespace`, `applicationId`).
- **Auth** → `KitConfig.kt` (`core/config/`): `AUTH_PROVIDER` (`SUPABASE`/`FIREBASE`/`STUB`=off),
  `GOOGLE_WEB_CLIENT_ID` (set = Google sign-in on).
- **Paywall** → `KitConfig.PAYWALL_ENABLED`, `ENTITLEMENT_ID`.
- **Ad / financial / health SDKs** → grep `app/build.gradle.kts` + `gradle/libs.versions.toml`
  for `play-services-ads`/AdMob/AppLovin, banking/crypto/lending/payment SDKs,
  `androidx.health`/Google Fit. (Kit ships none of these by default.)

---

## Phase 0 — Play account ready

Ask the developer (wait for their answer): *"Do you have a verified Google Play Console account?"*
- **Yes** → continue.
- **No** → tell them to create one at https://play.google.com/console (one-time **$25**
  fee) and complete identity verification, then resume. Stop here.

## Phase 1 — Signed AAB

If a signed AAB already exists (Resume check), skip. Otherwise call **`/kit-sign-release`**
— it creates the release **keystore** (first time; remind them to back it up —
losing it means they can never update the app) and builds the **signed** `app-release.aab`.

> ✅ You now have a signed build at `app/build/outputs/bundle/release/app-release.aab`.

## Phase 2 — Create the app on Play Console

Ask the developer (wait for their answer): *"Have you already created this app on Play Console — for example
while connecting RevenueCat to Google Play?"*
- **Yes** → skip to Phase 3.
- **No** → first read `applicationId` from `app/build.gradle.kts`, substitute it below,
  present verbatim, and wait:

> **Create your app in Play Console:**
> 1. https://play.google.com/console → **Create app**.
> 2. **App name** — your public name (≤ 30 chars, changeable later).
> 3. **Default language** — your main locale.
> 4. **App or game** → **App**.
> 5. **Free or paid** — pick **Free** if you sell subscriptions / in-app purchases (the
>    download is free; you charge via in-app billing). **Paid** only if users pay to
>    install — you **can't switch Free→Paid after publishing**.
> 6. **Declarations** — tick the Developer Program Policies + US export laws.
> 7. **Create app**.

Wait for "done".

## Phase 3 — Internal testing (get the build on a track)

> **Why now:** internal testing is **instant, no review**, registers your package on Play,
> and (for paid apps) **unblocks the RevenueCat ↔ Play connection** (~36h to propagate).

Skip if they already have a build on any track (e.g. from RevenueCat setup). Otherwise,
**one sub-step at a time**:

**3.1 — Select testers** (present, wait):
> Play Console → your app → **Test and release → Testing → Internal testing → Testers**
> tab. Tick a tester list, or **Create email list** and add emails. **Save**. (Up to 100;
> the join link appears after you publish.)

**3.2 — Create the release** (present, wait):
> **Create new release** (top-right). App signing by Google is on automatically. Under
> **App bundles**, **Upload** `app/build/outputs/bundle/release/app-release.aab`. Set a
> **Release name** (auto-filled is fine) + optional notes. **Next**.

**3.3 — Roll out** (present, wait):
> Review the preview (a "no debug symbols" warning is fine to ignore). **Save → Start
> rollout to Internal testing**. The track goes **Active** with your release live.

## Phase 4 — Legal + landing + host (so the listing's URLs exist)

The "Set up your app" checklist (Phase 5) needs a **public privacy URL** and the Data
safety answers. Generate + host them now so they're ready.

**4.1 — Screenshots.** Call **`/kit-generate-screenshots`** (the landing hero +
the store listing both need them) → `playstore/screenshots/`.

**4.2 — Legal content.** Call **`/kit-generate-legal`** → it writes
`playstore/privacy_policy.md`, terms, and `playstore/play_data_safety.md` (the Data
safety answers).

**4.3 — Generate the Data safety CSV** (for one-click import in 5.6). From the project
survey + `playstore/play_data_safety.md`, write **`playstore/play_data_safety.csv`**. Use
**`data_safety_sample_reference.csv`** (repo root) as the template if present; if it's not
there, get the exact template by having the developer open Play Console → Data safety →
**Export to CSV** (an empty export gives the current column + row format). Either way,
keep the 5 columns
(`Question ID, Response ID, Response value, Answer requirement, Human-friendly question label`)
and set **`true`** in the **Response value** column on exactly the rows the app's data
collection covers (e.g. collects data = Yes; encrypted in transit = Yes; account-creation
method per auth survey; the data types the SDKs collect — email/name/avatar from auth,
purchase history from RevenueCat, approximate location + device IDs from PostHog,
crash logs from Crashlytics/Sentry; plus the per-type usage/purpose rows). Leave the rest
blank. Also fill `PSL_ACCOUNT_DELETION_URL` with the hosted privacy URL once known.

**4.4 — Landing page (hosts privacy + terms → public URLs).** Call **`/kit-generate-landing`**.
It builds the static site (hero + screenshots + features + **privacy** + **terms**
+ contact) and hosts it (GitHub Pages recommended). Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 5.1
- Terms: `https://<site>/terms.html` → used by a pre-registration reward later

**Continue in `kit-publish-to-play-part-2.md`** for Phases 5–7.
