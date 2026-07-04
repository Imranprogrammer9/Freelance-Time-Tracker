package com.freelance.timetracker.core.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Firebase Auth-backed [AuthRepository].
 *
 * **Activation requirements** (kit ships INERT — won't work without these):
 *  1. Add `google-services.json` (from Firebase console) to `app/`.
 *  2. Apply the Google Services plugin in `app/build.gradle.kts`:
 *     ```
 *     plugins { id("com.google.gms.google-services") }
 *     ```
 *     and add the classpath to project `build.gradle.kts`.
 *  3. Set [com.freelance.timetracker.core.config.KitConfig.AUTH_PROVIDER] = FIREBASE.
 *
 * Without the JSON file [FirebaseAuth.getInstance] will throw at startup. The
 * factory in [com.freelance.timetracker.core.di.AppModules] guards lazy creation so
 * STUB / SUPABASE builds aren't affected.
 *
 * Account deletion uses `FirebaseUser.delete()` (works client-side, unlike Supabase).
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {

    override val sessionState: Flow<SessionState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            trySend(fa.currentUser?.toAuthUser()?.let(SessionState::SignedIn) ?: SessionState.SignedOut)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun currentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    override suspend fun signInWithEmail(email: String, password: String): AuthResult = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user?.toAuthUser()
            ?: return@runCatching AuthResult.Failure(AuthErrorCode.UNKNOWN)
        AuthResult.Success(user)
    }.getOrElse { AuthResult.Failure(it.toFirebaseAuthErrorCode(), it) }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user?.toAuthUser()
            ?: return@runCatching AuthResult.Failure(AuthErrorCode.UNKNOWN)
        AuthResult.Success(user)
    }.getOrElse { AuthResult.Failure(it.toFirebaseAuthErrorCode(), it) }

    override suspend fun sendPasswordReset(email: String): AuthResult = runCatching {
        auth.sendPasswordResetEmail(email).await()
        AuthResult.Success(AuthUser(id = "", email = email))
    }.getOrElse { AuthResult.Failure(it.toFirebaseAuthErrorCode(), it) }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteAccount(): AuthResult = runCatching {
        val user = auth.currentUser ?: return@runCatching AuthResult.Failure(AuthErrorCode.NOT_SIGNED_IN)
        user.delete().await()
        AuthResult.Success(AuthUser(id = user.uid, email = user.email))
    }.getOrElse { AuthResult.Failure(it.toFirebaseAuthErrorCode(), it) }

    override suspend fun signInWithGoogleIdToken(idToken: String, nonce: String?): AuthResult =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.toAuthUser()
                ?: return@runCatching AuthResult.Failure(AuthErrorCode.UNKNOWN)
            AuthResult.Success(user)
        }.getOrElse { AuthResult.Failure(it.toFirebaseAuthErrorCode(), it) }
}

private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(
    id = uid,
    email = email,
    displayName = displayName,
    avatarUrl = photoUrl?.toString(),
)

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}
