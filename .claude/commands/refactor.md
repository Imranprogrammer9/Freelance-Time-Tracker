---
description: Rename the kit — package, applicationId, and app display name — in one guided step
argument-hint: [newAppId] [newAppName]
---

You are running the ShipKit **`/refactor`** setup command.

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
to gather inputs, validate, confirm, and run the task.

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

## Step 3 — Confirm

Check `git status` first and warn the developer if the working tree already has
uncommitted changes — a clean tree means they can `git diff` / revert the rename.

Then show this summary and ask them to confirm:

```
Old ID   : <detected from app/build.gradle.kts namespace>
New ID   : <newAppId>
App Name : <newAppName>
```

## Step 4 — Run

From the project root, run (quote the app name — it may contain spaces):

```
./gradlew refactorPackage -PnewAppId=<newAppId> -PnewAppName="<newAppName>"
```

The Gradle task updates package declarations and renames directories by default,
so no extra flag is needed.

## Step 5 — Report

Relay the task's "Refactor complete!" output (it lists every changed file). Then
show the developer exactly this:

> **Rename done.** Your next build picks up the new package automatically — no
> further action needed.
> *(If you have the project open in Android Studio: File → Sync Project, so the
> IDE re-indexes the renamed files.)*

A sanity build — **only if this command was run on its own**, not as part of
`/start-kit` (that flow builds once at the end). Standalone, run:
`./gradlew :app:compileDebugKotlin`.

Keep messages short and beginner-friendly — explain *why* each step matters in
one line, not paragraphs.
