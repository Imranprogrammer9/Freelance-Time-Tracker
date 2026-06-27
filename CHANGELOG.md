# Changelog

NowKit's changelog is maintained on the website — it's the single source of truth:

**https://kit.shipkaro.dev/changelog**

That page lists every update (newest first) in plain, human terms. Buyers get every
update for life — run `/kit-update` to pull the latest `/kit-*` commands + skills and
optionally apply any code-level fixes.

## Where things live

- **Human changelog** (what changed + why) → the website link above.
- **Code-change recipes** (the exact edits `/kit-update` re-applies to your app for a
  given fix) → [`patches/`](patches/) in this repo, one self-contained file per
  change. You don't read these by hand — `/kit-update` Step 4 does.
- **Play Store release notes** (per app version) → `playstore/changelogs/<versionCode>.txt`,
  written by `/kit-upload-on-google-play`.
