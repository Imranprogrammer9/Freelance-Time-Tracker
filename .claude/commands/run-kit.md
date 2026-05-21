---
description: Build the kit and run it on a device or emulator
---

You are running **`/run-kit`** for ShipKit. Goal: get the app running so the
developer can see it work.

Audience: first-time mobile developers. Be brief and concrete.

**Docs:** https://kit.shipkaro.dev/docs/run

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Compile check

Run `./gradlew :app:compileDebugKotlin`. If it fails, read the error, fix it, and
explain in one line what was wrong. Do not continue until it is green.

## Step 2 — Find a device

Run `adb devices`.
- If a device or emulator is listed, continue.
- If none is listed, show the developer exactly this:

> **Start a device:**
> - **Emulator:** in Android Studio open **Device Manager** and start a virtual
>   device.
> - **Real phone:** enable **Developer options → USB debugging**, plug it in via
>   USB, and accept the "Allow USB debugging?" prompt on the phone.

Then re-run `adb devices`.

## Step 3 — Build + install

Run `./gradlew :app:installDebug`. This builds the debug APK and installs it on
the connected device.

## Step 4 — Launch

Read the `applicationId` from `app/build.gradle.kts`, then launch the app:

    adb shell monkey -p <applicationId> -c android.intent.category.LAUNCHER 1

Or simply tell the developer to tap the app icon on the device.

## Step 5 — What they should see

Show the developer exactly this:

> **First run — what to expect:**
> ShipKit opens on the **Welcome** screen with a "Launch Demo" button (unless
> you removed the demo with `/make-it-yours`). The demo runs splash → onboarding
> → paywall → auth → habit tracker → settings. Click through it to confirm auth,
> theme, and the paywall behave the way you configured them.

Report the build result. If something fails on the device but compiles cleanly,
say so plainly — runtime testing on screen is the developer's job; you cannot see
their device.
