---
description: Prepare release assets and upload your app to the Google Play Store
---

You are running **`/kit-upload-on-google-play`** for ShipKit.

Goal: take a built app to a Google Play release — assets, signing, listing copy,
data-safety, AAB build, and upload. Two paths: **first version** (more setup) vs
**update** (just bump and ship).

Audience: first-time mobile developers / vibe coders. Several steps below
involve the Google Play Console web UI which cannot be automated — for those,
present the verbatim block clearly and wait for the developer to confirm before
continuing.

**Docs:** https://kit.shipkaro.dev/docs/release

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you.

## Progress tracking

Before Step 1 runs, call **TaskCreate** with just one task: `Step 1 — Pick
release path`. Mark it `in_progress` immediately.

After the developer picks first-version vs update, mark that task `completed`
and immediately call **TaskCreate** again to append the **path-specific**
tasks below (only the ones for the chosen path). Mark each `in_progress`
when entering it, `completed` when done. If a step is **skipped** (e.g.
update path's "Screenshots if UI changed" when the developer says UI did not
change), mark the task `completed` and prefix its content with `[skipped] `
via **TaskUpdate**.

First-version path tasks (verbatim):

- A — Pre-flight check
- B — App icon
- C — Release keystore
- D — Screenshots
- E — Store listing copy
- F — Data Safety form
- G — Plan release analytics
- H — Create Play Console app
- I — Build signed AAB
- J — Manual upload

Update path tasks (verbatim):

- A — Pre-flight check
- B — Bump version
- C — Update RemoteAppConfig (if wired)
- D — Release notes
- E — Screenshots (if UI changed)
- F — Plan release analytics
- G — Build signed AAB
- H — Manual upload

## Step 1 — First version or update?

Ask (AskUserQuestion):
- **First version (never published)** — set up signing, generate assets, create
  the Play Console app, build the first AAB, upload manually.
- **Update an existing app** — bump version, write release notes, build, and
  upload manually in Play Console.

Then branch to the matching path below. Skip the path the developer did not
pick.

---

# First-version path

## A. Pre-flight check

Confirm before continuing:
- The app runs cleanly via `/kit-run-app` on a device.
- A Google Play Console account exists (https://play.google.com/console — one
  time $25 fee). If not, tell them to create it now and wait.
- Privacy + Terms URLs are settled. Don't validate them here — Step F runs
  `/kit-generate-legal` which produces both and walks the developer through
  hosting them.

## B. App icon

A real launcher icon is required for a Play release. Show this:

> **Make your app icon:**
> 1. Go to https://icon.kitchen
> 2. Upload your logo (or pick an emoji / text) and set the background color.
> 3. Click **Download** — you get a ZIP containing `res/mipmap-*` folders.
>
> Once downloaded, **send me the path to the ZIP** and I'll copy the icons
> in for you.

Wait for the path. Unzip it into `app/src/main/res/`, overwriting the existing
`mipmap-*` folders, then confirm.

## C. Release keystore

The signing key is permanent — every release **must** be signed with the same
key. Losing it means you cannot update the app ever. Show this:

> **Generate your release keystore** (run in your project root):
>
>     keytool -genkey -v \
>       -keystore release.keystore \
>       -alias upload \
>       -keyalg RSA -keysize 2048 -validity 10000
>
> Answer the prompts (name, organisation, …). Choose **strong** passwords for
> the keystore and the key — write them down somewhere safe **right now**.
> Losing these = you cannot update the app, ever.
>
> Then **move `release.keystore` somewhere outside the repo** (e.g. `~/keys/`).

After they confirm, show this:

> **Set the signing env vars** — add to `~/.zshrc` (macOS / Linux) or
> equivalent on Windows:
>
>     export RELEASE_STORE_FILE=/absolute/path/to/release.keystore
>     export RELEASE_STORE_PASSWORD=<keystore-password>
>     export RELEASE_KEY_ALIAS=upload
>     export RELEASE_KEY_PASSWORD=<key-password>
>
> Open a **new terminal** so the vars take effect.

`app/build.gradle.kts` already reads these env vars — when they are present, the
release buildType is signed automatically.

