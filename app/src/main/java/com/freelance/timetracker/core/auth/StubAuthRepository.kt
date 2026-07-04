package com.freelance.timetracker.core.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory dev auth. Default provider so the kit builds & runs immediately,
 * no Supabase / Firebase credentials required. Every sign-in succeeds with
 * a fake user. State is lost on process death (no persistence).
 *
 * Flip [com.freelance.timetracker.core.config.KitConfig.AUTH_PROVIDER] to SUPABASE or
 * FIREBASE to swap in a real impl.
 */
class StubAuthRepository : AuthRepository {

    private val _session = MutableStateFlow<SessionState>(SessionState.SignedOut)
    override val sessionState: Flow<SessionState> = _session.asStateFlow()

    override fun currentUser(): AuthUser? = (_session.value as? SessionState.SignedIn)?.user

    private fun fakeUser(email: String) = AuthUser(
        id = "stub-${email.hashCode()}",
        email = email,
        displayName = email.substringBefore('@'),
    )

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        delay(SIMULATED_LATENCY_MS)
        val user = fakeUser(email)
        _session.value = SessionState.SignedIn(user)
        return AuthResult.Success(user)
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult {
        delay(SIMULATED_LATENCY_MS)
        val user = fakeUser(email)
        _session.value = SessionState.SignedIn(user)
        return AuthResult.Success(user)
    }

    override suspend fun sendPasswordReset(email: String): AuthResult {
        delay(SIMULATED_LATENCY_MS)
        return AuthResult.Success(fakeUser(email))
    }

    override suspend fun signOut() {
        _session.value = SessionState.SignedOut
    }

    override suspend fun deleteAccount(): AuthResult {
        val email = currentUser()?.email ?: "unknown@stub"
        _session.value = SessionState.SignedOut
        return AuthResult.Success(fakeUser(email))
    }

    private companion object {
        // Short fake latency so loading spinners are visible during dev.
        const val SIMULATED_LATENCY_MS = 400L
    }
}
