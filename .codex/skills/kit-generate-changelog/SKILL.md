---
name: kit-generate-changelog
description: Generate a Play "What's new" changelog for the current version from git history since the last release
---

You are running **`/kit-generate-changelog`** for NowKit.

Goal: write a short, **user-facing** "What's new" changelog for the current
version, derived from the git history since the previous release. Output goes to
`playstore/changelogs/<versionCode>.txt` (Play's per-language release-notes file).

Audience: first-time mobile developers / vibe coders — they won't write release
notes by hand, and raw commit messages aren't user-facing. **You** translate the
commits into a few plain-language bullets a Play Store user would understand.

**Docs:** https://kit.shipkaro.dev/docs/release

## Step 1 — Read the current version

Read `app/build.gradle.kts` `defaultConfig`:

    versionCode = <N>
    versionName = "<X.Y.Z>"

`<versionCode>` names the output file. If you can't find them, stop and say so.

## Step 2 — Find the previous-release anchor

Pick the commit range "since the last release", in this priority order:

1. **Release tag** — `git tag --list 'release-*' 'v*' --sort=-creatordate | head -1`.
   If one exists, the range is `<tag>..HEAD`.
2. **Previous changelog file** — if no tag but `playstore/changelogs/` already has
   files, the highest-numbered one is the last shipped versionCode. Tell the
   developer there's no git tag to anchor on and ask whether to use a ref they
   name (a tag/commit) or just summarise the **last 30 commits**
   (`git log -n 30`).
3. **First release ever** — no tags, no prior changelog → this is v1. Summarise
   the whole history at a high level ("Initial release.") rather than listing
   every build commit.

Run `git log --no-merges --pretty=format:'%s' <range>` to get the subject lines.

## Step 3 — Turn commits into user-facing bullets

Rules:
- **Translate, don't copy.** `fix(auth): token expiry off-by-one` → "Fixed a
  sign-in issue." `feat(paywall): yearly plan` → "Added a yearly subscription."
- **Drop internal noise** — refactors, test/CI/build changes, dependency bumps,
  doc edits, anything a user can't see. If nothing user-facing changed, write a
  single honest line like "Bug fixes and performance improvements."
- **Group + dedupe** — one bullet per user-visible change, not per commit.
- **Plain text, one bullet per line, no Markdown** (no `-`, `*`, `#`). Play renders
  it literally. Keep the **whole file ≤ 500 characters** (Play's hard limit) — trim
  to the most important changes if over.
- Lead with the biggest user-facing wins.

## Step 4 — Write the file

Write the bullets to:

    playstore/changelogs/<versionCode>.txt

Create the `playstore/changelogs/` directory if it doesn't exist. Show the
developer the final text and the character count, and let them edit before you
move on.

## Step 5 — Offer the matching extras

- **RemoteAppConfig changelog** — if `KitConfig.REMOTE_CONFIG_PROVIDER` is
  `FIREBASE` or `SUPABASE`, the in-app "What's New" sheet reads `app_changelog`
  from the backend. Offer to format the same notes as a JSON entry for the
  developer to paste into their backend after the release goes live.
- **Translate it** — if the app ships more than one locale, mention
  `/kit-translate-listing` translates this changelog (and the store listing) into
  the other languages.
- **Tag the release (recommended)** — offer to create an anchor tag so the *next*
  `/kit-generate-changelog` has a clean starting point:

      git tag release-<versionCode>

  (Only create it after the developer confirms; don't push tags unless they ask.)

## Wrap up

One or two lines: where the file was written, its character count vs the 500
limit, and the single next action (usually: "paste this into the Play Console
release notes, or it's auto-picked up if you upload via `/kit-publish-to-play`").
