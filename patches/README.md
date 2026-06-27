# patches/

Machine-facing **code-change recipes** for `/kit-update`. The human changelog lives
on the website (https://kit.shipkaro.dev/changelog) and stays short. This directory
holds the *exact* edits to reproduce each code-level change in a buyer's app — the
`/kit-update` Step 4 agent reads these and re-applies them.

**Why a directory, not one file:** one self-contained file per change keeps any
single file small, makes each patch linkable + diffable, and lets the update agent
read only the recent, relevant ones instead of a giant history.

**These files are NOT pulled into the buyer's repo.** `/kit-update` only checks out
the command/skill dirs. It reads patches straight from the fetched kit ref:

```bash
git ls-tree --name-only upstream/main patches/     # list (dates in filenames)
git show upstream/main:patches/<file>              # read a specific patch
```

## File naming

`patches/<YYYY-MM-DD>-<slug>.md` — date first so a lexical sort is chronological.

## Format (copy this skeleton for a new patch)

```md
# <Human title>

- **Date:** YYYY-MM-DD
- **Applies when:** <condition, or "always">
- **Adds dependency:** <yes (name) / no>
- **Why:** <one line>

## Edits

### 1. <file path> — <where>
<what to add/change, with a fenced code block of the exact snippet>

## Verify

<command, e.g. `/kit-compile-app`>
```

## Rules for writing a patch

- Make it **self-sufficient** — name every file (including `gradle/libs.versions.toml`
  + `app/build.gradle.kts` for new dependencies), not just the `.kt` edit.
- Respect that the buyer **renamed the package** — refer to classes by name
  (`KitApplication`, `PurchaseManager`), never a hardcoded `dev.shipkaro.kit` path.
- State the **guard** if the change only applies in some configs (e.g. paywall on).
- Keep snippets minimal and idempotent — the agent skips a patch already present.
