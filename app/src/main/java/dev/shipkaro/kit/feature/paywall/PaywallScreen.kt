package dev.shipkaro.kit.feature.paywall

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import dev.shipkaro.kit.R
import dev.shipkaro.kit.core.analytics.AnalyticsManager
import dev.shipkaro.kit.core.analytics.ScreenNames
import dev.shipkaro.kit.core.billing.messageRes
import dev.shipkaro.kit.core.config.KitConfig
import dev.shipkaro.kit.core.designsystem.components.KitBanner
import dev.shipkaro.kit.core.designsystem.components.KitBannerStyle
import dev.shipkaro.kit.core.designsystem.components.KitBulletRow
import dev.shipkaro.kit.core.designsystem.components.KitButton
import dev.shipkaro.kit.core.designsystem.components.KitButtonStyle
import dev.shipkaro.kit.core.designsystem.components.KitPricingCard
import dev.shipkaro.kit.core.designsystem.theme.KitTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Paywall — hero, benefit list, selectable [KitPricingCard] tiers, CTA, restore.
 * Honors [KitConfig.PAYWALL_MODE] (SOFT shows a skip action; HARD does not).
 * Shows a configuration banner instead of tiers when RevenueCat has no API key.
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
    val analytics = koinInject<AnalyticsManager>()

    val packages = offering?.availablePackages.orEmpty()
    var selected by remember { mutableStateOf<Package?>(null) }
    LaunchedEffect(packages) {
        if (selected == null && packages.isNotEmpty()) {
            selected = packages.firstOrNull { it.packageType == PackageType.ANNUAL } ?: packages.first()
        }
    }

    LaunchedEffect(Unit) { analytics.logScreen(ScreenNames.PAYWALL) }
    LaunchedEffect(status) { if (status is PurchaseViewModel.Status.Entitled) onPurchased() }
    LaunchedEffect(isPremium) { if (isPremium) onPurchased() }

    val working = status is PurchaseViewModel.Status.Working

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KitTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.md),
    ) {
        Spacer(Modifier.height(KitTheme.spacing.xl))

        // Hero crown chip
        Column(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                KitTheme.icons.success,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
        }

        Text(
            text = stringResource(R.string.paywall_headline),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.paywall_subhead),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(KitTheme.spacing.sm))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
        ) {
            KitBulletRow(stringResource(R.string.paywall_benefit_1))
            KitBulletRow(stringResource(R.string.paywall_benefit_2))
            KitBulletRow(stringResource(R.string.paywall_benefit_3))
        }

        Spacer(Modifier.height(KitTheme.spacing.sm))

        when (val s = status) {
            is PurchaseViewModel.Status.Error ->
                KitBanner(text = stringResource(s.code.messageRes()), style = KitBannerStyle.ERROR)
            PurchaseViewModel.Status.NothingRestored ->
                KitBanner(text = stringResource(R.string.paywall_nothing_restored), style = KitBannerStyle.INFO)
            else -> Unit
        }

        if (!vm.isConfigured) {
            KitBanner(
                text = stringResource(R.string.paywall_not_configured),
                style = KitBannerStyle.WARNING,
            )
        } else if (packages.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KitTheme.spacing.sm),
            ) {
                packages.forEach { pkg ->
                    KitPricingCard(
                        title = pkg.tierLabel(),
                        price = pkg.product.price.formatted,
                        caption = pkg.tierCaption(),
                        badge = if (pkg.packageType == PackageType.ANNUAL) {
                            stringResource(R.string.paywall_badge_best_value)
                        } else {
                            null
                        },
                        selected = selected?.identifier == pkg.identifier,
                        onClick = { selected = pkg },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(KitTheme.spacing.sm))

        KitButton(
            text = stringResource(R.string.paywall_cta),
            onClick = { selected?.let { pkg -> activity?.let { vm.purchase(it, pkg) } } },
            loading = working,
            enabled = vm.isConfigured && selected != null && !working,
        )

        KitButton(
            text = stringResource(R.string.paywall_restore),
            onClick = vm::restore,
            style = KitButtonStyle.TEXT,
            enabled = !working,
        )

        if (isSoft) {
            KitButton(
                text = stringResource(R.string.paywall_skip),
                onClick = onDismiss,
                style = KitButtonStyle.TEXT,
                enabled = !working,
            )
        }

        Text(
            text = stringResource(R.string.paywall_fine_print),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = KitTheme.spacing.sm),
        )
    }
}

@Composable
private fun Package.tierLabel(): String = stringResource(
    when (packageType) {
        PackageType.LIFETIME -> R.string.paywall_tier_lifetime
        PackageType.ANNUAL -> R.string.paywall_tier_yearly
        PackageType.SIX_MONTH -> R.string.paywall_tier_six_month
        PackageType.THREE_MONTH -> R.string.paywall_tier_three_month
        PackageType.MONTHLY -> R.string.paywall_tier_monthly
        PackageType.WEEKLY -> R.string.paywall_tier_weekly
        else -> R.string.paywall_tier_plan
    },
)

@Composable
private fun Package.tierCaption(): String? = when (packageType) {
    PackageType.ANNUAL -> stringResource(R.string.paywall_caption_billed_yearly)
    PackageType.MONTHLY -> stringResource(R.string.paywall_caption_billed_monthly)
    PackageType.LIFETIME -> stringResource(R.string.paywall_caption_one_time)
    else -> null
}

/** Walk the Context wrapper chain to the hosting Activity (needed for the billing sheet). */
@Composable
private fun LocalActivity(): Activity? {
    var ctx: Context = LocalContext.current
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
