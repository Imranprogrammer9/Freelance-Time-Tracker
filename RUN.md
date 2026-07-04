# Run this app from your terminal

Build, install, and launch the app on a connected device — **no AI agent needed**.
Run everything from the project root.

## ⚡ Build + run in one command
With a device connected (step 1), this builds, installs, and launches in one go:

    ./gradlew :app:installDebug && adb shell monkey -p com.freelance.timetracker -c android.intent.category.LAUNCHER 1

(The numbered steps below are the same thing, broken down.)

## 1. Connect a device
    adb devices
- Need at least one device/emulator listed.
- Real phone: enable **Developer options → USB debugging**, plug in via USB, accept the prompt.
- Emulator: start one from Android Studio → Device Manager.

## 2. Build + install (debug)
    ./gradlew :app:installDebug

## 3. Launch
    adb shell monkey -p com.freelance.timetracker -c android.intent.category.LAUNCHER 1
(or just tap the app icon on the device)

## Other handy commands
    ./gradlew :app:assembleDebug       # build the debug APK without installing
    ./gradlew :app:compileDebugKotlin  # fast compile-check only
    ./gradlew bundleRelease            # signed release AAB (needs your keystore set up)
    adb uninstall com.freelance.timetracker     # remove the app from the device
