---
name: kit-save-to-github
description: Back up your app to your own private GitHub repo (creates it the first time, saves changes after)
---
You are running the NowKit **`/kit-save-to-github`** command.

Goal: get the developer's app onto **their own** private GitHub repo — created,
wired, and pushed — without them needing to know git or GitHub. First run creates
the repo and pushes everything; later runs just save new changes.

Audience: first-time / non-technical "vibe coder" developers. Many have never
used GitHub. Be brief, reassuring, and do the work for them. Explain *why* in one
line, never paragraphs.

**Docs:** https://kit.shipkaro.dev/docs/creating-new-project

When a section shows a block quoted with `>`, present that block to the developer
**verbatim**. Prose outside those blocks is instructions for you.

> **Why this matters:** right now your app lives only on your laptop. Saving it to
> GitHub gives you a private backup, a history you can roll back to, and the place
> your app will live from now on. It's free and private — only you can see it.

This command is the **provider-agnostic git/`gh` flow** — you run the commands, the
developer just answers a couple of questions. Never make the developer type git
commands themselves.

---

## Step 0 — Where does this repo point right now?

Run `git remote -v` and read the result:

- **`origin` contains `shipkaro-android-kit` / `wajahatkarim3`** (or any kit URL) →
  this is still pointing at the **kit repo**. This is a **first-time save** — you'll
  detach it and create the developer's own repo (Step 2A).
- **`origin` is already the developer's own repo** (their GitHub username) → this is
  a **re-save** — just commit and push the new changes (Step 2B).
- **no `origin` at all** → treat as **first-time save** (Step 2A).

Also run `git status --short` so you know whether there are uncommitted changes.

## Step 1 — Make sure `gh` is ready

The GitHub CLI (`gh`) does the repo creation + auth for you.

1. Check it's installed: `gh --version`. If missing, print the install block for
   their OS (detect with `uname`) and stop until they install it:

   > **You need the GitHub CLI first.** Install it, then re-run `/kit-save-to-github`:
   >
   > **macOS:** `brew install gh`
   > **Windows (Scoop):** `scoop install gh`
   > **Linux:** `sudo apt install gh`  *(or see https://github.com/cli/cli#installation)*

2. Check they're logged in: `gh auth status`. If not authenticated, tell them:

   > **Let's connect your GitHub account.** I'll start the login — it opens your
   > browser, you click to authorize, and you're done. If you don't have a GitHub
   > account yet, create a free one at https://github.com/signup first.

   Then run `gh auth login` (defaults: GitHub.com, HTTPS, authenticate via browser).
   Wait for it to finish before continuing. Never run it until they say go.

## Step 2A — First-time save (create their repo)

**Safety check first.** Confirm secrets won't be uploaded: `local.properties` and
any key files MUST be git-ignored. Run `git check-ignore local.properties` — it
should print the path (meaning it's ignored). If it does NOT, stop and fix
`.gitignore` before pushing. Never push API keys.

1. **Pick a repo name.** Ask the developer (free-form, suggest the current folder
   name as the default): "What should your GitHub repo be called?" Keep it simple,
   no spaces (e.g. `habit-tracker`).

2. **Detach the kit remote** so you never push back to the kit:

   ```
   git remote remove origin
   ```

   (Skip if there was no `origin`.)

3. **Commit the current state** if `git status` showed changes:

   ```
   git add -A
   git commit -m "My app — initial setup"
   ```

4. **Create the private repo and push** in one go:

   ```
   gh repo create <repo-name> --private --source=. --remote=origin --push
   ```

   This creates the repo under the developer's account, wires it as `origin`, and
   pushes everything.

## Step 2B — Re-save (push new changes)

`origin` already points at their repo. Just save the latest work:

1. Ask for a one-line description of what changed (default: `Update`).
2. Run:

   ```
   git add -A
   git commit -m "<their message>"
   git push
   ```

   If `git status` was clean (nothing to commit), tell them there are no new
   changes to save and stop — don't create an empty commit.

## Step 3 — Report

Get the URL with `gh repo view --json url -q .url` (or construct it) and show:

> **Saved to GitHub. ✅**
> Your app now lives at: `<repo URL>` (private — only you can see it).
>
> From now on, whenever you want to back up your latest changes, just run
> **`/kit-save-to-github`** again — I'll save and push them for you.

---

**Notes for you (not the developer):**

- Keep the kit's git history — it's a fine starting point; don't re-init unless the
  developer explicitly asks for a clean history.
- This is **optional** and **not required to ship to Google Play** (Play takes an
  AAB, not a repo). It's purely backup + versioning. Don't imply they must do it.
- No terminal / prefer a GUI? Mention **GitHub Desktop** (https://desktop.github.com)
  with its "Publish repository" button as a fallback — but only if they ask.
