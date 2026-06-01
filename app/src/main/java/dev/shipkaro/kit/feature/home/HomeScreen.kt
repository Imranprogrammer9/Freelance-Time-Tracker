package dev.shipkaro.kit.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.auth.AuthRepository
import dev.shipkaro.kit.core.auth.SessionState
import dev.shipkaro.kit.core.billing.PurchaseManager
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.settings.SettingsDivider
import dev.shipkaro.kit.core.designsystem.settings.SettingsSection
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import org.koin.compose.koinInject

/**
 * Default Home screen — the start destination after Splash / Onboarding / Auth / Paywall.
 *
 * Intentionally minimal: a brand mark + a "System status" panel that surfaces
 * what the kit wired up based on the developer's setup choices, so even a
 * non-coder can confirm at a glance that their config took effect. Replace
 * this with your app's real main screen when you start building.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onBrowseComponents: () -> Unit = {},
) {
    val auth = koinInject<AuthRepository>()
    val purchases = koinInject<PurchaseManager>()
    val session by auth.sessionState.collectAsState(initial = SessionState.Loading)
    val isPremium by purchases.isPremium.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            KitTheme.icons.settings,
                            contentDescription = stringResource(R.string.action_open_settings),
                        )
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
                .padding(KitTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(KitTheme.spacing.lg))

            Image(
                painter = painterResource(R.drawable.ic_shipkaro_mark),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )

            Spacer(Modifier.height(KitTheme.spacing.md))

            Text(
                text = stringResource(R.string.home_heading),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(KitTheme.spacing.xs))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(KitTheme.spacing.xl))

            SettingsSection(title = stringResource(R.string.home_status_section_title)) {
                StatusRow(
                    label = stringResource(R.string.home_status_auth),
                    value = authStatusText(session),
                )
                SettingsDivider()
                StatusRow(
                    label = stringResource(R.string.home_status_paywall),
                    value = paywallStatusText(isPremium),
                )
                SettingsDivider()
                StatusRow(
                    label = stringResource(R.string.home_status_analytics),
                    value = analyticsStatusText(),
                )
                SettingsDivider()
                StatusRow(
                    label = stringResource(R.string.home_status_remote_config),
                    value = remoteConfigStatusText(),
                )
            }

            Spacer(Modifier.height(KitTheme.spacing.xl))

            KitButton(
                text = stringResource(R.string.home_browse_components),
                onClick = onBrowseComponents,
                style = KitButtonStyle.PRIMARY,
                icon = KitTheme.icons.palette,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(KitTheme.spacing.sm))

            KitButton(
                text = stringResource(R.string.home_open_settings),
                onClick = onOpenSettings,
                style = KitButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KitTheme.spacing.md, vertical = KitTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun authStatusText(session: SessionState): String {
    if (!KitConfig.AUTH_ENABLED) return stringResource(R.string.home_status_disabled)
    return when (session) {
        is SessionState.SignedIn -> {
            val who = session.user.displayName ?: session.user.email.orEmpty()
            if (who.isNotBlank()) {
                stringResource(R.string.home_status_auth_signed_in, who)
            } else {
                stringResource(R.string.home_status_auth_signed_out)
            }
        }
        SessionState.SignedOut -> stringResource(R.string.home_status_auth_signed_out)
        SessionState.Loading -> "—"
    }
}

@Composable
private fun paywallStatusText(isPremium: Boolean): String {
    if (!KitConfig.PAYWALL_ENABLED) return stringResource(R.string.home_status_disabled)
    if (isPremium) return stringResource(R.string.home_status_paywall_premium)
    return when (KitConfig.PAYWALL_MODE) {
        KitConfig.PaywallMode.SOFT -> stringResource(R.string.home_status_paywall_soft)
        KitConfig.PaywallMode.HARD -> stringResource(R.string.home_status_paywall_hard)
    }
}

@Composable
private fun analyticsStatusText(): String =
    if (KitConfig.ANALYTICS_ENABLED) {
        stringResource(R.string.home_status_enabled)
    } else {
        stringResource(R.string.home_status_disabled)
    }

@Composable
private fun remoteConfigStatusText(): String = when (KitConfig.REMOTE_CONFIG_PROVIDER) {
    KitConfig.RemoteConfigProvider.LOCAL -> stringResource(R.string.home_status_remote_local)
    KitConfig.RemoteConfigProvider.FIREBASE -> stringResource(R.string.home_status_remote_firebase)
    KitConfig.RemoteConfigProvider.SUPABASE -> stringResource(R.string.home_status_remote_supabase)
}
