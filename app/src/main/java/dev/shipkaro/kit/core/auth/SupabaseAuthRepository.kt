package dev.shipkaro.kit.core.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Supabase-backed [AuthRepository]. Wraps the supabase-kt Auth plugin and adapts its
 * [SessionStatus] flow + [UserInfo] model to the kit's provider-agnostic
 * [SessionState] + [AuthUser].
 *
 * Account deletion: Supabase does NOT expose client-side delete. The kit calls
 * [signOut] and returns Success; production apps should invoke a Supabase Edge
 * Function with the service-role key to actually delete the user record.
 * TODO: ship an example Edge Function in Phase 5 docs.
 */
class SupabaseAuthRepository(
    private val supabase: SupabaseClient,
) : AuthRepository {

    override val sessionState: Flow<SessionState> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    if (user != null) SessionState.SignedIn(user.toAuthUser())
                    else SessionState.SignedOut
                }
                is SessionStatus.NotAuthenticated, is SessionStatus.RefreshFailure -> SessionState.SignedOut
                SessionStatus.Initializing -> SessionState.Loading
            }
        }

    override fun currentUser(): AuthUser? = supabase.auth.currentUserOrNull()?.toAuthUser()

    override suspend fun signInWithEmail(email: String, password: String): AuthResult = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val user = supabase.auth.currentUserOrNull()?.toAuthUser()
            ?: return@runCatching AuthResult.Failure(AuthErrorCode.UNKNOWN)
        AuthResult.Success(user)
    }.getOrElse { AuthResult.Failure(it.toSupabaseAuthErrorCode(), it) }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult = runCatching {
        val info = supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        // signUpWith returns null when email confirmation is required.
        if (info == null) AuthResult.EmailConfirmationRequired
        else AuthResult.Success(info.toAuthUser())
    }.getOrElse { AuthResult.Failure(it.toSupabaseAuthErrorCode(), it) }

    override suspend fun sendPasswordReset(email: String): AuthResult = runCatching {
        supabase.auth.resetPasswordForEmail(email)
        AuthResult.Success(AuthUser(id = "", email = email))
    }.getOrElse { AuthResult.Failure(it.toSupabaseAuthErrorCode(), it) }

    override suspend fun signOut() {
        runCatching { supabase.auth.signOut() }
    }

    override suspend fun deleteAccount(): AuthResult {
        // Supabase has no client-side delete. Sign out locally; caller wipes local data.
        // Production apps should call a Supabase Edge Function using service-role key.
        signOut()
        return AuthResult.Success(AuthUser(id = "", email = null))
    }

    override suspend fun signInWithGoogleIdToken(idToken: String, nonce: String?): AuthResult =
        runCatching {
            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
                if (nonce != null) this.nonce = nonce
            }
            val user = supabase.auth.currentUserOrNull()?.toAuthUser()
                ?: return@runCatching AuthResult.Failure(AuthErrorCode.UNKNOWN)
            AuthResult.Success(user)
        }.getOrElse { AuthResult.Failure(it.toSupabaseAuthErrorCode(), it) }

    override suspend fun startOAuthRedirect(provider: OAuthProvider): AuthResult = runCatching {
        // Opens a Custom Tab; the result returns via the deeplink intent-filter ->
        // SupabaseClient.handleDeeplinks(intent) in MainActivity. signInWith returns
        // immediately; the session change arrives asynchronously via [sessionState].
        when (provider) {
            OAuthProvider.GOOGLE -> supabase.auth.signInWith(Google)
        }
        AuthResult.Success(AuthUser(id = "", email = null))
    }.getOrElse { AuthResult.Failure(it.toSupabaseAuthErrorCode(), it) }
}

private fun UserInfo.toAuthUser(): AuthUser = AuthUser(
    id = id,
    email = email,
    displayName = (userMetadata?.get("full_name") ?: userMetadata?.get("name"))?.toString(),
    avatarUrl = userMetadata?.get("avatar_url")?.toString(),
)
