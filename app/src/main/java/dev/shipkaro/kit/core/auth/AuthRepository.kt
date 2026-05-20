package dev.shipkaro.kit.core.auth

import kotlinx.coroutines.flow.Flow

/**
 * Provider-agnostic auth contract. Screens + ViewModels depend on this; concrete
 * Supabase / Firebase / Stub implementations are picked by Koin based on
 * [dev.shipkaro.kit.core.config.KitConfig.AUTH_PROVIDER].
 */
interface AuthRepository {

    /** Hot stream of session state. Always emits a current value to new collectors. */
    val sessionState: Flow<SessionState>

    /** Currently signed-in user, or null. Cheap snapshot for synchronous reads. */
    fun currentUser(): AuthUser?

    suspend fun signInWithEmail(email: String, password: String): AuthResult

    suspend fun signUpWithEmail(email: String, password: String): AuthResult

    suspend fun sendPasswordReset(email: String): AuthResult

    suspend fun signOut()

    /**
     * Deletes the current user's account on the provider.
     * Callers are responsible for wiping local data (Room, DataStore) after this returns.
     */
    suspend fun deleteAccount(): AuthResult

    /**
     * Sign in with a Google ID token obtained via Credential Manager.
     *
     * The kit's preferred Google sign-in path is Credential Manager + this method
     * (native bottomsheet, no browser tab). Deeplink OAuth (via [startOAuthRedirect])
     * is the fallback for non-Play-Services devices or other OAuth providers.
     */
    suspend fun signInWithGoogleIdToken(idToken: String, nonce: String? = null): AuthResult =
        AuthResult.Failure(AuthErrorCode.PROVIDER_NOT_SUPPORTED)

    /**
     * Fallback: start OAuth redirect (browser tab) for a provider like Google.
     * Supabase impl uses Custom Tabs + deeplink callback. STUB/Firebase impls no-op.
     */
    suspend fun startOAuthRedirect(provider: OAuthProvider): AuthResult =
        AuthResult.Failure(AuthErrorCode.PROVIDER_NOT_SUPPORTED)
}

enum class OAuthProvider { GOOGLE }
