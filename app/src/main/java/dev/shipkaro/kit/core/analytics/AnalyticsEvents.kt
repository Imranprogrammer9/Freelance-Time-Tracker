package dev.shipkaro.kit.core.analytics

/**
 * Canonical analytics event names. Reference these constants instead of raw strings so
 * event names stay consistent across screens + dashboards. Ported from the KMM starter.
 */
object AnalyticsEvents {

    // Onboarding
    const val ONBOARDING_VIEWED = "onboarding_viewed"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val ONBOARDING_SKIPPED = "onboarding_skipped"

    // Auth
    const val LOGIN_ATTEMPT = "login_attempt"
    const val LOGIN_SUCCESS = "login_success"
    const val LOGIN_FAILURE = "login_failure"
    const val LOGIN_GOOGLE = "login_google"
    const val SIGNUP_ATTEMPT = "signup_attempt"
    const val SIGNUP_SUCCESS = "signup_success"
    const val SIGNUP_FAILURE = "signup_failure"
    const val LOGOUT = "logout"
    const val ACCOUNT_DELETED = "account_deleted"

    // Paywall
    const val PAYWALL_VIEWED = "paywall_viewed"
    const val PAYWALL_DISMISSED = "paywall_dismissed"
    const val PURCHASE_STARTED = "purchase_started"
    const val PURCHASE_COMPLETED = "purchase_completed"
    const val PURCHASE_CANCELLED = "purchase_cancelled"
    const val PURCHASE_ERROR = "purchase_error"
    const val RESTORE_STARTED = "restore_started"
    const val RESTORE_COMPLETED = "restore_completed"
    const val RESTORE_FAILED = "restore_failed"

    // Settings
    const val ANALYTICS_TOGGLED = "analytics_toggled"
    const val THEME_CHANGED = "theme_changed"
}

/** Canonical analytics parameter keys. */
object AnalyticsParams {
    const val METHOD = "method"
    const val ENABLED = "enabled"
    const val PRODUCT_ID = "product_id"
    const val ERROR_CODE = "error_code"
}
