package dev.shipkaro.kit.core.auth

import androidx.annotation.StringRes
import dev.shipkaro.kit.R
import java.io.IOException

/** Map an [AuthErrorCode] to the user-facing localized message resource. */
@StringRes
fun AuthErrorCode.messageRes(): Int = when (this) {
    AuthErrorCode.INVALID_CREDENTIALS -> R.string.auth_error_invalid_credentials
    AuthErrorCode.EMAIL_NOT_CONFIRMED -> R.string.auth_error_email_not_confirmed
    AuthErrorCode.USER_ALREADY_EXISTS -> R.string.auth_error_user_already_exists
    AuthErrorCode.WEAK_PASSWORD -> R.string.auth_error_weak_password
    AuthErrorCode.INVALID_EMAIL -> R.string.auth_error_invalid_email
    AuthErrorCode.NETWORK_ERROR -> R.string.auth_error_network
    AuthErrorCode.TOO_MANY_REQUESTS -> R.string.auth_error_too_many_requests
    AuthErrorCode.USER_NOT_FOUND -> R.string.auth_error_user_not_found
    AuthErrorCode.NOT_SIGNED_IN -> R.string.auth_error_not_signed_in
    AuthErrorCode.PROVIDER_NOT_SUPPORTED -> R.string.auth_error_provider_not_supported
    AuthErrorCode.UNKNOWN -> R.string.auth_error_unknown
}

/**
 * Translate a Supabase exception to an [AuthErrorCode].
 *
 * Uses pattern-matching on the exception class simple-name + message string rather than
 * `instanceof` checks, to stay resilient across supabase-kt minor versions where exception
 * class structure (or supabase's own AuthErrorCode enum) may shift.
 */
internal fun Throwable.toSupabaseAuthErrorCode(): AuthErrorCode {
    if (this is IOException) return AuthErrorCode.NETWORK_ERROR
    val msg = (message ?: "").lowercase()
    return when {
        "invalid_credentials" in msg || "invalid login credentials" in msg -> AuthErrorCode.INVALID_CREDENTIALS
        "email_not_confirmed" in msg || "email not confirmed" in msg -> AuthErrorCode.EMAIL_NOT_CONFIRMED
        "user_already_exists" in msg || "already registered" in msg ||
            "already been registered" in msg -> AuthErrorCode.USER_ALREADY_EXISTS
        "weak_password" in msg || "password should be" in msg -> AuthErrorCode.WEAK_PASSWORD
        "invalid_email" in msg || "invalid email" in msg -> AuthErrorCode.INVALID_EMAIL
        "user_not_found" in msg || "user not found" in msg -> AuthErrorCode.USER_NOT_FOUND
        "over_email_send_rate_limit" in msg || "too many requests" in msg ||
            "rate limit" in msg -> AuthErrorCode.TOO_MANY_REQUESTS
        "network" in msg || "unable to resolve host" in msg ||
            "timeout" in msg -> AuthErrorCode.NETWORK_ERROR
        else -> AuthErrorCode.UNKNOWN
    }
}

/**
 * Translate a Firebase Auth exception to an [AuthErrorCode]. Class-name match used in
 * place of `instanceof` so this file doesn't hard-depend on Firebase types at compile time
 * (Firebase impl already imports them; this stays light).
 */
internal fun Throwable.toFirebaseAuthErrorCode(): AuthErrorCode {
    if (this is IOException) return AuthErrorCode.NETWORK_ERROR
    val name = this::class.simpleName.orEmpty()
    val msg = (message ?: "").lowercase()
    return when {
        name == "FirebaseAuthInvalidCredentialsException" ->
            if ("badly formatted" in msg || "invalid email" in msg) AuthErrorCode.INVALID_EMAIL
            else AuthErrorCode.INVALID_CREDENTIALS
        name == "FirebaseAuthInvalidUserException" -> AuthErrorCode.USER_NOT_FOUND
        name == "FirebaseAuthUserCollisionException" -> AuthErrorCode.USER_ALREADY_EXISTS
        name == "FirebaseAuthWeakPasswordException" -> AuthErrorCode.WEAK_PASSWORD
        name == "FirebaseTooManyRequestsException" -> AuthErrorCode.TOO_MANY_REQUESTS
        name == "FirebaseNetworkException" -> AuthErrorCode.NETWORK_ERROR
        "network" in msg || "unable to resolve host" in msg -> AuthErrorCode.NETWORK_ERROR
        else -> AuthErrorCode.UNKNOWN
    }
}
