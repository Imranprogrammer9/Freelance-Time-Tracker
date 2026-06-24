---
description: Rename the kit — package, applicationId, and app display name — in one guided step
argument-hint: [newAppId] [newAppName]
---

You are running the NowKit **`/kit-change-app-id`** setup command.

Goal: turn this starter kit into the developer's own app by renaming the package
name, `applicationId`, and display name. This is usually the very first thing a
buyer does after cloning the kit.

Audience: first-time mobile developers. Be brief and beginner-friendly.

**Docs:** https://kit.shipkaro.dev/docs/rename

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

This command is a **thin wrapper** over the deterministic `refactorPackage`
Gradle task in `app/build.gradle.kts`. You do NOT hand-edit package declarations,
`namespace`, or directory names — the Gradle task does all of that. Your job is
to gather inputs, validate them, and run the task — no extra "are you sure?"
prompts, just go.

## Step 0 — Already renamed?

Read `app/build.gradle.kts` and check the current `namespace` / `applicationId`.

- If it is still `dev.shipkaro.kit`, the kit is unrenamed — continue to Step 1.
- If it is **anything other than `dev.shipkaro.kit`**, the rename already ran. Tell
  the developer the current applicationId and ask (**AskUserQuestion**) whether to:
  - **Keep it as-is** (recommended) — exit without changes.
  - **Rename again** — continue to Step 1 with a new value (the Gradle task rewrites
    from the *current* id, so a second rename is safe).

Do not blindly re-run the rename on an already-renamed app.

## Step 1 — Gather inputs

Arguments passed to this command (may be empty): `$ARGUMENTS`

If the first argument looks like a package name, treat it as `newAppId`. If a
second argument is present, treat it as `newAppName`.

For anything still missing, ask the user in plain conversational text (free-form
input — do not use a multiple-choice tool):

- **New application ID** — the package name, e.g. `com.acme.habittracker`. It
  should be a domain you control, reversed. Play Store requires it to be unique
  and stable — it cannot change after your first release.
- **New app name** — the display name under the launcher icon, e.g.
  `Habit Tracker`. Spaces are allowed.

The refactor always rewrites the `package`/`import` declarations and renames the
source directories along with the `applicationId` and app name. That is what
every developer wants — do NOT ask about it, just do it.

## Step 2 — Validate

`newAppId` MUST match this regex: `^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*){1,}$`
(lowercase, dot-separated, at least 2 segments). If it does not, explain the
problem and ask again — never run the task with an invalid value.

## Step 3 — Run

From the project root, run (quote the app name — it may contain spaces):

```
./gradlew refactorPackage -PnewAppId=<newAppId> -PnewAppName="<newAppName>"
```

The Gradle task updates package declarations and renames directories by default,
so no extra flag is needed.

## Step 4 — Update `/kit-run-app` with the new App ID

After the Gradle task succeeds, the new applicationId is now in
`app/build.gradle.kts`. The `/kit-run-app` slash command launches the app by
that ID via `adb shell monkey`, so its file needs the new value.

Edit `.claude/commands/kit-run-app.md` and replace **every occurrence** of the
old applicationId with the new one. There are two places:

1. The line `**App ID for this app:** \`<oldAppId>\`` near the top.
2. The `adb shell monkey -p <oldAppId> ...` line in Step 3.

Use the `oldAppId` and `newAppId` values from this run. After this, the
developer can run `/kit-run-app` and it will install and launch the renamed app
without any further configuration.

## Step 5 — Report

Relay the task's "Refactor complete!" output (it lists every changed file). Then
show the developer exactly this:

> **Rename done.** Your next build picks up the new package automatically — no
> further action needed.
> *(If you have the project open in Android Studio: File → Sync Project, so the
> IDE re-indexes the renamed files.)*

A sanity build — **only if this command was run on its own**, not as part of
`/kit-start-setup` (that flow builds once at the end). Standalone, run:
`./gradlew :app:compileDebugKotlin`.

Keep messages short and beginner-friendly — explain *why* each step matters in
one line, not paragraphs.
