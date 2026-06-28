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

## Progress tracking

Call **TaskCreate** with the tasks for the path the developer picks (Step 1), mark each
`in_progress` on entry, `completed` when done (prefix `[skipped] ` via **TaskUpdate** if
skipped).

---

## Step 1 — First release or update?

Ask (AskUserQuestion): *"Is this your app's first release, or an update to an app that's
already on Google Play?"*
- **First release (never published)** → do the **First-release path** below (Phases 0–7).
- **Update (already on Play)** → jump to the **Update path** at the end (much shorter).

---

# First-release path

## Phase 0 — Play account ready + account type (decides the whole path)

**0.1 — Account exists?** Ask (AskUserQuestion): *"Do you have a verified Google Play
Console account?"*
- **Yes** → continue to 0.2.
- **No** → tell them to create one at https://play.google.com/console (one-time **$25**
  fee) and complete identity verification, then resume. Stop here.

**0.2 — Account type (ask this NOW — it decides whether the 14-day closed-testing gate
applies).** Ask (AskUserQuestion): *"What kind of Play Console account is this?"*
- **Organisation account** → **exempt** from the closed-testing requirement.
- **Personal account created BEFORE 13 Nov 2023** → **exempt**.
- **Personal account created ON/AFTER 13 Nov 2023** → **not exempt** — must run closed
  testing (12 testers / 14 continuous days) before applying for production.

