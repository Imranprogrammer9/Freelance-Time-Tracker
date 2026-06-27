# Time-limited pre-registration reward (e.g. 30-day Premium pass)

- **Date:** 2026-06-28
- **Applies when:** the app uses the RevenueCat paywall (`KitConfig.PAYWALL_ENABLED = true`). The code is inert until you set a reward product ID, so it's safe to add to any paywall app.
- **Adds dependency:** no.
- **Why:** A Play **pre-registration reward** must be a one-time product, and RevenueCat grants a one-time product's entitlement **for life** — so a *time-limited* reward (e.g. "30 days free Premium") can't come from RevenueCat config. This makes `PurchaseManager` grant premium for a fixed number of days measured from the **Play purchase date** (read from `CustomerInfo.nonSubscriptionTransactions`, server-side — survives reinstalls, can't be reset by clearing app data). Used by `/kit-pre-register-setup`. Pairs with [[2026-06-28-foreground-entitlement-refresh]] (the foreground refresh is what makes the reward unlock the same session it's claimed).

## Edits

Two files. Both idempotent — skip anything already present.

### 1. `KitConfig.kt` (`core/config/`)

Add these two config values inside the `KitConfig` object, next to the other paywall
switches (after `PAYWALL_MODE`):

```kotlin
/**
 * Pre-registration reward — a **time-limited** premium unlock (set up by
 * `/kit-pre-register-setup`). Leave [PRE_REGISTER_REWARD_PRODUCT_ID] blank to disable
 * (the default — no behaviour change).
 *
 * Why this exists: a Play pre-registration reward must be a one-time product, and
 * RevenueCat grants a one-time product's entitlement **for life** — so a *timed* reward
 * (e.g. "30 days free") can't come from RevenueCat config. Instead, attach **no**
 * entitlement to the reward product in RevenueCat, set its product ID here, and
 * [PurchaseManager] grants premium for [PRE_REGISTER_REWARD_DURATION_DAYS] days measured
 * from the **Play purchase date** (read from `CustomerInfo`, server-side — survives
 * reinstalls, doesn't reset or stack).
 */
const val PRE_REGISTER_REWARD_PRODUCT_ID: String = ""
const val PRE_REGISTER_REWARD_DURATION_DAYS: Int = 30
```

### 2. `PurchaseManager.kt` (`core/billing/`)

Add the import (with the other `java.util` / coroutine imports):

```kotlin
import java.util.concurrent.TimeUnit
```

Find `applyCustomerInfo`. It currently is:

```kotlin
private fun applyCustomerInfo(info: CustomerInfo) {
    _customerInfo.value = info
    _isPremium.value = info.entitlements[KitConfig.ENTITLEMENT_ID]?.isActive == true
}
```

Replace it with (adds the reward check + the new helper):

```kotlin
private fun applyCustomerInfo(info: CustomerInfo) {
    _customerInfo.value = info
    val entitlementActive = info.entitlements[KitConfig.ENTITLEMENT_ID]?.isActive == true
    _isPremium.value = entitlementActive || isPreRegisterRewardActive(info)
}

/**
 * True while a pre-registration reward (a one-time product with **no** RevenueCat
 * entitlement) is still inside its window. Disabled when [KitConfig.PRE_REGISTER_REWARD_PRODUCT_ID]
 * is blank. The window is measured from the **Play purchase date** carried in
 * [CustomerInfo.nonSubscriptionTransactions] (server-side — survives reinstalls and can't be
 * reset by clearing app data). "Now" uses the device clock, so a user who rolls their clock
 * back could extend it — an acceptable trade for a free reward.
 */
private fun isPreRegisterRewardActive(info: CustomerInfo): Boolean {
    val productId = KitConfig.PRE_REGISTER_REWARD_PRODUCT_ID
    if (productId.isBlank()) return false
    val purchaseDate = info.nonSubscriptionTransactions
        .filter { it.productIdentifier == productId }
        .maxByOrNull { it.purchaseDate }
        ?.purchaseDate ?: return false
    val windowMs = TimeUnit.DAYS.toMillis(KitConfig.PRE_REGISTER_REWARD_DURATION_DAYS.toLong())
    return System.currentTimeMillis() - purchaseDate.time < windowMs
}
```

## Verify

```
/kit-compile-app
```
