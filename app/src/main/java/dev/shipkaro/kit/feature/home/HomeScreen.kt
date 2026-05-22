package dev.shipkaro.kit.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Default Home screen — the start destination after Splash / Onboarding / Auth / Paywall.
 *
 * Out of the box it is intentionally minimal: a brand placeholder and a button into
 * Settings (plus a button to launch the bundled demo while
 * `KitConfig.SAMPLE_FEATURE_ENABLED` is true). Replace the body with your real app's
 * main screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onLaunchDemo: () -> Unit,
) {
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
                .padding(KitTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_shipkaro_mark),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )

            Spacer(Modifier.height(KitTheme.spacing.lg))

            Text(
                text = stringResource(R.string.home_heading),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(KitTheme.spacing.sm))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(KitTheme.spacing.xxl))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
            ) {
                if (KitConfig.SAMPLE_FEATURE_ENABLED) {
                    KitButton(
                        text = stringResource(R.string.home_launch_demo),
                        onClick = onLaunchDemo,
                    )
                }
                KitButton(
                    text = stringResource(R.string.home_open_settings),
                    onClick = onOpenSettings,
                    style = KitButtonStyle.SECONDARY,
                )
            }
        }
    }
}
