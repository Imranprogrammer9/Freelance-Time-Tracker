---
description: Continuation of kit-upload-on-google-play
---

This contains the **Update path** of `/kit-upload-on-google-play` plus the
shared **Wrap up**. Run this path only if the developer picked "Update an
existing app" in Step 1. If they ran the First-version path, jump to the Wrap
up at the bottom.

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim**.

---

# Update path

## A. Pre-flight check

Confirm:
- The previous release was shipped from this machine (or the developer has the
  same `release.keystore` file from the first release).
- Signing env vars (`RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
  `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`) are still set. Run
  `echo $RELEASE_STORE_FILE` to verify.

Ask the user (wait for their answer): "Did any SDKs change since the last
release — enabled / disabled auth, paywall, analytics, AI, new Retrofit
endpoints, new Supabase tables?" If **yes**, Call /kit-generate-legal
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

Ask the user (wait for their answer):
- **UI changed — redo screenshots** — Call /kit-generate-screenshots.
- **UI unchanged — skip** — existing screenshots in `playstore/screenshots/`
  stay.

## F. Plan release analytics

Call /kit-plan-release-analytics. It reads the release notes from
Step D + the current codebase, asks the developer the release goal, suggests
3–5 events, and wires them in. Skip its Verify step — Step G below builds.

If the developer types "skip" when asked the release goal, skip this step
and continue.

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
