# Stop the paywall crashing when RevenueCat isn't configured yet

- **Date:** 2026-07-03
- **Applies when:** `KitConfig.PAYWALL_ENABLED` is `true` (the RevenueCat paywall is in use). Skip entirely for free apps (`PAYWALL_ENABLED = false`).
- **Adds dependency:** no.
- **Why:** With `PAYWALL_ENABLED = true` but **no RevenueCat API key set yet** (`revenuecat.android.api.key` missing from `local.properties` — e.g. you ran the app before finishing `/kit-setup-paywall`), the app **crashes** the moment the paywall shows (typically right after onboarding):

  ```
  kotlin.UninitializedPropertyAccessException: There is no singleton instance.
  Make sure you configure Purchases before trying to get the default instance.
    at com.revenuecat.purchases.Purchases$Companion.getSharedInstance(...)
    at ...ui.revenuecatui.data.PaywallViewModelImpl.<init>(...)
  ```

  `PurchaseManager.configure()` intentionally no-ops without a key, so RevenueCat's `Purchases` singleton is never initialised. But `PaywallScreen` composed RevenueCat's prebuilt `Paywall` **unconditionally**, and that composable reads the `Purchases` singleton inside its ViewModel constructor — it throws instead of rendering a graceful error state. The fix skips (dismisses) the paywall when RevenueCat isn't configured, so the app keeps working until the key is added. Once the key is set, the paywall renders normally.

## Edits

One file. Idempotent — if the guard is already present, skip.

### `PaywallScreen.kt` (`feature/paywall/PaywallScreen.kt`)

Find the top of the `PaywallScreen` composable. It currently looks like:

```kotlin
    val analytics = koinInject<AnalyticsManager>()
    val purchaseManager = koinInject<PurchaseManager>()
    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.PAYWALL) }

    Paywall(
```

Insert the `isConfigured` guard between the `logScreen` line and the `Paywall(` call:

```kotlin
    val analytics = koinInject<AnalyticsManager>()
    val purchaseManager = koinInject<PurchaseManager>()
    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.PAYWALL) }

    // RevenueCat has no API key → the Purchases singleton was never configured.
    // Composing the prebuilt Paywall here would crash, so skip it and move on.
    if (!purchaseManager.isConfigured) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    Paywall(
```

`PurchaseManager.isConfigured` already exists and is public (it is `BuildConfig.REVENUECAT_API_KEY.isNotEmpty()`), and `onDismiss` is already the screen's dismiss callback — no other changes needed. `onDismiss` routes to Home and marks the paywall seen, exactly like a normal dismiss.

(Optional, cosmetic) The KDoc above `PaywallScreen` may still claim the unconfigured paywall "renders RevenueCat's own error state — acceptable during development." That is stale — it crashes. Update the comment to describe the skip if you like; not required for the fix.

## Verify

```
/kit-compile-app
```

(or `./gradlew :app:compileDebugKotlin`.) Then, with no RevenueCat key set, run the app through onboarding — it should reach Home instead of crashing.
