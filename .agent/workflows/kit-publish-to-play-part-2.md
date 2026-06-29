---
description: kit-publish-to-play (part 2 of 3) — app icon + version, create app, listing assets + legal + host, signed build + internal testing
---

Continued from kit-publish-to-play.md.

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

---

**Continued in `kit-publish-to-play-part-3.md` (Phases 5, 6, 7, the Update path, wrap up).**
