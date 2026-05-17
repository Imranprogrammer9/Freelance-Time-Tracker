package dev.shipkaro.kit.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shipkaro.kit.core.data.settings.SettingsRepository
import dev.shipkaro.kit.core.designsystem.theme.ThemeMode
import dev.shipkaro.kit.core.locale.LocaleManager
import kotlinx.coroutines.launch

class SettingsViewModel(private val settings: SettingsRepository) : ViewModel() {

    val themeMode = settings.themeMode

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    val languages = LocaleManager.supported
    fun currentLanguageTag(): String = LocaleManager.currentTag()
    fun setLanguage(tag: String) = LocaleManager.setLanguage(tag)
}
