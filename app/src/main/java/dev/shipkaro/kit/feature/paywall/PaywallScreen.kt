package dev.shipkaro.kit.feature.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shipkaro.kit.R

/**
 * Placeholder paywall. Phase 2 wires RevenueCat offerings + entitlement check.
 */
@Composable
fun PaywallScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.paywall_title))
        Button(onClick = onContinue) { Text(stringResource(R.string.paywall_subscribe)) }
        TextButton(onClick = onContinue) { Text(stringResource(R.string.paywall_skip)) }
    }
}
