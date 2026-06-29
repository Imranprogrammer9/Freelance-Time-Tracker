---
description: Generate Play Store screenshots — analyse your app for ASO captions, then frame your real shots (free, no distortion) or AI-polish with Gemini, or drop your own PNGs
---

You are running **`/kit-generate-screenshots`** for NowKit. Goal: produce phone screenshots
for the Google Play Store. The `aso-appstore-screenshots` skill analyses your app and writes
the ASO captions + tells the developer **which raw screens to capture**; the developer drops
those raw PNGs in, then picks a **renderer** — a **free framed editor** (their real shots,
no distortion) or **AI-polished** images via Gemini — or just drops in their own finished PNGs.

Audience: first-time mobile developers / vibe coders. Be brief; do not paste walls of text.
Final images must end up at `playstore/screenshots/`.

This command is **standalone** — run it anytime to refresh screenshots (before a release,
after a UI redesign, when re-shooting for a new tier of device). `/kit-upload-on-google-play`
Step E and `/kit-publish-to-play` Phase 3.1 invoke it inline.

## Step 0 — Detect existing state

1. List `playstore/screenshots/` — count any PNGs that already exist.
2. Note their dimensions if you can (e.g. via `file` or `sips` on macOS).

Branch:

- **2+ PNGs already present** — AskUserQuestion:
  - **Keep as-is** — exit (developer already has shots from a previous run).
  - **Regenerate** — overwrite via the flow below.
  - **Add more** — append-only; pick numbering after the highest existing.
- **0 or 1 PNGs** — Play needs ≥ 2 for a release; walk the full flow.

## Step 1 — Pick the source

AskUserQuestion:

- **Generate from my app** (recommended) — the `aso-appstore-screenshots` skill analyses your
  code (screens, strings, KitConfig features), picks 3–5 conversion-driving benefits, writes
  the ASO caption/title copy, and tells you exactly which **raw** screens to capture. You drop
  those raw PNGs in, then pick how to turn them into store images.
- **I already have finished store images** — drop final PNGs straight into
  `playstore/screenshots/` (jump to **Step 4 — Own images**).

Branch on the answer.

## Step 2 — Analyse + capture the raw shots (shared by both renderers)

Invoke the `aso-appstore-screenshots` skill for its **planning** pass — this part needs no
image MCP and no API credits. Let it:
- pick 3–5 benefits and write the **ASO caption / title** for each screenshot, and
- tell the developer **which raw screens to capture** from the running app (real-looking data,
  not "Test 1"; pick one mode — light or dark — and stay consistent; clean status bar) and to
  drop the PNGs in **`playstore/raw-shots/`**.

*(If the skill insists on the image MCP before it will even plan, do the Gemini MCP setup from
Step 3B first, then come back.)*

**Capture the caption plan** — the ordered list of screens + their headline copy — you reuse
it in whichever renderer the developer picks. Then wait until they've dropped the raw PNGs in
`playstore/raw-shots/` and said "done". `ls playstore/raw-shots/`; confirm the count matches
the plan (≥ 2). These raw shots feed **both** renderers.

## Step 3 — Pick how to render the store images

Both renderers take the **same raw shots + captions** from Step 2 and output to
`playstore/screenshots/`. AskUserQuestion:

- **Framed editor — free, true to your app** (recommended) — frames your real screenshots
  with the captions + a background at the **exact** Play resolution. No AI, **no stretching**
  — what you captured is what ships. Installs Node tooling and opens a visual editor in your
  browser. → **Step 3A**.
- **Gemini AI — polished, paid** — composites your raw shots into richer AI marketing scenes.
  **Costs Gemini API credits** (the API is no longer free), and needs a one-time **image-MCP
  setup + a Claude Code restart**. → **Step 3B**.

### Step 3A — Framed editor (ParthJadhav `app-store-screenshots`, MIT, free)

1. **Install the skill** (you run it): `npx skills add ParthJadhav/app-store-screenshots`
   (needs **Node 18+**). If Node/`npx` is missing, tell the developer to install Node 18+ and
   retry. If the skill doesn't register right away, have them **restart Claude Code once** (it's
   free — no API cost) and re-run this command; the Resume/Step 0 check lands them back here.
2. **Invoke the `app-store-screenshots` skill** ("Build Google Play screenshots for my app").
   Give it the raw PNGs in `playstore/raw-shots/` and the **Step 2 captions**, and tell it to
   target **Android phone — 1080 × 1920** PNGs. It scaffolds a small Next.js editor and has you
   run its dev server (a local `localhost` URL).
