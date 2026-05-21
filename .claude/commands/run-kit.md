---
description: Build the kit and run it on a device or emulator
---

You are running **`/run-kit`** for ShipKit. Goal: get the app
running so the developer can see it work.

Audience: first-time mobile developers. Be brief and concrete.

## Step 1 — Compile check

Run `./gradlew :app:compileDebugKotlin`. If it fails, read the error, fix it, and
explain in one line what was wrong. Do not continue until it is green.

## Step 2 — Find a device

Run `adb devices`.
- If a device or emulator is listed, continue.
- If none is listed, guide the developer: in Android Studio open Device Manager
  and start an emulator, or plug in a phone with USB debugging enabled. Then
  re-run `adb devices`.

## Step 3 — Build + install

Run `./gradlew :app:installDebug`. This builds the debug APK and installs it on
the connected device.

## Step 4 — Launch

Read the `applicationId` from `app/build.gradle.kts`, then launch the app:

    adb shell monkey -p <applicationId> -c android.intent.category.LAUNCHER 1

Or simply tell the developer to tap the app icon on the device.

## Step 5 — What they should see

Tell the developer the kit opens on the Welcome screen with a "Launch Demo"
button (unless they removed the demo via `/make-it-yours`). The demo runs splash
→ onboarding → paywall → auth → habit tracker → settings. Suggest they click
through it to confirm auth, theme, and paywall behave the way they configured
them.

Report the build result. If something fails on the device but compiles cleanly,
say so plainly — runtime testing on screen is the developer's job; you cannot see
their device.
