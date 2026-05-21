package dev.shipkaro.kit.core.billing

import androidx.annotation.StringRes
import dev.shipkaro.kit.R

/** Map a [BillingErrorCode] to the user-facing localized message resource. */
@StringRes
fun BillingErrorCode.messageRes(): Int = when (this) {
    BillingErrorCode.NOT_CONFIGURED -> R.string.billing_error_not_configured
    BillingErrorCode.NETWORK_ERROR -> R.string.billing_error_network
    BillingErrorCode.STORE_PROBLEM -> R.string.billing_error_store
    BillingErrorCode.ALREADY_PURCHASED -> R.string.billing_error_already_purchased
    BillingErrorCode.PAYMENT_PENDING -> R.string.billing_error_payment_pending
    BillingErrorCode.PURCHASE_NOT_ALLOWED -> R.string.billing_error_not_allowed
    BillingErrorCode.PRODUCT_NOT_AVAILABLE -> R.string.billing_error_product_unavailable
    BillingErrorCode.UNKNOWN -> R.string.billing_error_unknown
}
