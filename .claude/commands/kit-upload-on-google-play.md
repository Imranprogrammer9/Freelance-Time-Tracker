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

## Step 1 — First version or update?

Ask (AskUserQuestion):
- **First version (never published)** — set up signing, generate assets, create
  the Play Console app, build the first AAB, upload manually.
- **Update an existing app** — bump version, write release notes, build, and
  upload via Fastlane.

Then branch to the matching path below. Skip the path the developer did not
pick.

---

# First-version path

## A. Pre-flight check

Confirm before continuing:
- The app runs cleanly via `/kit-run-app` on a device.
- `KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` are set to real published
  URLs (Play **requires** a real privacy policy). If they still equal the
  defaults (`https://example.com/...`), ask the developer for both URLs and
  edit `KitConfig.kt` before continuing.
- A Google Play Console account exists (https://play.google.com/console — one
  time $25 fee). If not, tell them to create it now and wait.

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
  `fastlane/metadata/android/en-US/images/phoneScreenshots/` named
  `1.png`, `2.png`, … (1080×1920 portrait, up to 8 images).

If **generate**: invoke the `aso-appstore-screenshots` skill and let it drive
the screenshot flow. When it finishes, copy the produced images into
`fastlane/metadata/android/en-US/images/phoneScreenshots/`.

If **provide**: wait for confirmation, then `ls` the directory and report the
count back to the developer (Play needs ≥ 2).

## E. Store listing copy

Show this:

> **Listing copy I need (each line goes into Play Console):**
> 1. **App name** (max 30 chars). Usually your app's display name.
> 2. **Short description** (max 80 chars) — one-line elevator pitch.
> 3. **Long description** (max 4000 chars) — features, benefits, why install.

Collect each in plain conversational text (free-form, no multi-choice). Write
each to the matching file:
- App name → `fastlane/metadata/android/en-US/title.txt`
- Short description → `fastlane/metadata/android/en-US/short_description.txt`
- Long description → `fastlane/metadata/android/en-US/full_description.txt`

## F. Data Safety form

Play **requires** a Data Safety declaration in the Console — web-only, cannot be
automated. The kit ships `legal/play-data-safety.md` as a per-SDK template.

Show this:

> **Data Safety in Play Console:**
> 1. Open `legal/play-data-safety.md` in your editor — it lists every SDK the
>    kit uses and what data each collects.
> 2. In Play Console go to **App content → Data safety**.
> 3. Fill the form using the template's values. Per-SDK answers are
>    pre-mapped.
> 4. Submit it. Without this, your release cannot be published.

## G. Create the Play Console app

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

## H. Build the signed AAB

Run from the project root:

    ./gradlew bundleRelease

The signed Android App Bundle lands at:

    app/build/outputs/bundle/release/app-release.aab

If it fails, the usual cause is the signing env vars not being picked up — open
a new terminal and re-check `echo $RELEASE_STORE_FILE`.

## I. Upload — manual for the first release

Fastlane upload needs a Play Console service-account JSON key, which the
developer has not set up yet. For the first release, upload manually:

> **Upload your first release manually:**
> 1. In Play Console, open your app → **Internal testing → Create new
>    release** (recommend internal first, promote to Production later).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Add release notes (a few short bullet points of what's in this version).
> 4. **Save → Review release → Start rollout to Internal testing**.
>
> Once it's stable, promote to Production from the Play Console UI.

After the first release ships, set up Fastlane for future automated uploads
(see the Update path below for the service-account JSON setup).

---

# Update path

## A. Pre-flight check

Confirm:
- The previous release was shipped from this machine (or the developer has the
  same `release.keystore` file from the first release).
- Signing env vars (`RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
  `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`) are still set. Run
  `echo $RELEASE_STORE_FILE` to verify.

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

    fastlane/metadata/android/en-US/changelogs/<versionCode>.txt

where `<versionCode>` is the new value from step B. Plain text, one bullet per
line (no Markdown), max 500 chars total (Play's limit).

## E. Screenshots (if UI changed)

Ask (AskUserQuestion):
- **UI changed — redo screenshots** — same Generate / Provide branch as the
  first-version path (see D in that path above).
- **UI unchanged — skip** — existing screenshots stay.

## F. Build the signed AAB

    ./gradlew bundleRelease

## G. Upload via Fastlane

If `fastlane/play-publisher-key.json` does **not** exist yet, show this:

> **One-time: set up Fastlane Play upload:**
> 1. In Play Console: **Setup → API access**.
> 2. Click **Create new service account** — Play links to Google Cloud Console.
> 3. In Google Cloud Console, create the service account, then create a JSON
>    key for it.
> 4. Back in Play Console, grant the service account the **Release manager**
>    role on your app.
> 5. Save the downloaded JSON as `fastlane/play-publisher-key.json` in this
>    project. `.gitignore` already covers it.

Then run:

    bundle exec fastlane internal

This uploads the AAB to the **Internal testing** track. After the developer has
smoke-tested it, promote to Production:

    bundle exec fastlane promote

---

## Wrap up

Summarise:
- Which path ran (first version / update).
- What was set up vs skipped (e.g. "screenshots generated", "data safety form
  pending in Play Console", "Fastlane key still to be created").
- The single next action — usually one of:
  - "Smoke-test on Internal testing, then promote to Production."
  - "Bump `latest_version` / `app_changelog` in your backend RemoteAppConfig now."
  - "Complete the Play Console **Set up your app** checklist before your
    release can roll out."

Keep it to a few lines.
