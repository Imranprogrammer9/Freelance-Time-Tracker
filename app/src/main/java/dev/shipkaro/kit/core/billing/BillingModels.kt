package dev.shipkaro.kit.core.billing

/**
 * Outcome of a purchase / restore call. Discriminated so the paywall renders the
 * right state without try/catch noise. [PurchaseOutcome.Cancelled] is a normal
 * user action, not an error — never show an error banner for it.
 */
sealed interface PurchaseOutcome {
    /** Purchase / restore succeeded and the entitlement is now active. */
    data object Success : PurchaseOutcome

    /** Entitlement still inactive after restore (nothing to restore). */
    data object NothingToRestore : PurchaseOutcome

    /** User dismissed the billing sheet. Not an error. */
    data object Cancelled : PurchaseOutcome

    /** Failed. UI maps [code] to a localized message; [cause] is for logs only. */
    data class Failure(val code: BillingErrorCode, val cause: Throwable? = null) : PurchaseOutcome
}

/** Provider-agnostic billing error categories, mapped from RevenueCat's PurchasesErrorCode. */
enum class BillingErrorCode {
    NOT_CONFIGURED,
    NETWORK_ERROR,
    STORE_PROBLEM,
    ALREADY_PURCHASED,
    PAYMENT_PENDING,
    PURCHASE_NOT_ALLOWED,
    PRODUCT_NOT_AVAILABLE,
    UNKNOWN,
}
