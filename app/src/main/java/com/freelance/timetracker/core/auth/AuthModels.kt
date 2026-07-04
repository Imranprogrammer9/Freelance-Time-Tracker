package com.freelance.timetracker.core.auth

/** Authenticated user surface exposed by [AuthRepository]. Minimal — extend in app code as needed. */
data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String? = null,
    val avatarUrl: String? = null,
)

/**
 * Result of an auth operation. Discriminated outcome so screens render the right state
 * without try/catch noise.
 */
sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data object EmailConfirmationRequired : AuthResult

    /**
     * Auth call failed. UI should render [code].messageRes() via stringResource — DO NOT
     * surface the raw [cause] to users (it leaks URLs, headers, internal codes). [cause]
     * is kept only for Logcat / Crashlytics.
     */
    data class Failure(
        val code: AuthErrorCode,
        val cause: Throwable? = null,
    ) : AuthResult
}

/**
 * Provider-agnostic auth error categories. Each repo translates its own exceptions
 * into one of these; UI maps to a localized message via [messageRes].
 */
enum class AuthErrorCode {
    INVALID_CREDENTIALS,
    EMAIL_NOT_CONFIRMED,
    USER_ALREADY_EXISTS,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    NETWORK_ERROR,
    TOO_MANY_REQUESTS,
    USER_NOT_FOUND,
    NOT_SIGNED_IN,
    PROVIDER_NOT_SUPPORTED,
    UNKNOWN,
}

/**
 * Top-level auth session state. Drives nav (auth gate) + UI.
 *
 *  - [Loading]   : session restore in progress (e.g., reading persisted token on app start)
 *  - [SignedIn]  : confirmed authenticated user
 *  - [SignedOut] : no session, or session expired
 */
sealed interface SessionState {
    data object Loading : SessionState
    data class SignedIn(val user: AuthUser) : SessionState
    data object SignedOut : SessionState
}
