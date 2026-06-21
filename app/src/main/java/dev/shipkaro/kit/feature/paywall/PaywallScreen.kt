package dev.shipkaro.kit.feature.paywall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.analytics.ScreenNames
import dev.shipkaro.kit.core.billing.PurchaseManager
import org.koin.compose.koinInject

/**
 * Paywall screen — renders RevenueCat's prebuilt Paywall composable from the
 * `purchases-ui` artifact (standard Jetpack Compose for Android, NOT Compose
 * Multiplatform).
 *
 * The paywall design itself is configured in the RevenueCat dashboard
 * (Project Settings → Paywalls). This screen just hosts it and forwards
 * purchase / dismiss events to the kit's navigation. If RevenueCat is not
 * configured for this app (no API key in `local.properties`, no offering
 * published, no paywall designed), the composable renders RevenueCat's own
 * error state on device — acceptable during development.
 */
@Composable
fun PaywallScreen(
    onPurchased: () -> Unit,
    onDismiss: () -> Unit,
) {
    val analytics = koinInject<AnalyticsManager>()
    val purchaseManager = koinInject<PurchaseManager>()
    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.PAYWALL) }

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
