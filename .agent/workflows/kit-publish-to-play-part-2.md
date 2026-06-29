---
description: kit-publish-to-play (part 2 of 3) — listing assets, signed build + internal testing, the 11-task setup checklist
---

Continued from kit-publish-to-play.md.

## Phase 3 — Listing assets + legal + host (generates the privacy/terms URLs)

The build (Phase 4) and the "Set up your app" checklist (Phase 5) both need finished assets
and a **public privacy URL**. Generate + host them now — **before** the build, so the app's
in-app Settings links are baked in.

**3.1 — Screenshots (REQUIRED — do not defer).** **First update `playstore/.publish-progress.md`**
(mark phases 0–2 done, phase3 in-progress) — `/kit-generate-screenshots` may install the
Gemini MCP and ask the developer to **restart Claude Code**, which ends this session, and the
progress file is how you pick up here afterwards. Then call **`/kit-generate-screenshots`**
→ `playstore/screenshots/`. These are **not optional**: the store listing (5.11) won't save
without **≥ 2 phone screenshots**, and the listing is part of the "Set up your app"
checklist that **gates the closed-testing and production tracks**. If the developer doesn't
want generated ones, they must drop their own PNGs into `playstore/screenshots/` now. After a
restart, re-running `/kit-publish-to-play` lands you back here via the Resume check.

**3.2 — Listing copy.** Call **`/kit-generate-aso`** → `playstore/title.txt`,
`short_description.txt`, `full_description.txt` (used in 5.11).

**3.3 — Legal content.** Call **`/kit-generate-legal`** → `playstore/privacy_policy.md`
+ `.html`, terms, and `playstore/play_data_safety.md` (the Data safety answers, used in 5.6).

**3.4 — Landing page (hosts privacy + terms → public URLs).** Call **`/kit-generate-landing`**.
It builds + hosts the static site and produces the styled **`privacy.html`**,
**`terms.html`**, and an unlisted **`data-safety.html`**. Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 3.5 + 5.1
- Terms: `https://<site>/terms.html` → used in 3.5 + by a pre-registration reward later
- Data safety (unlisted): `https://<site>/data-safety.html` → handy reference for 5.6

**3.5 — Write the URLs into the app (so Settings links work).** Set
`KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` (`core/config/`) to the hosted URLs from
3.4. The in-app **Settings → Privacy policy / Terms** entries open these — they must be in
the code **before** Phase 4 builds the AAB, or the shipped app links to the placeholders.

**3.6 — Plan release analytics.** Call **`/kit-plan-release-analytics`** (don't ask
permission — a funnel is load-bearing for "did this launch work?"). It wires 3–5 release
events into the code.

## Phase 4 — Build the signed AAB + put it on internal testing

**4.1 — Build the signed AAB.** Call **`/kit-sign-release`** — it creates the release
**keystore** (first time; remind them to back it up — losing it means they can never update
the app) and builds the **signed** `app-release.aab`, now carrying the **new icon (Phase 1)
and the privacy/terms URLs (Phase 3.5)**.

> ✅ Signed build at `app/build/outputs/bundle/release/app-release.aab`.

**Then put it on internal testing.** This is **instant, no review**, registers your package
on Play, and (for paid apps) confirms the RevenueCat ↔ Play connection. **Skip if a build is
already on a track** (e.g. the placeholder `/kit-setup-paywall` uploaded). Otherwise, one
sub-step at a time:

**4.2 — Select testers** (present, wait):
> Play Console → your app → **Test and release → Testing → Internal testing → Testers**
> tab. Tick a tester list, or **Create email list** and add emails. **Save**. (Up to 100;
> the join link appears after you publish.)

**4.3 — Create the release** (present, wait):
> **Create new release** (top-right). App signing by Google is on automatically. Under
> **App bundles**, **Upload** `app/build/outputs/bundle/release/app-release.aab`. Set a
> **Release name** (auto-filled is fine) + optional notes. **Next**.

**4.4 — Roll out** (present, wait):
> Review the preview (a "no debug symbols" warning is fine to ignore). **Save → Start
> rollout to Internal testing**. The track goes **Active** with your release live.

## Phase 5 — Set up your app (the 11-task checklist)

> **Always work from the Dashboard checklist.** Go to **Dashboard → "Set up your app" →
> View tasks** — it shows a **"X of 11 complete"** progress bar. **The loop for every
> task: open it from this checklist → fill it → Save → return to the checklist** (✓, the
> counter ticks up). Walk top to bottom, **one task at a time, waiting for "done"** after
> each. 11 tasks.

**5.1 — Set privacy policy:** paste the hosted **privacy** URL (3.4) into the *Privacy
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

---

**Continued in `kit-publish-to-play-part-3.md` (Phases 6, 7, the Update path, Wrap up).**
