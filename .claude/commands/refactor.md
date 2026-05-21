---
description: Rename the kit — package, applicationId, and app display name — in one guided step
argument-hint: [newAppId] [newAppName]
---

You are running the ShipKaro Android Kit **`/refactor`** setup command.

Goal: turn this starter kit into the developer's own app by renaming the package
name, `applicationId`, and display name. This is usually the very first thing a
buyer does after cloning the kit.

This command is a **thin wrapper** over the deterministic `refactorPackage` Gradle
task defined in `app/build.gradle.kts`. You do NOT hand-edit package declarations,
`namespace`, or directory names yourself — the Gradle task does all of that. Your
job is only to gather inputs, validate them, confirm, and run the task.

## Step 1 — Gather inputs

Arguments passed to this command (may be empty): `$ARGUMENTS`

If the first argument looks like a package name, treat it as `newAppId`. If a
second argument is present, treat it as `newAppName`.

For anything still missing, ask the user in plain conversational text (this is
free-form input, so do not use a multiple-choice tool):

- **New application ID** — the package name, e.g. `com.acme.habittracker`.
  Tell the user this is permanent-ish and should be a domain they control,
  reversed (Play Store requires a unique, stable applicationId).
- **New app name** — the display name shown under the launcher icon, e.g.
  `Habit Tracker`. Spaces are allowed.

Then ask one yes/no question with the **AskUserQuestion** tool:

- **Update package declarations too?** (`shouldUpdatePackageName`)
  - **Yes (recommended)** — also rewrites `package`/`import` statements in every
    `.kt` file and renames the source directories. This is what almost everyone
    wants.
  - **No** — only changes `applicationId` + app name, leaves the `com.shipkaro.*`
    package untouched. Pick this only if the developer has a specific reason to
    keep the original package path.

## Step 2 — Validate

The `newAppId` MUST match this regex: `^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*){1,}$`
(lowercase, dot-separated, at least 2 segments). If it does not, explain the
problem and ask again — do not run the task with an invalid value.

## Step 3 — Confirm

Before running, show the user a short summary and ask them to confirm:

```
Old ID   : <detected from app/build.gradle.kts namespace>
New ID   : <newAppId>
App Name : <newAppName>
Package  : <"declarations updated" or "left as-is">
```

Note that this rewrites files in the working tree. Recommend the user has a clean
git state first (so they can `git diff` / revert) — check `git status` and warn if
there are already uncommitted changes.

## Step 4 — Run

From the project root, run (quote the app name — it may contain spaces):

```
./gradlew refactorPackage -PnewAppId=<newAppId> -PnewAppName="<newAppName>"
```

Append ` -PshouldUpdatePackageName=false` only if the user chose **No** in Step 1.

## Step 5 — Report

Relay the Gradle task's output (it prints a "Refactor complete!" block listing
every changed file plus next steps). Then remind the user of the manual follow-ups
the task itself flags:

1. Sync Gradle in Android Studio.
2. If the package changed: Invalidate Caches / Restart.
3. Update `google-services.json` if Firebase is configured.
4. Update the OAuth deeplink scheme/host in `AndroidManifest.xml` + `KitConfig`.

Finally, suggest a sanity build: `./gradlew :app:compileDebugKotlin`.

Keep your messages short and beginner-friendly — the audience is first-time mobile
developers. Explain *why* each step matters in one line, not paragraphs.
