package dev.shipkaro.kit.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.designsystem.components.KitDialog
import dev.shipkaro.kit.core.designsystem.settings.AccountRow
import dev.shipkaro.kit.core.designsystem.settings.DangerRow
import dev.shipkaro.kit.core.designsystem.settings.LegalLinks
import dev.shipkaro.kit.core.designsystem.settings.NavRow
import dev.shipkaro.kit.core.designsystem.settings.SettingsDivider
import dev.shipkaro.kit.core.designsystem.settings.SettingsSection
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import dev.shipkaro.kit.core.designsystem.theme.ThemeMode
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = koinViewModel(),
) {
    val theme by vm.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val user by vm.currentUser.collectAsState(initial = null)
    val deleteStatus by vm.deleteStatus.collectAsState()
    val db = koinInject<KitDatabase>()

    val selectedLabel = stringResource(R.string.settings_value_selected)
    val privacyLabel = stringResource(R.string.legal_privacy)
    val termsLabel = stringResource(R.string.legal_terms)
    val accountFallback = stringResource(R.string.settings_account_default_name)

    LaunchedEffect(deleteStatus) {
        if (deleteStatus is SettingsViewModel.DeleteStatus.Done) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(KitTheme.icons.back, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            user?.let {
                AccountRow(
                    name = it.displayName ?: it.email ?: accountFallback,
                    email = it.email.orEmpty(),
                    avatarUrl = it.avatarUrl,
                    onClick = { /* future: profile screen */ },
                )
                SettingsDivider()
            }

            SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                ThemeMode.entries.forEach { mode ->
                    NavRow(
                        title = stringResource(mode.labelRes()),
                        valueText = if (theme == mode) selectedLabel else null,
                        onClick = { vm.setThemeMode(mode) },
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.settings_section_language)) {
                vm.languages.forEach { lang ->
                    val current = vm.currentLanguageTag()
                    NavRow(
                        title = lang.displayName,
                        valueText = if (current.startsWith(lang.tag)) selectedLabel else null,
                        onClick = { vm.setLanguage(lang.tag) },
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.settings_section_account)) {
                NavRow(
                    title = stringResource(R.string.settings_sign_out),
                    leading = KitTheme.icons.logout,
                    onClick = { vm.signOut() },
                )
                DangerRow(
                    title = stringResource(R.string.settings_delete_account),
                    subtitle = stringResource(R.string.settings_delete_account_subtitle),
                    leading = KitTheme.icons.delete,
                    onClick = { vm.askDeleteAccount() },
                )
            }

            Spacer(Modifier.height(KitTheme.spacing.lg))

            LegalLinks(
                links = listOf(
                    privacyLabel to { /* TODO: open privacy URL */ },
                    termsLabel to { /* TODO: open terms URL */ },
                ),
            )
        }
    }

    if (deleteStatus is SettingsViewModel.DeleteStatus.Confirming ||
        deleteStatus is SettingsViewModel.DeleteStatus.Working ||
        deleteStatus is SettingsViewModel.DeleteStatus.Error
    ) {
        KitDialog(
            title = stringResource(R.string.settings_delete_dialog_title),
            message = stringResource(R.string.settings_delete_dialog_message),
            confirmLabel = stringResource(R.string.action_delete),
            dismissLabel = stringResource(R.string.action_cancel),
            destructive = true,
            onConfirm = { vm.confirmDeleteAccount(db) },
            onDismiss = { vm.cancelDelete() },
        )
    }
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}
