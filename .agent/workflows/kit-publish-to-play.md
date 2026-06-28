---
description: Publish your app to Google Play the right way — first release (signed build, app setup, closed testing's 12-tester / 14-day gate, production) or a fast update — in the correct order, no maze
---

You are running **`/kit-publish-to-play`** for NowKit.

Goal: take the developer's app from "built on my machine" to **live on Google Play**,
walking the Play Console **in the correct order** so they never hit the dependency maze
(privacy URL needs a hosted landing page, which needs screenshots; closed testing gates
production; etc.). Handles both a **first release** and a **fast update**.

Audience: first-time mobile developers / vibe coders. Most of this is manual Play Console
work that **can't** be automated — your job is to do the **local/code work for them**
(builds, version bumps, asset generation, surveys, the Data safety CSV) and then walk each
web step, **one at a time**.

## How to run this (read before starting)

- **Pace it — ONE sub-step at a time.** Present a step, then **wait for the developer to
  say "done"** before the next. **Never dump multiple Play Console screens at once** —
  they will lose their place. This is the #1 rule.
- When a block is quoted with `>`, show it to the developer **verbatim**. Prose outside
  `>` blocks is instructions for *you*.
- A first release is **multi-day** — Phase 6 (closed testing) alone is **14 days**. Save
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
  created the app on Play Console / uploaded a build to a track / started closed testing?"*
  Resume at the right phase, mark earlier ones done.

## Survey the project (do this once, up front)

Read these so your guidance is accurate — never assume:
- **Base package + applicationId** → `app/build.gradle.kts` (`namespace`, `applicationId`).
- **Current version** → `app/build.gradle.kts` (`versionCode`, `versionName`).
- **Auth** → `KitConfig.kt` (`core/config/`): `AUTH_PROVIDER` (`SUPABASE`/`FIREBASE`/`STUB`=off),
  `GOOGLE_WEB_CLIENT_ID` (set = Google sign-in on).
- **Paywall** → `KitConfig.PAYWALL_ENABLED`, `ENTITLEMENT_ID`.
- **Remote config** → whether `RemoteAppConfig` is wired to a backend (Supabase/Firebase) vs LOCAL.
- **Ad / financial / health SDKs** → grep `app/build.gradle.kts` + `gradle/libs.versions.toml`
  for `play-services-ads`/AdMob/AppLovin, banking/crypto/lending/payment SDKs,
  `androidx.health`/Google Fit. (Kit ships none of these by default.)

---

## Step 1 — First release or update?

Ask the developer (wait for their answer): *"Is this your app's first release, or an update
to an app that's already on Google Play?"*
- **First release (never published)** → do the **First-release path** below (Phases 0–7).
- **Update (already on Play)** → jump to the **Update path** (in part 2, much shorter).

---

# First-release path

## Phase 0 — Play account ready

Ask the developer (wait for their answer): *"Do you have a verified Google Play Console
account?"*
- **Yes** → continue.
- **No** → tell them to create one at https://play.google.com/console (one-time **$25**
  fee) and complete identity verification, then resume. Stop here.

## Phase 1 — Set the version, then build the signed AAB

**1.1 — Version.** Read `versionCode` / `versionName` in `app/build.gradle.kts`.
- **First upload ever:** `versionCode = 1` is fine — leave it.
- (Every later upload must have a **higher** `versionCode` — the Update path handles that.)

**1.2 — Signed AAB.** If a signed AAB already exists (Resume check), skip. Otherwise call
**`/kit-sign-release`** — it creates the release **keystore** (first time; remind
them to back it up — losing it means they can never update the app) and builds the
**signed** `app-release.aab`.

> ✅ Signed build at `app/build/outputs/bundle/release/app-release.aab`.

## Phase 2 — Create the app on Play Console

Ask the developer (wait for their answer): *"Have you already created this app on Play
Console — for example while connecting RevenueCat to Google Play?"*
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

## Phase 4 — Listing assets + legal + host (so the listing's URLs exist)

The "Set up your app" checklist (Phase 5) needs a **public privacy URL** and Data safety
answers. Generate + host them now so they're ready.

**4.1 — Screenshots.** Call **`/kit-generate-screenshots`** (the landing hero + the
store listing both need them) → `playstore/screenshots/`.

**4.2 — Listing copy.** Call **`/kit-generate-aso`** → `playstore/title.txt`,
`short_description.txt`, `full_description.txt` (used in 5.11).

**4.3 — Legal content.** Call **`/kit-generate-legal`** → `playstore/privacy_policy.md`
+ `.html`, terms, and `playstore/play_data_safety.md` (the Data safety answers, used in 5.6).

**4.4 — Landing page (hosts privacy + terms → public URLs).** Call **`/kit-generate-landing`**.
It builds + hosts the static site and produces the styled **`privacy.html`**,
**`terms.html`**, and an unlisted **`data-safety.html`**. Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 5.1
- Terms: `https://<site>/terms.html` → used by a pre-registration reward later
- Data safety (unlisted): `https://<site>/data-safety.html` → handy reference for 5.6

**4.5 — Plan release analytics.** Call **`/kit-plan-release-analytics`** (don't ask
permission — a funnel is load-bearing for "did this launch work?"). It wires 3–5 release
events into the code.

**Continue in `kit-publish-to-play-part-2.md`** for Phases 5–7 and the Update path.
