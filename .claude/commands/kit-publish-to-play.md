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
- **The Play Console steps here are transcribed from the real, current console — screen titles,
  button labels and radio-option wording are captured from actual screenshots.** When a block is
  quoted with `>`, present it to the developer **verbatim** — the exact titles/labels/options.
  **Do NOT paraphrase a step, reorder it, or swap in a menu path or label from your own memory.**
  Play's UI changes and your training data is stale (for example: there is **no "App content →
  …" menu** anymore — every task is a **row in the Dashboard "Set up your app" checklist**; the
  "App access" page is now **"Sign in details"**). If the real screen ever differs from the text,
  **trust the screen, do the step, and tell the developer the kit's wording looks outdated** —
  never silently invent a different path. Prose outside `>` blocks is instructions for *you*.
- A first release is **multi-day** — Phase 6 (closed testing) alone is **14 days**. Save
  progress by reading the project each run (see Resume check) and pick up where they left.
- **Docs:** https://kit.shipkaro.dev/docs/release

## Resume check — do this FIRST

A first release is **multi-day**, and one step (the screenshots' Gemini MCP) makes the
developer **restart Claude Code**, which ends the session. So progress is **persisted to a
file** — read it before greeting and resume, never restart.

**1. Read the durable progress file** `playstore/.publish-progress.md` if it exists. It
records the release type, the Phase 0 account-exempt decision, and which phases are done.
It's a **hint, not the source of truth** — always re-verify against the filesystem below
(disk wins on any conflict; e.g. if the AAB was deleted, rebuild it).

**2. Re-verify against the project** (also covers a project with no progress file yet — work
done before it existed):
- **App icon done?** `playstore/play_store_icon.png` exists **and** the `mipmap-*` launcher
  PNGs differ from the kit placeholder → Phase 1's icon step is done; skip it.
- **Signed AAB?** `ls app/build/outputs/bundle/release/app-release.aab`; `release.*` keys in
  `local.properties`.
- **Legal generated?** `ls playstore/privacy_policy.md playstore/play_data_safety.md`.
- **Listing copy?** `ls playstore/title.txt playstore/full_description.txt`.
- **Screenshots?** `ls playstore/screenshots/` (≥ 2 PNGs).
- **Gemini image MCP installed?** is a `generate_image` tool available — if yes, the MCP
  setup inside `/kit-generate-screenshots` is already done; don't reinstall.

**3. Ask only what code can't tell you** (Play Console state): *"Have you already created the
app on Play Console / uploaded a build to a track / started closed testing?"*

Reconcile all three, resume at the first unfinished phase, mark earlier ones done, and
**rewrite the progress file** to match.

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

Keep two records in sync:
- **Live, in-session:** call **TaskCreate** with the tasks for the path the developer picks
  (Step 1), mark each `in_progress` on entry, `completed` when done (prefix `[skipped] ` via
  **TaskUpdate** if skipped). This is wiped when Claude Code restarts.
- **Durable, across restarts:** maintain `playstore/.publish-progress.md`. **Write it when
  the path is chosen**, **update it at the end of every phase**, and **always update it right
  before any step that ends the session** — i.e. before invoking `/kit-generate-screenshots`
  (it may install an MCP and ask for a restart). This file is what the Resume check reads.
  Keep it short:

  ```markdown
  # /kit-publish-to-play progress  (auto-written — safe to delete to start over)
  release_type: first          # first | update
  account_exempt: no           # yes | no | unknown  (the Phase 0 decision)

  - [x] phase0  Play account + account type
  - [x] phase1  App icon + version
  - [ ] phase2  Create app on Play Console
  - [ ] phase3  Listing assets + legal + host (screenshots, ASO, legal, landing)
  - [ ] phase4  Build signed AAB + internal testing
  - [ ] phase5  Set up your app (11-task checklist)
  - [ ] phase6  Closed testing (12-tester / 14-day)
  - [ ] phase7  Production release

  last_step: phase3 — screenshots; Gemini MCP installed, awaiting restart
  ```

  The `last_step` line is a free-text breadcrumb — make it specific enough to resume from
  (which phase, which sub-step, what you were waiting on).

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
  Phases 1 → 2 → 3 → 4 → 5 → **straight to Phase 7 (production)**. **Skip Phase 6
  entirely** — in Progress tracking mark task `6` `[skipped]` (don't present the 14-day
  flow, it'll just confuse them). Tell the developer plainly: *"Your account is exempt, so
  you can publish to production right after the 'Set up your app' checklist — no 14-day
  test needed."*
- **Not exempt** → the **Phase 6 closed-testing gate applies** (the 12-tester / 14-day
  long pole). Keep task `6`.

Carry this exempt/not-exempt decision through the whole run.

## Phase 1 — App icon + version

**1.1 — App launcher icon.** The kit
ships a **placeholder** launcher icon; a real one is required for a Play release. Skip only
if the developer already replaced it. Ask which way (AskUserQuestion):

- **A) icon.kitchen (easiest, proper adaptive icon)** — present, wait:
  > 1. Go to https://icon.kitchen
  > 2. Upload your logo (or pick an emoji / text), set the background color.
  > 3. **Download** the ZIP (contains `res/mipmap-*` folders).
  > 4. Send me the path to the ZIP.
  Then unzip it into `app/src/main/res/`, overwriting the `mipmap-*` folders. Confirm.

- **B) Generate with Gemini (Nano Banana)** — you write the prompt from the app, they
  generate. First **survey the app** (purpose/category from `namespace` + `feature/` +
  `strings.xml`; brand colour from `core/designsystem/theme/Color.kt`), then fill every
  `{…}` in this JSON template from the survey (no blanks left):
  ```json
  {
    "style": "flat 2D illustration, cute and minimal, soft gradients, bold outlines, app icon style, playful and friendly",
    "scene": "a {character_type} face close-up",
    "elements": {
      "main_subject": "{character_type} face",
      "expression": "{expression}",
      "eye_style": "{eye_style}",
      "colors": { "primary": "{primary_color}", "secondary": "{secondary_color}", "details": "{detail_colors}" },
      "extras": "{optional_details}"
    },
    "composition": { "framing": "centered close-up face", "cropping": "tight crop so face fills the frame", "perspective": "flat, no depth" },
    "formatting": { "background": "{background_color_or_gradient}", "corner_radius": "no radius", "aspect_ratio": "1:1", "resolution": "4000x4000" }
  }
  ```
  Pick a `{character_type}` that fits the app (e.g. a habit app → a friendly mascot animal;
  a finance app → a coin/owl), set `{primary_color}` to the brand colour, etc.

  **Do not paste the filled prompt into the chat** — the terminal's gutter and line-wrap make
  it impossible for the developer to copy cleanly. Instead **write the filled JSON to
  `playstore/icon-prompt.txt`** (use your file-write tool — overwrite if it exists), then copy
  it straight to their clipboard:
  ```bash
  if command -v pbcopy >/dev/null; then pbcopy < playstore/icon-prompt.txt
  elif command -v wl-copy >/dev/null; then wl-copy < playstore/icon-prompt.txt
  elif command -v xclip >/dev/null; then xclip -selection clipboard < playstore/icon-prompt.txt
  elif command -v clip.exe >/dev/null; then clip.exe < playstore/icon-prompt.txt
  else echo "NO_CLIPBOARD"; fi
  ```
  Then walk them through generating it (present verbatim):
  > **Your icon prompt is copied to your clipboard** (also saved at `playstore/icon-prompt.txt`).
  > 1. Open **https://gemini.google.com** and sign in.
  > 2. Click **Create images** (the image tool in the prompt bar) and pick the **Thinking**
  >    model — that's **Nano Banana Pro** (`gemini-3-pro-image`), best for crisp icons. (Free
  >    tier may fall back to the faster model after a few generations — fine for an icon.)
  > 3. **Paste** the prompt (⌘V / Ctrl-V) and send.
  > 4. When the image appears, hover it and **download the PNG** (download icon, or the
  >    three-dot menu → Download).
  > 5. Send me the saved PNG path.

  If the copy step printed `NO_CLIPBOARD`, tell them to open `playstore/icon-prompt.txt` and
  copy it manually. Wait for the path → install via the resize step below.

