---
description: Pull the latest NowKit /kit-* commands + skills into your app — your code is never touched
---

You are running **`/kit-update`** for NowKit.

Goal: bring the developer's app up to date with the latest NowKit **commands and
skills** (the `/kit-*` flows + the bundled skills) **without touching their app
code**. Then separately offer to apply any **code-level** bug fixes from the
changelog that they want — the agent edits those files, the developer approves
each one.

Audience: vibe-coder. This is **their** repo (their app); the kit is upstream.
They own the code; the commands/skills are kit-owned and safe to refresh.

## What this updates — and what it never touches

- **Updated (overwritten with the latest kit version) — agent files only:**
  `.claude/commands/`, `.claude/skills/`, `.cursor/commands/`,
  `.opencode/commands/`, `.agent/workflows/`, `.codex/skills/`, and `AGENTS.md`.
  These are package-agnostic, identical across every buyer, and the developer
  doesn't edit them — so they refresh with zero conflicts and the kit's package
  rename is irrelevant here.
- **Never touched:** `app/` (their code), `build.gradle.kts`, `KitConfig.kt`,
  `local.properties`, `CLAUDE.md` (may hold their own notes), or anything else.
  Their app is safe. Code-level fixes are handled separately in Step 4, opt-in.

## Step 0 — Pre-flight

- Confirm this is a git repo. If the **command/skill dirs** have uncommitted
  changes, ask the developer to commit or stash first so the update stays
  reviewable (their `app/` changes are fine — we won't touch those).
- Establish the **kit upstream URL**. Default:
  `https://github.com/wajahatkarim3/shipkaro-android-kit.git` — but confirm with
  the developer (they may have been given a different distribution repo). They
  need **read access** (added as a collaborator when they bought the kit). If a
  later `git fetch` fails with auth, tell them to run `gh auth login` and accept
  the repo invite, then re-run.

## Step 1 — Add the kit as a transient upstream + fetch

The developer's `origin` is their own private app repo; we add the kit as a
**separate** `upstream` remote just for this update:

```bash
git remote add upstream <KIT_URL> 2>/dev/null || git remote set-url upstream <KIT_URL>
git fetch upstream --tags
```

Target ref = latest `upstream/main` by default (or a specific release tag if the
developer names one).

## Step 2 — Pull ONLY the command + skill dirs

Do it on a branch so it's reviewable and revertable:

```bash
git checkout -b kit-update-<today>
git checkout upstream/main -- \
  .claude/commands .claude/skills \
  .cursor/commands .opencode/commands \
  .agent/workflows .codex/skills \
  AGENTS.md
```

- `git checkout <ref> -- <path>` only needs the fetched ref — **no merge-base /
  shared history required**, so it works even though `/kit-save-to-github`
  detached the original kit origin.
- If a path doesn't exist upstream or locally, skip it — don't fail the whole run.
- Then commit: `git commit -m "chore: update NowKit commands + skills"`.

## Step 3 — Show what changed

- Run `git show --stat HEAD` (or `git diff --stat HEAD~1`) to list the refreshed
  command/skill files.
- Summarise the notable command/skill changes in plain language — read the kit's
  `CHANGELOG.md` from `upstream/main`, or
  https://kit.shipkaro.dev/changelog, for entries since the developer's last
  update. Keep it short.

## Step 4 — Offer code-level fixes (opt-in)

Command/skill updates (Steps 1–3) are safe and automatic. **Code** bug fixes are
**not** pulled — the developer owns `app/`. But some fixes live in `.kt` files and
matter (e.g. billing, auth). So:

1. From the changelog, identify the **code-level** fixes since their last update —
   the ones that change files under `app/` (e.g. *paywall premium-unlock*,
   *Supabase quoted name/avatar*, *neutral gray theme*, *edge-to-edge insets*).
2. List each with a one-line **what + why**, then **AskUserQuestion (multi-select)**:
   which to apply.
3. For each chosen fix: find the matching file in **their** repo (respect their
   package rename + any customizations), and apply the same change. If they've
   heavily customised that file, **show the change and confirm** before editing.
   If the file/pattern isn't found (already fixed, or that feature removed), say
   so and skip cleanly.
4. After applying, run **`/kit-compile-app`** to confirm the build is green.

## Step 5 — Wrap up

- Optionally drop the transient remote: `git remote remove upstream`.
- Tell the developer, briefly:
  - Commands + skills refreshed on branch **`kit-update-<today>`**.
  - Which code fixes were applied (or "none — commands/skills only").
  - To keep it: `git checkout main && git merge kit-update-<today>`. To back out:
    just delete the branch.
- Suggest a commit / `/kit-save-to-github` once they're happy.

> This command edits `app/` **only** for the specific code fixes the developer
> explicitly approves in Step 4 — never as part of the commands/skills refresh.
