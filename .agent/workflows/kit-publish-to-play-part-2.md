---
description: kit-publish-to-play (part 2 of 2) — Set up your app, closed testing, production, and the update path
---

Continued from kit-publish-to-play.md.

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

Call **`/kit-generate-changelog`** for the "What's new" notes, then present verbatim:

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

**U2 — Release notes.** Call **`/kit-generate-changelog`** → writes the user-facing
"What's new" to `playstore/changelogs/<versionCode>.txt` from git history.

**U3 — Screenshots (only if the UI changed).** If this update changed screens, call
**`/kit-generate-screenshots`** to refresh `playstore/screenshots/`. Otherwise skip.

**U4 — Remote config (if wired).** If `RemoteAppConfig` is on a backend (not LOCAL), remind
the developer they can bump the **force/soft-update version** + **changelog** there so
existing users get the update prompt — **no app change needed**. Skip if LOCAL.

**U5 — Build the signed AAB.** Call **`/kit-sign-release`** (reuses the existing
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
