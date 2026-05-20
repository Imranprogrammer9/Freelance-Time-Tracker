package dev.shipkaro.kit.core.auth

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
    data class Failure(val message: String, val cause: Throwable? = null) : AuthResult
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
