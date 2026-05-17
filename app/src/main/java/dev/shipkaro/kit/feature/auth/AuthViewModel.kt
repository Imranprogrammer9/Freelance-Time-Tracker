package dev.shipkaro.kit.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Placeholder auth VM. Phase 1 wires Supabase (and toggle-able Firebase)
 * behind this interface so screens never change.
 */
class AuthViewModel : ViewModel() {

    data class State(val loading: Boolean = false, val signedIn: Boolean = false)

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun signIn() {
        viewModelScope.launch {
            _state.value = State(loading = true)
            // TODO Phase 1: real Supabase/Firebase sign-in.
            _state.value = State(loading = false, signedIn = true)
        }
    }
}