## D. Screenshots

Play requires at least 2 phone screenshots. Ask (AskUserQuestion):
- **Generate them automatically** — uses the `aso-appstore-screenshots` skill
  available in this Claude Code install. The skill reads the app's code,
  derives the core benefits, and produces ASO-optimised screenshot images.
- **I'll provide my own** — they drop PNGs into
  `playstore/screenshots/` named
  `1.png`, `2.png`, … (1080×1920 portrait, up to 8 images).

If **generate**: invoke the `aso-appstore-screenshots` skill and let it drive
the screenshot flow. When it finishes, copy the produced images into
`playstore/screenshots/`.

If **provide**: wait for confirmation, then `ls` the directory and report the
count back to the developer (Play needs ≥ 2).

## E. Store listing copy

Ask (AskUserQuestion):
- **Generate them automatically** — uses the `aso-googleplay-listing` skill
  shipped with this kit. The skill builds a keyword strategy and writes app
  name, short description, and long description tuned for Play SEO.
- **I'll write my own** — they hand you the three fields conversationally.

If **generate**: invoke the `aso-googleplay-listing` skill and let it drive the
flow. It writes the three files at the paths below when done.

If **write own**: ask for each in plain conversational text (free-form, no
multi-choice):
- **App name** (max 30 chars). Usually your app's display name.
- **Short description** (max 80 chars) — one-line elevator pitch.
- **Long description** (max 4000 chars) — features, benefits, why install.

Either way, the three files end up at:
- App name → `playstore/title.txt`
- Short description → `playstore/short_description.txt`
- Long description → `playstore/full_description.txt`

## F. Data Safety form + Privacy policy

Run `/kit-generate-legal` inline. That command scans the active SDKs +
KitConfig, asks the developer the legal questions (company name, contact email,
jurisdiction, GDPR / CCPA / COPPA scope), and writes:

- `playstore/privacy_policy.md` — source of truth.
- `playstore/privacy_policy.html` — host this on GitHub Pages / Netlify /
  Vercel so it has a public URL.
- `playstore/play_data_safety.md` — filled Play Console form answers.

After the generator finishes, show:

> **Now host the privacy policy:**
> 1. Open `playstore/privacy_policy.html` — that's the file Play will scrape.
> 2. Pick a host (free options): GitHub Pages, Cloudflare Pages, Vercel,
>    Netlify. Drop the `.html` file in a public repo / project.
> 3. Copy the public URL.
> 4. In Play Console go to **App content → Privacy policy** → paste that URL.
> 5. In Play Console go to **App content → Data safety** → open
>    `playstore/play_data_safety.md` side-by-side and fill the web form.
> 6. Update `KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` to the hosted
>    URLs so Settings → Privacy / Terms opens them in-app.

Wait for the developer to confirm the policy is hosted and the Data Safety
form is submitted before continuing.

## G. Plan release analytics

Run `/kit-plan-release-analytics` inline (don't ask permission — funnels are
load-bearing for "did this release work?"). That command asks the developer
the release goal, suggests 3–5 events, and wires them into the codebase. Skip
its own Verify step here — Step I below builds.

If the developer types "skip" when asked the release goal, mark this task
`[skipped]` and continue.

## H. Create the Play Console app

Manual web step:

> **Create your app in Play Console:**
> 1. Open https://play.google.com/console.
> 2. Click **Create app**.
> 3. Enter app name, default language, **Free / Paid**, accept the
>    declarations.
> 4. Complete the **Set up your app** checklist (target audience, ads, content
>    rating, government apps if relevant, news app if relevant, COVID-19
>    contact-tracing if relevant).

Wait for the developer to confirm the app exists in Play Console.

## I. Build the signed AAB

Run from the project root:

    ./gradlew bundleRelease

The signed Android App Bundle lands at:

    app/build/outputs/bundle/release/app-release.aab

If it fails, the usual cause is the signing env vars not being picked up — open
a new terminal and re-check `echo $RELEASE_STORE_FILE`.

## J. Upload — manual

Show this:

