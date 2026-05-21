package dev.shipkaro.kit.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shipkaro.kit.core.analytics.AnalyticsEvents
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.analytics.AnalyticsParams
import dev.shipkaro.kit.core.auth.AuthErrorCode
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.AuthResult
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.theme.ThemeMode
import dev.shipkaro.kit.core.locale.LocaleManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val auth: AuthRepository,
    private val analytics: AnalyticsManager,
) : ViewModel() {

    sealed interface DeleteStatus {
        data object Idle : DeleteStatus
        data object Confirming : DeleteStatus
        data object Working : DeleteStatus
        data class Error(val code: AuthErrorCode) : DeleteStatus
        data object Done : DeleteStatus
    }

    private val _deleteStatus = MutableStateFlow<DeleteStatus>(DeleteStatus.Idle)
    val deleteStatus: StateFlow<DeleteStatus> = _deleteStatus.asStateFlow()

    val themeMode = settings.themeMode
    val currentUser = auth.sessionState.map { auth.currentUser() }
    val analyticsEnabled = settings.analyticsEnabled

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            // Log the toggle while analytics is still on, so an opt-out is captured.
            analytics.logEvent(AnalyticsEvents.ANALYTICS_TOGGLED, mapOf(AnalyticsParams.ENABLED to enabled))
            settings.setAnalyticsEnabled(enabled)
        }
    }

    val languages = LocaleManager.supported
    fun currentLanguageTag(): String = LocaleManager.currentTag()
    fun setLanguage(tag: String) = LocaleManager.setLanguage(tag)

    fun signOut() {
        viewModelScope.launch { auth.signOut() }
    }

    fun askDeleteAccount() {
        _deleteStatus.value = DeleteStatus.Confirming
    }

    fun cancelDelete() {
        _deleteStatus.value = DeleteStatus.Idle
    }

    /**
     * Play-required data-deletion flow:
     *  1. delete on auth provider
     *  2. wipe DataStore prefs
     *  3. wipe Room data
     */
    fun confirmDeleteAccount(db: KitDatabase) {
        viewModelScope.launch {
            _deleteStatus.value = DeleteStatus.Working
            when (val result = auth.deleteAccount()) {
                is AuthResult.Failure -> {
                    _deleteStatus.value = DeleteStatus.Error(result.code)
                    return@launch
                }
                else -> Unit
            }
            settings.clearAll()
            // Room.clearAllTables() is synchronous + asserts off main thread.
            withContext(Dispatchers.IO) { db.clearAllTables() }
            _deleteStatus.value = DeleteStatus.Done
        }
    }
}

