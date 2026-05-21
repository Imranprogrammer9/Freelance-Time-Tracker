---
description: Add google-services.json and apply the Firebase Gradle plugins (shared helper)
---

You are running **`/setup-firebase`** for the ShipKaro Android Kit. This is a
shared helper used by `/setup-auth`, `/setup-analytics`, and `/setup-ops`. It
makes the Firebase SDKs (already on the classpath) actually connect to a Firebase
project.

Audience: first-time mobile developers. Be brief; you make the edits.

## Step 1 — Create the Firebase project + register the Android app

Guide the developer:
1. Go to console.firebase.google.com and create a project (or pick an existing
   one).
2. Add an Android app. The **package name Firebase asks for must exactly match**
   the `applicationId` in `app/build.gradle.kts` — open that file, read the
   value, and tell them exactly what to paste.
3. Download the generated `google-services.json`.

## Step 2 — Place google-services.json

The file goes at `app/google-services.json` (next to `app/build.gradle.kts`).
Ask the developer to confirm they have moved it there, then verify the file
exists. Do NOT continue until it does.

`google-services.json` holds project identifiers — recommend keeping it out of
any public repo. Check `.gitignore`; for this private kit repo, committing it is
the developer's call.

## Step 3 — Apply the Gradle plugins

The Firebase deps compile without plugins, but Analytics / Crashlytics / Remote
Config only report once the plugins are applied. The kit's deps are present; the
plugins are not yet declared. If the plugins are already declared (check
`gradle/libs.versions.toml`), skip this step.

1. In `gradle/libs.versions.toml`, under `[versions]` add (use the latest stable
   — at time of writing google-services 4.4.x, the Crashlytics plugin 3.0.x):

       google-services = "4.4.2"
       firebase-crashlytics-plugin = "3.0.2"

   and under `[plugins]` add:

       google-services = { id = "com.google.gms.google-services", version.ref = "google-services" }
       firebase-crashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebase-crashlytics-plugin" }

2. In the root `build.gradle.kts` `plugins { }` block, add (with `apply false`):

       alias(libs.plugins.google.services) apply false
       alias(libs.plugins.firebase.crashlytics) apply false

3. In `app/build.gradle.kts` `plugins { }` block, add (no `apply false`):

       alias(libs.plugins.google.services)
       alias(libs.plugins.firebase.crashlytics)

   Add the Crashlytics plugin only if the developer wants crash reporting; the
   `google-services` plugin is required for any Firebase service.

## Step 4 — Verify

**Skip the build if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

The common failure here is a package-name mismatch — the plugin reports that
`google-services.json` has no client for the app's `applicationId`. If that
happens, the package registered in Firebase does not match `applicationId` in
`app/build.gradle.kts`; fix it in the Firebase console (add an app with the right
package) and re-download the file.

Report success and return to whichever command sent you here.
