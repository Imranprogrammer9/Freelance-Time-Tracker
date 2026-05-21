package dev.shipkaro.kit.core.billing

import android.app.Activity
import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.core.config.KitConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Single owner of RevenueCat state — ported from the ShipKaro KMM starter's
 * `PurchaseManager`, adapted to the native RevenueCat Android SDK and extended with
 * offerings + a `purchase()` call (the KMM version relied on RevenueCat's prebuilt
 * Paywall composable; this kit ships a custom paywall, so the manager owns purchasing).
 *
 * Builds & runs with no RevenueCat API key: when [BuildConfig.REVENUECAT_API_KEY] is
 * blank, [isConfigured] is false, [configure] no-ops, and the user is treated as
 * non-premium. Set the key in `local.properties` (`revenuecat.android.api.key`).
 */
class PurchaseManager(private val appContext: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _currentOffering = MutableStateFlow<Offering?>(null)
    val currentOffering: StateFlow<Offering?> = _currentOffering.asStateFlow()

    /** True only when a RevenueCat API key is present. Paywall checks this to decide UI. */
    val isConfigured: Boolean get() = BuildConfig.REVENUECAT_API_KEY.isNotEmpty()

    /**
     * Initialise the RevenueCat SDK. Call once at app start (from KitApplication).
     * No-op when no API key is configured.
     */
    fun configure() {
        if (!isConfigured) return
        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
        Purchases.configure(PurchasesConfiguration.Builder(appContext, BuildConfig.REVENUECAT_API_KEY).build())
        refresh()
    }

    /** Re-pull entitlement status + offerings. Safe to call any time after [configure]. */
    fun refresh() {
        if (!isConfigured) return
        refreshCustomerInfo()
        refreshOfferings()
    }

    private fun refreshCustomerInfo() {
        scope.launch {
            runCatching { Purchases.sharedInstance.awaitCustomerInfo() }
                .onSuccess { applyCustomerInfo(it) }
        }
    }

    private fun refreshOfferings() {
        scope.launch {
            runCatching { Purchases.sharedInstance.awaitOfferings() }
                .onSuccess { _currentOffering.value = it.current }
        }
    }

    /** Launch the Google Play billing sheet for [pkg]. Must be called from a UI coroutine. */
    suspend fun purchase(activity: Activity, pkg: Package): PurchaseOutcome {
        if (!isConfigured) return PurchaseOutcome.Failure(BillingErrorCode.NOT_CONFIGURED)
        return try {
            val params = PurchaseParams.Builder(activity, pkg).build()
            val result = Purchases.sharedInstance.awaitPurchase(params)
            applyCustomerInfo(result.customerInfo)
            if (_isPremium.value) PurchaseOutcome.Success
            else PurchaseOutcome.Failure(BillingErrorCode.UNKNOWN)
        } catch (e: PurchasesTransactionException) {
            if (e.userCancelled) PurchaseOutcome.Cancelled
            else PurchaseOutcome.Failure(e.code.toBillingErrorCode(), e)
        }
    }

    /** Restore prior purchases for the signed-in store account. */
    suspend fun restore(): PurchaseOutcome {
        if (!isConfigured) return PurchaseOutcome.Failure(BillingErrorCode.NOT_CONFIGURED)
        return try {
            val info = Purchases.sharedInstance.awaitRestore()
            applyCustomerInfo(info)
            if (_isPremium.value) PurchaseOutcome.Success else PurchaseOutcome.NothingToRestore
        } catch (e: PurchasesTransactionException) {
            PurchaseOutcome.Failure(e.code.toBillingErrorCode(), e)
        } catch (e: PurchasesException) {
            PurchaseOutcome.Failure(e.code.toBillingErrorCode(), e)
        }
    }

    private fun applyCustomerInfo(info: CustomerInfo) {
        _customerInfo.value = info
        _isPremium.value = info.entitlements[KitConfig.ENTITLEMENT_ID]?.isActive == true
    }
}

private fun PurchasesErrorCode.toBillingErrorCode(): BillingErrorCode = when (this) {
    PurchasesErrorCode.NetworkError -> BillingErrorCode.NETWORK_ERROR
    PurchasesErrorCode.StoreProblemError -> BillingErrorCode.STORE_PROBLEM
    PurchasesErrorCode.ProductAlreadyPurchasedError,
    PurchasesErrorCode.ReceiptAlreadyInUseError -> BillingErrorCode.ALREADY_PURCHASED
    PurchasesErrorCode.PaymentPendingError -> BillingErrorCode.PAYMENT_PENDING
    PurchasesErrorCode.PurchaseNotAllowedError,
    PurchasesErrorCode.PurchaseInvalidError -> BillingErrorCode.PURCHASE_NOT_ALLOWED
    PurchasesErrorCode.ProductNotAvailableForPurchaseError -> BillingErrorCode.PRODUCT_NOT_AVAILABLE
    else -> BillingErrorCode.UNKNOWN
}
