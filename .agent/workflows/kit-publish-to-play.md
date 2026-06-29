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
- **Update (already on Play)** → jump to the **Update path** (much shorter) in part 3.

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

**1.2 — Version.** Read `versionCode` / `versionName` in `app/build.gradle.kts`.
- **First upload ever:** `versionCode = 1` is fine — leave it.
- (Every later upload must have a **higher** `versionCode` — the Update path handles that.)

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

---

**Continued in `kit-publish-to-play-part-2.md` (Phases 3, 4, 5).**
