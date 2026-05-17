package dev.shipkaro.kit.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.designsystem.theme.ThemeMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = koinViewModel(),
) {
    val theme by vm.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val currentLang = vm.currentLanguageTag()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.settings_theme))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = theme == mode,
                    onClick = { vm.setThemeMode(mode) },
                    label = { Text(mode.name) },
                )
            }
        }

        Text(stringResource(R.string.settings_language))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            vm.languages.forEach { lang ->
                FilterChip(
                    selected = currentLang.startsWith(lang.tag),
                    onClick = { vm.setLanguage(lang.tag) },
                    label = { Text(lang.displayName) },
                )
            }
        }

        Button(onClick = onBack) { Text(stringResource(R.string.action_back)) }
    }
}
