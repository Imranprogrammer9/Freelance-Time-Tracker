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

A first release is multi-day, and the screenshots' Gemini MCP makes the developer restart
Claude Code (which ends the session) — so persist progress to a file. Maintain
`playstore/.publish-progress.md`: **write it when the path is chosen** (Step 1), **update it
at the end of every phase**, and **always update it right before any step that ends the
session** — i.e. before calling `/kit-generate-screenshots` (it may install an MCP and ask
for a restart). The Resume check reads it. Keep it short:

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

Ask the developer (wait for their answer): *"Is this your app's first release, or an update
to an app that's already on Google Play?"*
- **First release (never published)** → do the **First-release path** below (Phases 0–7).
- **Update (already on Play)** → jump to the **Update path** at the end (much shorter).

---

# First-release path

## Phase 0 — Play account ready + account type (decides the whole path)

**0.1 — Account exists?** Ask the developer (wait for their answer): *"Do you have a verified
Google Play Console account?"*
- **Yes** → continue to 0.2.
- **No** → tell them to create one at https://play.google.com/console (one-time **$25**
  fee) and complete identity verification, then resume. Stop here.

**0.2 — Account type (ask this NOW — it decides whether the 14-day closed-testing gate
applies).** Ask the developer (wait for their answer): *"What kind of Play Console account is
this?"*
- **Organisation account** → **exempt** from the closed-testing requirement.
- **Personal account created BEFORE 13 Nov 2023** → **exempt**.
- **Personal account created ON/AFTER 13 Nov 2023** → **not exempt** — must run closed
  testing (12 testers / 14 continuous days) before applying for production.

**Branch on the answer — set this for the rest of the run:**
- **Exempt** → there is **NO closed-testing gate**. The path is shorter:
  Phases 1 → 2 → 3 → 4 → 5 → **straight to Phase 7 (production)**. **Skip Phase 6
  entirely** (don't present the 14-day flow, it'll just confuse them). Tell the developer
  plainly: *"Your account is exempt, so you can publish to production right after the 'Set
  up your app' checklist — no 14-day test needed."*
- **Not exempt** → the **Phase 6 closed-testing gate applies** (the 12-tester / 14-day
  long pole).

Carry this exempt/not-exempt decision through the whole run.

## Phase 1 — App icon + version

**1.1 — App launcher icon.** The kit
ships a **placeholder** launcher icon; a real one is required for a Play release. Skip only
if the developer already replaced it. Ask the developer which way (wait for their answer):

- **A) icon.kitchen (easiest, proper adaptive icon)** — present, wait:
  > 1. Go to https://icon.kitchen
  > 2. Upload your logo (or pick an emoji / text), set the background color.
  > 3. **Download** the ZIP (contains `res/mipmap-*` folders).
  > 4. Send me the path to the ZIP.
  Then unzip it into `app/src/main/res/`, overwriting the `mipmap-*` folders. Confirm.

- **B) Generate with Gemini (Nano Banana)** — you write the prompt from the app, they
  generate. First **survey the app** (purpose/category from `namespace` + `feature/` +
  `strings.xml`; brand colour from `core/designsystem/theme/Color.kt`), then
  fill every `{…}` in this JSON template from the survey (no blanks left):
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
higher** than the highest already on Play. Ask the user (wait for their answer): *"Is any build
already uploaded to a Play track — e.g. a placeholder from connecting RevenueCat, or an earlier
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
