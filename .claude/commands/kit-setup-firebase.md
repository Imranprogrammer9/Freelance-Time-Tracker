---
description: Add google-services.json and apply the Firebase Gradle plugins (shared helper)
---

You are running **`/kit-setup-firebase`** for ShipKit. This is a shared helper used
by `/kit-setup-auth`, `/kit-setup-analytics`, and `/kit-setup-updates`. It makes the Firebase
SDKs (already on the classpath) connect to a Firebase project.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/firebase

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Create the Firebase project + register the Android app

Open `app/build.gradle.kts`, read the `applicationId` value, and tell the
developer that exact string — they must paste it in the steps below.

Then show the developer exactly this:

> **Create the Firebase project:**
> 1. Go to https://console.firebase.google.com and click **Add project**
>    (or pick an existing project).
> 2. Click **Add app** → the **Android** icon.
> 3. For **Android package name**, paste your app's `applicationId` exactly.
> 4. Click **Register app**.
> 5. Click **Download google-services.json**.

## Step 2 — Place google-services.json

Do NOT make the developer move the file themselves. Ask them to paste the full
path to the `google-services.json` they just downloaded — it is usually in their
Downloads folder (e.g. `~/Downloads/google-services.json`).

Once they give you the path, copy the file yourself to `app/google-services.json`
(next to `app/build.gradle.kts`) and verify it landed. Do NOT continue until the
copy succeeds.

`google-services.json` holds project identifiers — recommend keeping it out of
any public repo.

## Step 3 — Apply the Gradle plugins

The Firebase deps compile without plugins, but Analytics / Crashlytics / Remote
Config only report once the plugins are applied. If the plugins are already
declared (check `gradle/libs.versions.toml`), skip this step.

1. In `gradle/libs.versions.toml`, under `[versions]` add (use the latest stable
   — at time of writing google-services 4.4.x, Crashlytics plugin 3.0.x):

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

**Skip the build if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

The common failure here is a package-name mismatch — the plugin reports that
`google-services.json` has no client for the app's `applicationId`. If so, the
package registered in Firebase does not match `applicationId`; fix it in the
Firebase console and re-download the file.

Report success and return to whichever command sent you here.