- **C) I have my own icon** — present, wait:
  > Give me a **square PNG, at least 512×512** (1024+ ideal). Send me the path.
  Wait for the path → install via the resize step below.

**Install (for B / C — resize the source PNG into every density + the Play icon).** Run:
```bash
SRC="<path to the square source PNG>"
RES="app/src/main/res"
for pair in "mdpi 48" "hdpi 72" "xhdpi 96" "xxhdpi 144" "xxxhdpi 192"; do
  set -- $pair; mkdir -p "$RES/mipmap-$1"
  sips -z "$2" "$2" "$SRC" --out "$RES/mipmap-$1/ic_launcher.png" >/dev/null
  sips -z "$2" "$2" "$SRC" --out "$RES/mipmap-$1/ic_launcher_round.png" >/dev/null
done
mkdir -p playstore && sips -z 512 512 "$SRC" --out playstore/play_store_icon.png >/dev/null
```
(`sips` is built into macOS; on Linux/Windows use ImageMagick:
`magick "$SRC" -resize 192x192 "$RES/mipmap-xxxhdpi/ic_launcher.png"` etc.) **Then handle
the adaptive icon:** the kit uses an adaptive launcher (`mipmap-anydpi-v26/ic_launcher.xml`
→ a foreground drawable), so on Android 8+ the device shows that, not the legacy PNGs above.
Point it at the new art — replace the foreground drawable (e.g. `ic_launcher_foreground`)
with the resized image, **or** delete `mipmap-anydpi-v26/ic_launcher.xml` so the device
falls back to the new legacy PNGs. Verify the new icon actually shows (it appears in
`/kit-run-app`). The `playstore/play_store_icon.png` (512×512) is what you'll upload in 5.11.