3. Present, then **wait** for "done":
   > The screenshot editor is open in your browser. Import your raw shots, and for each one
   > confirm the caption, pick a background, and check the device frame. When it looks right,
   > click **Export bundle** to download the store-ready PNGs (a ZIP). Tell me where the ZIP
   > saved, then say "done".
4. Unzip it; copy the **Android phone (1080×1920)** PNGs into `playstore/screenshots/` as
   `1.png`, `2.png`, … in the plan's order. Wipe stale numbered files first if Step 0 found
   leftovers and the developer picked **Regenerate**. → **Step 4**.

### Step 3B — Gemini AI (aso skill + Gemini image MCP)

First make sure the Gemini image MCP is set up — **only if it's missing**. Check: is a
`generate_image` tool available (from a Gemini MCP)? If yes, skip to the generation step. If
not, set it up. **You (the agent) do the install and the registration yourself — only the API
key and the final restart need the developer.** Don't ask them to run commands you can run. Use
the **same MCP the skill's source repo specifies** — `@houtini/gemini-mcp`
(github.com/adamlyttleapps/claude-skill-aso-appstore-screenshots) — not any other fork.

**Step A — API key** (the developer does this; present, wait):
> Get a **Gemini API key** (Nano Banana Pro is the `gemini-3-pro-image` model — **paid**
> tier): open https://aistudio.google.com/apikey, create a key, and **paste it here**.

Wait for them to paste the key. Hold it for Step C — **never echo the key back into the chat.**

**Step B — Install the server (you run this).** Run it yourself with Bash:
```bash
npm install -g @houtini/gemini-mcp
```
Only if it fails with `EACCES`/permission, tell the developer to re-run it with `sudo` or fix
their npm prefix (`npm config set prefix ~/.npm-global` then add `~/.npm-global/bin` to PATH)
and retry — don't hand it to them otherwise.

**Step C — Register it (you run this).** Register the server with the key from Step A. Use the
CLI so you don't hand-edit JSON and the key lands in user-scope config (never the repo):
```bash
claude mcp add gemini -s user -e GEMINI_API_KEY=<the key from Step A> -- gemini-mcp
```
Substitute the real key in the command; **do not print the expanded command (with the key)
back to the developer.** If `claude` isn't on PATH or it errors, fall back to telling them to
add it manually per the skill repo's README and stop.

**Step D — Restart** (the developer does this; present, wait):
> **Restart Claude Code** so the MCP server loads. Say "done".

Then re-check the `generate_image` tool exists. If it does → generate. If not, point them at
the skill repo's README and stop (don't guess at the config).

**Generate.** Let the `aso-appstore-screenshots` skill generate, using the raw shots in
`playstore/raw-shots/` + the **Step 2 captions**, at **1080 × 1920** PNG. When it returns
paths, **copy each PNG** into `playstore/screenshots/` as `1.png`, `2.png`, … in display
order. Wipe stale numbered files first if Step 0 found leftovers and the developer picked
**Regenerate**. → **Step 4**.

## Step 4 — Own images (only if they picked "I already have finished store images")

Show this verbatim. Then **STOP and wait** for "done":

> **Drop your phone screenshots in `playstore/screenshots/`**
> 1. Use these names exactly: `1.png`, `2.png`, … in the order Play should display them. Up to
>    8 images.
> 2. Recommended dimensions: **1080 × 1920** portrait (16:9). Play accepts 320–3840 per side,
>    16:9 or 9:16 ratio. PNG or JPEG.
> 3. The folder is already on disk; just drop the files in.
>
> Say "done" when the files are in place.

## Step 5 — Verify

After whichever path finished, `ls playstore/screenshots/`. Count the `*.png` / `*.jpg` files
that match the `<n>.png` pattern. If count < 2, tell the developer Play requires at least 2
phone screenshots and ask them to add more. If ≥ 2:

- Print the count + the full path to the folder (`/absolute/path/to/playstore/screenshots/`).
- Mention they can preview by opening `1.png` in their file viewer.
- Mention `/kit-upload-on-google-play` Step E (and `/kit-publish-to-play` Phase 3.1) will
  reference whatever's in this folder when it's time to ship.

Don't run any build — screenshots are static assets, no compile needed.

## Optional: tablet + Wear screenshots (mention only if asked)

Play Console also accepts 7-inch tablet (1200×1920+) and 10-inch tablet (1600×2560+)
screenshots in separate fields. Most apps skip these — phone shots are mandatory, tablet are
optional. If the developer asks, point them at Play Console → **Store presence → Main store
listing → Tablet screenshots** and tell them to repeat this command with a different output
folder (`playstore/screenshots-tablet-7/` and `-10/`).

Wear / TV / Auto screenshots are out of scope for the kit's micro-SaaS target.
