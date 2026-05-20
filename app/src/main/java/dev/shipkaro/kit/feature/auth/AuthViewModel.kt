package dev.shipkaro.kit.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.AuthResult
import dev.shipkaro.kit.core.auth.GoogleSignInManager
import dev.shipkaro.kit.core.auth.OAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One ViewModel drives all three auth screens (SignIn / SignUp / ForgotPassword) since
 * they share field state + the same repo. Screen-specific actions ([signIn], [signUp],
 * [sendReset]) update [Status] which the screen renders.
 */
class AuthViewModel(
    private val repo: AuthRepository,
    private val google: GoogleSignInManager,
) : ViewModel() {

    enum class Mode { SIGN_IN, SIGN_UP, FORGOT }

    /** UI status — drives loading spinners + banners + nav. */
    sealed interface Status {
        data object Idle : Status
        data object Loading : Status
        data class Error(val message: String) : Status
        data object EmailConfirmationRequired : Status
        data object Authenticated : Status
        data object ResetEmailSent : Status
    }

    data class UiState(
        val mode: Mode = Mode.SIGN_IN,
        val email: String = "",
        val password: String = "",
        val status: Status = Status.Idle,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun setMode(mode: Mode) = _state.update { it.copy(mode = mode, status = Status.Idle) }
    fun setEmail(email: String) = _state.update { it.copy(email = email, status = Status.Idle) }
    fun setPassword(password: String) = _state.update { it.copy(password = password, status = Status.Idle) }

    fun signIn() = launchAuth {
        repo.signInWithEmail(state.value.email.trim(), state.value.password)
    }

    fun signUp() = launchAuth {
        repo.signUpWithEmail(state.value.email.trim(), state.value.password)
    }

    fun sendReset() = launchAuth {
        repo.sendPasswordReset(state.value.email.trim())
    }

    /**
     * Google sign-in. Tries Credential Manager (native bottomsheet) first; falls back to
     * OAuth redirect via Supabase deeplink when Credential Manager is unconfigured or fails.
     */
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _state.update { it.copy(status = Status.Loading) }
            when (val outcome = google.requestIdToken(activityContext)) {
                is GoogleSignInManager.Outcome.Success -> {
                    val res = repo.signInWithGoogleIdToken(outcome.idToken, outcome.nonce)
                    applyResult(res)
                }
                GoogleSignInManager.Outcome.NotConfigured -> {
                    // Fallback: OAuth redirect via Custom Tabs + deeplink (works without a web client ID).
                    val res = repo.startOAuthRedirect(OAuthProvider.GOOGLE)
                    applyResult(res, treatSuccessAsLoading = true)
                }
                is GoogleSignInManager.Outcome.Failure -> {
                    // Fallback on Credential Manager failure (no Play Services, user dismissed, etc.).
                    val res = repo.startOAuthRedirect(OAuthProvider.GOOGLE)
                    if (res is AuthResult.Failure) applyResult(AuthResult.Failure(outcome.message))
                    else applyResult(res, treatSuccessAsLoading = true)
                }
            }
        }
    }

    private fun applyResult(result: AuthResult, treatSuccessAsLoading: Boolean = false) {
        _state.update {
            it.copy(
                status = when (result) {
                    is AuthResult.Success -> if (treatSuccessAsLoading) Status.Loading else Status.Authenticated
                    AuthResult.EmailConfirmationRequired -> Status.EmailConfirmationRequired
                    is AuthResult.Failure -> Status.Error(result.message)
                },
            )
        }
    }

    private fun launchAuth(block: suspend () -> AuthResult) {
        viewModelScope.launch {
            _state.update { it.copy(status = Status.Loading) }
            val result = block()
            _state.update {
                it.copy(
                    status = when (result) {
                        is AuthResult.Success -> if (state.value.mode == Mode.FORGOT) {
                            Status.ResetEmailSent
                        } else {
                            Status.Authenticated
                        }
                        AuthResult.EmailConfirmationRequired -> Status.EmailConfirmationRequired
                        is AuthResult.Failure -> Status.Error(result.message)
                    },
                )
            }
        }
    }
}
