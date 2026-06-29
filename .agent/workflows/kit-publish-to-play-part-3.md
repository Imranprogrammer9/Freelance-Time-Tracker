---
description: kit-publish-to-play (part 3 of 4) — the 11-task "Set up your app" checklist
---

Continued from kit-publish-to-play.md.

## Phase 5 — Set up your app (the 11-task checklist)

> **Everything here lives in ONE place: Dashboard → "Set up your app" → View tasks** (a
> **"X of 11 complete"** bar). **Do NOT send the developer to an "App content" menu, a
> "Policy" side-menu, or anywhere else** — that's the older Play Console layout. In the
> current console **all 11 tasks are rows in this one Dashboard checklist**: Set privacy
> policy · Sign in details · Ads · Content rating · Target audience · Data safety · Government
> apps · Financial features · Health · Select an app category and provide contact details ·
> Set up your store listing. **The loop for every task: click its row in this checklist →
> fill it → Save → return to the checklist** (✓, the counter ticks up). Top to bottom, **one
> task at a time, waiting for "done"** after each.

Each task below is titled with the **exact row label**. **Open that row from the Dashboard
checklist — never improvise a different menu path.**

> **After you Save a task, Play often pops a "Go to Publishing overview?" dialog. Click "Not
> now".** Don't submit task-by-task — you batch *everything* into a single review submission at
> the very end (the **Publishing overview → Send app for review** step in Phase 7). Submitting
> piecemeal scatters the review and is slower.

**5.1 — Set privacy policy:** open the **Set privacy policy** row → paste the hosted **privacy**
URL (3.4) into the *Privacy policy URL* field → **Save**.

**5.2 — Sign in details** — open the **Sign in details** row from the checklist. The page itself
notes *"This declaration was previously called 'App access'"* — **do NOT** hunt for an "App
access" menu, and **do NOT** use the old "All or some functionality is restricted" option; the
current screen is a simple **Yes / No** question.

**Survey first** (`KitConfig.kt`): `AUTH_PROVIDER` (`SUPABASE`/`FIREBASE`/`STUB`=off),
`GOOGLE_WEB_CLIENT_ID` (set = Google sign-in on), `PAYWALL_ENABLED`. The page asks
**"Is any part of your app restricted?"** — pick from the survey:
- **Auth ON (any provider) and/or paywall ON** → **Yes** (account sign-in **and** payments are
  both listed under the Yes option as restrictions).
- **Auth OFF and paywall OFF** → **No** → **Save** → done.