> Note: icon.kitchen (A) yields the cleanest **adaptive** icon. B/C from a single flat PNG
> give a standard icon — fine to ship, just not safe-zone-tuned for adaptive masks.

**1.2 — Version (must beat anything already on Play).** Read `versionCode` / `versionName` in
`app/build.gradle.kts`. **The trap:** connecting RevenueCat — or any earlier attempt — may have
**already uploaded a build to a track, consuming a `versionCode`**, and Play **rejects a
duplicate `versionCode`**. So the build you're about to make (Phase 4) must be **strictly
higher** than the highest already on Play. Ask (AskUserQuestion): *"Is any build already
uploaded to a Play track — e.g. a placeholder from connecting RevenueCat, or an earlier
attempt?"* (carry this answer to Phase 4):
- **No build anywhere** → `versionCode = 1` is fine; leave it.
- **Yes, a build exists** → find its `versionCode` (Play Console → **Test and release → App
  bundle explorer**, or the track's release page). **Edit `app/build.gradle.kts` now: set
  `versionCode` to that number + 1** (and bump `versionName` too if you like, e.g. `0.1.0` →
  `0.1.1` — only `versionCode` must be unique). Do this **before** Phase 4 builds; confirm the
  new values back to the developer. If they're unsure but a placeholder likely exists, bump to
  at least `2` to be safe.

> The build comes **later** (Phase 4) — *after* the landing page exists, so the app's
> privacy/terms URLs are baked into the build.

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

## Phase 3 — Listing assets + legal + host (generates the privacy/terms URLs)

The build (Phase 4) and the "Set up your app" checklist (Phase 5) both need finished assets
and a **public privacy URL**. Generate + host them now — **before** the build, so the app's
in-app Settings links are baked in.

**3.1 — Screenshots (REQUIRED — do not defer).** **First update `playstore/.publish-progress.md`**
(mark phases 0–2 done, phase3 in-progress) — `/kit-generate-screenshots` may install the
Gemini MCP and ask the developer to **restart Claude Code**, which ends this session, and
the progress file is how you pick up here afterwards. Then run **`/kit-generate-screenshots`**
inline → `playstore/screenshots/`. These are **not optional**: the store listing (5.11) won't
save without **≥ 2 phone screenshots**, and the listing is part of the "Set up your app"
checklist that **gates the closed-testing and production tracks**. If the developer doesn't
want generated ones, they must drop their own PNGs into `playstore/screenshots/` now. After a
restart, re-running `/kit-publish-to-play` lands you back here via the Resume check.

**3.2 — Listing copy.** Run **`/kit-generate-aso`** inline → `playstore/title.txt`,
`short_description.txt`, `full_description.txt` (used in 5.11).

**3.3 — Legal content.** Run **`/kit-generate-legal`** inline → `playstore/privacy_policy.md`
+ `.html`, terms, and `playstore/play_data_safety.md` (the Data safety answers, used in 5.6).

**3.4 — Landing page (hosts privacy + terms → public URLs).** Run **`/kit-generate-landing`**
inline. It builds + hosts the static site and produces the styled **`privacy.html`**,
**`terms.html`**, and an unlisted **`data-safety.html`**. Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 3.5 + 5.1
- Terms: `https://<site>/terms.html` → used in 3.5 + by a pre-registration reward later
- Data safety (unlisted): `https://<site>/data-safety.html` → handy reference for 5.6

**3.5 — Write the URLs into the app (so Settings links work).** Set
`KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` (`core/config/`) to the hosted URLs from
3.4. The in-app **Settings → Privacy policy / Terms** entries open these — they must be in
the code **before** Phase 4 builds the AAB, or the shipped app links to the placeholders.

**3.6 — Plan release analytics.** Run **`/kit-plan-release-analytics`** inline (don't ask
permission — a funnel is load-bearing for "did this launch work?"). It wires 3–5 release
events into the code.

## Phase 4 — Build the signed AAB + put it on internal testing

**4.0 — Paywall billing readiness (only if the app sells — do this BEFORE the build).** From the
Survey: if `PAYWALL_ENABLED = false`, **skip this** (free app, no billing). If
`PAYWALL_ENABLED = true`:
- **Verify the billing permission is declared:** `app/src/main/AndroidManifest.xml` must have an
  **uncommented** `<uses-permission android:name="com.android.vending.BILLING" />`. If it's still
  commented, **uncomment it now** — Play won't unlock product creation without it in the uploaded
  build, and the build must carry it. Present verbatim to confirm the dashboard side:
> **You have a paywall — is billing wired up?** Before real users can pay you need:
> 1. **Products** (one-time and/or subscription) created **+ activated** in Play Console.
> 2. **A service-account JSON** uploaded to RevenueCat (~24–36 h to propagate — the RevenueCat ↔
>    Play connection should show **Verified**).
> 3. **An offering + published paywall** in RevenueCat.
> 4. The build **on a testing track + the tester opt-in URL opened** on your device — Play only
>    serves products to a build on a track, to opted-in testers (the internal-testing upload
>    below puts it on a track; just open the opt-in URL afterwards or products stay empty).
>
> Not done yet? Full guide: **https://kit.shipkaro.dev/docs/paywall** (or `/kit-setup-paywall` →
> "Set up products + Play billing"). Don't block the build on it — but finish billing on the test
> track before promoting to production.