> **Upload your release manually in Play Console:**
> 1. Open your app → **Internal testing → Create new release** (recommend
>    internal testing first; you can promote to Production later from the
>    same screen).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Open `playstore/title.txt`, `playstore/short_description.txt`, and
>    `playstore/full_description.txt` and paste each into the matching Play
>    Console field (Main store listing).
> 4. Upload the PNGs from
>    `playstore/screenshots/` to the
>    **Phone screenshots** section.
> 5. Add release notes (a few short bullets of what's in this version).
> 6. **Save → Review release → Start rollout to Internal testing**.
>
> Once you've smoke-tested it, promote to Production from the same screen.

---

# Update path

## A. Pre-flight check

Confirm:
- The previous release was shipped from this machine (or the developer has the
  same `release.keystore` file from the first release).
- Signing env vars (`RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
  `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`) are still set. Run
  `echo $RELEASE_STORE_FILE` to verify.

Ask the developer (AskUserQuestion): "Did any SDKs change since the last
release — enabled / disabled auth, paywall, analytics, AI, new Retrofit
endpoints, new Supabase tables?" If **yes**, run `/kit-generate-legal` inline
to regenerate the privacy policy + Data Safety mapping; tell them to re-host
the new `playstore/privacy_policy.html`. If **no**, skip — the existing hosted
policy is still accurate.

## B. Bump the version

Open `app/build.gradle.kts`. In `defaultConfig` find:

    versionCode = N
    versionName = "X.Y.Z"

Ask the developer for the next `versionName` (semver — e.g. `0.2.0` for a minor
feature release, `0.1.1` for a patch). Increment `versionCode` by **1** from
its current value. Update both lines.

## C. Update RemoteAppConfig (if wired)

If `KitConfig.REMOTE_CONFIG_PROVIDER` is `FIREBASE` or `SUPABASE`, the kit reads
`latest_version` / `latest_version_name` / `app_changelog` from the backend.
Remind the developer to bump these on their backend **after** the Play release
goes live, so users see the new version in the in-app "What's New" sheet and
the soft-update gate triggers correctly.

Show this:

> **Backend keys to update after Play release goes live:**
> - `latest_version` — set to the new `versionCode`.
> - `latest_version_name` — set to the new `versionName`.
> - `app_changelog` — append a new entry (JSON) with your release notes.
>
> Keep `min_supported_version` as it was unless you want to force users on
> older builds to update.

## D. Release notes

Ask the developer for 3–5 short bullet points of what changed. Write them to:

    playstore/changelogs/<versionCode>.txt

where `<versionCode>` is the new value from step B. Plain text, one bullet per
line (no Markdown), max 500 chars total (Play's limit).

## E. Screenshots (if UI changed)

Ask (AskUserQuestion):
- **UI changed — redo screenshots** — same Generate / Provide branch as the
  first-version path (see D in that path above).
- **UI unchanged — skip** — existing screenshots stay.

## F. Plan release analytics

Run `/kit-plan-release-analytics` inline. It reads the release notes from
Step D + the current codebase, asks the developer the release goal, suggests
3–5 events, and wires them in. Skip its Verify step — Step G below builds.

If the developer types "skip" when asked the release goal, mark this task
`[skipped]` and continue.

## G. Build the signed AAB

    ./gradlew bundleRelease

## H. Upload — manual

Show this:

> **Upload this update manually in Play Console:**
> 1. Open your app → **Internal testing → Create new release** (or whichever
>    track you ship from — Closed / Open testing / Production).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Paste the release notes from
>    `playstore/changelogs/<versionCode>.txt` into the
>    **Release notes** field.
> 4. If you re-did screenshots in step E, upload the new PNGs from
>    `playstore/screenshots/`.
> 5. **Save → Review release → Start rollout**.

---

## Wrap up

Summarise:
- Which path ran (first version / update).
- What was set up vs skipped (e.g. "screenshots generated", "data safety form
  pending in Play Console", "release notes file written").
- The single next action — usually one of:
  - "Smoke-test on Internal testing, then promote to Production in Play Console."
  - "Bump `latest_version` / `app_changelog` in your backend RemoteAppConfig now."
  - "Complete the Play Console **Set up your app** checklist before your
    release can roll out."

Keep it to a few lines.
