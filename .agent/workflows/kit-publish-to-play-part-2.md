---
description: kit-publish-to-play (part 2 of 2) — Set up your app, closed testing, production
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

**5.6 — Data safety:** ⭐ **Import from CSV** (top-right) → upload
`playstore/play_data_safety.csv` (4.3) → the whole 5-stage form fills → review **Preview**
→ **Submit**. *(Fallback if CSV import fails: walk the wizard from `play_data_safety.md`.)*

**5.7 — Government apps:** indie/company app → **No** → Save.

**5.8 — Financial features:** financial-SDK survey → none → tick **"My app doesn't provide
any financial features"** → Save. *(A subscription / IAP is NOT a financial feature.)*

**5.9 — Health:** health-SDK survey → none → tick **"My app does not have any health
features"** → Save.

**5.10 — Store settings (category + contact):** **App category** → App + a best-fit
**Category** (suggest from the app's purpose, e.g. Productivity, Health & Fitness, Tools)
+ optional Tags. **Contact details** → support **email** (required), phone (optional),
**website** = your landing URL. Leave **External marketing** on. Save each.

**5.11 — Set up your store listing:** first call **`/kit-generate-aso`** if not done
(writes `playstore/title.txt`, `short_description.txt`, `full_description.txt`). Then:
> **Create default store listing** → **App name** (≤30) from `title.txt`, **Short
> description** (≤80) from `short_description.txt`, **Full description** (≤4000) from
> `full_description.txt`. **Graphics:** App icon (512×512), Feature graphic (1024×500),
> **Phone screenshots** (≥2) from `playstore/screenshots/`. **Save**.

When this is saved the checklist shows **11/11** and the public tracks unlock.

## Phase 6 — Closed testing (the 12-tester / 14-day gate)

First ask the developer (wait for their answer): *"Is your Play account exempt from closed testing? (exempt =
an organisation account, or a personal account created BEFORE 13 Nov 2023)."*
- **Exempt** → skip to Phase 7.
- **Not exempt (personal, on/after 13 Nov 2023)** → closed testing is **required** before
  production. This is a **14-day** process — start it now and keep building meanwhile.

> ⏰ This is the long pole. The app you closed-test must be your **real app** (build your
> core features first with `/kit-design-app`) — an empty shell can be rejected in review.

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

## Wrap up

State plainly: which phase they finished, what's next (often "wait out the 14-day closed
test, then Apply for production", or "rollout is live"), and that **open testing** and
**pre-registration** are optional extras. Keep it to a few lines.
