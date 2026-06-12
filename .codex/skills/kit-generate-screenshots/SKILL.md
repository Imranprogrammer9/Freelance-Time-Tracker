---
name: kit-generate-screenshots
description: Generate Play Store screenshots for your app — codebase-driven, ASO-optimised, or drop your own PNGs
---
You are running **`/kit-generate-screenshots`** for NowKit. Goal: produce
phone screenshots for the Google Play Store, either by invoking the codebase-
driven `aso-appstore-screenshots` skill or by letting the developer drop their
own PNGs into the right folder.

Audience: first-time mobile developers / vibe coders. Be brief; do not paste
walls of text. Output paths must end up at `playstore/screenshots/`.

This command is **standalone** — you can run it anytime to refresh screenshots
(before a release, after a UI redesign, when re-shooting for a new tier of
device). `/kit-upload-on-google-play` Step E invokes this command inline.

## Step 0 — Detect existing state

1. List `playstore/screenshots/` — count any PNGs that already exist.
2. Note their dimensions if you can (e.g. via `file` or `sips` on macOS).

Branch:

- **2+ PNGs already present** — Ask the user (wait for their answer):
  - **Keep as-is** — exit (developer already has shots from a previous run).
  - **Regenerate** — overwrite via the skill flow below.
  - **Add more** — append-only; pick numbering after the highest existing.
- **0 or 1 PNGs** — Play needs ≥ 2 for a release; walk the full flow.

## Step 1 — Pick the source

Ask the user (wait for their answer):

- **Generate automatically** (recommended) — uses the
  `aso-appstore-screenshots` skill installed at your Claude Code level. The
  skill analyses your kit's code (screens, strings, KitConfig features), picks
  3–5 conversion-driving benefits, and produces ASO-optimised images. Despite
  the "appstore" name, the skill is codebase-driven — works for Play too with
  Android dimensions.
- **I'll provide my own** — drop PNGs into `playstore/screenshots/`.

Branch on the answer.

## Step 2A — Generate via the skill

Invoke the `aso-appstore-screenshots` skill. When it asks for output target /
dimensions, instruct it to produce **Play Store phone screenshots**:

- **Format:** PNG (Play accepts JPEG too but PNG is the convention for kit
  output).
- **Dimensions:** **1080 × 1920** (16:9 portrait). Play accepts anywhere from
  320 to 3840 on each side with a 16:9 or 9:16 ratio; 1080×1920 covers every
  device tier the kit targets without inflating file sizes.
- **Count:** 4–6 by default. Play allows 2–8. Ask the developer if they want
  a different count.
- **Output folder:** anywhere the skill chooses — we copy into
  `playstore/screenshots/` ourselves after it finishes.

When the skill returns paths to generated images, **copy each PNG** into
`playstore/screenshots/` with names `1.png`, `2.png`, … in the recommended
display order. Wipe any stale numbered files in that folder first if Step 0
detected leftovers from an old run and the developer picked **Regenerate**.

Print a summary: "Generated N screenshots at `playstore/screenshots/1.png` …
`N.png`. Open `playstore/screenshots/1.png` to preview."

## Step 2B — Provide your own

Show this verbatim. Then **STOP and wait** for "done":

> **Drop your phone screenshots in `playstore/screenshots/`**
> 1. Use these names exactly: `1.png`, `2.png`, … in the order Play should
>    display them. Up to 8 images.
> 2. Recommended dimensions: **1080 × 1920** portrait (16:9). Play accepts
>    320-3840 per side, 16:9 or 9:16 ratio. PNG or JPEG.
> 3. The folder is already on disk; just drop the files in.
>
> Say "done" when the files are in place.

After "done", `ls playstore/screenshots/`. Count the `*.png` / `*.jpg` files
that match the `<n>.png` pattern. If count < 2, tell the developer Play
requires at least 2 phone screenshots and ask them to add more. If ≥ 2,
report the count back.

## Step 3 — Verify

Wrap up:

- Print the count + the full path to the folder
  (`/absolute/path/to/playstore/screenshots/`).
- Mention they can preview by opening `1.png` in their file viewer.
- Mention `/kit-upload-on-google-play` Step E will reference whatever's in
  this folder when it's time to ship.

Don't run any build — screenshots are static assets, no compile needed.

## Optional: tablet + Wear screenshots (mention only if asked)

Play Console also accepts 7-inch tablet (1200×1920+) and 10-inch tablet
(1600×2560+) screenshots in separate fields. Most apps skip these — phone
shots are mandatory, tablet are optional. If the developer asks, point them
at Play Console → **Store presence → Main store listing → Tablet screenshots**
and tell them to repeat this command with a different output folder
(`playstore/screenshots-tablet-7/` and `-10/`).

Wear / TV / Auto screenshots are out of scope for the kit's micro-SaaS target.
