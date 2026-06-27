# Refresh entitlements when the app returns to the foreground

- **Date:** 2026-06-28
- **Applies when:** `KitConfig.PAYWALL_ENABLED` is `true` (the RevenueCat paywall is in use). Skip entirely for free apps.
- **Adds dependency:** yes — `androidx.lifecycle:lifecycle-process`
- **Why:** The kit only re-pulled RevenueCat entitlements on `configure()` / `purchase()` / `restore()`. An entitlement granted *outside* the app — a Play pre-registration reward claimed in the Play Store, or a purchase made on another device — wouldn't unlock premium until the next cold start. Re-checking on every foreground fixes that (and is what makes the pre-registration "premium pass" reward unlock the same session it's claimed).

## Edits

Three files. All idempotent — if a piece is already present, skip it.

### 1. `gradle/libs.versions.toml` — `[libraries]` section

Add (reuse the **existing** `lifecycle` version ref already used by the other
lifecycle entries — do **not** add a new version):

```toml
androidx-lifecycle-process = { group = "androidx.lifecycle", name = "lifecycle-process", version.ref = "lifecycle" }
```

### 2. `app/build.gradle.kts` — `dependencies { }` block

Add next to the other lifecycle dependencies:

```kotlin
implementation(libs.androidx.lifecycle.process) // ProcessLifecycleOwner → refresh entitlements on app foreground
```

### 3. `KitApplication.kt` (the `Application` subclass — name may differ if renamed)

Add the imports:

```kotlin
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
```

Then, inside `onCreate()`, find where the paywall is initialised. It currently looks
like:

```kotlin
// RevenueCat init. No-ops when no API key is configured.
if (KitConfig.PAYWALL_ENABLED) {
    get<PurchaseManager>().configure()
}
```

Replace that block with:

```kotlin
// RevenueCat init. No-ops when no API key is configured.
if (KitConfig.PAYWALL_ENABLED) {
    val purchaseManager = get<PurchaseManager>()
    purchaseManager.configure()
    // Re-pull entitlements every time the app comes to the foreground. Catches
    // entitlements granted out-of-band — e.g. a Play pre-registration reward claimed
    // in the Play Store, or a purchase made on another device — so the paywall unlocks
    // without waiting for the next cold start. refresh() no-ops without an API key.
    ProcessLifecycleOwner.get().lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                purchaseManager.refresh()
            }
        },
    )
}
```

`PurchaseManager.refresh()` already exists and is public — no change needed there. It
re-pulls customer info + offerings and re-evaluates `isPremium`.

## Verify

```
/kit-compile-app
```

(or `./gradlew :app:compileDebugKotlin`). A new Gradle dependency means the next
compile re-syncs Gradle — expected.
