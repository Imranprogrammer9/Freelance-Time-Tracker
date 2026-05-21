package dev.shipkaro.kit.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import dev.shipkaro.kit.core.designsystem.components.KitFeatureRow
import dev.shipkaro.kit.core.designsystem.theme.KitTheme

/**
 * Kit entry point — a placeholder "what is this kit" screen with a button into the
 * bundled demo app. The developer REPLACES this screen with their real start screen;
 * see the "Make it yours" guide. There is no real app flow out of the box — the demo
 * is the only thing wired.
 */
@Composable
fun WelcomeScreen(onLaunchDemo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KitTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xxxl))

        Image(
            painter = painterResource(R.drawable.ic_shipkaro_mark),
            contentDescription = null,
            modifier = Modifier.size(88.dp),
        )

        Spacer(Modifier.height(KitTheme.spacing.lg))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(KitTheme.spacing.sm))
        Text(
            text = stringResource(R.string.welcome_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(KitTheme.spacing.xxl))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.lg),
        ) {
            KitFeatureRow(
                icon = KitTheme.icons.lock,
                title = stringResource(R.string.welcome_feature_auth_title),
                description = stringResource(R.string.welcome_feature_auth_desc),
            )
            KitFeatureRow(
                icon = KitTheme.icons.star,
                title = stringResource(R.string.welcome_feature_paywall_title),
                description = stringResource(R.string.welcome_feature_paywall_desc),
                chipColor = KitTheme.colors.warning,
            )
            KitFeatureRow(
                icon = KitTheme.icons.info,
                title = stringResource(R.string.welcome_feature_ops_title),
                description = stringResource(R.string.welcome_feature_ops_desc),
                chipColor = KitTheme.colors.info,
            )
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(KitTheme.spacing.xxl))

        if (KitConfig.SAMPLE_FEATURE_ENABLED) {
            KitButton(
                text = stringResource(R.string.welcome_launch_demo),
                onClick = onLaunchDemo,
            )
        }
        Spacer(Modifier.height(KitTheme.spacing.sm))
        Text(
            text = stringResource(R.string.welcome_replace_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(KitTheme.spacing.lg))
    }
}
