package dev.shipkaro.kit.core.auth

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dev.shipkaro.kit.core.config.KitConfig
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Native Google sign-in via Android Credential Manager (Android 9+ / Play Services).
 *
 * Returns a Google ID token + the nonce that was bound to it — both passed to
 * [AuthRepository.signInWithGoogleIdToken] for verification with Supabase / Firebase.
 *
 * Falls back to [AuthRepository.startOAuthRedirect] (browser tab + deeplink callback)
 * when:
 *   - [KitConfig.GOOGLE_WEB_CLIENT_ID] is blank (no client ID configured), OR
 *   - Credential Manager throws (no Play Services, no Google account on device, etc.)
 *
 * Caller (typically [dev.shipkaro.kit.feature.auth.AuthViewModel]) handles fallback.
 */
class GoogleSignInManager(private val appContext: Context) {

    /** Result of a Credential Manager prompt. */
    sealed interface Outcome {
        data class Success(val idToken: String, val nonce: String) : Outcome
        data class Failure(val message: String, val cause: Throwable? = null) : Outcome
        data object NotConfigured : Outcome
    }

    /**
     * Show the Credential Manager bottom-sheet using [activityContext] (must be an Activity
     * context — required to render the system UI). Returns the ID token + nonce on success.
     */
    suspend fun requestIdToken(activityContext: Context): Outcome {
        val clientId = KitConfig.GOOGLE_WEB_CLIENT_ID
        if (clientId.isBlank()) return Outcome.NotConfigured

        val nonce = generateNonce()
        val hashedNonce = sha256(nonce)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            // false = show ALL Google accounts (sign-up + sign-in). true = only previously-authorized.
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val manager = CredentialManager.create(appContext)
        return runCatching {
            val response = manager.getCredential(context = activityContext, request = request)
            val credential = response.credential
            val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Outcome.Success(idToken = googleIdCredential.idToken, nonce = nonce)
        }.getOrElse { e ->
            when (e) {
                is GetCredentialException -> Outcome.Failure(e.message ?: "Credential request failed", e)
                is GoogleIdTokenParsingException -> Outcome.Failure("Invalid Google ID token", e)
                else -> Outcome.Failure(e.message ?: "Unknown error", e)
            }
        }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(NONCE_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val NONCE_BYTES = 32
    }
}