**4.1 — Build the signed AAB.** Run **`/kit-sign-release`** inline — it creates the release
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

## Phase 5.5 — Register your release SHA-1 (so Google sign-in works on Play builds)

**Only if Google sign-in is on** — from the Survey: `GOOGLE_WEB_CLIENT_ID` is set and
`AUTH_PROVIDER` ≠ `STUB`. If Google sign-in is off, **skip this phase.** Do it now (after the
checklist, before closed testing / production) so sign-in works for testers and live users.

Play re-signs your app with **App Signing by Google**, so the certificate users actually get is
**Google's**, not your upload key — and native Google sign-in only accepts the SHA-1s registered
during `/kit-setup-auth` (that was your *debug* key). Register the **release** SHA-1 now, or
sign-in works on your dev machine but **fails for every Play user**. (Needs a build already on a
track — your Phase 4 internal-testing upload covers it.)

**Get the SHA-1** (present verbatim, wait for the value):
> Get your **release SHA-1** from Play Console:
> 1. Left sidebar → **Test and release → App integrity → App signing** (or the top search bar →
>    `app signing` — nav-proof if Google moved it).
> 2. Under **App signing key certificate**, copy the **SHA-1** value → paste it back here.
> (The page only appears once a build is on a track — give it a minute after the upload finishes.)

*(Self-managed keystore instead of Play App Signing? Read it from the keystore yourself:
`keytool -list -v -keystore release.keystore -alias upload | grep SHA1` — using the `release.*`
values from `local.properties`.)*

**Register it** — branch on `AUTH_PROVIDER`:
- **Supabase** — present verbatim, wait for "done":
  > Add the **release** SHA-1 to Google Cloud Console:
  > 1. https://console.cloud.google.com → the **same project** as the OAuth clients from
  >    `/kit-setup-auth`.
  > 2. **APIs & Services → Credentials → + Create credentials → OAuth client ID**.
  > 3. Type **Android**, Name `Android (release)`, Package = your `applicationId`, **SHA-1
  >    certificate fingerprint** = the value above → **Create**.
  > 4. Keep the existing `Android (debug)` client too — debug sign-in keeps working in dev.
  >    Say **done**.
- **Firebase** — present verbatim, wait for the file path:
  > Add the **release** SHA-1 to Firebase:
  > 1. https://console.firebase.google.com → your project → **Project settings → General**.
  > 2. Under **Your apps** → your Android app → **Add fingerprint** → paste the release SHA-1 →
  >    Save.
  > 3. **Download google-services.json** (it now lists both debug + release) → tell me the path.
  When they give the path, copy that file to `app/google-services.json` (overwrite) — the next
  build picks it up.

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

**Then submit everything for review (this is the step people miss).** All your saved tasks +
this release sit as **pending changes** until you submit them in one batch:
> Left sidebar → **Publishing overview** → under **"Changes not yet submitted for review"**
> click **Send app for review**. Play runs quick checks first (a few minutes — "Up to N
> minutes remaining") and sends it once they pass. This is where all the **"Not now"** dialogs
> from Phase 5 finally get submitted, together.

(For an **exempt** account that skipped Phase 6, this Publishing-overview submit *is* your
production submission — there's no separate "apply for production" gate.) After Google's review
clears, the app is **live on Google Play**. 🎉

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
