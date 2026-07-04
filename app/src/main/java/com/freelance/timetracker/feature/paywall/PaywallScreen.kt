package com.freelance.timetracker.feature.paywall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.freelance.timetracker.core.analytics.AnalyticsManager
import com.freelance.timetracker.core.analytics.ScreenNames
import com.freelance.timetracker.core.billing.PurchaseManager
import org.koin.compose.koinInject

/**
 * Paywall screen — renders RevenueCat's prebuilt Paywall composable from the
 * `purchases-ui` artifact (standard Jetpack Compose for Android, NOT Compose
 * Multiplatform).
 *
 * The paywall design itself is configured in the RevenueCat dashboard
 * (Project Settings → Paywalls). This screen just hosts it and forwards
 * purchase / dismiss events to the kit's navigation.
 *
 * If RevenueCat is not configured (no API key in `local.properties` yet), the
 * prebuilt [Paywall] must NOT be composed: its ViewModel reads the `Purchases`
 * singleton in its constructor and throws `UninitializedPropertyAccessException`
 * ("There is no singleton instance") — it does not render a graceful error
 * state. So we detect that up front and simply dismiss (skip) the paywall, which
 * lets the app keep working until the key is added. Once configured, the paywall
 * renders normally; an empty/undesigned offering then shows RC's own empty state.
 */
@Composable
fun PaywallScreen(
    onPurchased: () -> Unit,
    onDismiss: () -> Unit,
) {
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
        options = PaywallOptions.Builder(dismissRequest = onDismiss)
            .setShouldDisplayDismissButton(true)
            .setListener(object : PaywallListener {
                override fun onPurchaseCompleted(
                    customerInfo: CustomerInfo,
                    storeTransaction: StoreTransaction,
                ) {
                    // RC's prebuilt Paywall completes the purchase internally; it does
                    // NOT touch PurchaseManager, so refresh() here to re-pull the
                    // entitlement and update the isPremium StateFlow every screen reads.
                    // Without this, the purchase succeeds but premium never unlocks.
                    purchaseManager.refresh()
                    onPurchased()
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    if (customerInfo.entitlements.active.isNotEmpty()) {
                        purchaseManager.refresh()
                        onPurchased()
                    }
                }

                override fun onPurchaseError(error: PurchasesError) {
                    // RC's Paywall surfaces its own error UI; no extra action needed.
                }

                override fun onRestoreError(error: PurchasesError) {
                    // RC's Paywall surfaces its own error UI; no extra action needed.
                }
            })
            .build(),
    )
}
