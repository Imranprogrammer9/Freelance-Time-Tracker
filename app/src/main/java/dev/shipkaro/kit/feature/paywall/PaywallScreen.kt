package dev.shipkaro.kit.feature.paywall

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.Package
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.billing.messageRes
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.designsystem.components.KitBanner
import dev.shipkaro.kit.core.designsystem.components.KitBannerStyle
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.components.KitCard
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Custom paywall built on kit components. Honors [KitConfig.PAYWALL_MODE]:
 *  - SOFT: shows a skip action ([onDismiss]) so the user can continue free
 *  - HARD: no skip — only purchase or restore advances ([onPurchased])
 *
 * When RevenueCat has no API key ([PurchaseViewModel.isConfigured] false), shows a
 * configuration placeholder instead of live offerings so the kit still runs.
 */
@Composable
fun PaywallScreen(
    onPurchased: () -> Unit,
    onDismiss: () -> Unit,
    vm: PurchaseViewModel = koinViewModel(),
) {
    val status by vm.status.collectAsState()
    val offering by vm.currentOffering.collectAsState()
    val isPremium by vm.isPremium.collectAsState()
    val activity = LocalActivity()
    val isSoft = KitConfig.PAYWALL_MODE == KitConfig.PaywallMode.SOFT

    LaunchedEffect(status) {
        if (status is PurchaseViewModel.Status.Entitled) onPurchased()
    }
    // Already-entitled user (e.g. restored elsewhere) never sees the paywall.
    LaunchedEffect(isPremium) {
        if (isPremium) onPurchased()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KitTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xl))

        Text(
            text = stringResource(R.string.paywall_headline),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.paywall_subhead),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(KitTheme.spacing.sm))

        BenefitsList()

        Spacer(Modifier.height(KitTheme.spacing.sm))

        when (val s = status) {
            is PurchaseViewModel.Status.Error ->
                KitBanner(text = stringResource(s.code.messageRes()), style = KitBannerStyle.ERROR)
            PurchaseViewModel.Status.NothingRestored ->
                KitBanner(text = stringResource(R.string.paywall_nothing_restored), style = KitBannerStyle.INFO)
            else -> Unit
        }

        val working = status is PurchaseViewModel.Status.Working

        if (!vm.isConfigured) {
            KitBanner(
                text = stringResource(R.string.paywall_not_configured),
                style = KitBannerStyle.WARNING,
            )
        } else {
            val packages = offering?.availablePackages.orEmpty()
            packages.forEach { pkg ->
                PackageCard(
                    pkg = pkg,
                    enabled = !working,
                    onClick = { activity?.let { vm.purchase(it, pkg) } },
                )
            }
        }

        Spacer(Modifier.height(KitTheme.spacing.sm))

        KitButton(
            text = stringResource(R.string.paywall_restore),
            onClick = vm::restore,
            style = KitButtonStyle.TEXT,
            loading = working,
        )

        // Skip is SOFT-mode only — HARD paywall blocks until purchase/restore.
        if (isSoft) {
            KitButton(
                text = stringResource(R.string.paywall_skip),
                onClick = onDismiss,
                style = KitButtonStyle.TEXT,
                enabled = !working,
            )
        }
    }
}

@Composable
private fun BenefitsList() {
    // Kit author edits these benefit lines for their app (kept localized in strings.xml).
    val benefits = listOf(
        R.string.paywall_benefit_1,
        R.string.paywall_benefit_2,
        R.string.paywall_benefit_3,
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
    ) {
        benefits.forEach { res ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    KitTheme.icons.success,
                    contentDescription = null,
                    tint = KitTheme.colors.success,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(KitTheme.spacing.sm))
                Text(stringResource(res), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun PackageCard(
    pkg: Package,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    KitCard(onClick = if (enabled) onClick else null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pkg.product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                pkg.product.description.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = pkg.product.price.formatted,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Walk the Context wrapper chain to the hosting Activity (needed for the billing sheet). */
@Composable
private fun LocalActivity(): Activity? {
    var ctx: Context = androidx.compose.ui.platform.LocalContext.current
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
