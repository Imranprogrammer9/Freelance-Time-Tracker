---
name: kit-run-app
description: Compile, install, and launch the app on a connected device
---
You are running **`/kit-run-app`** for NowKit.

Audience: first-time mobile developers / vibe coders. They want one command that
gets the app running on their device — be brief and concrete.

**App ID for this app:** `dev.shipkaro.kit`

> NOTE — this file is rewritten by `/kit-change-app-id` after the kit is
> renamed, so the App ID above always matches `applicationId` in
> `app/build.gradle.kts`. If you suspect drift, re-read `app/build.gradle.kts`
> and prefer the live value.

## Step 1 — Find a device

Run `adb devices`.
- If at least one device or emulator is listed, continue.
- If none is listed, show the developer exactly this:

> **Start a device:**
> - **Emulator:** in Android Studio open **Device Manager** and start a virtual
>   device.
> - **Real phone:** enable **Developer options → USB debugging**, plug it in via
>   USB, and accept the "Allow USB debugging?" prompt on the phone.

Then re-run `adb devices`. Wait until one appears before continuing.

## Step 2 — Build + install

From the project root, run:

    ./gradlew :app:installDebug

This compiles the debug APK and installs it on the connected device. If it
fails, read the error, explain in one line, and stop — do not try to launch.

## Step 3 — Launch

Run:

    adb shell monkey -p dev.shipkaro.kit -c android.intent.category.LAUNCHER 1

Or tell the developer to tap the app icon on the device. Report success or
failure in one line.

## Step 4 — Save the run commands (so they work without an AI agent)

Vibe coders sometimes hit their AI quota and then can't run the app at all. After
a **successful** launch, make sure a **`RUN.md`** exists at the project root with
the plain commands they can paste into any terminal themselves.

If `RUN.md` already exists, skip silently. If it doesn't, read the live
`applicationId` from `app/build.gradle.kts` (don't hardcode `dev.shipkaro.kit` —
the dev may have renamed), then write `RUN.md` with that value substituted for
`<APPLICATION_ID>`:

```markdown
# Run this app from your terminal

Build, install, and launch the app on a connected device — **no AI agent needed**.
Run everything from the project root.

## 1. Connect a device
    adb devices
- Need at least one device/emulator listed.
- Real phone: enable **Developer options → USB debugging**, plug in via USB, accept the prompt.
- Emulator: start one from Android Studio → Device Manager.

## 2. Build + install (debug)
    ./gradlew :app:installDebug

## 3. Launch
    adb shell monkey -p <APPLICATION_ID> -c android.intent.category.LAUNCHER 1
(or just tap the app icon on the device)

## Other handy commands
    ./gradlew :app:assembleDebug       # build the debug APK without installing
    ./gradlew :app:compileDebugKotlin  # fast compile-check only
    ./gradlew bundleRelease            # signed release AAB (needs your keystore set up)
    adb uninstall <APPLICATION_ID>     # remove the app from the device
```

Then tell the developer, briefly:

> **Saved your run commands to `RUN.md`.** If your AI agent ever runs out, open
> `RUN.md` and paste those into your terminal from the project root — that's all
> you need to build, install, and launch the app yourself. Bookmark it.