**Branch on the answer — set this for the rest of the run:**
- **Exempt** → there is **NO closed-testing gate**. The path is shorter:
  Phase 1 → 2 → 3 (optional) → 4 → 5 → **straight to Phase 7 (production)**. **Skip Phase 6
  entirely** — in Progress tracking mark task `6` `[skipped]` (don't present the 14-day
  flow, it'll just confuse them). Tell the developer plainly: *"Your account is exempt, so
  you can publish to production right after the 'Set up your app' checklist — no 14-day
  test needed."*
- **Not exempt** → the **Phase 6 closed-testing gate applies** (the 12-tester / 14-day
  long pole). Keep task `6`.

Carry this exempt/not-exempt decision through the whole run.

## Phase 1 — Set the version, then build the signed AAB

**1.1 — Version.** Read `versionCode` / `versionName` in `app/build.gradle.kts`.
- **First upload ever:** `versionCode = 1` is fine — leave it.
- (Every later upload must have a **higher** `versionCode` — the Update path handles that.)

**1.2 — Signed AAB.** If a signed AAB already exists (Resume check), skip. Otherwise run
**`/kit-sign-release`** inline — it creates the release **keystore** (first time; remind
them to back it up — losing it means they can never update the app) and builds the
**signed** `app-release.aab`.

> ✅ Signed build at `app/build/outputs/bundle/release/app-release.aab`.

## Phase 2 — Create the app on Play Console

Ask (AskUserQuestion): *"Have you already created this app on Play Console — for example
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

## Phase 4 — Listing assets + legal + host (so the listing's URLs exist)

The "Set up your app" checklist (Phase 5) needs a **public privacy URL** and Data safety
answers. Generate + host them now so they're ready.

**4.1 — Screenshots (REQUIRED — do not defer).** Run **`/kit-generate-screenshots`** inline
→ `playstore/screenshots/`. These are **not optional**: the store listing (5.11) won't save
without **≥ 2 phone screenshots**, and the listing is part of the "Set up your app"
checklist that **gates the closed-testing and production tracks** — so you can't start
closed testing without them. If the developer doesn't want generated ones, they must drop
their own PNGs into `playstore/screenshots/` now. Don't skip past this step.

**4.2 — Listing copy.** Run **`/kit-generate-aso`** inline → `playstore/title.txt`,
`short_description.txt`, `full_description.txt` (used in 5.11).

**4.3 — Legal content.** Run **`/kit-generate-legal`** inline → `playstore/privacy_policy.md`
+ `.html`, terms, and `playstore/play_data_safety.md` (the Data safety answers, used in 5.6).

**4.4 — Landing page (hosts privacy + terms → public URLs).** Run **`/kit-generate-landing`**
inline. It builds + hosts the static site and produces the styled **`privacy.html`**,
**`terms.html`**, and an unlisted **`data-safety.html`**. Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 5.1
- Terms: `https://<site>/terms.html` → used by a pre-registration reward later
- Data safety (unlisted): `https://<site>/data-safety.html` → handy reference for 5.6

**4.5 — Plan release analytics.** Run **`/kit-plan-release-analytics`** inline (don't ask
permission — a funnel is load-bearing for "did this launch work?"). It wires 3–5 release
events into the code.

## Phase 5 — Set up your app (the 11-task checklist)

> **Always work from the Dashboard checklist.** Go to **Dashboard → "Set up your app" →
> View tasks** — it shows a **"X of 11 complete"** progress bar. **The loop for every
> task: open it from this checklist → fill it → Save → return to the checklist** (✓, the
> counter ticks up). Walk top to bottom, **one task at a time, waiting for "done"** after
> each. 11 tasks.

**5.1 — Set privacy policy:** paste the hosted **privacy** URL (4.4) into the *Privacy
policy URL* field → **Save**.

**5.2 — Sign in details:** (auth survey result drives this)
> *"Is any part of your app restricted?"* — **Yes** if the app has auth (any provider) or
> a paywall; **No** only if neither.
- On Yes → **+ Add details** → **Name** (`Reviewer test account`) + account:
  - **Email login available** → create a **test email + password** user; enter it.
  - **Google sign-in only** → provide a **real Google account you own with 2-Step
    Verification + OTP turned OFF** in its Security settings (reviewers can't pass 2FA),
    or document a guest/demo mode in "Any other information".
  - **No auth, paywall only** → no login; explain premium access in "Any other information".
  - ☑ **full-access checkbox** only if that account actually unlocks Premium (grant it the
    entitlement first — reviewers can't purchase). → **Save**.

**5.3 — Ads:** ad-SDK survey → none → **No**; present → **Yes** (adds "Contains ads" label). Save.

**5.4 — Content rating:** **Start questionnaire** (IARC) → Step 1 email + **Category** (suggest
**All Other App Types** unless game/social) + agree ToS → Step 2: **for a typical
productivity/utility app answer "No" to every content question** (the form only grows if
you say Yes) → Step 3 Summary → **Submit**.

**5.5 — Target audience:** **Target age** → tick **13+ groups** (`13-15`, `16-17`,
`18 and over`); **do not** tick under-13 unless it's truly a kids' app (triggers a heavy
Families burden). No under-13 → steps 2–4 auto-skip → **Summary → Save**.

**5.6 — Data safety (one-click via CSV):**
- **Template:** use **`data_safety_sample_reference.csv`** (repo root — Google's Data safety
  import format, all ~780 rows) as the template if it's present. If it isn't (e.g. an older
  buyer who pulled via `/kit-update`), have the developer open the **Data safety** page →
  **Export to CSV** (top-right) — an empty export gives the exact current template instead.
- **Fill it:** keep the 5 columns, and set **`true`** in the **Response value** column on
  exactly the rows the app covers (from the survey + `playstore/play_data_safety.md`):
  collects data = Yes; encrypted in transit = Yes; account-creation method per auth survey;
  the data types the SDKs collect (email / name / avatar from auth, purchase history from
  RevenueCat, approximate location + device IDs from PostHog, crash logs from
  Crashlytics/Sentry) + their per-type usage/purpose rows; and `PSL_ACCOUNT_DELETION_URL` =
  the hosted privacy URL. Leave everything else blank. Write the filled file to
  `playstore/play_data_safety.csv`.
> On the **Data safety** page → **Import from CSV** → upload `playstore/play_data_safety.csv`
> → the whole 5-stage form fills → review the **Preview** → **Submit**.
- *(Fallback if import fails: walk the wizard by hand from `play_data_safety.md` / the
  hosted `data-safety.html`.)*

**5.7 — Government apps:** indie/company app → **No** → Save.

**5.8 — Financial features:** financial-SDK survey → none → tick **"My app doesn't provide
any financial features"** → Save. *(A subscription / IAP is NOT a financial feature.)*

**5.9 — Health:** health-SDK survey → none → tick **"My app does not have any health
features"** → Save.

**5.10 — Store settings (category + contact):** **App category** → App + a best-fit
**Category** (suggest from the app's purpose, e.g. Productivity, Health & Fitness, Tools)
+ optional Tags. **Contact details** → support **email** (required), phone (optional),
**website** = your landing URL. Leave **External marketing** on. Save each.

**5.11 — Set up your store listing:**
> **Create default store listing** → **App name** (≤30) from `playstore/title.txt`, **Short
> description** (≤80) from `short_description.txt`, **Full description** (≤4000) from
> `full_description.txt`. **Graphics:** App icon (512×512), Feature graphic (1024×500),
> **Phone screenshots** (≥2) from `playstore/screenshots/`. **Save**.

When this is saved the checklist shows **11/11** and the public tracks unlock.

## Phase 6 — Closed testing (the 12-tester / 14-day gate)

**Only for NOT-exempt accounts** (decided back in Phase 0.2). **If the account is exempt,
you already skipped this — go straight to Phase 7.** Do not run the 14-day flow for an
exempt account.

For a not-exempt account, closed testing is **required** before production — a **14-day**
process. Two prerequisites:
- **"Set up your app" must be 11/11 (Phase 5)** — including the **store listing with
  screenshots**. Closed testing won't start until the checklist is complete (the Closed
  testing card literally says *"finish setting up your app"* first).
- The app you closed-test must be your **real app** (build your core features first with
  `/kit-design-app`) — an empty shell can be rejected in review.

> ⏰ This is the long pole — start it as soon as Phase 5 is done and keep polishing meanwhile.

**6.1 — Set up the closed test track** (Dashboard → Closed testing → tasks): **Select
countries and regions**, then **Select testers** — add a list with **≥ 12 testers**. Each
tester must **open the opt-in link and accept** to count. (Cohort tip: shared tester pool.)

**6.2 — Create + roll out the release:** **Create new release** → upload the AAB (or
**promote** your internal build) → name + notes → **Preview** → **Send the release to
Google for review** (closed testing IS reviewed).

**6.3 — The 14-day clock** (Dashboard → Production → *Apply for access to production*):
three criteria with live progress —
> 1. **Publish a closed testing release** ✓
> 2. **Have at least 12 testers opted-in** (*"N testers currently opted-in"* — need ≥ 12)
> 3. **Run your closed test with ≥ 12 testers for ≥ 14 days** (*"12 testers have been
>    opted in for X days continuously"*)
- It must be **12+ opted-in CONTINUOUSLY for 14 days** — if it drops below 12, continuity
  can reset. The **Apply for production** button stays greyed until day 14, then turns blue.

**6.4 — Apply for production:** click **Apply for production** → answer the questions about
your closed test → **submit**. Status: *"reviewing your application… usually 7 days or
less."* When granted: *"Congratulations! Your app has been granted production access."*

## Phase 7 — Production release (go live)

Run **`/kit-generate-changelog`** inline for the "What's new" notes, then present verbatim:

> **Publish to production:**
> 1. **Production → Create new release**.
> 2. Upload the signed AAB (or **promote** your closed-testing build).
> 3. Paste the **What's new** notes from `playstore/changelogs/<versionCode>.txt`.
> 4. **Preview → Review release → Start rollout to Production** (full, or a staged % you
>    ramp up).

After Google's review clears, the app is **live on Google Play**. 🎉

---

# Update path (app already on Google Play)

Short flow — no closed-testing gate, no "Set up your app" (already done). **One step at a
time, waiting for "done".**

**U1 — Bump the version (do this BEFORE building).** Every build uploaded to Play needs a
**unique, higher `versionCode`** — Play rejects a duplicate (the #1 upload snag). In
`app/build.gradle.kts`: **increment `versionCode` by 1**, and bump `versionName` to the
next release string (ask the developer, e.g. `0.1.0 → 0.1.1`).

**U2 — Release notes.** Run **`/kit-generate-changelog`** inline → writes the user-facing
"What's new" to `playstore/changelogs/<versionCode>.txt` from git history.

**U3 — Screenshots (only if the UI changed).** If this update changed screens, run
**`/kit-generate-screenshots`** inline to refresh `playstore/screenshots/`. Otherwise skip.

**U4 — Remote config (if wired).** If `RemoteAppConfig` is on a backend (not LOCAL), remind
the developer they can bump the **force/soft-update version** + **changelog** there so
existing users get the update prompt — **no app change needed**. Skip if LOCAL.

**U5 — Build the signed AAB.** Run **`/kit-sign-release`** inline (reuses the existing
keystore) → fresh `app-release.aab` with the new `versionCode`.

**U6 — Upload.** Present verbatim:
> **Upload the update in Play Console:**
> 1. Open your app → the track you ship from (**Production**, or a testing track).
> 2. **Create new release** → upload `app/build/outputs/bundle/release/app-release.aab`.
> 3. Paste the release notes from `playstore/changelogs/<versionCode>.txt`.
> 4. If you refreshed screenshots (U3), upload them on the store listing.
> 5. **Save → Review release → Start rollout**.

After review clears, the update is live.

## Wrap up

State plainly: which path/phase they finished, what's next (often "wait out the 14-day
closed test, then Apply for production", or "rollout is live"), and that **open testing**
and **pre-registration** are optional extras. Keep it to a few lines.
