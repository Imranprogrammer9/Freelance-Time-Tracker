---
description: Rename the kit — package, applicationId, and app display name — in one guided step
argument-hint: [newAppId] [newAppName]
---

You are running the ShipKit **`/refactor`** setup command.

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

The refactor always rewrites the `package`/`import` declarations and renames the
source directories along with the `applicationId` and app name. That is what
every developer wants, so do NOT ask about it — just do it.

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
```

Note that this rewrites files in the working tree. Recommend the user has a clean
git state first (so they can `git diff` / revert) — check `git status` and warn if
there are already uncommitted changes.

## Step 4 — Run

From the project root, run (quote the app name — it may contain spaces):

```
./gradlew refactorPackage -PnewAppId=<newAppId> -PnewAppName="<newAppName>"
```

The Gradle task updates package declarations and renames directories by default,
so no extra flag is needed.

## Step 5 — Report

Relay the Gradle task's output (it prints a "Refactor complete!" block listing
every changed file plus next steps). Then remind the user of the manual follow-ups
the task itself flags:

1. Sync Gradle in Android Studio.
2. If the package changed: Invalidate Caches / Restart.
3. Update `google-services.json` if Firebase is configured.
4. Update the OAuth deeplink scheme/host in `AndroidManifest.xml` + `KitConfig`.

Finally, a sanity build — but **only if this command was run on its own**. If
you are running as part of `/start-kit`, skip it; that flow builds once at the
end. Standalone, suggest: `./gradlew :app:compileDebugKotlin`.

Note: `./gradlew refactorPackage` in Step 4 is the actual work and must always
run — only the *extra verification* build is what gets skipped inside `/start-kit`.

Keep your messages short and beginner-friendly — the audience is first-time mobile
developers. Explain *why* each step matters in one line, not paragraphs.