On **Yes**, present verbatim:
> Select **Yes**. A **Sign in details** card appears → click **+ Add details** (opens the
> *"Add sign in details"* dialog). Fill it top to bottom:
> 1. **Name** * (required, ≤60) — a label so Google knows what it's for, e.g.
>    `Reviewer test account`.
> 2. **Username, email address, or phone number** (≤100) — the account a reviewer logs in with:
>    - **Email login** → create a **test email + password** user in your auth backend and enter
>      that email here (simplest — recommended).
>    - **Google-sign-in only** → a **real Google account you own** with **2-Step Verification +
>      OTP turned OFF** in its *Security* settings (reviewers can't pass 2FA).
> 3. **Password** (≤100) — that account's password.
> 4. **Any other information required to access your app** (≤500) — leave **blank** if a
>    username + password is all that's needed. Use it only for extras: a **guest/demo mode**,
>    how a reviewer **reaches Premium**, or bypassing 2FA/biometrics.
> 5. ☑ **"Sign in details in this declaration provide full access to all the features and
>    content within this app, including premium or paid content"** — tick **only if** this
>    account unlocks Premium. Reviewers can't purchase, so grant it the entitlement first
>    (e.g. a RevenueCat **promotional entitlement** on that user).
> 6. Click **Add** (bottom-right of the dialog), then **Save** on the page.

(Auth OFF but paywall ON → no login to hand over: pick **Yes**, leave username/password blank,
and use **"Any other information"** to explain the app opens freely and how a reviewer reaches
Premium.) Create the test account, fill it in, then say **done**.

**5.3 — Ads** — open the **Ads** row from the checklist. Survey first: grep `app/build.gradle.kts`
+ `gradle/libs.versions.toml` for an ad SDK (`play-services-ads`/AdMob/AppLovin/any ad network —
the kit ships none). The page asks **"Does your app contain ads?"** — present verbatim:
> Pick one, then **Save**:
> - **No, my app does not contain ads** — if no ad SDK is in the project (the kit default).
> - **Yes, my app contains ads** — if an ad SDK is present (Play then shows a **"Contains ads"**
>   label next to your app).

**5.4 — Content rating:** **Start questionnaire** (IARC) → Step 1 email + **Category** (suggest
**All Other App Types** unless game/social) + agree ToS → Step 2: **for a typical
productivity/utility app answer "No" to every content question** (the form only grows if
you say Yes) → Step 3 Summary → **Submit**.

**5.5 — Target audience:** **Target age** → tick **13+ groups** (`13-15`, `16-17`,
`18 and over`); **do not** tick under-13 unless it's truly a kids' app (triggers a heavy
Families burden). No under-13 → steps 2–4 auto-skip → **Summary → Save**.

**5.6 — Data safety (one-click CSV import)** — open the **Data safety** row from the checklist.
`/kit-generate-legal` (Phase 3.3) already wrote **`playstore/play_data_safety.csv`** from your
SDK scan — confirm it exists (`ls playstore/play_data_safety.csv`). **If it's missing** (older
app, or legal was generated before this feature), fill the template
`data_safety_sample_reference.csv` (repo root — Google's import format) now: set **`true`** in
the **`Response value`** column on the rows the app covers (collection; encrypted-in-transit;
account-creation method per the auth survey; the active SDKs' data types — email/name/avatar from
auth, purchase history from RevenueCat, approximate location + device IDs from PostHog, crash
logs from Crashlytics/Sentry — plus their purpose rows; deletion row = hosted privacy URL), and
write it to `playstore/play_data_safety.csv`. Then present verbatim:
> On the **Data safety** page → top-right **Import from CSV** → upload
> `playstore/play_data_safety.csv` → the whole 5-step form fills → review the **Preview** →
> **Submit**.
*(If an import ever fails, fall back to filling the wizard by hand from `play_data_safety.md` /
the hosted `data-safety.html`.)*

**5.7 — Government apps** — open the **Government apps** row. The page asks **"Is your app
developed by or on behalf of a government?"** (e.g. a national health, city parking, or state
licensing app). For a normal indie/company app, present verbatim:
> Select **No** → **Save**.

**5.8 — Financial features:** financial-SDK survey → none → tick **"My app doesn't provide
any financial features"** → Save. *(A subscription / IAP is NOT a financial feature.)*

**5.9 — Health:** health-SDK survey → none → tick **"My app does not have any health
features"** → Save.

**5.10 — Select an app category and provide contact details:** open that row → **App category**
→ App + a best-fit **Category** (suggest from the app's purpose, e.g. Productivity, Health &
Fitness, Tools) + optional Tags. **Contact details** → support **email** (required), phone
(optional), **website** = your landing URL. Leave **External marketing** on. Save each.

**5.11 — Set up your store listing** — open the **Set up your store listing** (Main store
listing) row. Present verbatim:
> **App name** (≤30) from `playstore/title.txt`, **Short description** (≤80) from
> `short_description.txt`, **Full description** (≤4000) from `full_description.txt`.
> **Graphics:**
> - **App icon** (512×512) → `playstore/play_store_icon.png`
> - **Feature graphic** (1024×500) → `playstore/feature_graphic.png` (generated by
>   `/kit-generate-screenshots`)
> - **Phone screenshots** (≥2) → from `playstore/screenshots/`
> **Save**.

When this is saved the checklist shows **11/11** and the public tracks unlock.

---

**Continued in `kit-publish-to-play-part-4.md` (Phases 6, 7, the Update path, wrap up).**
