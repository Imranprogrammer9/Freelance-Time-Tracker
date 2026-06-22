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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.analytics.ScreenNames
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.data.local.KitDatabase
import dev.shipkaro.kit.core.designsystem.components.KitBottomSheet
import dev.shipkaro.kit.core.designsystem.components.KitDialog
import dev.shipkaro.kit.core.designsystem.components.KitListItem
import dev.shipkaro.kit.core.designsystem.settings.AccountRow
import dev.shipkaro.kit.core.designsystem.settings.DangerRow
import dev.shipkaro.kit.core.designsystem.settings.LegalLinks
import dev.shipkaro.kit.core.designsystem.settings.NavRow
import dev.shipkaro.kit.core.designsystem.settings.SettingsDivider
import dev.shipkaro.kit.core.designsystem.settings.SettingsSection
import dev.shipkaro.kit.core.designsystem.settings.ToggleRow
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import dev.shipkaro.kit.core.designsystem.theme.ThemeMode
import dev.shipkaro.kit.core.locale.LocaleManager
import dev.shipkaro.kit.core.util.CustomTabs
import dev.shipkaro.kit.core.util.PlayStoreLauncher
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onWhatsNew: () -> Unit,
    onProfile: () -> Unit = {},
    onOssLicenses: () -> Unit = {},
    vm: SettingsViewModel = koinViewModel(),
) {
    val theme by vm.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val user by vm.currentUser.collectAsState(initial = null)
    val analyticsEnabled by vm.analyticsEnabled.collectAsState(initial = true)
    val deleteStatus by vm.deleteStatus.collectAsState()
    val db = koinInject<KitDatabase>()
    val analytics = koinInject<AnalyticsManager>()
    val context = LocalContext.current

    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.SETTINGS) }
    // Intentionally NO LaunchedEffect on DeleteStatus.Done → calling onBack() here
    // raced with KitNavHost's sign-out redirect (both popped the back stack, leaving
    // an empty stack and a blank screen). After deletion the session flips to
    // SignedOut and KitNavHost routes back to Auth automatically; we don't need to
    // pop from this screen.

    var themeSheet by remember { mutableStateOf(false) }
    var languageSheet by remember { mutableStateOf(false) }

    val accountFallback = stringResource(R.string.settings_account_default_name)
    val privacyLabel = stringResource(R.string.legal_privacy)
    val termsLabel = stringResource(R.string.legal_terms)

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
                .verticalScroll(rememberScrollState())
                .padding(top = KitTheme.spacing.sm),
        ) {
            if (KitConfig.AUTH_ENABLED) {
                user?.let {
                    AccountRow(
                        name = it.displayName ?: it.email ?: accountFallback,
                        email = it.email.orEmpty(),
                        avatarUrl = it.avatarUrl,
                        onClick = onProfile,
                    )
                    Spacer(Modifier.height(KitTheme.spacing.lg))
                }
            }

            SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                NavRow(
                    title = stringResource(R.string.settings_theme),
                    leading = KitTheme.icons.palette,
                    chipColor = MaterialTheme.colorScheme.primary,
                    valueText = stringResource(theme.labelRes()),
                    onClick = { themeSheet = true },
                )
                if (vm.languages.size > 1) {
                    SettingsDivider()
                    NavRow(
                        title = stringResource(R.string.settings_language),
                        leading = KitTheme.icons.language,
                        chipColor = KitTheme.colors.info,
                        valueText = vm.languages
                            .firstOrNull { vm.currentLanguageTag().startsWith(it.tag) }?.displayName,
                        onClick = { languageSheet = true },
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.settings_section_privacy)) {
                ToggleRow(
                    title = stringResource(R.string.settings_analytics),
                    subtitle = stringResource(R.string.settings_analytics_subtitle),
                    leading = KitTheme.icons.shield,
                    chipColor = KitTheme.colors.success,
                    checked = analyticsEnabled,
                    onCheckedChange = vm::setAnalyticsEnabled,
                )
            }

            SettingsSection(title = stringResource(R.string.settings_section_about)) {
                NavRow(
                    title = stringResource(R.string.settings_whats_new),
                    leading = KitTheme.icons.update,
                    chipColor = KitTheme.colors.info,
                    onClick = onWhatsNew,
                )
                SettingsDivider()
                NavRow(
                    title = stringResource(R.string.settings_rate_app),
                    leading = KitTheme.icons.star,
                    chipColor = KitTheme.colors.warning,
                    onClick = { PlayStoreLauncher.openListing(context) },
                )
                SettingsDivider()
                NavRow(
                    title = stringResource(R.string.settings_oss_licenses),
                    leading = KitTheme.icons.info,
                    chipColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onOssLicenses,
                )
            }

            if (KitConfig.AUTH_ENABLED) {
                SettingsSection(title = stringResource(R.string.settings_section_account)) {
                    NavRow(
                        title = stringResource(R.string.settings_sign_out),
                        leading = KitTheme.icons.logout,
                        chipColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { vm.signOut() },
                    )
                    SettingsDivider()
                    DangerRow(
                        title = stringResource(R.string.settings_delete_account),
                        subtitle = stringResource(R.string.settings_delete_account_subtitle),
                        leading = KitTheme.icons.delete,
                        onClick = { vm.askDeleteAccount() },
                    )
                }
            }

            LegalLinks(
                links = listOf(
                    privacyLabel to { CustomTabs.openUrl(context, KitConfig.PRIVACY_URL) },
                    termsLabel to { CustomTabs.openUrl(context, KitConfig.TERMS_URL) },
                ),
            )
            Spacer(Modifier.height(KitTheme.spacing.lg))
        }
    }

    if (themeSheet) {
        ThemePickerSheet(
            current = theme,
            onPick = {
                vm.setThemeMode(it)
                themeSheet = false
            },
            onDismiss = { themeSheet = false },
        )
    }

    if (languageSheet) {
        LanguagePickerSheet(
            languages = vm.languages,
            currentTag = vm.currentLanguageTag(),
            onPick = {
                vm.setLanguage(it)
                languageSheet = false
            },
            onDismiss = { languageSheet = false },
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerSheet(
    current: ThemeMode,
    onPick: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    KitBottomSheet(onDismiss = onDismiss) {
        Text(
            stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = KitTheme.spacing.sm),
        )
        ThemeMode.entries.forEach { mode ->
            KitListItem(
                headline = stringResource(mode.labelRes()),
                trailing = checkTrailing(selected = mode == current),
                onClick = { onPick(mode) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(
    languages: List<LocaleManager.Language>,
    currentTag: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    KitBottomSheet(onDismiss = onDismiss) {
        Text(
            stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = KitTheme.spacing.sm),
        )
        languages.forEach { lang ->
            KitListItem(
                headline = lang.displayName,
                trailing = checkTrailing(selected = currentTag.startsWith(lang.tag)),
                onClick = { onPick(lang.tag) },
            )
        }
    }
}

/** Trailing check mark for the selected option in a picker sheet, or null. */
private fun checkTrailing(selected: Boolean): (@Composable () -> Unit)? =
    if (!selected) {
        null
    } else {
        {
            Icon(
                KitTheme.icons.check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}

